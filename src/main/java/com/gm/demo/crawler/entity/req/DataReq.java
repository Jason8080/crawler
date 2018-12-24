package com.gm.demo.crawler.entity.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author Jason
 */
@Data
@ApiModel("数据请求实体")
public class DataReq implements Serializable {
    @ApiModelProperty("数据表")
    @NotEmpty(message = "数据表是空")
    private String tab;
    @ApiModelProperty("是否爬取")
    private Integer isCrawl;
}
