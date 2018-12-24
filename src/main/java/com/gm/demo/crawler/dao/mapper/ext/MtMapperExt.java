package com.gm.demo.crawler.dao.mapper.ext;

import com.github.pagehelper.Page;
import com.gm.demo.crawler.entity.req.MtDataReq;
import com.gm.demo.crawler.entity.req.MtDistinctReq;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * The interface Mt mapper ext.
 *
 * @author Jason
 */
public interface MtMapperExt {
    /**
     * 获取美团1页数据
     *
     * @param req the req
     * @return page page
     */
    @Select({"<script>",
            "select * from `${req.tab}`",
            "<where>",
                "<if test='req.isCrawl!=null'>",
                    "isCrawl=#{req.isCrawl}",
                "</if>",
            "</where>",
            "</script>"})
    Page<Map<String,Object>> getPage(@Param("req") MtDataReq req);

    /**
     * 获取重复.
     *
     * @param req the req
     * @return the distinct
     */
    @Select({"<script>",
            "SELECT id FROM (",
                "select `${req.tab}`.*, count(*) as count from `${req.tab}` group by",
                "<foreach collection ='req.fields' item='field' separator=','>",
                    "`${field}`",
                "</foreach>",
                "having count>1",
            ") tab",
            "</script>"})
    List<Integer> getDistinct(@Param("req") MtDistinctReq req);

    /**
     * 批量删除.
     *
     * @param tab the tab
     * @param ids the ids
     * @return the integer
     */
    @Delete({"<script>",
            "delete from `${tab}`",
            "where id",
            "<foreach collection ='ids' item='id' separator=',' open='IN (' close=')'>",
                "#{id}",
            "</foreach>",
            "</script>"})
    Integer del(@Param("tab") String tab, @Param("ids") List<Integer> ids);
}
