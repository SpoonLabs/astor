package org.apache.commons.math.special;


public class Erf {
	private Erf() {
		super();
	}

	public static double erf(double x) throws org.apache.commons.math.MathException {
		double ret = org.apache.commons.math.special.Gamma.regularizedGammaP(0.5, (x * x), 1.0E-15, 10000);
		if (x < 0) {
			ret = -ret;
		} 
		return ret;
	}
}

