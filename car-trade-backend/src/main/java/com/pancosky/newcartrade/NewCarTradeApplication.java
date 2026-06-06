package com.pancosky.newcartrade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.pancosky.newcartrade.mapper")
public class NewCarTradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewCarTradeApplication.class, args);
    }
}
