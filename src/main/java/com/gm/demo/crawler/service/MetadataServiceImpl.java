package com.gm.demo.crawler.service;

import com.gm.demo.crawler.dao.mapper.MetadataMapper;
import com.gm.demo.crawler.dao.mapper.TabMapper;
import com.gm.demo.crawler.dao.mapper.ext.MetadataMapperExt;
import com.gm.demo.crawler.dao.model.Metadata;
import com.gm.demo.crawler.entity.SaveMetadataReq;
import com.gm.utils.base.Bean;
import com.gm.utils.base.Bool;
import com.gm.utils.base.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jason
 */
@Service
public class MetadataServiceImpl {

    @Autowired
    TabMapper tabMapper;

    @Autowired
    MetadataMapper metadataMapper;

    @Autowired
    MetadataMapperExt metadataMapperExt;

    public List<Metadata> getTab(String tab) {
        return metadataMapperExt.getTab(tab);
    }

    /**
     * 保存元数据
     * @param req
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer save(SaveMetadataReq req) {
        Metadata metadata = Bean.toBean(req, Metadata.class);
        List<Metadata> data = metadataMapperExt.getTab(req.getTab());
        Map<String, Integer> map = data.stream().collect(Collectors.toMap(Metadata::getField, Metadata::getId));
        // 不存在表先创建
        if (Bool.isNull(data)) {
            tabMapper.createTab(req.getTab());
        }
        // 字段存在就更改
        if (map.containsKey(req.getField())){
            // 更改表字段
            tabMapper.alterChange(Convert.toEmpty(req.getOldField(), req.getField()), metadata);
            // 更新元数据
            metadata.setId(map.get(req.getField()));
            metadataMapper.updateByPrimaryKeySelective(metadata);
        }else {
            // 添加表字段
            tabMapper.alterAdd(metadata);
            // 添加元数据
            metadataMapper.insertSelective(metadata);
        }
        return metadata.getId();
    }

    /**
     * 删除表
     * @param tab
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean dropTab(String tab) {
        tabMapper.dropTab(tab);
        metadataMapperExt.dropTab(tab);
        return true;
    }
}
