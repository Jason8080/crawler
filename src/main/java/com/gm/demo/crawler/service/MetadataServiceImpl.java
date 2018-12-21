package com.gm.demo.crawler.service;

import com.gm.demo.crawler.dao.mapper.MetadataMapper;
import com.gm.demo.crawler.dao.mapper.TabMapper;
import com.gm.demo.crawler.dao.mapper.ext.MetadataMapperExt;
import com.gm.demo.crawler.dao.model.Metadata;
import com.gm.demo.crawler.entity.req.ModifyMetadataReq;
import com.gm.demo.crawler.entity.req.SaveMetadataReq;
import com.gm.utils.base.Bean;
import com.gm.utils.base.Bool;
import com.gm.utils.base.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Metadata service.
 *
 * @author Jason
 */
@Service
public class MetadataServiceImpl {

    /**
     * The constant ID.
     */
    public static final String ID = "id";

    /**
     * The Tab mapper.
     */
    @Autowired
    TabMapper tabMapper;

    /**
     * The Metadata mapper.
     */
    @Autowired
    MetadataMapper metadataMapper;

    /**
     * The Metadata mapper ext.
     */
    @Autowired
    MetadataMapperExt metadataMapperExt;

    /**
     * Gets tab.
     *
     * @param tab the tab
     * @return the tab
     */
    public List<Metadata> getTab(String tab) {
        return metadataMapperExt.getTab(tab);
    }

    /**
     * 保存元数据
     *
     * @param req the req
     * @return integer integer
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer save(@Valid SaveMetadataReq req) {
        Metadata metadata = Bean.toBean(req, Metadata.class);
        // 查找该字段是否存在
        Metadata oldMetadata = metadataMapperExt.getMetadata(req.getTab(), req.getField());
        // 设置默认元数据信息
        Metadata defMetadata = new Metadata();
        defMetadata.setDataType("varchar");
        defMetadata.setLen(10);
        // 补全目标元数据信息
        metadata = Bean.copy(Convert.toEmpty(oldMetadata, defMetadata), metadata);
        // 查找该表所有元数据
        List<Metadata> data = metadataMapperExt.getTab(req.getTab());
        Map<String, Integer> map = data.stream().collect(Collectors.toMap(Metadata::getField, Metadata::getId));
        // 不存在表先创建
        if (Bool.isNull(data)) {
            createTab(req);
        }
        // 字段存在就更改
        if (map.containsKey(req.getField()) || map.containsKey(req.getOldField()) || ID.equalsIgnoreCase(req.getField())){
            // 更改表字段
            tabMapper.alterChange(Convert.toEmpty(req.getOldField(), req.getField()), metadata);
            // 更新元数据
            metadata.setId(map.get(Convert.toEmpty(req.getOldField(), req.getField())));
            metadataMapper.updateByPrimaryKeySelective(metadata);
        } else {
            // 添加表字段
            tabMapper.alterAdd(metadata);
            // 添加元数据
            metadataMapper.insertSelective(metadata);
        }
        return metadata.getId();
    }

    public Boolean modify(ModifyMetadataReq req) {
        Metadata metadata = Bean.toBean(req, Metadata.class);
        tabMapper.alterModify(metadata);
        return true;
    }

    /**
     * 创建表.
     *
     * @param req the req
     * @return the integer
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Integer createTab(SaveMetadataReq req) {
        Metadata id = new Metadata();
        id.setField("id");
        id.setDataType("int");
        id.setLen(11);
        id.setComment("主键");
        id.setTab(req.getTab());
        metadataMapper.insertSelective(id);
        tabMapper.createTab(req.getTab());
        return id.getId();
    }

    /**
     * 删除表
     *
     * @param tab the tab
     * @return boolean boolean
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean dropTab(String tab) {
        metadataMapperExt.dropTab(tab);
        tabMapper.dropTab(tab);
        return true;
    }

    /**
     * 保存表数据.
     *
     * @param tab    the tab
     * @param fields the fields
     * @param maps   the maps
     * @return the integer
     */
    public Integer save(String tab, Collection<String> fields, Map<String, String>... maps) {
        return tabMapper.save(tab, fields, maps);
    }
}
