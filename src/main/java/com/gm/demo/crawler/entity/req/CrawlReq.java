package com.gm.demo.crawler.entity.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Map;

/**
 * @author Jason
 */
@Data
@ApiModel("爬取请求实体")
public class CrawlReq implements Serializable {
    @ApiModelProperty(value = "目标网址", required = true)
    @NotEmpty(message = "目标网址是空")
    @Pattern(message = "目标网址格式错误",
            regexp = "^(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?$"
    )
    private String url;

    @ApiModelProperty("请求头")
    private Map<String, String> headers;
    @ApiModelProperty("请求参")
    private Map<String, Object> params;
}
