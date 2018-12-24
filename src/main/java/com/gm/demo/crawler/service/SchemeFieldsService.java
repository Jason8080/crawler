package com.gm.demo.crawler.service;

import com.gm.demo.crawler.dao.mapper.ext.SchemeFieldsMapperExt;
import com.gm.demo.crawler.dao.model.SchemeFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 提取方案服务
 *
 * @author Jason
 */
@Service
public class SchemeFieldsService {

    @Autowired
    SchemeFieldsMapperExt schemeFieldsMapperExt;

    public SchemeFields getTab(String tab) {
        return schemeFieldsMapperExt.getTab(tab);
    }
}
