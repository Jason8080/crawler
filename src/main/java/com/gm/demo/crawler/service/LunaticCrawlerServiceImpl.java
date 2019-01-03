package com.gm.demo.crawler.service;

import com.gm.demo.crawler.dao.mapper.TabMapper;
import com.gm.demo.crawler.dao.mapper.ext.LunaticUrlFiltersMapperExt;
import com.gm.demo.crawler.dao.model.Gather;
import com.gm.demo.crawler.entity.req.SearchCrawlReq;
import com.gm.enums.Regexp;
import com.gm.strong.Str;
import com.gm.utils.base.Bool;
import com.gm.utils.base.Convert;
import com.gm.utils.base.ExceptionUtils;
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

    private static final String span_suffix = "</span>";

    private static final String span_prefix = "<span data-spm-anchor-id=";

    private static final String dyn_regexp = ".*>";

    private static final String store_regex = span_prefix + dyn_regexp + ".*" + span_suffix;

    public Integer handlerGoods(SearchCrawlReq req, Gather gather, String url, String html) {

        List<String> spans = Regex.find(html, store_regex);
        List<String> stores = spans.stream().map(span -> {
            span = span.substring(span_prefix.length(), span.lastIndexOf(span_suffix));
            return span.replaceFirst(dyn_regexp, "");
        }).collect(Collectors.toList());
        for (int i = 1; i <= stores.size(); i++) {
            String store = stores.get(i);
            if (store.contains(req.getTitle())) {
                return ExceptionUtils.process(String.format("找到你的商品在第%s个%s", i, url));
            }
        }
        return 44;
    }

    public static void main(String[] args) {

        String source = "<span data-spm-anchor-id=\"a230r.1.14.i0.61fd6afaZLpS83\">友艺阁旗舰店</span>";
        List<String> spans = Regex.find(source, store_regex);
        List<String> stores = spans.stream().map(span -> {
            span = span.substring(span_prefix.length(), span.lastIndexOf(span_suffix));
            return span.replaceFirst(dyn_regexp, "");
        }).collect(Collectors.toList());
        System.out.println(stores);
    }


    public Integer handlerMobile(Gather gather, String url, String html) {

        String title = Convert.toEmpty(Regex.findFirst(html, Regexp.FIND_HTML_TITLE.getCode()), "<title></title>");
        title = title.substring("<title>".length(), title.length() - "</title>".length());
        List<String> mobiles = Regex.find(html, "([^\\d])((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18([0-3]|[5-9]))|(177))\\d{8}([^\\d])");
        mobiles = mobiles.stream().map(x -> x.substring(1, x.length() - 1)).distinct().collect(Collectors.toList());
        List<Map<String, Object>> maps = new ArrayList();
        if (!Bool.isNull(mobiles) && !isBlacklist(url)) {
            for (String mobile : mobiles) {
                Map<String, Object> map = new HashMap(0);
                map.put("title", title);
                map.put("mobile", mobile);
                map.put("url", url);
                maps.add(map);
            }
            return handler(gather.getTab(), maps, gather.getFilters().split(","));
        }
        return 0;
    }

    private boolean isBlacklist(String url) {

        Set<String> set = lunaticUrlFiltersMapperExt.getAll();
        if (new Str(url).contains(set.toArray(new String[0]))) {
            return true;
        }
        return false;
    }

    public Set<String> getAll() {

        return lunaticUrlFiltersMapperExt.getAll();
    }
}
