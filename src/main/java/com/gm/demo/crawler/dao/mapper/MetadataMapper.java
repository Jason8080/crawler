package com.gm.demo.crawler.dao.mapper;

import com.gm.demo.crawler.dao.model.Metadata;

/**
 * The interface Metadata mapper.
 *
 * @author Jason
 */
public interface MetadataMapper {

    /**
     * Delete by primary key int.
     *
     * @param id the id
     * @return the int
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * Insert int.
     *
     * @param record the record
     * @return the int
     */
    int insert(Metadata record);

    /**
     * Insert selective int.
     *
     * @param record the record
     * @return the int
     */
    int insertSelective(Metadata record);

    /**
     * Select by primary key metadata.
     *
     * @param id the id
     * @return the metadata
     */
    Metadata selectByPrimaryKey(Integer id);

    /**
     * Update by primary key selective int.
     *
     * @param record the record
     * @return the int
     */
    int updateByPrimaryKeySelective(Metadata record);

    /**
     * Update by primary key int.
     *
     * @param record the record
     * @return the int
     */
    int updateByPrimaryKey(Metadata record);
}