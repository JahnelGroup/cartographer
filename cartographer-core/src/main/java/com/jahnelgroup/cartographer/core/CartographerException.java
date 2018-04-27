package com.jahnelgroup.cartographer.core;

import com.jahnelgroup.cartographer.core.migration.Migration;
import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;

public class CartographerException extends Exception {

    public CartographerException(String message){
        super(message);
    }
    public CartographerException(String index, String message){
        super("index="+index+", " + message);
    }
    public CartographerException(Migration migration, String message){
        this(migration.getMetaInfo(), message);
    }
    public CartographerException(MigrationMetaInfo metaInfo, String message){
        super("migration="+metaInfo+", " + message);
    }

    public CartographerException(Exception e) {
        super(e);
    }
    public CartographerException(String index, Exception e){
        super("index="+index, e);
    }
    public CartographerException(Migration migration, Exception e){
        this(migration.getMetaInfo(), e);
    }
    public CartographerException(MigrationMetaInfo metaInfo, Exception e){
        super("migration="+metaInfo, e);
    }
}
