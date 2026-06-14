package com.pancosky.newcartrade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.pancosky.newcartrade.mapper")
@EnableAsync
@EnableScheduling
public class CarTradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarTradeApplication.class, args);
    }
}
