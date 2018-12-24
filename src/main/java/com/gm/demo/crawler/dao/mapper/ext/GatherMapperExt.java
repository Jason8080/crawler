package com.gm.demo.crawler.dao.mapper.ext;

import com.gm.demo.crawler.dao.model.Gather;
import com.gm.demo.crawler.entity.req.SaveGatherReq;
import org.apache.ibatis.annotations.*;

/**
 * The interface Mt fields mapper ext.
 *
 * @author Jason
 */
public interface GatherMapperExt {
    /**
     * 获取方案.
     *
     * @param tab tab
     * @return tab tab
     */
    @Select("select * from `gather` where tab=#{tab}")
    Gather getTab(@Param("tab") String tab);

    /**
     * 保存方案.
     *
     * @param req the req
     * @return the integer
     */
    @Insert({"<script>",
            "insert into `gather` (`tab`,`page`,`data`,`filters`,`echo`,`api_example`)",
            "values(#{req.tab},#{req.page},#{req.data},#{req.filters},#{req.echo},#{req.apiExample})",
            "</script>"})
    @Options(useGeneratedKeys = true)
    Integer save(@Param("req") SaveGatherReq req);

    /**
     * 删除方案.
     *
     * @param tab the tab
     * @return the integer
     */
    @Delete("delete from `gather` where tab=#{tab}")
    Integer del(@Param("tab") String tab);
}
