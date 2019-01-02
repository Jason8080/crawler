package com.gm.demo.crawler.dao.mapper.ext;

import org.apache.ibatis.annotations.Select;

import java.util.List;
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

}
