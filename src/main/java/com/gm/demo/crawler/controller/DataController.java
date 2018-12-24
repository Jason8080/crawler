package com.gm.demo.crawler.controller;

import com.gm.demo.crawler.entity.req.DataReq;
import com.gm.demo.crawler.entity.req.MtDistinctReq;
import com.gm.demo.crawler.service.DataServiceImpl;
import com.gm.model.request.PageReq;
import com.gm.model.response.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

/**
 * @author Jason
 */
@RestController
@Api(tags = "数据管理")
@RequestMapping("data")
public class DataController {
    @Autowired
    DataServiceImpl dataService;


    @PostMapping("getPage")
    @ApiOperation(value = "获取1页数据")
    public JsonResult<Map<String, Object>> getPage(PageReq pageReq, @Valid DataReq req) {
        return JsonResult.as(dataService.getPage(pageReq, req));
    }

    @PostMapping("distinct")
    @ApiOperation(value = "数据去重")
    public JsonResult<Integer> distinct(@Valid MtDistinctReq req) {
        return JsonResult.as(dataService.distinct(req));
    }
}
