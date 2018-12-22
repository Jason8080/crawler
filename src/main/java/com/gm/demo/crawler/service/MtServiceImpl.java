package com.gm.demo.crawler.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.gm.demo.crawler.dao.mapper.MetadataMapper;
import com.gm.demo.crawler.dao.mapper.TabMapper;
import com.gm.demo.crawler.dao.mapper.ext.MetadataMapperExt;
import com.gm.demo.crawler.dao.mapper.ext.MtMapperExt;
import com.gm.demo.crawler.entity.req.MtDataReq;
import com.gm.model.request.PageReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Jason
 */
@Service
public class MtServiceImpl {

    @Autowired
    MtMapperExt mtMapperExt;

    public Page<Map<String, Object>> getPage(PageReq pageReq, MtDataReq req) {
        PageHelper.startPage(pageReq.getPageNo(), pageReq.getPageSize());
        return mtMapperExt.getPage(req);
    }
}
