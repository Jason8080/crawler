package com.gm.demo.crawler.dao.mapper.ext;

import com.github.pagehelper.Page;
import com.gm.demo.crawler.entity.req.MtDataReq;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * @author Jason
 */
public interface MtMapperExt {
    /**
     * 获取美团1页数据
     *
     * @param req
     * @return
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
}
