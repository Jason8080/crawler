package com.gm.demo.crawler.entity.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Jason
 */
@Data
@ApiModel("美团评论数据请求")
public class MtCommentDataReq extends MtDataReq {
    @ApiModelProperty(hidden = true)
    private String tab = "mt_comment";
    @ApiModelProperty(hidden = true)
    private Integer isCrawl = null;
}
