package fr.inria.astor.test.repair.approaches;

/**
 * 
 * @author Matias Martinez
 *
 */
public class TestHelper {

	public static boolean equalsNoParentesis(String s1, String s2) {
		return getWithoutParentheses(s1).equals(getWithoutParentheses(s2));
	}

	public static String getWithoutParentheses(String s1) {
		return s1.replace("(", "").replace(")", "");
	}
}
