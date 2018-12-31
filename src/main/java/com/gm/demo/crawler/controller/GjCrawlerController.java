package com.gm.demo.crawler.controller;

import com.gm.demo.crawler.dao.model.Gather;
import com.gm.demo.crawler.entity.req.CrawlReq;
import com.gm.demo.crawler.service.HtmlCrawlerServiceImpl;
import com.gm.demo.crawler.service.GatherServiceImpl;
import com.gm.help.base.Quick;
import com.gm.model.response.HttpResult;
import com.gm.model.response.JsonResult;
import com.gm.strong.Rules;
import com.gm.strong.Str;
import com.gm.utils.base.Assert;
import com.gm.utils.base.Convert;
import com.gm.utils.base.ExceptionUtils;
import com.gm.utils.base.Logger;
import com.gm.utils.ext.Math;
import com.gm.utils.ext.Web;
import com.gm.utils.third.Http;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

/**
 * The type Mt crawler controller.
 *
 * @author Jason
 */
@RestController
@Api(tags = "赶集爬虫控制器")
@RequestMapping("gj/crawler")
public class GjCrawlerController {
    String[] checkResult = {"验证码", "过于频繁"};

    @Autowired
    HtmlCrawlerServiceImpl htmlCrawlerService;
    @Autowired
    GatherServiceImpl gatherService;

    @PostMapping("mobile")
    @ApiOperation(value = "释放一只手机爬虫")
    public JsonResult comment(@RequestBody @Valid CrawlReq req) {
        String format = String.format("请提取方案{%s}不存在", req.getTab());
        Gather gather = Assert.Null(gatherService.getTab(req.getTab()), format);
        Integer total = pages(req.getUrl(), gather, req.getHeaders(), req.getParams());
        return JsonResult.as(total);
    }

    /**
     * 分页爬取数据
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    private Integer pages(final String url, Gather gather, Map<String, String> headers, Map<String, Object> params) {
        Integer[] sum = {0};
        P page = new P(1, 100);
        Quick.echo(x -> {
            String newUrl = getUrl(url, page);
            HttpResult result = Http.doGet(newUrl, headers, params);
            String html = new String(result.getResult());
            if(new Str(html).contains(checkResult)){
                ExceptionUtils.process("要验证了~");
            }
            sum[0] += htmlCrawlerService.handler(gather, html);
            Logger.debug("gather:   ".concat(sum[0].toString()).concat("\n").concat(newUrl));
            // 分页方案
            String parse = Logger.exec(r -> Rules.parse(page, gather.getPage().split(",")[0].split("=")[1]));
            page.setOffset(Math.execute(parse, Integer.class));
        });
        return sum[0];
    }

    private String getUrl(String url, P page) {
        return Rules.parse(page, url);
    }

    /**
     * 分页对象
     */
    @Data
    static class P {
        private Integer offset;
        private Integer pageSize;

        public P(Integer offset) {
            this.offset = offset;
        }

        /**
         * Instantiates a new P.
         *
         * @param offset   the offset
         * @param pageSize the page size
         */
        public P(Integer offset, Integer pageSize) {
            this.offset = offset;
            this.pageSize = pageSize;
        }
    }
}
