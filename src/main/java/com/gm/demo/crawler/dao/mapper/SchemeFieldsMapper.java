package com.gm.demo.crawler.dao.mapper;

import com.gm.demo.crawler.dao.model.SchemeFields;

public interface SchemeFieldsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SchemeFields record);

    int insertSelective(SchemeFields record);

    SchemeFields selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SchemeFields record);

    int updateByPrimaryKey(SchemeFields record);
}