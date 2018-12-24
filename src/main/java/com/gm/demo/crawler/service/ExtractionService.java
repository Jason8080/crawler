package com.gm.demo.crawler.service;

import com.gm.demo.crawler.dao.mapper.ext.ExtractionMapperExt;
import com.gm.demo.crawler.dao.model.Extraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 提取方案服务
 *
 * @author Jason
 */
@Service
public class ExtractionService {

    @Autowired
    ExtractionMapperExt extractionMapperExt;

    public Extraction getTab(String tab) {
        return extractionMapperExt.getTab(tab);
    }
}
