package com.gm.demo.crawler.service;

import com.gm.demo.crawler.dao.mapper.TabMapper;
import com.gm.demo.crawler.dao.model.Metadata;
import com.gm.demo.crawler.entity.req.SaveMetadataReq;
import com.gm.help.base.Quick;
import com.gm.utils.base.Assert;
import com.gm.utils.base.Bool;
import com.gm.utils.base.Convert;
import com.gm.utils.base.ExceptionUtils;
import com.gm.utils.ext.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
    public static final String MT_COMMENT_TAB = "mt_comment";

    @Autowired
    TabMapper tabMapper;
    @Autowired
    MetadataServiceImpl metadataService;

    /**
     * Handler list.
     *
     * @param result the result
     * @return the list
     */
    public Integer handler(String result) {
        // 获取返回的Json对象
        Map<String, Object> map = Json.toMap(result);
        // 美团的Json数据放在data里
        Object data = Assert.isNull(map.get(DATA_FIELD), String.format("美团数据是空,完整响应\n%s", result));
        map = Json.o2o(data, Map.class);
        // 遍历查找评论信息
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> next = it.next();
            String key = next.getKey();
            // 是个集合
            if (key.contains(COMMENT_FIELD)) {
                List<Map<String, Object>> cs = (List<Map<String, Object>>) next.getValue();
                return handler(cs);
            }
        }
        return 0;
    }

    /**
     * 处理评论信息.
     *
     * @param maps 评论信息
     */
    public Integer handler(List<Map<String, Object>> maps) {
        if(maps.size()<=0){
            ExceptionUtils.cast("没有数据了");
        }
        List<Metadata> data = metadataService.getTab(MT_COMMENT_TAB);
        Map<String, Metadata> fields = data.stream()
                .filter(x -> !ID.equalsIgnoreCase(x.getField()))
                .collect(Collectors.toMap(Metadata::getField, x -> x));
        for (Map<String, Object> map : maps) {
            // 去除系统需求字段
            Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> next = it.next();
                String key = next.getKey();
                // 从字段将要存储值
                String value = next.getValue()!=null?next.getValue().toString():null;
                if (Bool.isNull(value) || !fields.containsKey(key)) {
                    it.remove();
                } else {
                    // 此字段数据库信息
                    Metadata metadata = fields.get(key);
                    // 检测长度
                    if (metadata.getLen() < Convert.toEmpty(value).length()) {
                        SaveMetadataReq req = new SaveMetadataReq();
                        req.setTab(MT_COMMENT_TAB);
                        req.setField(metadata.getField());
                        req.setLen(value.length() + 10);
                        metadataService.save(req);
                    }
                }
            }
        }
        Integer count = tabMapper.filters(MT_COMMENT_TAB, Arrays.asList(USERNAME_FIELD, COMMENT_FIELD), maps.toArray(new HashMap[0]));
        // 重复不再保存
        if (count > 0) {
            return 0;
        }
        // 存储系统需求信息
        return metadataService.save(MT_COMMENT_TAB, fields.keySet(), maps.toArray(new HashMap[0]));
    }
}
