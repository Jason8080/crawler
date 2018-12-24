package com.gm.demo.crawler.dao.mapper;

import com.gm.demo.crawler.dao.model.Gather;

public interface GatherMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Gather record);

    int insertSelective(Gather record);

    Gather selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Gather record);

    int updateByPrimaryKey(Gather record);
}