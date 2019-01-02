package com.gm.demo.crawler.dao.mapper.ext;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

/**
 * The interface Lunatic url filters mapper ext.
 *
 * @author Jason
 */
public interface LunaticUrlFiltersMapperExt {
    /**
     * Gets all.
     *
     * @return the all
     */
    @Select("select url from lunatic_url_filters order by id")
    Set<String> getAll();

    /**
     * Gets count.
     *
     * @param url the url
     * @return the count
     */
    @Select("select count(*) from lunatic_url_filters where url like CONCAT(#{url},'%')")
    @ResultType(Long.class)
    Long getCount(@Param("url") String url);
}
