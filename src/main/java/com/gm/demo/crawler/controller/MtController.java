package com.gm.demo.crawler.controller;

import com.gm.demo.crawler.entity.req.CrawlReq;
import com.gm.demo.crawler.service.MtServiceImpl;
import com.gm.model.response.HttpResult;
import com.gm.model.response.JsonResult;
import com.gm.utils.third.Http;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

    @Autowired
    MtServiceImpl mtService;

    @PostMapping("entry")
    @ApiOperation(value = "释放一只爬虫")
    public JsonResult entry(@RequestBody @Valid CrawlReq req) {
        HttpResult result = Http.doGet(req.getUrl(), req.getHeaders(), req.getParams());
        if(!JsonResult.SUCCESS.equals(result.getStatus())){
            result = Http.doPost(req.getUrl(), req.getHeaders(), req.getParams());
            if(!JsonResult.SUCCESS.equals(result.getStatus())){
                JsonResult.unsuccessful(new String(result.getResult()));
            }
        }
        String json = new String(result.getResult());
        return JsonResult.as(mtService.handler(json));
    }
}
