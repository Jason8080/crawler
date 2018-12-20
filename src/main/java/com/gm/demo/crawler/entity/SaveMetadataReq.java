package com.gm.demo.crawler.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Jason
 */
@Data
@ApiModel("保存元数据请求实体")
public class SaveMetadataReq implements Serializable {
    @ApiModelProperty(hidden = true)
    private Integer id;

    private String field;

    private Integer len;

    @ApiModelProperty("数据类型")
    private String varchar;

    @ApiModelProperty("表名")
    private String tab;
}
