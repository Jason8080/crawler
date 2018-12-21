package com.gm.demo.crawler.entity.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Jason
 */
@Data
@ApiModel("Cookies")
public class Cookies implements Serializable {
    @ApiModelProperty("名")
    private String name;
    @ApiModelProperty("值")
    private String value;
    @ApiModelProperty(value = "路径", example = "/")
    private String path = "/";
    @ApiModelProperty(value = "域", example = "127.0.0.1")
    private String domain;
    @ApiModelProperty(value = "版本", example = "0")
    private Integer version = 0;
}
