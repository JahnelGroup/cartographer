package com.jahnelgroup.cartographer.core.config;

import java.util.Iterator;

public class ConfigUtils {

    public static void injectCartographerConfiguration(final CartographerConfiguration config, Object ... targets){
        for(Object target : targets){

            if( target instanceof Iterable ){
                Iterator it = ((Iterable)target).iterator();
                while(it.hasNext()){
                    inject(config, it.next());
                }
            }

            inject(config, target);
        }
    }

    private static void inject(CartographerConfiguration config, Object target) {
        if( target instanceof ConfigurationAware){
            ((ConfigurationAware)target).setCartographerConfiguration(config);
        }
    }

}
