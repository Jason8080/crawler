package com.gm.demo.crawler.service;

import com.gm.demo.crawler.dao.mapper.GatherMapper;
import com.gm.demo.crawler.dao.mapper.TabMapper;
import com.gm.demo.crawler.dao.mapper.ext.GatherMapperExt;
import com.gm.demo.crawler.dao.model.Gather;
import com.gm.demo.crawler.entity.req.SaveGatherReq;
import com.gm.demo.crawler.entity.req.SaveMetadataReq;
import com.gm.utils.base.Bean;
import com.gm.utils.base.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 收集方案服务
 *
 * @author Jason
 */
@Service
public class GatherServiceImpl {

    @Autowired
    GatherMapper gatherMapper;
    @Autowired
    TabMapper tabMapper;
    @Autowired
    GatherMapperExt gatherMapperExt;
    @Autowired
    MetadataServiceImpl metadataService;

    public Gather getTab(String tab) {
        return gatherMapperExt.getTab(tab);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer save(SaveGatherReq req) {
        // 保存收集方案
        Gather gather = gatherMapperExt.getTab(req.getTab());
        if(Bool.isNull(gather)){
            gatherMapperExt.save(req);
        }else {
            req.setId(gather.getId());
            Gather newG = Bean.toBean(req, Gather.class);
            gatherMapper.updateByPrimaryKey(newG);
        }
        // 建立收集表
        SaveMetadataReq tabReq = new SaveMetadataReq();
        tabReq.setTab(req.getTab());
        tabReq.setFields(req.getCollect().split(","));
        metadataService.save(tabReq);
        return req.getId();
    }

    public Integer del(String tab) {
        metadataService.dropTab(tab);
        return gatherMapperExt.del(tab);
    }
}
