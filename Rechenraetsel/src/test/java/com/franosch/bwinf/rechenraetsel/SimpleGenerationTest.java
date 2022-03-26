package com.franosch.bwinf.rechenraetsel;

import org.junit.Test;

public class SimpleGenerationTest {


    @Test
    public void simpleTest(){
        Generator generator = new Generator(2);
        System.out.println(generator.generate());
    }

}
