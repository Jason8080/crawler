package com.gm.demo.crawler.dao.mapper;

import com.gm.demo.crawler.dao.model.Metadata;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * The interface Tab mapper.
 *
 * @author Jason
 */
public interface TabMapper {

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
    @Update("ALTER TABLE `${m.tab}` ADD COLUMN `${m.field}` ${m.varchar}(${m.len}) DEFAULT NULL COMMENT ${m.comment}")
    void alterAdd(@Param("m") Metadata metadata);

    /**
     * 更改表字段.
     *
     * @param oldField the old field
     * @param metadata the metadata
     */
    @Update({"<script>",
            "ALTER TABLE `${m.tab}`",
            "CHANGE COLUMN `${oldField}` `${m.field}` ${m.varchar}(${m.len})",
            "DEFAULT NULL",
            "COMMENT ${m.comment}",
            "</script>"})
    void alterChange(@Param("oldField") String oldField, @Param("m") Metadata metadata);

    /**
     * 删除表字段.
     *
     * @param metadata the metadata
     */
    @Update("ALTER TABLE `${m.tab}` DROP COLUMN `${m.field}`")
    void alterDrop(@Param("m") Metadata metadata);
}
