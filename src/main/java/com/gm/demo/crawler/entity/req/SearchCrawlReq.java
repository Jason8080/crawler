package com.gm.demo.crawler.entity.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author Jason
 */
@Data
@ApiModel("搜索爬虫请求")
public class SearchCrawlReq extends CrawlReq {
    @ApiModelProperty("指定关键词")
    @Length(min = 1, max = 10, message = "关键词长度1-10")
    private String keyword;
    @ApiModelProperty("指定旺旺号")
    @Length(min = 1, max = 10, message = "旺旺号长度1-10")
    private String title;
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
