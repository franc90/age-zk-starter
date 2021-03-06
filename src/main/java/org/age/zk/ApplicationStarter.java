package org.age.zk;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class ApplicationStarter implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationStarter.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("age-zk-starter started");

        TimeUnit.SECONDS.sleep(100);
    }

}
