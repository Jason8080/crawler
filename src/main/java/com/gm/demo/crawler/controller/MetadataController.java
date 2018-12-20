package com.gm.demo.crawler.controller;

import com.gm.demo.crawler.dao.model.Metadata;
import com.gm.demo.crawler.entity.req.SaveMetadataReq;
import com.gm.demo.crawler.service.MetadataServiceImpl;
import com.gm.model.response.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Jason
 */
@RestController
@RequestMapping("metadata")
@Api(tags = "元数据管理")
public class MetadataController {

    @Autowired
    MetadataServiceImpl metadataService;


    @GetMapping("getTab/{tab}")
    @ApiOperation(value = "获取元数据", response = Metadata.class)
    public JsonResult<List<Metadata>> getTab(@PathVariable("tab") String tab) {
        return JsonResult.as(metadataService.getTab(tab));
    }

    @PostMapping("save")
    @ApiOperation(value = "保存元数据", response = Integer.class)
    public JsonResult<Integer> getTab(@Valid SaveMetadataReq req) {
        return JsonResult.as(metadataService.save(req));
    }

    @PostMapping("dropTab/{tab}")
    @ApiOperation(value = "删除元数据")
    public JsonResult<Integer> dropTab(@PathVariable("tab") String tab) {
        return JsonResult.as(metadataService.dropTab(tab));
    }
}
