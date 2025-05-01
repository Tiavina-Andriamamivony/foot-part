package org.prog3.foot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan({"org.prog3.foot","org.prog3.foot.controllers"})
public class FootApplication {

    public static void main(String[] args) {
        SpringApplication.run(FootApplication.class, args);
    }

}
