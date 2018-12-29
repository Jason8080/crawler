package com.gm.demo.crawler.entity.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import java.io.Serializable;

/**
 * @author Jason
 */
@Data
@ApiModel("保存收集方案实体")
public class SaveGatherReq implements Serializable {
    @ApiModelProperty(hidden = true)
    @Null(message = "不能指定方案ID")
    private Integer id;

    @ApiModelProperty("方案名/数据表名")
    @NotEmpty(message = "方案名/数据表名是空")
    @Length(min = 2, max = 20, message = "方案名/数据表名长度为2-20")
    private String tab;

    @ApiModelProperty("翻页方案")
    @NotEmpty(message = "翻页方案是空")
    private String page;

    @ApiModelProperty("Json result format is data")
    private String data;

    @ApiModelProperty("数据列字段")
    @NotEmpty(message = "数据集字段是空")
    private String echo;

    @ApiModelProperty("收集字段")
    @NotEmpty(message = "收集字段是空")
    private String collect;

    @ApiModelProperty("过滤字段")
    @NotEmpty(message = "过滤字段是空")
    private String filters;

    @ApiModelProperty("api示例")
    private String apiExample;
}