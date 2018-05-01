package com.jahnelgroup.cartographer.spring.config;

import com.jahnelgroup.cartographer.core.Cartographer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;

public class CartographerMigrationInitializer implements InitializingBean, Ordered {

    private final Cartographer cartographer;

    private int order = 0;

    public CartographerMigrationInitializer(Cartographer cartographer) {
        this.cartographer = cartographer;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.cartographer.migrate();
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
