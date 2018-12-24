package com.gm.demo.crawler.controller;

import com.gm.demo.crawler.entity.req.DataReq;
import com.gm.demo.crawler.entity.req.SaveGatherReq;
import com.gm.demo.crawler.service.DataServiceImpl;
import com.gm.demo.crawler.service.GatherServiceImpl;
import com.gm.model.request.PageReq;
import com.gm.model.response.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.xml.crypto.Data;
import java.util.Map;

/**
 * @author Jason
 */
@RestController
@RequestMapping("gather")
@Api(tags = "收集方案管理")
public class GatherController {

    @Autowired
    DataServiceImpl dataService;
    @Autowired
    GatherServiceImpl gatherService;

    @PostMapping("getPage")
    @ApiOperation(value = "获取1页方案")
    public JsonResult<Map<String, Object>> getPage(PageReq pageReq) {
        DataReq req = new DataReq();
        req.setTab("gather");
        return JsonResult.as(dataService.getPage(pageReq, req));
    }

    @PostMapping("save")
    @ApiOperation(value = "保存方案")
    public JsonResult<Integer> save(@Valid SaveGatherReq req) {
        return JsonResult.as(gatherService.save(req));
    }
}
