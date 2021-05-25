package com.cqupt.springbootweb;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

// 快捷键
// ctrl+n 搜索类
@SpringBootApplication
@Slf4j
@MapperScan("com.cqupt.springbootweb.mapper")
public class SpringbootWebApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SpringbootWebApplication.class, args);
//        AopAutoConfiguration aopAutoConfiguration = context.getBean(AopAutoConfiguration.class);
//        log.info("存在cityService" + aopAutoConfiguration);
    }

}
