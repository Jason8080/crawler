package com.gm.demo.crawler.entity.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Jason
 */
@Data
@ApiModel("搜索爬虫请求")
public class SearchCrawlReq extends CrawlReq {
    @ApiModelProperty("指定搜索词")
    private String key;
    @ApiModelProperty("指定关键词")
    private String keyword;
    @ApiModelProperty("指定旺旺号")
    private String content;
    @ApiModelProperty("查询项")
    private String[] items;
    @ApiModelProperty("查询值")
    private String[] values;

    public String getKey(Integer index) {
        return items.length > index ? items[index] : "";
    }

    public String getVal(Integer index) {
        return values.length > index ? values[index] : "";
    }
}
