package com.gm.demo.crawler.dao.mapper.ext;

import com.gm.demo.crawler.dao.model.Metadata;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Jason
 */
public interface MetadataMapperExt {
    /**
     * 获取指定表元数据.
     *
     * @param tab the tab
     * @return the tab
     */
    @Select("SELECT * FROM `metadata` WHERE tab=#{tab}")
    List<Metadata> getTab(@Param("tab") String tab);

    /**
     * Drop tab.
     *
     * @param tab the tab
     */
    @Delete("DELETE FROM `metadata` WHERE tab=#{tab}")
    void dropTab(@Param("tab") String tab);
}
