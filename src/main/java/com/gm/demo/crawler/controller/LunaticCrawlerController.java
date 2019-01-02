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
import com.gm.utils.base.Convert;
import com.gm.utils.base.Logger;
import com.gm.utils.ext.Regex;
import com.gm.utils.ext.Web;
import com.gm.utils.third.Http;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 丧心病狂爬虫控制器
 *
 * @author Jason
 */
@RestController
@Api(tags = "丧心病狂控制器")
@RequestMapping("lunatic")
public class LunaticCrawlerController {

    String[] checkResult = {"HTTP 404 Not Found", "验证码", "过于频繁"};
    String[] urlContain = {"58.com"};
    Map<String, Integer> webExclude = new HashMap();
    String[] urlExclude = {".jpg", ".png", ".gif", ".js", ".css", "down", ".exe", ".zip", "apple.com", "about"};

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
        String domain = Web.getRootDomain(root);
        Quick.loop(root, url -> {
            String newUrl = getHttp(url.toString());
            HttpResult result = Quick.exec(x -> Http.doGet(newUrl, headers, params));
            String html = new String(Convert.toEmpty(result, new HttpResult()).getResult());
            if (new Str(html).contains(checkResult)) {
                Logger.info("要验证了~");
            }
            Integer count = lunaticCrawlerService.handler(gather, newUrl, html);
//            if (count <= 0) {
//                String key = Web.nonArgs(newUrl);
//                int val = Convert.toEmpty(webExclude.get(key), 0);
//                webExclude.put(key, ++val);
//            }
            sum[0] += count;
            Logger.debug("gather:   ".concat(sum[0].toString()).concat("\n").concat(newUrl));
            List<String> urls = Regex.find(html, Regexp.FIND_URL.getCode());
            for (int i = 0; i < urls.size(); i++) {
                String s = urls.get(i);
                s = s.replaceAll("amp;", "");
                s = s.trim();
                urls.set(i, s);
                if (!new Str(s).contains(gather.getData().split(","))
                        || !new Str(s).contains(domain)
                        || new Str(s).contains(urlExclude)
                        /*|| Convert.toEmpty(webExclude.get(getHttp(Web.nonArgs(s))), 0) > 5*/) {
                    urls.remove(i--);
                }
            }
            return urls;
        });
        return sum[0];
    }

    private String getHttp(String url) {
        return url.startsWith("http") ? url : "http:".concat(url);
    }
}
