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
import com.gm.utils.base.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MetadataServiceImpl {

    public static final String ID = "id";

    @Autowired
    TabMapper tabMapper;

    @Autowired
    MetadataMapper metadataMapper;

    @Autowired
    MetadataMapperExt metadataMapperExt;


    @Autowired
    MetadataServiceImpl metadataService;

    public List<Metadata> getTab(String tab) {
        return metadataMapperExt.getTab(tab);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
    public Integer save(@Valid SaveMetadataReq req) {
        // 查找该表所有元数据
        List<Metadata> data = metadataMapperExt.getTab(req.getTab());
        // 不存在表先创建
        if (Bool.isNull(data)) {
            data.add(createTab(req));
        }
        Map<String, Metadata> map = data.stream().collect(Collectors.toMap(Metadata::getField, x -> x));
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

    @Transactional(rollbackFor = Exception.class)
    public Boolean dropTab(String tab) {
        metadataMapperExt.dropTab(tab);
        tabMapper.dropTab(tab);
        return true;
    }

    public Integer save(String tab, Collection<String> fields, Map<String, String>... maps) {
        if (maps.length > 0) {
            return tabMapper.save(tab, fields, maps);
        }
        return 0;
    }

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

    public void replace(List<Map<String, Object>> maps, String symbol, CharSequence... filters) {
        maps.stream().forEach(x -> {
            Iterator<Map.Entry<String, Object>> it = x.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> next = it.next();
                Object value = next.getValue();
                if (value instanceof String) {
                    for (CharSequence field : filters) {
                        value = ((String) value).replace(field, symbol);
                        value = ((String) value).replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "`");
                    }
                    x.put(next.getKey(), value);
                }
            }
        });
    }

    public void checkFields(String tab, Map<String, Metadata> fields, Map<String, Object> map) {
        Map<String, String> def = fields.values().stream().filter(x -> !Bool.isNull(x.getDef())).collect(Collectors.toMap(Metadata::getField, x -> x.getDef()));
        // 如果有默认值需要设置一下
        Iterator<Map.Entry<String, String>> itDef = def.entrySet().iterator();
        while (itDef.hasNext()) {
            Map.Entry<String, String> defNext = itDef.next();
            map.put(defNext.getKey(), defNext.getValue());
        }
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> next = it.next();
            String key = next.getKey();
            // 从字段将要存储值
            String value = next.getValue() != null ? next.getValue().toString() : "";
            if (fields.containsKey(key)) {
                // 此字段数据信息
                Metadata metadata = fields.get(key);
                // 检测长度
                if (!"text".equals(metadata.getDataType()) && metadata.getLen() < Convert.toEmpty(value).length()) {
                    SaveMetadataReq req = new SaveMetadataReq();
                    req.setTab(tab);
                    req.setFields(new String[]{metadata.getField()});
                    // 长度不够
                    int len;
                    if ("varchar".equals(metadata.getDataType())) {
                        len = value.length() * 3 + 10;
                        req.setLen(len > 255 ? 0 : len);
                        req.setDataType(len > 255 ? "text" : metadata.getDataType());
                    } else {
                        len = value.length() + 1;
                        req.setLen(len);
                    }
                    metadataService.save(req);
                }
            }
        }
    }
}
