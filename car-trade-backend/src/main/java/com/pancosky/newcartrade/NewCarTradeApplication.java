package com.pancosky.newcartrade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.pancosky.newcartrade.mapper")
@EnableAsync
public class NewCarTradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewCarTradeApplication.class, args);
    }
}
