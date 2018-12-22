package com.gm.demo.crawler.controller;

import com.gm.demo.crawler.entity.req.CrawlReq;
import com.gm.demo.crawler.service.MtServiceImpl;
import com.gm.help.base.Quick;
import com.gm.model.response.HttpResult;
import com.gm.model.response.JsonResult;
import com.gm.utils.base.ExceptionUtils;
import com.gm.utils.base.Logger;
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
 * @author Jason
 */
@RestController
@Api(tags = "美团爬虫入口")
@RequestMapping("mt")
public class MtController {

    public static final String offset = "offset";
    public static final String pageNo = "page";
    public static final String pageSize = "pageSize";

    @Autowired
    MtServiceImpl mtService;

    @PostMapping("entry")
    @ApiOperation(value = "释放一只爬虫")
    public JsonResult entry(@RequestBody @Valid CrawlReq req) {
        Integer total = merchantPages(req.getUrl(), req.getHeaders(), req.getParams());
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
    private Integer merchantPages(final String url, Map<String, String> headers, Map<String, Object> params) {
        Integer[] sum = {0};
        Integer no = Integer.parseInt(Web.getParam(url, pageNo));
        P page = new P(no);
        Quick.echo(x -> {
            String newUrl = url.replace(
                    pageNo.concat("=1"),
                    pageNo.concat("=").concat(page.newPageNo.toString())
            );
            HttpResult result = Http.doGet(newUrl, headers, params);
            if (!JsonResult.SUCCESS.equals(result.getStatus())) {
                result = Http.doPost(newUrl, headers, params);
                if (!JsonResult.SUCCESS.equals(result.getStatus())) {
                    JsonResult.unsuccessful(new String(result.getResult()));
                }
            }
            String json = new String(result.getResult());
            Integer count = mtService.handlerMerchant(json);
            sum[0] += count;
            Logger.debug("gather:   ".concat(sum[0].toString()).concat("\n").concat(newUrl));
            if (count <= 0) {
                ExceptionUtils.cast();
            }
            // 从这里开始
            page.setNewPageNo(page.newPageNo + 1);
        });
        return sum[0];
    }

    /**
     * 分页爬取评论数据
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    private Integer commentPages(final String url, Map<String, String> headers, Map<String, Object> params) {
        Integer[] sum = {0};
        Integer start = Integer.parseInt(Web.getParam(url, offset));
        Integer size = Integer.parseInt(Web.getParam(url, pageSize));
        P page = new P(start, size);
        Quick.echo(x -> {
            String newUrl = url.replace(
                    offset.concat("=0"),
                    offset.concat("=").concat(page.newStart.toString())
            );
            HttpResult result = Http.doGet(newUrl, headers, params);
            if (!JsonResult.SUCCESS.equals(result.getStatus())) {
                result = Http.doPost(newUrl, headers, params);
                if (!JsonResult.SUCCESS.equals(result.getStatus())) {
                    JsonResult.unsuccessful(new String(result.getResult()));
                }
            }
            String json = new String(result.getResult());
            Integer count = mtService.handlerComment(json);
            sum[0] += count;
            Logger.debug("gather:   ".concat(sum[0].toString()).concat("\n").concat(newUrl));
            if (count < page.getPageSize()) {
                ExceptionUtils.cast();
            }
            // 从这里开始
            page.setNewStart(page.newStart + page.pageSize);
        });
        return sum[0];
    }

    /**
     * 分页对象
     */
    @Data
    static class P {
        private Integer newStart;
        private Integer newPageNo;
        private Integer pageSize;

        public P(Integer newPageNo) {
            this.newPageNo = newPageNo;
        }

        public P(Integer newStart, Integer pageSize) {
            this.newStart = newStart;
            this.pageSize = pageSize;
        }
    }
}
