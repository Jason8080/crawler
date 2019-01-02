package com.gm.demo.crawler.controller;

import com.gm.demo.crawler.dao.model.Gather;
import com.gm.demo.crawler.entity.req.TaoBaoCrawlReq;
import com.gm.demo.crawler.service.GatherServiceImpl;
import com.gm.demo.crawler.service.TbCrawlerServiceImpl;
import com.gm.help.base.Quick;
import com.gm.model.response.HttpResult;
import com.gm.model.response.JsonResult;
import com.gm.strong.Rules;
import com.gm.strong.Str;
import com.gm.utils.base.Assert;
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

/**
 * @author Jason
 */
@RestController
@Api(tags = "淘宝爬虫控制器")
@RequestMapping("tb/crawler")
public class TbCrawlerController {
    String[] checkResult = {"验证码", "过于频繁"};

    @Autowired
    TbCrawlerServiceImpl tbCrawlerService;
    @Autowired
    GatherServiceImpl gatherService;

    @PostMapping("store")
    @ApiOperation(value = "释放一只店铺爬虫")
    public JsonResult comment(@RequestBody @Valid TaoBaoCrawlReq req) {
        String format = String.format("请提取方案{%s}不存在", req.getTab());
        Gather gather = Assert.Null(gatherService.getTab(req.getTab()), format);
        Integer total = pages(req, gather);
        return JsonResult.as(total);
    }

    private Integer pages(TaoBaoCrawlReq req, Gather gather) {
        Integer[] sum = {0};
        P page = new P(1, 100);
        Quick.echo(x -> {
            String newUrl = getUrl(req, page);
            HttpResult result = Http.doGet(newUrl, req.getHeaders(), req.getParams());
            String content = new String(result.getResult());
            if(new Str(content).contains(checkResult)){
                ExceptionUtils.process("要验证了~");
            }
            sum[0] += tbCrawlerService.handler(req, gather, content);
            Logger.debug("gather:   ".concat(sum[0].toString()).concat("\n").concat(newUrl));
            // 分页方案
            String parse = Logger.exec(r -> Rules.parse(page, gather.getPage().split(",")[0].split("=")[1]),"分页失败");
            page.setOffset(Math.execute(parse, Integer.class));
        });
        return sum[0];
    }

    private String getUrl(TaoBaoCrawlReq req, P page) {
        String url = Web.replace(req.getUrl(), "q", req.getKeyword());
        return Rules.parse(page, url);
    }

    @Data
    static class P {
        private Integer offset;
        private Integer pageSize;

        public P(Integer offset) {
            this.offset = offset;
        }

        public P(Integer offset, Integer pageSize) {
            this.offset = offset;
            this.pageSize = pageSize;
        }
    }
}
