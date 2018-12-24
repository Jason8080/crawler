package com.gm.demo.crawler.dao.mapper.ext;

import com.gm.demo.crawler.dao.model.MtFields;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * The interface Mt fields mapper ext.
 *
 * @author Jason
 */
public interface MtFieldsMapperExt {
    /**
     * Gets tab.
     *
     * @param tab tab
     * @return tab
     */
    @Select("select * from `mt_fields` where tab=#{tab}")
    MtFields getTab(@Param("tab") String tab);
}
