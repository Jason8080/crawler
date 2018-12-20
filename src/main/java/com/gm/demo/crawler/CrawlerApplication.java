package com.gm.demo.crawler;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Jason
 */
@EnableSwagger2
@SpringBootApplication
@ComponentScan("com.gm.demo.crawler")
@MapperScan("com.gm.demo.crawler.dao.mapper.**")
public class CrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlerApplication.class, args);
    }
}
