package com.jahnelgroup.cartographer.core.config;

public class ConfigUtils {

    public static void injectCartographerConfiguration(final CartographerConfiguration config, Object ... targets){
        for(Object target : targets){
            if( target instanceof ConfigurationAware ){
                ((ConfigurationAware)target).setCartographerConfiguration(config);
            }
        }
    }

}
