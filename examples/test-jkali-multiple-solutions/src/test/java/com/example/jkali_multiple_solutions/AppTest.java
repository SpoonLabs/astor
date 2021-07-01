package com.example.jkali_multiple_solutions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AppTest {
    
    @Test
    public void testSum1() {
    	assertEquals(13, App.sum(10, 3)); 
    }

    @Test
    public void testSum2() {
    	assertEquals(13, App.sum(3, 10)); 
    }
}
