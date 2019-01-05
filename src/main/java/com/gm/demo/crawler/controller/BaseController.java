package com.gm.demo.crawler.controller;

import com.gm.demo.crawler.dao.model.Gather;
import com.gm.strong.Rules;
import com.gm.utils.ext.Web;
import com.gm.utils.third.Http;
import lombok.Data;

/**
 * @author Jason
 */
public class BaseController {

    /**
     * 分页对象
     */
    @Data
    static class Page {

        private Integer offset;

        private Integer pageSize;

        public Page(Integer offset) {

            this.offset = offset;
        }

        /**
         * Instantiates a new Page.
         *
         * @param offset   the offset
         * @param pageSize the page size
         */
        public Page(Integer offset, Integer pageSize) {

            this.offset = offset;
            this.pageSize = pageSize;
        }
    }


    protected String getUrl(String url, Page page, Gather gather) {

        String[] split = gather.getPage().split(",");
        if (split.length > 0) {
            String name = split[0].split("=")[0];
            url = Web.replace(url, name, page.getOffset());
        }
        if (split.length > 1) {
            String name = split[1];
            url = Web.replace(url, name, page.getPageSize());

        }
        return Rules.parse(page, url);
    }


    protected String getHttp(String url) {

        if (url.startsWith(Http.HTTPS) || url.startsWith(Http.HTTP_)) {
            return url;
        }
        return "http:".concat(url);
    }
}
