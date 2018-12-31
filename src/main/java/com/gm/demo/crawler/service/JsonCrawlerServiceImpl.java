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
 * @author Jason
 */
@Service
public class JsonCrawlerServiceImpl extends CrawlerServiceImpl {

    @Autowired
    TabMapper tabMapper;

    @Autowired
    MetadataServiceImpl metadataService;

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

    public Integer handler(Gather gather, String result) {
        List<Map<String, Object>> cs = getStringObjectMap(result, gather);
        return handler(gather.getTab(), cs, gather.getFilters().toLowerCase().split(","));
    }

}
