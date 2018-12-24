package com.gm.demo.crawler.dao.mapper;

import com.gm.demo.crawler.dao.model.MtFields;

public interface MtFieldsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MtFields record);

    int insertSelective(MtFields record);

    MtFields selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MtFields record);

    int updateByPrimaryKey(MtFields record);
}