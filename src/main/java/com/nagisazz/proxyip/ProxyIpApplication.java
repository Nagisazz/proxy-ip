package com.nagisazz.proxyip;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @auther zhushengzhe
 * @date 2022/01/08 00:12
 */
@SpringBootApplication
@EnableScheduling
@MapperScan("com.nagisazz.proxyip.dao")
public class ProxyIpApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProxyIpApplication.class,args);
    }
}
