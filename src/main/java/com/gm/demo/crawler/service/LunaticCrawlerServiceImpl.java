package com.gm.demo.crawler.service;

import com.gm.demo.crawler.dao.mapper.LunaticUrlFiltersMapper;
import com.gm.demo.crawler.dao.mapper.TabMapper;
import com.gm.demo.crawler.dao.mapper.ext.LunaticUrlFiltersMapperExt;
import com.gm.demo.crawler.dao.model.Gather;
import com.gm.enums.Regexp;
import com.gm.strong.Str;
import com.gm.utils.base.Bool;
import com.gm.utils.base.Convert;
import com.gm.utils.ext.Regex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
    @Autowired
    LunaticUrlFiltersMapperExt lunaticUrlFiltersMapperExt;

    Set<String> filters = new HashSet();
    public Integer handler(Gather gather, String url, String html) {
        List<Map<String, Object>> maps = new ArrayList();
        String title = Convert.toEmpty(Regex.findFirst(html, Regexp.FIND_HTML_TITLE.getCode()), "<title></title>");
        title = title.substring("<title>".length(), title.length() - "</title>".length());
        List<String> mobiles = Regex.find(html, Regexp.FIND_MOBILE.getCode());
        mobiles = mobiles.stream().distinct().collect(Collectors.toList());
        if(!new Str(url).contains(getFilters().toArray(new String[0]))) {
            for (String mobile : mobiles) {
                Map<String, Object> map = new HashMap(0);
                map.put("title", title);
                map.put("mobile", mobile);
                map.put("url", url);
                maps.add(map);
            }
        }
        return handler(gather.getTab(), maps, gather.getFilters().split(","));
    }

    private Set<String> getFilters() {
        if(!Bool.haveNull(lunaticUrlFiltersMapperExt, this.filters)){
            Set<String> filters = lunaticUrlFiltersMapperExt.getAll();
            this.filters.addAll(filters);
        }
        return this.filters;
    }
}
