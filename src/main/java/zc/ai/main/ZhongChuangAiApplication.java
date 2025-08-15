package zc.ai.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"zc.ai.service.keystore,zc.ai.main.conf"})
public class ZhongChuangAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhongChuangAiApplication.class, args);
    }
}
