package com.gm.demo.crawler.controller;

import com.gm.demo.crawler.entity.req.CrawlReq;
import com.gm.demo.crawler.service.MtCrawlerServiceImpl;
import com.gm.help.base.Quick;
import com.gm.model.response.HttpResult;
import com.gm.model.response.JsonResult;
import com.gm.strong.Rules;
import com.gm.utils.base.Logger;
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
@Api(tags = "美团爬虫控制器")
@RequestMapping("mt/crawler")
public class MtCrawlerController {

    public static final String MT_COMMENT_TAB = "mt_comment_ms";
    public static final String MT_MERCHANT_TAB = "mt_merchant_ms";
    /**
     * The constant offset.
     */
    public static final String offset = "[offset]";
    /**
     * The constant pageSize.
     */
    public static final String pageSize = "[pageSize]";
    /**
     * The constant pageNo.
     */
    public static final String pageNo = "[pageNo]";
    /**
     * The constant id.
     */
    public static final String id = "[id]";

    /**
     * The Mt crawler service.
     */
    @Autowired
    MtCrawlerServiceImpl mtCrawlerService;

    /**
     * Merchant json result.
     *
     * @param req the req
     * @return the json result
     */
    @PostMapping("merchant")
    @ApiOperation(value = "释放一只商家爬虫")
    public JsonResult merchant(@RequestBody @Valid CrawlReq req) {
        Integer total = merchantPages(req.getUrl(), req.getHeaders(), req.getParams());
        return JsonResult.as(total);
    }

    /**
     * Comment json result.
     *
     * @param req the req
     * @return the json result
     */
    @PostMapping("comment")
    @ApiOperation(value = "释放一只评论爬虫")
    public JsonResult comment(@RequestBody @Valid CrawlReq req) {
        Integer total = commentPages(req.getUrl(), req.getHeaders(), req.getParams());
        return JsonResult.as(total);
    }

    /**
     * 分页爬取商家数据
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    private Integer merchantPages(String url, Map<String, String> headers, Map<String, Object> params) {
        Integer[] sum = {0};
        P page = new P(1);
        Quick.echo(x -> {
            String newUrl = getMerchantUrl(url, page);
            HttpResult result = Http.doGet(newUrl, headers, params);
            if (!JsonResult.SUCCESS.equals(result.getStatus())) {
                result = Http.doPost(newUrl, headers, params);
                if (!JsonResult.SUCCESS.equals(result.getStatus())) {
                    JsonResult.unsuccessful(new String(result.getResult()));
                }
            }
            String json = new String(result.getResult());
            sum[0] += mtCrawlerService.handler(MT_MERCHANT_TAB,json);
            Logger.debug("gather:   ".concat(sum[0].toString()).concat("\n").concat(newUrl));
            // 从这里开始
            page.setPageNo(page.pageNo + 1);
        });
        return sum[0];
    }

    private String getMerchantUrl(final String url, P page) {
        return Rules.parse(page, url.replace("page".concat("=1"), "page".concat("=").concat(page.pageNo.toString())));
    }

    /**
     * 分页爬取评论数据
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    private Integer commentPages(String url, Map<String, String> headers, Map<String, Object> params) {
        Integer[] sum = {0};
        P page = new P(1, 100);
        Quick.echo(x -> {
            String newUrl = getCommentUrl(url, page);
            HttpResult result = Http.doGet(newUrl, headers, params);
            if (!JsonResult.SUCCESS.equals(result.getStatus())) {
                result = Http.doPost(newUrl, headers, params);
                if (!JsonResult.SUCCESS.equals(result.getStatus())) {
                    JsonResult.unsuccessful(new String(result.getResult()));
                }
            }
            String json = new String(result.getResult());
            sum[0] += mtCrawlerService.handler(MT_COMMENT_TAB, json);
            Logger.debug("gather:   ".concat(sum[0].toString()).concat("\n").concat(newUrl));
            // 从这里开始
            page.setOffset(page.offset + page.pageSize);
        });
        return sum[0];
    }

    private String getCommentUrl(String url, P page) {
        return Rules.parse(page, url.replace("offset".concat("=0"), "offset".concat("=").concat(page.offset.toString())));
    }

    /**
     * 分页对象
     */
    @Data
    static class P {
        private Integer offset;
        private Integer pageNo;
        private Integer pageSize;

        /**
         * Instantiates a new P.
         *
         * @param pageNo the page no
         */
        public P(Integer pageNo) {
            this.pageNo = pageNo;
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
