package test;

public class MyBuggy {

	public Integer operation(int i1, int i2, String type) {

		if ("*".equals(type)) {
			return i1 * i2;

		}

		if ("+".equals(type)) {
			return i1 + i2;

		}

		if ("-".equals(type)) {
			return i1 - i2;

		}

		if ("gr".equals(type)) {

			if (i1 >= i1 || ((i1 == i2) || (i1 > i2))) // buggy
				return i2;

			if (i1 > i2)
				return i1;

		}

		return null;
	}

}
