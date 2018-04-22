package com.jahnelgroup.cartographer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jahnelgroup.cartographer.index.IndexService;
import com.jahnelgroup.cartographer.index.IndexServiceImpl;
import com.jahnelgroup.cartographer.index.migration.MigrationProcessor;
import com.jahnelgroup.cartographer.index.migration.MigrationProcessorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CartographerAutoConfiguration {

    @Autowired
    CartographerProperties cartographerProperties;

    @Bean
    CartographerProperties cartographerProperties(){
        return new CartographerProperties();
    }

    @Bean
    IndexService cartographerIndexService(){
        return new IndexServiceImpl(new RestTemplate(), cartographerProperties);
    }

    @Bean
    MigrationProcessor migrationProcessor(
            CartographerProperties cartographerProperties,
            ResourceLoader resourceLoader,
            IndexService indexService,
            ObjectMapper objectMapper
    ){
        return new MigrationProcessorImpl(
                cartographerProperties,
                resourceLoader,
                indexService,
                objectMapper);
    }



}
