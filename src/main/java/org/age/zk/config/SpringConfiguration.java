package org.age.zk.config;

import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan("org.age.zk")
@PropertySource("classpath:cluster.properties")
public class SpringConfiguration {

    @Bean(name = "propertyConfigIn")
    public static PropertySourcesPlaceholderConfigurer propertyConfigIn() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public EventBus eventBus() {
        return new EventBus();
    }

}
