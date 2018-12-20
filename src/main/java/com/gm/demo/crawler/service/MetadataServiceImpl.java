package com.gm.demo.crawler.service;

import com.gm.demo.crawler.dao.mapper.MetadataMapper;
import com.gm.demo.crawler.dao.model.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jason
 */
@Service
public class MetadataServiceImpl {

    @Autowired
    MetadataMapper metadataMapper;

    public List<Metadata> getTab(String tab) {
        return metadataMapper.getTab(tab);
    }
}
