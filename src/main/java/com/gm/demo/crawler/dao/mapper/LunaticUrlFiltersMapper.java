package com.gm.demo.crawler.dao.mapper;

import com.gm.demo.crawler.dao.model.LunaticUrlFilters;

public interface LunaticUrlFiltersMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LunaticUrlFilters record);

    int insertSelective(LunaticUrlFilters record);

    LunaticUrlFilters selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LunaticUrlFilters record);

    int updateByPrimaryKey(LunaticUrlFilters record);
}