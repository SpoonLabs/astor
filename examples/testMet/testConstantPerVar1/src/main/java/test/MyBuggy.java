package test;

public class MyBuggy {

	public final int MINUS_ONE = -1;
	public final int ONE = 1;

	public Integer operation(Integer i1, Integer i2, String type) {

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

			if (i2 > i1)
				return i2;

			if (i1 >= i2)
				return i1;

		}

		if ("absmult".equals(type)) {
			// Returns the multiplication of two positive values
			return // toPositive(i1) * ((i2) * MINUS_ONE);
			toPositive(i1) * toPositive(i2);
		}

		return null;
	}

	public int getMinusOne() {
		return -1;
	}

	public int toPositive(int n) {
		if (n >= 0)
			return n;
		else
			return n * 1;// buggy

	}

	public int toNegative(int n) {
		if (n <= 0)
			return n;
		else
			return n * -1;

	}

	public int doubleNumber(int n) {

		return n * 2;

	}
}
