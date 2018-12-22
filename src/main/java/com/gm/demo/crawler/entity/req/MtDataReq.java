package com.gm.demo.crawler.entity.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author Jason
 */
@Data
@ApiModel("美团数据请求")
public class MtDataReq {
    @ApiModelProperty("数据表")
    @NotEmpty(message = "数据表是空")
    private String tab = "mt_merchant";
    @ApiModelProperty("是否已爬取评论")
    private Integer isCrawl;
}
