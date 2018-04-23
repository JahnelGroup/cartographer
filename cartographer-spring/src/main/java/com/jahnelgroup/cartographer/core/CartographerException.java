package com.jahnelgroup.cartographer.core;

public class CartographerException extends Exception {

    public CartographerException(String message){
        super(message);
    }

    public CartographerException(Exception e) {
        super(e);
    }
}
