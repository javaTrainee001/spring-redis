package com.entor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.entor.mapper")
public class RedisStarter {
    /**
     * 启动类 hot-fix
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(RedisStarter.class, args);
    }
}
