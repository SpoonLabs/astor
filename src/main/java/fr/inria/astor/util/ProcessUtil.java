package fr.inria.astor.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
/**
 * 
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public class ProcessUtil {

	
	public static int currentNumberProcess(){
		int count = 0;
		 try {
		        String line;
		        
		        String[] cmd = {
		        		"/bin/sh",
		        		"-c",
		        		"ps -ux | grep java"
		        		};
		        Process p = Runtime.getRuntime().exec(cmd);
		        BufferedReader input =
		                new BufferedReader(new InputStreamReader(p.getInputStream()));
		        while ((line = input.readLine()) != null) {
		           // System.out.println(line);
		            count++;
		        }
		        input.close();
		    } catch (Exception err) {
		        err.printStackTrace();
		    }
		return count;
	}
}
