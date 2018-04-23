package com.jahnelgroup.cartographer;

public class CartographerException extends Exception {

    public CartographerException(String message){
        super(message);
    }

    public CartographerException(Exception e) {
        super(e);
    }
}
