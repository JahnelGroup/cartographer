package com.jahnelgroup.cartographer.spring.autoconfigure;

import com.jahnelgroup.cartographer.core.Cartographer;
import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
@ConditionalOnClass(Cartographer.class)
@ConditionalOnProperty(prefix = "cartographer", name = "enabled", matchIfMissing = true)
@AutoConfigureAfter({DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class, ElasticsearchAutoConfiguration.class})
public class CartographerAutoConfiguration {

    @Configuration
    @ConditionalOnMissingBean(Cartographer.class)
    @EnableConfigurationProperties({CartographerProperties.class})
    public static class CartographerConfig {

        private final CartographerProperties cartographerProperties;

        public CartographerConfig(CartographerProperties cartographerProperties){
            this.cartographerProperties = cartographerProperties;
        }

        @Bean
        public Cartographer cartographer() throws Exception {
            Cartographer cartographer = new Cartographer();
            BeanUtils.copyProperties(cartographerProperties, cartographer.getConfig());
            return cartographer;
        }

        @Bean
        public CartographerMigrationInitializer cartographerInitializer(Cartographer cartographer){
            return new CartographerMigrationInitializer(cartographer);
        }

    }

}
