package com.gm.demo.crawler.controller;

import com.gm.demo.crawler.dao.model.Gather;
import com.gm.demo.crawler.entity.req.CrawlReq;
import com.gm.demo.crawler.service.GatherServiceImpl;
import com.gm.demo.crawler.service.LunaticCrawlerServiceImpl;
import com.gm.enums.Regexp;
import com.gm.help.base.Quick;
import com.gm.model.response.HttpResult;
import com.gm.model.response.JsonResult;
import com.gm.strong.Str;
import com.gm.utils.base.Assert;
import com.gm.utils.base.Logger;
import com.gm.utils.ext.Regex;
import com.gm.utils.third.Http;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 丧心病狂爬虫控制器
 *
 * @author Jason
 */
@RestController
@Api(tags = "丧心病狂控制器")
@RequestMapping("lunatic")
public class LunaticCrawlerController {

    String[] checkResult = {"验证码", "过于频繁"};

    @Autowired
    GatherServiceImpl gatherService;

    @Autowired
    LunaticCrawlerServiceImpl lunaticCrawlerService;

    @PostMapping("mobile")
    @ApiOperation(value = "释放一只手机爬虫")
    public JsonResult comment(@RequestBody @Valid CrawlReq req) {
        String format = String.format("请提取方案{%s}不存在", req.getTab());
        Gather gather = Assert.Null(gatherService.getTab(req.getTab()), format);
        Integer total = pages(req.getUrl(), gather, req.getHeaders(), req.getParams());
        return JsonResult.as(total);
    }

    private Integer pages(String root, Gather gather, Map<String, String> headers, Map<String, Object> params) {
        Integer[] sum = {0};
        Quick.loop(root, url -> {
            String newUrl = url.toString().startsWith("http") ? url.toString() : "http:".concat(url.toString());
            HttpResult result = Quick.exec(x->Http.doGet(newUrl, headers, params));
            String html = Quick.exec(x->new String(result.getResult()));
            if (new Str(html).contains(checkResult)) {
                Logger.info("要验证了~");
            }
            sum[0] += lunaticCrawlerService.handler(gather, html);
            Logger.debug("gather:   ".concat(sum[0].toString()).concat("\n").concat(newUrl));
            List<String> urls = Regex.find(html, Regexp.FIND_URL.getCode());
            urls = urls
                    .stream()
                    .distinct()
                    .filter(x -> new Str(x).contains(gather.getData().split(",")))
                    .collect(Collectors.toList());
            urls.remove(root);
            urls.remove(newUrl);
            return urls;
        });
        return sum[0];
    }
}
