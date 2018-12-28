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
import java.util.*;
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
        // 查找该表所有元数据
        List<Metadata> data = metadataMapperExt.getTab(req.getTab());
        // 不存在表先创建
        if (Bool.isNull(data)) {
            data.add(createTab(req));
        }
        Map<String, Metadata> map = data.stream().collect(Collectors.toMap(Metadata::getField, x->x));
        Metadata metadata = Bean.toBean(req, Metadata.class);
        for (String field : req.getFields()) {
            // 此字段名称不一样
            metadata.setField(field);
            // 查找该字段是否存在
            Metadata oldMetadata = map.get(field);
            // 设置默认元数据信息
            Metadata defMetadata = new Metadata();
            defMetadata.setDataType("varchar");
            defMetadata.setLen(10);
            // 补全目标元数据信息
            metadata = Bean.copy(Convert.toEmpty(oldMetadata, defMetadata), metadata);
            // 字段存在就更改
            if (map.containsKey(field) || map.containsKey(req.getOldField()) || ID.equalsIgnoreCase(field)) {
                // 更改表字段
                tabMapper.alterChange(Convert.toEmpty(req.getOldField(), field), metadata);
                // 更新元数据
                metadata.setId(map.get(Convert.toEmpty(req.getOldField(), field)).getId());
                metadataMapper.updateByPrimaryKeySelective(metadata);
            } else {
                // 添加表字段
                tabMapper.alterAdd(metadata);
                // 添加元数据
                metadataMapper.insertSelective(metadata);
            }
        }
        return metadata.getId();
    }

    public Boolean modify(ModifyMetadataReq req) {
        Metadata old = metadataMapperExt.getMetadata(req.getTab(), req.getField());
        Metadata metadata = Bean.toBean(req, Metadata.class);
        tabMapper.alterModify(metadata);
        metadata.setId(old.getId());
        metadataMapper.updateByPrimaryKeySelective(metadata);
        return true;
    }

    /**
     * 创建表.
     *
     * @param req the req
     * @return the integer
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Metadata createTab(@Valid SaveMetadataReq req) {
        Metadata id = new Metadata();
        id.setField("id");
        id.setDataType("int");
        id.setLen(11);
        id.setComment("主键");
        id.setTab(req.getTab());
        metadataMapper.insertSelective(id);
        tabMapper.createTab(req.getTab());
        return id;
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
        if (maps.length > 0) {
            return tabMapper.save(tab, fields, maps);
        }
        return 0;
    }

    /**
     * 去重.
     *
     * @param tab     the tab
     * @param maps    the maps
     * @param filters the filters
     */
    public void distinct(String tab, List<Map<String, Object>> maps, String... filters) {
        List<Map<String, Object>> exists = Bool.isNull(maps) ? new ArrayList() :
                tabMapper.filters(tab, Arrays.asList(filters), maps.toArray(new HashMap[0]));
        for (int i = 0; i < maps.size(); i++) {
            Map<String, Object> map = maps.get(i);
            for (Map<String, Object> exist : exists) {
                // 默认过滤字段值都相同
                boolean containAll = true;
                for (String field : filters) {
                    Object mapVal = map.get(field);
                    Object existVal = exist.get(field);
                    if (!Bool.haveNull(mapVal, existVal) && !mapVal.toString().equals(existVal.toString())) {
                        containAll = false;
                    }
                }
                if (containAll) {
                    maps.remove(i--);
                    break;
                }
            }
        }
    }

    /**
     * 替换特俗字符.
     *
     * @param maps    the maps
     * @param symbol  the symbol
     * @param filters the filters
     */
    public void replace(List<Map<String, Object>> maps, String symbol, CharSequence... filters) {
        maps.stream().forEach(x -> {
            Iterator<Map.Entry<String, Object>> it = x.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> next = it.next();
                Object value = next.getValue();
                if (value instanceof String) {
                    for (CharSequence field : filters) {
                        value = value.toString().replace(field, symbol);
                    }
                    x.put(next.getKey(), value);
                }
            }
        });
    }
}
