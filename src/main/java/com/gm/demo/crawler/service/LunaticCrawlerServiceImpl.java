package com.gm.demo.crawler.service;

import com.gm.demo.crawler.dao.mapper.TabMapper;
import com.gm.demo.crawler.dao.mapper.ext.LunaticUrlFiltersMapperExt;
import com.gm.demo.crawler.dao.model.Gather;
import com.gm.enums.Regexp;
import com.gm.strong.Str;
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

    public Integer handler(Gather gather, String url, String html) {
        String title = Convert.toEmpty(Regex.findFirst(html, Regexp.FIND_HTML_TITLE.getCode()), "<title></title>");
        title = title.substring("<title>".length(), title.length() - "</title>".length());
        List<String> mobiles = Regex.find(html, "([^\\d])((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18([0-3]|[5-9]))|(177))\\d{8}([^\\d])");
        mobiles = mobiles.stream().map(x -> x.substring(1, x.length() - 1)).distinct().collect(Collectors.toList());
        List<Map<String, Object>> maps = new ArrayList();
        for (String mobile : mobiles) {
            Map<String, Object> map = new HashMap(0);
            map.put("title", title);
            map.put("mobile", mobile);
            map.put("url", url);
            maps.add(map);
        }
        return handler(gather.getTab(), maps, gather.getFilters().split(","));
    }

    private boolean isBlacklist(String url) {
        Set<String> set = lunaticUrlFiltersMapperExt.getAll();
        if (new Str(url).contains(set.toArray(new String[0]))) {
            return true;
        }
        return false;
    }
}
