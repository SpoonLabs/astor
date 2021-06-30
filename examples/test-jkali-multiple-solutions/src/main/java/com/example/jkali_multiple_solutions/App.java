package com.example.jkali_multiple_solutions;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        
    }
    
    public static int sum(int x, int y) {
    	
    	// Buggy if-statement to be removed
    	if (x > y) {
    		x++;
    	}
    	
    	return x+y;
    }
}
