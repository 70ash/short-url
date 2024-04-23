package com.forzlp.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Author 70ash
 * Date 2024/3/2 21:13
 * Description:
 */
@SpringBootApplication
@MapperScan("com.forzlp.project.dao.mapper")
public class ProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }
}
