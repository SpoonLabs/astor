package com.example.many_solutions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    
    @Test
    public void testSum() {
    	assertEquals(13, App.sum(10, 3)); 
    }
}
