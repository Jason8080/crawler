package com.gm.demo.crawler.service;

import com.gm.demo.crawler.dao.mapper.TabMapper;
import com.gm.demo.crawler.dao.model.Gather;
import com.gm.enums.Regexp;
import com.gm.utils.ext.Regex;
import com.gm.utils.ext.Web;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jason
 */
@Service
public class LunaticCrawlerServiceImpl extends CrawlerServiceImpl {

    @Autowired
    TabMapper tabMapper;
    @Autowired
    MetadataServiceImpl metadataService;

    public Integer handler(Gather gather, String url, String html) {
        List<String> mobiles = Regex.find(html, Regexp.FIND_MOBILE.getCode());
        mobiles = mobiles.stream().distinct().collect(Collectors.toList());
        List<Map<String, Object>> maps = new ArrayList();
        for (String mobile : mobiles) {
            Map<String, Object> map = new HashMap(0);
            map.put("url", url);
            map.put("mobile", mobile);
            maps.add(map);
        }
        return handler(gather.getTab(), maps, gather.getFilters().split(","));
    }
}
