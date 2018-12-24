package com.gm.demo.crawler.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.gm.demo.crawler.dao.mapper.ext.MtMapperExt;
import com.gm.demo.crawler.entity.req.MtDataReq;
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
public class MtServiceImpl {

    @Autowired
    MtMapperExt mtMapperExt;

    public Page<Map<String, Object>> getPage(PageReq pageReq, MtDataReq req) {
        PageHelper.startPage(pageReq.getPageNo(), pageReq.getPageSize());
        return mtMapperExt.getPage(req);
    }

    public Integer distinct(MtDistinctReq req) {
        List<Integer> ids = new ArrayList();
        while ((ids.addAll(mtMapperExt.getDistinct(req)))) {
        }
        if (ids.size() > 0) {
            return mtMapperExt.del(req.getTab(), ids);
        }
        return 0;
    }
}
