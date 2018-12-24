package com.gm.demo.crawler.dao.mapper;

import com.gm.demo.crawler.dao.model.Extraction;

public interface ExtractionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Extraction record);

    int insertSelective(Extraction record);

    Extraction selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Extraction record);

    int updateByPrimaryKey(Extraction record);
}