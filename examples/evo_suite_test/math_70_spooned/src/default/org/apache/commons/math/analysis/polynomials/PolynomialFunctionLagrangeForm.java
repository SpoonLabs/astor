package org.apache.commons.math.analysis.polynomials;


public class PolynomialFunctionLagrangeForm implements org.apache.commons.math.analysis.UnivariateRealFunction {
	private double[] coefficients;

	private final double[] x;

	private final double[] y;

	private boolean coefficientsComputed;

	public PolynomialFunctionLagrangeForm(double[] x ,double[] y) throws java.lang.IllegalArgumentException {
		org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm.verifyInterpolationArray(x, y);
		this.x = new double[x.length];
		this.y = new double[y.length];
		java.lang.System.arraycopy(x, 0, this.x, 0, x.length);
		java.lang.System.arraycopy(y, 0, this.y, 0, y.length);
		coefficientsComputed = false;
	}

	public double value(double z) throws org.apache.commons.math.FunctionEvaluationException {
		try {
			return org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm.evaluate(x, y, z);
		} catch (org.apache.commons.math.DuplicateSampleAbscissaException e) {
			throw new org.apache.commons.math.FunctionEvaluationException(e , z , e.getPattern() , e.getArguments());
		}
	}

	public int degree() {
		return (x.length) - 1;
	}

	public double[] getInterpolatingPoints() {
		double[] out = new double[x.length];
		java.lang.System.arraycopy(x, 0, out, 0, x.length);
		return out;
	}

	public double[] getInterpolatingValues() {
		double[] out = new double[y.length];
		java.lang.System.arraycopy(y, 0, out, 0, y.length);
		return out;
	}

	public double[] getCoefficients() {
		if (!(coefficientsComputed)) {
			computeCoefficients();
		} 
		double[] out = new double[coefficients.length];
		java.lang.System.arraycopy(coefficients, 0, out, 0, coefficients.length);
		return out;
	}

	public static double evaluate(double[] x, double[] y, double z) throws java.lang.IllegalArgumentException, org.apache.commons.math.DuplicateSampleAbscissaException {
		org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm.verifyInterpolationArray(x, y);
		int nearest = 0;
		final int n = x.length;
		final double[] c = new double[n];
		final double[] d = new double[n];
		double min_dist = java.lang.Double.POSITIVE_INFINITY;
		for (int i = 0 ; i < n ; i++) {
			c[i] = y[i];
			d[i] = y[i];
			final double dist = java.lang.Math.abs((z - (x[i])));
			if (dist < min_dist) {
				nearest = i;
				min_dist = dist;
			} 
		}
		double value = y[nearest];
		for (int i = 1 ; i < n ; i++) {
			for (int j = 0 ; j < (n - i) ; j++) {
				final double tc = (x[j]) - z;
				final double td = (x[(i + j)]) - z;
				final double divider = (x[j]) - (x[(i + j)]);
				if (divider == 0.0) {
					throw new org.apache.commons.math.DuplicateSampleAbscissaException(x[i] , i , (i + j));
				} 
				final double w = ((c[(j + 1)]) - (d[j])) / divider;
				c[j] = tc * w;
				d[j] = td * w;
			}
			if (nearest < (0.5 * ((n - i) + 1))) {
				value += c[nearest];
			} else {
				nearest--;
				value += d[nearest];
			}
		}
		return value;
	}

	protected void computeCoefficients() throws java.lang.ArithmeticException {
		final int n = (degree()) + 1;
		coefficients = new double[n];
		for (int i = 0 ; i < n ; i++) {
			coefficients[i] = 0.0;
		}
		final double[] c = new double[n + 1];
		c[0] = 1.0;
		for (int i = 0 ; i < n ; i++) {
			for (int j = i ; j > 0 ; j--) {
				c[j] = (c[(j - 1)]) - ((c[j]) * (x[i]));
			}
			c[0] *= -(x[i]);
			c[(i + 1)] = 1;
		}
		final double[] tc = new double[n];
		for (int i = 0 ; i < n ; i++) {
			double d = 1;
			for (int j = 0 ; j < n ; j++) {
				if (i != j) {
					d *= (x[i]) - (x[j]);
				} 
			}
			if (d == 0.0) {
				for (int k = 0 ; k < n ; ++k) {
					if ((i != k) && ((x[i]) == (x[k]))) {
						throw org.apache.commons.math.MathRuntimeException.createArithmeticException("identical abscissas x[{0}] == x[{1}] == {2} cause division by zero", i, k, x[i]);
					} 
				}
			} 
			final double t = (y[i]) / d;
			tc[(n - 1)] = c[n];
			coefficients[(n - 1)] += t * (tc[(n - 1)]);
			for (int j = n - 2 ; j >= 0 ; j--) {
				tc[j] = (c[(j + 1)]) + ((tc[(j + 1)]) * (x[i]));
				coefficients[j] += t * (tc[j]);
			}
		}
		coefficientsComputed = true;
	}

	public static void verifyInterpolationArray(double[] x, double[] y) throws java.lang.IllegalArgumentException {
		if ((x.length) != (y.length)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("dimension mismatch {0} != {1}", x.length, y.length);
		} 
		if ((x.length) < 2) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} points are required, got only {1}", 2, x.length);
		} 
	}
}

