package com.gm.demo.crawler.controller;

import com.gm.model.response.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jason
 */
@RestController
@Api(tags = "美团爬虫入口")
@RequestMapping("mt")
public class MtController {

    @PostMapping("entry/{url}")
    @ApiOperation(value = "释放一只爬虫", response = Integer.class)
    public JsonResult main(@PathVariable("url") String url) {
        return JsonResult.SUCCESS_;
    }
}
