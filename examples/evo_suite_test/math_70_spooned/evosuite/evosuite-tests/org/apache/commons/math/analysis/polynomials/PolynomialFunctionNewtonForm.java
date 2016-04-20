package org.apache.commons.math.analysis.polynomials;


public class PolynomialFunctionNewtonForm implements org.apache.commons.math.analysis.UnivariateRealFunction {
	private double[] coefficients;

	private final double[] c;

	private final double[] a;

	private boolean coefficientsComputed;

	public PolynomialFunctionNewtonForm(double[] a ,double[] c) throws java.lang.IllegalArgumentException {
		org.apache.commons.math.analysis.polynomials.PolynomialFunctionNewtonForm.verifyInputArray(a, c);
		this.a = new double[a.length];
		this.c = new double[c.length];
		java.lang.System.arraycopy(a, 0, this.a, 0, a.length);
		java.lang.System.arraycopy(c, 0, this.c, 0, c.length);
		coefficientsComputed = false;
	}

	public double value(double z) throws org.apache.commons.math.FunctionEvaluationException {
		return org.apache.commons.math.analysis.polynomials.PolynomialFunctionNewtonForm.evaluate(a, c, z);
	}

	public int degree() {
		return c.length;
	}

	public double[] getNewtonCoefficients() {
		double[] out = new double[a.length];
		java.lang.System.arraycopy(a, 0, out, 0, a.length);
		return out;
	}

	public double[] getCenters() {
		double[] out = new double[c.length];
		java.lang.System.arraycopy(c, 0, out, 0, c.length);
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

	public static double evaluate(double[] a, double[] c, double z) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException {
		org.apache.commons.math.analysis.polynomials.PolynomialFunctionNewtonForm.verifyInputArray(a, c);
		int n = c.length;
		double value = a[n];
		for (int i = n - 1 ; i >= 0 ; i--) {
			value = (a[i]) + ((z - (c[i])) * value);
		}
		return value;
	}

	protected void computeCoefficients() {
		final int n = degree();
		coefficients = new double[n + 1];
		for (int i = 0 ; i <= n ; i++) {
			coefficients[i] = 0.0;
		}
		coefficients[0] = a[n];
		for (int i = n - 1 ; i >= 0 ; i--) {
			for (int j = n - i ; j > 0 ; j--) {
				coefficients[j] = (coefficients[(j - 1)]) - ((c[i]) * (coefficients[j]));
			}
			coefficients[0] = (a[i]) - ((c[i]) * (coefficients[0]));
		}
		coefficientsComputed = true;
	}

	protected static void verifyInputArray(double[] a, double[] c) throws java.lang.IllegalArgumentException {
		if (((a.length) < 1) || ((c.length) < 1)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("empty polynomials coefficients array");
		} 
		if ((a.length) != ((c.length) + 1)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("array sizes should have difference 1 ({0} != {1} + 1)", a.length, c.length);
		} 
	}
}

