package com.jahnelgroup.cartographer.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class CartographerTest {

    @Test
    public void test() throws Exception{
        Cartographer cartographer = new Cartographer();
        cartographer.migrate();
    }

}
