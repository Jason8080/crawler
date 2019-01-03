package com.gm.demo.crawler.service;

import com.gm.demo.crawler.dao.model.Gather;
import com.gm.demo.crawler.entity.req.SearchCrawlReq;
import com.gm.utils.base.Bool;
import com.gm.utils.base.Num;
import com.gm.utils.ext.Regex;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Jason
 */
@Service
public class TbCrawlerServiceImpl extends CrawlerServiceImpl {

    public Integer handler(SearchCrawlReq req, Gather gather, String content) {

        String first = Regex.findFirst(content, "jsonp[0-9]*\\(");
        content = content.replace(first, "");
        int len = content.length() - 2;
        content = content.substring(0, Num.nature(len));
        List<Map<String, Object>> maps = getStringObjectMap(content, gather);
        for (int i = 0; i < maps.size(); i++) {
            Map<String, Object> map = maps.get(i);
            boolean b = true;
            for (int j = 0; b; j++) {
                String key = req.getKey(j);
                String val = req.getVal(j);
                if (Bool.haveNull(key, val)) {
                    Object o = map.get(key);
                    if (o == null || !val.contains(o.toString())) {
                        b = false;
                    }
                }
            }
            // 不符合要求
            if (!b) {
                maps.remove(i--);
            }
        }
        return handler(gather.getTab(), maps, gather.getFilters().split(","));
    }
}
