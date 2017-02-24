package cn.com.bsfit.frms.pay.engine;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import cn.com.bsfit.frms.engine.Application;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@ComponentScan
public class ValidateEngine {

    public static void main(String[] args) {
        try {
            new SpringApplicationBuilder().bannerMode(Mode.OFF).sources(Application.class).run(args);
        } catch (BeanCreationException e) {
            System.err.println("请确认已正确配置该平台，请检查是否正确配置");
            e.printStackTrace();
        }
    }
}
