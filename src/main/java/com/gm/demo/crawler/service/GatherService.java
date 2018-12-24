package com.gm.demo.crawler.service;

import com.gm.demo.crawler.dao.mapper.ext.GatherMapperExt;
import com.gm.demo.crawler.dao.model.Gather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 收集方案服务
 *
 * @author Jason
 */
@Service
public class GatherService {

    @Autowired
    GatherMapperExt gatherMapperExt;

    public Gather getTab(String tab) {
        return gatherMapperExt.getTab(tab);
    }
}
