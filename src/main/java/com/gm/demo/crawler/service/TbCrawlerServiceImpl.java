package com.gm.demo.crawler.service;

import com.gm.demo.crawler.dao.model.Gather;
import com.gm.demo.crawler.entity.req.TaoBaoCrawlReq;
import com.gm.utils.base.Bool;
import com.gm.utils.base.ExceptionUtils;
import com.gm.utils.ext.Regex;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Jason
 */
@Service
public class TbCrawlerServiceImpl extends CrawlerServiceImpl {
    public Integer handler(TaoBaoCrawlReq req, Gather gather, String content) {
        String first = Regex.findFirst(content, "jsonp[0-9]?(");
        content = content.replaceFirst(first, "");
        content = content.substring(0, content.length() - 1);
        List<Map<String, Object>> maps = getStringObjectMap(content, gather);
        String[] filters = gather.getFilters().split(",");
        for (int i = 0; i < maps.size(); i++) {
            Map<String, Object> map = maps.get(i);
            boolean b = true;
            for (int j = 0; b; j++) {
                String key = req.getKey(j);
                String val = req.getVal(j);
                if (!Bool.contains(filters, key)) {
                    ExceptionUtils.process(String.format("查询项{%s}不存在", key));
                }
                Object o = map.get(key);
                if (o == null) {
                    b = false;
                } else if (val.contains(o.toString())) {
                    return i;
                }
            }
        }
        return 44;
    }
}
