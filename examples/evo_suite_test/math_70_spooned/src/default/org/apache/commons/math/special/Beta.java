package org.apache.commons.math.special;


public class Beta {
	private static final double DEFAULT_EPSILON = 1.0E-14;

	private Beta() {
		super();
	}

	public static double regularizedBeta(double x, double a, double b) throws org.apache.commons.math.MathException {
		return org.apache.commons.math.special.Beta.regularizedBeta(x, a, b, DEFAULT_EPSILON, java.lang.Integer.MAX_VALUE);
	}

	public static double regularizedBeta(double x, double a, double b, double epsilon) throws org.apache.commons.math.MathException {
		return org.apache.commons.math.special.Beta.regularizedBeta(x, a, b, epsilon, java.lang.Integer.MAX_VALUE);
	}

	public static double regularizedBeta(double x, double a, double b, int maxIterations) throws org.apache.commons.math.MathException {
		return org.apache.commons.math.special.Beta.regularizedBeta(x, a, b, DEFAULT_EPSILON, maxIterations);
	}

	public static double regularizedBeta(double x, final double a, final double b, double epsilon, int maxIterations) throws org.apache.commons.math.MathException {
		double ret;
		if (((((((java.lang.Double.isNaN(x)) || (java.lang.Double.isNaN(a))) || (java.lang.Double.isNaN(b))) || (x < 0)) || (x > 1)) || (a <= 0.0)) || (b <= 0.0)) {
			ret = java.lang.Double.NaN;
		} else if (x > ((a + 1.0) / ((a + b) + 2.0))) {
			ret = 1.0 - (org.apache.commons.math.special.Beta.regularizedBeta((1.0 - x), b, a, epsilon, maxIterations));
		} else {
			org.apache.commons.math.util.ContinuedFraction fraction = new org.apache.commons.math.util.ContinuedFraction() {
				@java.lang.Override
				protected double getB(int n, double x) {
					double ret;
					double m;
					if ((n % 2) == 0) {
						m = n / 2.0;
						ret = ((m * (b - m)) * x) / (((a + (2 * m)) - 1) * (a + (2 * m)));
					} else {
						m = (n - 1.0) / 2.0;
						ret = (-(((a + m) * ((a + b) + m)) * x)) / ((a + (2 * m)) * ((a + (2 * m)) + 1.0));
					}
					return ret;
				}

				@java.lang.Override
				protected double getA(int n, double x) {
					return 1.0;
				}
			};
			ret = ((java.lang.Math.exp(((((a * (java.lang.Math.log(x))) + (b * (java.lang.Math.log((1.0 - x))))) - (java.lang.Math.log(a))) - (org.apache.commons.math.special.Beta.logBeta(a, b, epsilon, maxIterations))))) * 1.0) / (fraction.evaluate(x, epsilon, maxIterations));
		}
		return ret;
	}

	public static double logBeta(double a, double b) {
		return org.apache.commons.math.special.Beta.logBeta(a, b, DEFAULT_EPSILON, java.lang.Integer.MAX_VALUE);
	}

	public static double logBeta(double a, double b, double epsilon, int maxIterations) {
		double ret;
		if ((((java.lang.Double.isNaN(a)) || (java.lang.Double.isNaN(b))) || (a <= 0.0)) || (b <= 0.0)) {
			ret = java.lang.Double.NaN;
		} else {
			ret = ((org.apache.commons.math.special.Gamma.logGamma(a)) + (org.apache.commons.math.special.Gamma.logGamma(b))) - (org.apache.commons.math.special.Gamma.logGamma((a + b)));
		}
		return ret;
	}
}

