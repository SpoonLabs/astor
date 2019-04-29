package test;

public class MyBuggy {

	String codeGr = "gr";

	public Integer operation(int i1, int i2, String type) {

		MyBuggy myinst = new MyBuggy();
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
			return myinst.toNegative(i2, 1) * myinst.toPositive(i2);// Buggy parameter

		}

		return null;
	}

	public int toPositive(int n) {
		if (n >= 0)
			return n;
		else
			return n * -1;

	}

	public int toPositive(int n, int factor) {
		if (n >= 0)
			return n;
		else
			return n * -1 * factor;

	}

	public int toPositive(String i) {
		// buggy
		return Integer.parseInt(i);

	}

	public int toNegative(int n) {
		if (n <= 0)
			return n;
		else
			return n * -1;

	}

	public int toNegative(int n, int factor) {
		if (n <= 0)
			return n;
		else
			return n * -1 * factor;

	}

	private int _meta_1_1(int i1, int i2) {
		try {
			if ("1".equals(System.getProperty("mutnumber_1"))) {
				return i1;
			}
		} catch (java.lang.Exception e) {
		}
		return i2;
	}

	private int _meta_2_1(test.MyBuggy myinst, int i1, int i2) {
		try {
			if ("1".equals(System.getProperty("mutnumber_2"))) {
				return myinst.toPositive(1, 1);
			}
			if ("2".equals(System.getProperty("mutnumber_2"))) {
				return myinst.toPositive(this._meta_1_1(i1, i2), 1);
			}
			if ("3".equals(System.getProperty("mutnumber_2"))) {
				return myinst.toPositive(1, this._meta_1_1(i1, i2));
			}
		} catch (java.lang.Exception e) {
		}
		return myinst.toNegative(this._meta_1_1(i1, i2), 1);
	}

}
