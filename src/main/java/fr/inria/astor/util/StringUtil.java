package fr.inria.astor.util;

import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
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
		int limit = 100;
		s1=(s1.length()>limit)?(s1.substring(0, limit)+"[...]"):s1;
		return s1;
	}
	
	public static String[] concat(String[] a, String[] b) {
		   int aLen = a.length;
		   int bLen = b.length;
		   String[] c= new String[aLen+bLen];
		   System.arraycopy(a, 0, c, 0, aLen);
		   System.arraycopy(b, 0, c, aLen, bLen);
		   return c;
		}
}
