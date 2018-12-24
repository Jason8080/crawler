package com.gm.demo.crawler.dao.mapper;

import com.github.pagehelper.Page;
import com.gm.demo.crawler.dao.model.Metadata;
import com.gm.demo.crawler.entity.req.DataReq;
import com.gm.demo.crawler.entity.req.MtDistinctReq;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 数据持久化操作.
 *
 * @author Jason
 */
public interface TabMapper {

    /**
     * 获取1页数据
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
    Page<Map<String, Object>> getPage(@Param("req") DataReq req);

    /**
     * 创建表.
     *
     * @param tab the tab
     */
    @Update("CREATE TABLE ${tab} ( " +
            "id int(11) NOT NULL AUTO_INCREMENT," +
            "PRIMARY KEY (id)" +
            ")")
    void createTab(@Param("tab") String tab);

    /**
     * 更新表名.
     *
     * @param tab    the tab
     * @param newTab the new tab
     */
    @Update("ALTER TABLE `${tab}` RENAME TO `${newTab}`")
    void updateTab(@Param("tab") String tab, @Param("newTab") String newTab);


    /**
     * 删除表.
     *
     * @param tab the tab
     */
    @Update("DROP TABLE IF EXISTS ${tab}")
    void dropTab(@Param("tab") String tab);

    /**
     * 判断是否有表.
     *
     * @param tab the tab
     * @return true 有表 false 无表
     */
    @Select("SELECT count(*) " +
            "FROM information_schema.TABLES " +
            "WHERE LCASE(table_name)=#{tab}")
    Boolean existTab(@Param("tab") String tab);

    /**
     * 添加表字段.
     *
     * @param metadata the metadata
     */
    @Update({"<script>",
            "ALTER TABLE `${m.tab}`",
            "ADD COLUMN `${m.field}`",
            "${m.dataType}(${m.len})",
            "DEFAULT NULL ",
            "<if test='m.comment!=null and m.comment!=\"\"'>",
            "COMMENT '${m.comment}'",
            "</if>",
            "</script>"})
    void alterAdd(@Param("m") Metadata metadata);

    /**
     * 更改表字段.
     *
     * @param oldField the old field
     * @param metadata the metadata
     */
    @Update({"<script>",
            "ALTER TABLE `${m.tab}`",
            "CHANGE COLUMN `${oldField}` `${m.field}` ${m.dataType}(${m.len})",
            "<if test='m.dataType==\"text\"'>",
            "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",
            "</if>",
            "DEFAULT NULL",
            "<if test='m.comment!=null and m.comment!=\"\"'>",
            "COMMENT '${m.comment}'",
            "</if>",
            "</script>"})
    void alterChange(@Param("oldField") String oldField, @Param("m") Metadata metadata);

    /**
     * 更改表字段.
     *
     * @param metadata the metadata
     */
    @Update({"<script>",
            "ALTER TABLE `${m.tab}`",
            "MODIFY COLUMN `${m.field}` ${m.dataType}(${m.len})",
            "<if test='m.dataType==\"text\"'>",
            "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",
            "</if>",
            "DEFAULT NULL",
            "<if test='m.comment!=null and m.comment!=\"\"'>",
            "COMMENT '${m.comment}'",
            "</if>",
            "</script>"})
    void alterModify(@Param("m") Metadata metadata);

    /**
     * 删除表字段.
     *
     * @param metadata the metadata
     */
    @Update("ALTER TABLE `${m.tab}` DROP COLUMN `${m.field}`")
    void alterDrop(@Param("m") Metadata metadata);

    /**
     * 保存表数据.
     *
     * @param tab    the tab
     * @param fields the fields
     * @param maps   the maps
     * @return the integer
     */
    @Insert({"<script>",
            "insert into `${tab}` ",
            "<foreach collection ='fields' item='field' separator=',' open='(' close=')'>",
            "`${field}`",
            "</foreach>",
            "values",
            "<trim>",
            "<foreach collection ='maps' item='map' separator=','>",
            "<foreach collection ='fields' item='field' separator=',' open='(' close=')'>",
            "'${map[field]}'",
            "</foreach>",
            "</foreach>",
            "</trim>",
            "</script>"})
    Integer save(@Param("tab") String tab,
                 @Param("fields") Collection<String> fields,
                 @Param("maps") Map<String, String>... maps);

    /**
     * 查找重复记录数.
     *
     * @param tab
     * @param fields the fields
     * @param maps   the maps
     * @return the long
     */
    @Select({"<script>",
            "select * from `${tab}` ",
            "<where>",
            "<trim>",
            "<foreach collection ='maps' item='map' separator='or'>",
            "<foreach collection ='fields' item='field' separator='and' open='(' close=')'>",
            "<if test='map[field]!=null and map[field]!=\"\"'>",
            "${field} = '${map[field]}'",
            "</if>",
            "</foreach>",
            "</foreach>",
            "</trim>",
            "</where>",
            "</script>"})
    List<Map<String, Object>> filters(@Param("tab") String tab,
                                      @Param("fields") Collection<String> fields,
                                      @Param("maps") Map<String, String>... maps);

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
