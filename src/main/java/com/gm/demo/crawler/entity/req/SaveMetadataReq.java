package com.gm.demo.crawler.entity.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import java.io.Serializable;

/**
 * @author Jason
 */
@Data
@ApiModel("保存元数据请求实体")
public class SaveMetadataReq implements Serializable {
    @ApiModelProperty(hidden = true)
    @Null(message = "不能指定序列号")
    private Integer id;

    @ApiModelProperty("旧字段名")
    @Length(min = 2, max = 20, message = "字段名长度2-20")
    private String oldField;

    @ApiModelProperty(value = "新字段名", required = true)
    @NotEmpty(message = "新字段名是空")
    @Length(min = 2, max = 20, message = "字段名长度2-20")
    private String field;

    @ApiModelProperty(value = "数据类型", example = "varchar")
    @Length(min = 2, max = 20, message = "数据类型长度2-20")
    private String dataType;

    @ApiModelProperty(value = "数据长度")
    @Min(value = 1, message = "数据长度小于1")
    @Max(value = 1000, message = "数据长度大于1000")
    private Integer len;

    @ApiModelProperty(value = "字段注释")
    @Length(max = 100, message = "注释长度大于100")
    private String comment;

    @ApiModelProperty(value = "表名", required = true)
    @NotEmpty(message = "表名是空")
    @Length(min = 2, max = 20, message = "表名长度2-20")
    private String tab;
}
