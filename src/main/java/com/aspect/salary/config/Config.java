package com.aspect.salary.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:config.properties")
@ConfigurationProperties(prefix = "key")

public class Config {
    private String something;

    public String getSomething() {
        return something;
    }

    public void setSomething(String something) {
        this.something = something;
    }
}
