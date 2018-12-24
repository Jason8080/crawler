package com.gm.demo.crawler.entity.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Jason
 */
@Data
@ApiModel("美团商家数据请求")
public class MtMerchantDataReq extends MtDataReq {
    @ApiModelProperty(hidden = true)
    private String tab = "mt_merchant";
    @ApiModelProperty("是否已爬取评论")
    private Integer isCrawl;
}
