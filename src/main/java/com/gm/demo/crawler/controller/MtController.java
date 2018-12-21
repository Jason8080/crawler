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

/**
 * @author Jason
 */
@RestController
@Api(tags = "美团爬虫入口")
@RequestMapping("mt")
public class MtController {

    public static final String offset = "offset";
    public static final String pageSize = "pageSize";

    @Autowired
    MtServiceImpl mtService;

    @PostMapping("entry")
    @ApiOperation(value = "释放一只爬虫")
    public JsonResult entry(@RequestBody @Valid CrawlReq req) {
        final String url = req.getUrl();
        Integer start = Integer.parseInt(Web.getParam(url, offset));
        Integer size = Integer.parseInt(Web.getParam(url, pageSize));
        P page = new P(start, 0, size);
        // 统计本次爬取总记录数
        Integer[] sum = {0};
        Quick.echo(x -> {
            String newUrl = url.replace(
                    offset.concat("=0"),
                    offset.concat("=").concat(page.newStart.toString())
            );
            HttpResult result = Http.doGet(newUrl, req.getHeaders(), req.getParams());
            if (!JsonResult.SUCCESS.equals(result.getStatus())) {
                result = Http.doPost(req.getUrl(), req.getHeaders(), req.getParams());
                if (!JsonResult.SUCCESS.equals(result.getStatus())) {
                    JsonResult.unsuccessful(new String(result.getResult()));
                }
            }
            String json = new String(result.getResult());
            Integer count = mtService.handler(json);
            sum[0] += count;
            if (count < page.getPageSize()) {
                // 收集到第gather条
                Logger.debug("gather:   ".concat(sum[0].toString())).concat("\n").concat(url);
                // 没有更多数据了结束
                ExceptionUtils.cast();
            } else {
                Logger.debug("gather:   ".concat(count.toString())).concat("\n").concat(url);
                // 从这里开始
                page.setNewStart(page.getOldStart() + page.getPageSize() + 1);
            }
        });
        return JsonResult.as(sum[0]);
    }

    /**
     * 分页对象
     */
    @Data
    static class P {
        private Integer oldStart;
        private Integer newStart;
        private Integer pageSize;

        public P(Integer oldStart, Integer newStart, Integer pageSize) {
            this.oldStart = oldStart;
            this.newStart = newStart;
            this.pageSize = pageSize;
        }
    }
}
