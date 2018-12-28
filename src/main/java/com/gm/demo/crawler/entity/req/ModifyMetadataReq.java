package com.gm.demo.crawler.entity.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * @author Jason
 */
@Data
@ApiModel("更改元数据实体")
public class ModifyMetadataReq {
    @ApiModelProperty(value = "字段名", required = true)
    @NotEmpty(message = "字段名是空")
    @Length(min = 2, max = 20, message = "字段名长度2-20")
    private String field;

    @ApiModelProperty(value = "数据类型", example = "varchar")
    @Length(min = 2, max = 10, message = "数据类型长度2-10")
    private String dataType = "text";

    @ApiModelProperty(value = "数据长度", example = "10")
    @Min(value = 1, message = "数据长度小于1")
    @Max(value = 1000, message = "数据长度大于1000")
    private Integer len = 255;

    @ApiModelProperty(value = "字段注释")
    @Length(max = 100, message = "注释长度大于100")
    private String comment;

    @ApiModelProperty(value = "表名", required = true)
    @NotEmpty(message = "表名是空")
    @Length(min = 2, max = 20, message = "表名长度2-20")
    private String tab;
}
