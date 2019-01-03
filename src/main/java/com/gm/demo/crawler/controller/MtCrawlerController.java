package com.gm.demo.crawler.controller;

import com.gm.demo.crawler.dao.model.Gather;
import com.gm.demo.crawler.entity.req.CrawlReq;
import com.gm.demo.crawler.service.GatherServiceImpl;
import com.gm.demo.crawler.service.JsonCrawlerServiceImpl;
import com.gm.help.base.Quick;
import com.gm.model.response.HttpResult;
import com.gm.model.response.JsonResult;
import com.gm.strong.Rules;
import com.gm.utils.base.Assert;
import com.gm.utils.base.Logger;
import com.gm.utils.ext.Math;
import com.gm.utils.third.Http;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "美团爬虫控制器")
@RequestMapping("mt/crawler")
public class MtCrawlerController extends BaseController {

    @Autowired
    JsonCrawlerServiceImpl jsonCrawlerService;
    @Autowired
    GatherServiceImpl gatherService;

    @PostMapping("merchant")
    @ApiOperation(value = "释放一只商家爬虫")
    public JsonResult merchant(@RequestBody @Valid CrawlReq req) {
        String format = String.format("提取方案{%s}不存在", req.getTab());
        Gather gather = Assert.Null(gatherService.getTab(req.getTab()), format);
        Integer total = pages(req.getUrl(), gather, req.getHeaders(), req.getParams());
        return JsonResult.as(total);
    }

    @PostMapping("comment")
    @ApiOperation(value = "释放一只评论爬虫")
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
        Page page = new Page(0, 100);
        Quick.echo(x -> {
            String newUrl = getUrl(url, page, gather);
            HttpResult result = Http.doGet(newUrl, headers, params);
            if (!JsonResult.SUCCESS.equals(result.getStatus())) {
                result = Http.doPost(newUrl, headers, params);
                if (!JsonResult.SUCCESS.equals(result.getStatus())) {
                    JsonResult.unsuccessful(new String(result.getResult()));
                }
            }
            String json = new String(result.getResult());
            sum[0] += jsonCrawlerService.handler(gather, json);
            Logger.debug("gather:   ".concat(sum[0].toString()).concat("\n").concat(newUrl));
            // 分页方案
            String parse = Logger.exec(r -> Rules.parse(page, gather.getPage().split(",")[0].split("=")[1]), "美团分页失败");
            page.setOffset(Math.execute(parse, Integer.class));
        });
        return sum[0];
    }
}
