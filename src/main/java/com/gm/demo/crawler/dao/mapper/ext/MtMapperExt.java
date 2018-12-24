package com.gm.demo.crawler.dao.mapper.ext;

import com.github.pagehelper.Page;
import com.gm.demo.crawler.entity.req.DataReq;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
    Page<Map<String,Object>> getPage(@Param("req") DataReq req);
}
