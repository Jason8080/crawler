package com.gm.demo.crawler.service;

import com.gm.demo.crawler.dao.mapper.TabMapper;
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
 * @author Jason
 */
@Service
public class MtServiceImpl {

    public static final String ID = "id";
    public static final String DATA_FIELD = "data";
    public static final String USERNAME_FIELD = "userName";
    public static final String COMMENT_FIELD = "comment";
    public static final String POI_FIELD = "poi";
    public static final String POI_ID_FIELD = "poiId";
    public static final String MT_COMMENT_TAB = "mt_comment";
    public static final String MT_MERCHANT_TAB = "mt_merchant";

    @Autowired
    TabMapper tabMapper;
    @Autowired
    MetadataServiceImpl metadataService;

    /**
     * 直接获取数据
     *
     * @param result
     * @return
     */
    private Map<String, Object> getStringObjectMap(String result) {
        // 获取返回的Json对象
        Map<String, Object> map = Json.toMap(result);
        // 美团的Json数据放在data里
        Object data = map.get(DATA_FIELD);
        if (Bool.isNull(data)) {
            Logger.error(String.format("美团数据是空,完整响应\n%s", result));
        }
        map = Json.o2o(data, Map.class);
        return map;
    }

    /**
     * 商家列表处理
     *
     * @param json
     * @return
     */
    public Integer handlerMerchant(String json) {
        Map<String, Object> map = getStringObjectMap(json);
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> next = it.next();
            String key = next.getKey();
            // 是个集合
            if (key.contains(POI_FIELD)) {
                List<Map<String, Object>> ms = (List<Map<String, Object>>) next.getValue();
                Integer count = handler(MT_MERCHANT_TAB, ms, POI_ID_FIELD);
                if (count > 0) {
                    return count;
                }
            }
        }
        return 0;
    }

    /**
     * 评论列表处理.
     *
     * @param result the result
     * @return the list
     */
    public Integer handlerComment(String result) {
        Map<String, Object> map = getStringObjectMap(result);
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> next = it.next();
            String key = next.getKey();
            // 是个集合
            if (key.contains(COMMENT_FIELD)) {
                List<Map<String, Object>> cs = (List<Map<String, Object>>) next.getValue();
                Integer count = handler(MT_COMMENT_TAB, cs, COMMENT_FIELD, USERNAME_FIELD);
                if (count > 0) {
                    return count;
                }
            }
        }
        return 0;
    }

    /**
     * 处理信息.
     *
     * @param maps 评论信息
     */
    public Integer handler(String tab, List<Map<String, Object>> maps, String... filters) {
        if (maps.size() <= 0) {
            ExceptionUtils.cast("没有数据了");
        }
        List<Metadata> data = metadataService.getTab(tab);
        Map<String, Metadata> fields = data.stream()
                .filter(x -> !ID.equalsIgnoreCase(x.getField()))
                .collect(Collectors.toMap(Metadata::getField, x -> x));
        for (int i = 0; i < maps.size(); i++) {
            Map<String, Object> map = maps.get(i);
            // 是否有空
            boolean have = false;
            for (String field : filters) {
                Object o = map.get(field);
                if (Bool.isNull(o)) {
                    // 去除空评论
                    maps.remove(i--);
                    have = true;
                    break;
                }
            }
            if (!have) {
                checkFields(tab, fields, map);
            }
        }
        // 存储系统需求信息
        return metadataService.save(tab, fields.keySet(), maps.toArray(new HashMap[0]));
    }

    private void checkFields(String tab, Map<String, Metadata> fields, Map<String, Object> map) {
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> next = it.next();
            String key = next.getKey();
            // 从字段将要存储值
            String value = next.getValue() != null ? next.getValue().toString() : "";
            if (new Str(value).contains("\'", "\"")) {
                value = value.replace("\'", "`");
                value = value.replace("\"", "`");
                map.put(key, value.trim());
            }
            if (!Bool.isNull(value) && fields.containsKey(key)) {
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
