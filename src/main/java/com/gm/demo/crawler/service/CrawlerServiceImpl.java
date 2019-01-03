package com.gm.demo.crawler.service;

import com.gm.demo.crawler.dao.model.Gather;
import com.gm.demo.crawler.dao.model.Metadata;
import com.gm.strong.Str;
import com.gm.utils.base.Bool;
import com.gm.utils.base.ExceptionUtils;
import com.gm.utils.base.Logger;
import com.gm.utils.ext.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jason
 */
@Service
public class CrawlerServiceImpl {

    public static final String ID = "id";
    public static final String IS_CRAWL = "isCrawl";
    public static final String[] DEFAULT_FIELD = {ID, IS_CRAWL};

    @Autowired
    MetadataServiceImpl metadataService;

    public List<Map<String, Object>> getStringObjectMap(String result, Gather gather) {
        // 获取返回的Json对象｛全部小写｝
        Map<String, Object> map = Json.toMap(result.toLowerCase());
        if(!Bool.isNull(map)) {
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
        }
        return ExceptionUtils.process(Logger.error(String.format("收集数据失败!\n{%s}", result)));
    }

    public Integer handler(String tab, List<Map<String, Object>> maps, String... filters) {
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
            metadataService.checkFields(tab, fields, map);
        }
        // 去除特殊字符
        metadataService.replace(maps, "`", "\'", "\"", "\\");
        // 去重
        metadataService.distinct(tab, maps, filters);
        // 存储系统需求信息
        return metadataService.save(tab, fields.keySet(), maps.toArray(new HashMap[0]));
    }

}
