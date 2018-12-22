package com.gm.demo.crawler.controller;

import com.gm.demo.crawler.entity.req.MtDataReq;
import com.gm.demo.crawler.service.MtServiceImpl;
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
@Api(tags = "美团数据控制器")
@RequestMapping("mt")
public class MtController {

    @Autowired
    MtServiceImpl mtService;

    @PostMapping("merchant/getPage")
    @ApiOperation(value = "获取1页商家")
    public JsonResult<Map<String,Object>> getPage(PageReq pageReq, @Valid MtDataReq req) {
        return JsonResult.as(mtService.getPage(pageReq, req));
    }
}
