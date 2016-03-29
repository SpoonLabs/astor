package fr.inria.astor.util;

import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author matias
 *
 */
public class StringUtil {

	
	public static String trunc(CtElement e){
		if(e == null){
			return "-";
		}
		return trunc(e.toString());
	}
	
	public static String trunc(String s){
		String s1 = s.replace("\n" , " ");
		s1=(s1.length()>25)?(s1.substring(0, 25)+"[...]"):s1;
		return s1;
	}
}
