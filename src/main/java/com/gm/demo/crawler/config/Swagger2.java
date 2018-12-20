package com.gm.demo.crawler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


/****
 ** @author Jason
 **/
@Configuration
public class Swagger2 {

    @Bean
    public Docket allApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("客户端")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gm.demo.crawler.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("G m l e e 官网在线文档")
                .description("简单优雅的风格，通用易懂的方式。")
                .termsOfServiceUrl("http://gmlee.cn/")
                .version("1.0")
                .build();
    }
}
