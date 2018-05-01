package com.jahnelgroup.cartographer.spring.config;

import com.jahnelgroup.cartographer.core.Cartographer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Cartographer.class)
@ConditionalOnProperty(value = "cartographer.enabled", havingValue = "true")
@AutoConfigureAfter({DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class, ElasticsearchAutoConfiguration.class})
public class CartographerAutoConfiguration {

    @Configuration
    @ConditionalOnMissingBean(Cartographer.class)
    public static class FlywayConfiguration {

        @Bean
        public Cartographer cartographer() throws Exception {
            return new Cartographer();
        }

        @Bean
        public CartographerMigrationInitializer cartographerInitializer(Cartographer cartographer){
            return new CartographerMigrationInitializer(cartographer);
        }

    }

}
