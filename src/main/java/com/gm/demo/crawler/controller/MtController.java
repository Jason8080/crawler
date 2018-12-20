package com.gm.demo.crawler.controller;

import com.gm.demo.crawler.entity.req.CrawlReq;
import com.gm.model.response.HttpResult;
import com.gm.model.response.JsonResult;
import com.gm.utils.ext.Json;
import com.gm.utils.third.Http;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author Jason
 */
@RestController
@Api(tags = "美团爬虫入口")
@RequestMapping("mt")
public class MtController {

    @GetMapping("entry")
    @ApiOperation(value = "释放一只爬虫")
    public JsonResult entry(@Valid CrawlReq req) {
        HttpResult result = Http.doGet(req.getUrl());
        return JsonResult.as(Json.format(new String(result.getResult())));
    }
}
