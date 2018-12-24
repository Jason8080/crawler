package com.gm.demo.crawler.entity.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Jason
 */
@Data
@ApiModel("数据去重请求")
public class MtDistinctReq {
    @ApiModelProperty("数据表")
    private String tab;
    @ApiModelProperty("重复字段")
    private String[] fields;
}
