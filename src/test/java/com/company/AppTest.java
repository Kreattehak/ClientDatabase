package com.company;

import com.company.configuration.AppConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AppTest {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = null;
        try {
            System.setProperty("spring.profiles.active", "dev");
            context = new AnnotationConfigApplicationContext(AppConfiguration.class);
            MyApplication application = context.getBean(MyApplication.class);

            application.letsTest();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            context.close();
        }
    }
}
