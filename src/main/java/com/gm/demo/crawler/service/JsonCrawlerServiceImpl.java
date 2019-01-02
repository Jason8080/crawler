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

    public Integer handler(Gather gather, String result) {
        List<Map<String, Object>> cs = getStringObjectMap(result, gather);
        return handler(gather.getTab(), cs, gather.getFilters().toLowerCase().split(","));
    }


    @Override
    public Integer handler(String tab, List<Map<String, Object>> maps, String... filters) {
        if (maps.size() <= 0) {
            ExceptionUtils.cast(Logger.error("没有数据了"));
        }
        return super.handler(tab, maps, filters);
    }
}
