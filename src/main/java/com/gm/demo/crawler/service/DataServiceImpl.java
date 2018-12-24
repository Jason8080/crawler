package com.gm.demo.crawler.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.gm.demo.crawler.dao.mapper.TabMapper;
import com.gm.demo.crawler.entity.req.DataReq;
import com.gm.demo.crawler.entity.req.MtDistinctReq;
import com.gm.model.request.PageReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jason
 */
@Service
public class DataServiceImpl {

    @Autowired
    TabMapper tabMapper;


    public Page<Map<String, Object>> getPage(PageReq pageReq, DataReq req) {
        PageHelper.startPage(pageReq.getPageNo(), pageReq.getPageSize());
        return tabMapper.getPage(req);
    }

    public Integer distinct(MtDistinctReq req) {
        Integer sum = 0;
        List<Integer> ids = new ArrayList();
        while ((ids.addAll(tabMapper.getDistinct(req)))) {
            if (ids.size() > 0) {
                sum += tabMapper.del(req.getTab(), ids);
                ids.clear();
            } else {
                return sum;
            }
        }
        return sum;
    }
}
