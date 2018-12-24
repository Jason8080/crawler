package com.gm.demo.crawler.service;

import com.gm.demo.crawler.dao.mapper.TabMapper;
import com.gm.demo.crawler.dao.mapper.ext.MtFieldsMapperExt;
import com.gm.demo.crawler.dao.model.Metadata;
import com.gm.demo.crawler.dao.model.MtFields;
import com.gm.demo.crawler.entity.req.SaveMetadataReq;
import com.gm.strong.Str;
import com.gm.utils.base.*;
import com.gm.utils.ext.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jason
 */
@Service
public class MtCrawlerServiceImpl {

    public static final String ID = "id";
    public static final String IS_CRAWL = "isCrawl";
    public static final String[] DEFAULT_FIELD = {ID, IS_CRAWL};

    @Autowired
    TabMapper tabMapper;
    @Autowired
    MtFieldsMapperExt mtFieldsMapperExt;
    @Autowired
    MetadataServiceImpl metadataService;

    /**
     * 直接获取数据
     *
     * @param result
     * @param mfs
     * @return
     */
    private Map<String, Object> getStringObjectMap(String result, MtFields mfs) {
        // 获取返回的Json对象｛全部小写｝
        Map<String, Object> map = Json.toMap(result.toLowerCase());
        // 美团的Json数据放在data里
        Object data = map.get(mfs.getData().toLowerCase());
        if (Bool.isNull(data)) {
            ExceptionUtils.cast(Logger.error("美团数据是空,可能需要完善访问要求"));
        }
        map = Json.o2o(data, Map.class);
        return map;
    }

    /**
     * 评论列表处理.
     *
     * @param result the result
     * @return the list
     */
    public Integer handler(String tab, String result) {
        MtFields mfs = Assert.isNull(mtFieldsMapperExt.getTab(tab), String.format("请配置提取方案%s", result));
        Map<String, Object> map = getStringObjectMap(result, mfs);
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> next = it.next();
            // 是个集合
            if (new Str(mfs.getEcho().toLowerCase().split(",")).contains(next.getKey())) {
                if (Bool.isNull(next.getValue())) {
                    ExceptionUtils.cast(Logger.error(String.format("没有数据了%s", Json.toJson(map))));
                }
                List<Map<String, Object>> cs = (List<Map<String, Object>>) next.getValue();
                return handler(mfs.getTab(), cs, mfs.getFilters().toLowerCase().split(","));
            }
        }
        return 0;
    }

    /**
     * 处理信息.
     *
     * @param maps 数据信息
     */
    public Integer handler(String tab, List<Map<String, Object>> maps, String... filters) {
        if (maps.size() <= 0) {
            ExceptionUtils.cast(Logger.error("没有数据了"));
        }
        List<Metadata> data = metadataService.getTab(tab);
        Map<String, Metadata> fields = data.stream()
                .filter(x -> !new Str(DEFAULT_FIELD).contains(x.getField()))
                .collect(Collectors.toMap(x -> x.getField().toLowerCase(), x -> x));
        for (int i = 0; i < maps.size(); i++) {
            Map<String, Object> map = maps.get(i);
            stop:
            for (String field : filters) {
                Object o = map.get(field);
                if (Bool.isNull(o)) {
                    maps.remove(i--);
                    break stop;
                }
            }
            checkFields(tab, fields, map);
        }
        // 去除特殊字符
        metadataService.replace(maps, "`", "\'", "\"", "\\");
        // 去重
        metadataService.distinct(tab, maps, filters);
        // 存储系统需求信息
        return metadataService.save(tab, fields.keySet(), maps.toArray(new HashMap[0]));
    }

    private void checkFields(String tab, Map<String, Metadata> fields, Map<String, Object> map) {
        Map<String, String> def = fields.values().stream().filter(x -> !Bool.isNull(x.getDef())).collect(Collectors.toMap(Metadata::getField, x -> x.getDef()));
        // 如果有默认值需要设置一下
        Iterator<Map.Entry<String, String>> itDef = def.entrySet().iterator();
        while (itDef.hasNext()) {
            Map.Entry<String, String> defNext = itDef.next();
            map.put(defNext.getKey(), defNext.getValue());
        }
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> next = it.next();
            String key = next.getKey();
            // 从字段将要存储值
            String value = next.getValue() != null ? next.getValue().toString() : "";
            if (fields.containsKey(key)) {
                // 此字段数据信息
                Metadata metadata = fields.get(key);
                // 检测长度
                if (metadata.getLen() < Convert.toEmpty(value).length()) {
                    SaveMetadataReq req = new SaveMetadataReq();
                    req.setTab(tab);
                    req.setField(metadata.getField());
                    // 长度不够
                    req.setLen(value.length() + 10);
                    metadataService.save(req);
                }
            }
        }
    }
}
