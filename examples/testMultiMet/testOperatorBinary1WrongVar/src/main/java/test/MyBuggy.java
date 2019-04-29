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

			if (i2 > i2)
				return i2;

			if (i1 > i2)
				return i1;

		}

		return null;
	}

}
