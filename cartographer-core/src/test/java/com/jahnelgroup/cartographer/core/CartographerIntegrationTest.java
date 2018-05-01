package com.jahnelgroup.cartographer.core;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@Ignore
@RunWith(BlockJUnit4ClassRunner.class)
public class CartographerIntegrationTest {

    @Test
    public void test() throws Exception{
        Cartographer cartographer = new Cartographer();
        cartographer.getConfig().setClean(true);
        cartographer.migrate();
    }

}
