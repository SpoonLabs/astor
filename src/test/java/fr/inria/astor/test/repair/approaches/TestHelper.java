package fr.inria.astor.test.repair.approaches;

/**
 * 
 * @author Matias Martinez
 *
 */
public class TestHelper {

	public static boolean equalsNoParentesis(String s1, String s2) {
		return getWithoutParenth(s1).equals(getWithoutParenth(s2));
	}

	public static String getWithoutParenth(String s1) {
		return s1.replace("(", "").replace(")", "");
	}
}
