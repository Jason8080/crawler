package com.gm.demo.crawler.dao.mapper.ext;

import com.gm.demo.crawler.dao.model.Gather;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * The interface Mt fields mapper ext.
 *
 * @author Jason
 */
public interface GatherMapperExt {
    /**
     * Gets tab.
     *
     * @param tab tab
     * @return tab
     */
    @Select("select * from `scheme_fields` where tab=#{tab}")
    Gather getTab(@Param("tab") String tab);
}
