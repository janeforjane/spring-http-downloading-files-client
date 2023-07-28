package org.example;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class Config {


    @Bean
    public RestTemplate blockchainRest(RestTemplateBuilder builder){
        return builder.build();
    }
}
