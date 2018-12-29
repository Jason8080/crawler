package com.gm.demo.crawler.service;

import com.gm.demo.crawler.dao.mapper.TabMapper;
import com.gm.demo.crawler.dao.model.Gather;
import com.gm.demo.crawler.dao.model.Metadata;
import com.gm.demo.crawler.entity.req.SaveMetadataReq;
import com.gm.strong.Str;
import com.gm.utils.base.Bool;
import com.gm.utils.base.Convert;
import com.gm.utils.base.ExceptionUtils;
import com.gm.utils.base.Logger;
import com.gm.utils.ext.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Crawler service.
 *
 * @author Jason
 */
@Service
public class CrawlerServiceImpl {

    /**
     * The constant ID.
     */
    public static final String ID = "id";
    /**
     * The constant IS_CRAWL.
     */
    public static final String IS_CRAWL = "isCrawl";
    /**
     * The constant DEFAULT_FIELD.
     */
    public static final String[] DEFAULT_FIELD = {ID, IS_CRAWL};

    /**
     * The Tab mapper.
     */
    @Autowired
    TabMapper tabMapper;
    /**
     * The Metadata service.
     */
    @Autowired
    MetadataServiceImpl metadataService;

    /**
     * 根据方案收集路径收集数据
     *
     * @param result
     * @param gather
     * @return
     */
    private List<Map<String, Object>> getStringObjectMap(String result, Gather gather) {
        // 获取返回的Json对象｛全部小写｝
        Map<String, Object> map = Json.toMap(result.toLowerCase());
        // 美团的Json数据放在data里
        String[] split = gather.getData().split(",");
        for (int i = 0; i < split.length; i++) {
            Object data = map.get(split[i].toLowerCase());
            if (i == split.length - 1) {
                return (List<Map<String, Object>>) data;
            } else if (!Bool.isNull(data)) {
                map = Json.o2o(data, Map.class);
            }
        }
        return ExceptionUtils.process(Logger.error(String.format("数据收集失败! {%s}", gather.getData())));
    }

    /**
     * 数据列表收集.
     *
     * @param gather 收集方案
     * @param result JsonResult
     * @return 收集储量
     */
    public Integer handler(Gather gather, String result) {
        List<Map<String, Object>> cs = getStringObjectMap(result, gather);
        return handler(gather.getTab(), cs, gather.getFilters().toLowerCase().split(","));
    }

    /**
     * 处理数据集.
     *
     * @param tab     收集方案
     * @param maps    数据集
     * @param filters 过滤字段
     * @return 处理数量
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
                if (!"text".equals(metadata.getDataType()) && metadata.getLen() < Convert.toEmpty(value).length()) {
                    SaveMetadataReq req = new SaveMetadataReq();
                    req.setTab(tab);
                    req.setFields(new String[]{metadata.getField()});
                    // 长度不够
                    int len;
                    if ("varchar".equals(metadata.getDataType())) {
                        len = value.length() * 3 + 10;
                        req.setLen(len > 255 ? 0 : len);
                        req.setDataType(len > 255 ? "text" : metadata.getDataType());
                    } else {
                        len = value.length() + 1;
                        req.setLen(len);
                    }
                    metadataService.save(req);
                }
            }
        }
    }
}
