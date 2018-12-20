package com.gm.demo.crawler.controller;

import com.gm.demo.crawler.dao.model.Metadata;
import com.gm.demo.crawler.service.MetadataServiceImpl;
import com.gm.model.response.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @PostMapping("getTab/{tab}")
    @ApiOperation(value = "获取表元数据", response = Metadata.class)
    public JsonResult<List<Metadata>> getTab(@PathVariable("tab") String tab) {
        return JsonResult.as(metadataService.getTab(tab));
    }
}
