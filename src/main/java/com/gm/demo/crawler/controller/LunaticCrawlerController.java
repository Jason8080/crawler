package com.gm.demo.crawler.controller;

import com.gm.constant.Cn;
import com.gm.demo.crawler.dao.model.Gather;
import com.gm.demo.crawler.entity.req.SearchCrawlReq;
import com.gm.demo.crawler.service.GatherServiceImpl;
import com.gm.demo.crawler.service.LunaticCrawlerServiceImpl;
import com.gm.enums.Regexp;
import com.gm.help.base.Quick;
import com.gm.model.response.HttpResult;
import com.gm.model.response.JsonResult;
import com.gm.strong.Rules;
import com.gm.strong.Str;
import com.gm.utils.base.Assert;
import com.gm.utils.base.Bool;
import com.gm.utils.base.Convert;
import com.gm.utils.base.Logger;
import com.gm.utils.ext.Math;
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
public class LunaticCrawlerController extends BaseController {

    String[] checkResult = {"HTTP 404 Not Found", "验证", "过于频繁"};

    private static Map<String, Integer> webExclude = new HashMap();

    String[] urlExclude = {".jpg", ".png", ".gif", ".js", ".css", "down", ".exe", ".zip", ".dtd"};

    @Autowired
    GatherServiceImpl gatherService;

    @Autowired
    LunaticCrawlerServiceImpl lunaticCrawlerService;

    @PostMapping("mobile")
    @ApiOperation(value = "释放一只手机爬虫")
    public JsonResult mobile(@RequestBody @Valid SearchCrawlReq req) {

        String format = String.format("请提取方案{%s}不存在", req.getTab());
        Gather gather = Assert.Null(gatherService.getTab(req.getTab()), format);
        Integer total = mobilePages(req, gather);
        return JsonResult.as(total);
    }

    @PostMapping("store")
    @ApiOperation(value = "释放一只店铺爬虫")
    public JsonResult store(@RequestBody @Valid SearchCrawlReq req) {

        String format = String.format("请提取方案{%s}不存在", req.getTab());
        Gather gather = Assert.Null(gatherService.getTab(req.getTab()), format);
        Integer total = storePages(req, gather);
        return JsonResult.as(total);
    }

    private Integer storePages(SearchCrawlReq req, Gather gather) {

        Integer[] sum = {0};
        Page page = new Page(0, 100);
        String root = Web.replace(req.getUrl(), "q", req.getKeyword());
        Quick.echo(z -> {
            String newUrl = getUrl(root, page, gather);
            HttpResult result = Quick.exec(x -> Http.doGet(newUrl, req.getHeaders(), req.getParams()));
            String html = new String(Convert.toEmpty(result, new HttpResult()).getResult());
            if (new Str(html).contains(checkResult)) {
                Logger.info("要验证了~");
            }
            sum[0] += lunaticCrawlerService.handlerGoods(req, gather, newUrl, html);
            int i = sum[0].intValue() / 44;
            Logger.debug(("gather:   " + i).concat("\n").concat(newUrl));
            // 分页方案
            String parse = Logger.exec(r -> Rules.parse(page, gather.getPage().split(",")[0].split("=")[1]), "赶集分页失败");
            page.setOffset(Math.execute(parse, Integer.class));
        });
        return sum[0];
    }

    private Integer mobilePages(SearchCrawlReq req, Gather gather) {

        Integer[] sum = {Cn.ZERO};
        String domain = Web.getRootDomain(req.getUrl());
        Quick.loop(req.getUrl(), url -> {
            String newUrl = getHttp(url.toString());
            String html = "";
            String key = Web.nonArgs(newUrl);
            if (Convert.toEmpty(webExclude.get(key), Cn.ZERO) < Cn.THREE) {
                HttpResult result = Quick.exec(x -> Http.doGet(newUrl, req.getHeaders(), req.getParams()));
                html = new String(Convert.toEmpty(result, new HttpResult()).getResult());
                Integer count = lunaticCrawlerService.handlerMobile(req, gather, newUrl, html);
                sum[0] += count;
                if (count <= Cn.ZERO) {
                    int val = Convert.toEmpty(webExclude.get(key), Cn.ZERO);
                    webExclude.put(key, ++val);
                } else if (count > Cn.TWO * Cn.FOUR * Cn.TWO) {
                    Logger.debug("Exceed:   ".concat(count + "").concat("\n").concat(newUrl));
                } else {
                    Logger.debug("gather:   ".concat(sum[0].toString()).concat("\n").concat(newUrl));
                }
            }
            List<String> urls = Regex.find(html, Regexp.FIND_URL.getCode());
            for (int i = 0; i < urls.size(); i++) {
                String s = urls.get(i);
                s = s.replaceAll("amp;", "");
                s = s.trim();
                urls.set(i, s);
                if (!new Str(s).contains(domain)
                        || new Str(s).contains(urlExclude)
                        || Convert.toEmpty(webExclude.get(getHttp(Web.nonArgs(s))), Cn.ZERO) > Cn.TWO) {
                    urls.remove(i--);
                }
            }
            return urls;
        });
        return sum[0];
    }
}
