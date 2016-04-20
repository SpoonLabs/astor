package org.apache.commons.math.analysis.interpolation;


public final class NevilleInterpolatorTest extends junit.framework.TestCase {
	public void testSinFunction() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.analysis.interpolation.UnivariateRealInterpolator interpolator = new org.apache.commons.math.analysis.interpolation.NevilleInterpolator();
		double[] x;
		double[] y;
		double z;
		double expected;
		double result;
		double tolerance;
		int n = 6;
		double min = 0.0;
		double max = 2 * (java.lang.Math.PI);
		x = new double[n];
		y = new double[n];
		for (int i = 0 ; i < n ; i++) {
			x[i] = min + ((i * (max - min)) / n);
			y[i] = f.value(x[i]);
		}
		double derivativebound = 1.0;
		org.apache.commons.math.analysis.UnivariateRealFunction p = interpolator.interpolate(x, y);
		z = (java.lang.Math.PI) / 4;
		expected = f.value(z);
		result = p.value(z);
		tolerance = java.lang.Math.abs((derivativebound * (partialerror(x, z))));
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		z = (java.lang.Math.PI) * 1.5;
		expected = f.value(z);
		result = p.value(z);
		tolerance = java.lang.Math.abs((derivativebound * (partialerror(x, z))));
		junit.framework.Assert.assertEquals(expected, result, tolerance);
	}

	public void testExpm1Function() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.interpolation.UnivariateRealInterpolator interpolator = new org.apache.commons.math.analysis.interpolation.NevilleInterpolator();
		double[] x;
		double[] y;
		double z;
		double expected;
		double result;
		double tolerance;
		int n = 5;
		double min = -1.0;
		double max = 1.0;
		x = new double[n];
		y = new double[n];
		for (int i = 0 ; i < n ; i++) {
			x[i] = min + ((i * (max - min)) / n);
			y[i] = f.value(x[i]);
		}
		double derivativebound = java.lang.Math.E;
		org.apache.commons.math.analysis.UnivariateRealFunction p = interpolator.interpolate(x, y);
		z = 0.0;
		expected = f.value(z);
		result = p.value(z);
		tolerance = java.lang.Math.abs((derivativebound * (partialerror(x, z))));
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		z = 0.5;
		expected = f.value(z);
		result = p.value(z);
		tolerance = java.lang.Math.abs((derivativebound * (partialerror(x, z))));
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		z = -0.5;
		expected = f.value(z);
		result = p.value(z);
		tolerance = java.lang.Math.abs((derivativebound * (partialerror(x, z))));
		junit.framework.Assert.assertEquals(expected, result, tolerance);
	}

	public void testParameters() throws java.lang.Exception {
		org.apache.commons.math.analysis.interpolation.UnivariateRealInterpolator interpolator = new org.apache.commons.math.analysis.interpolation.NevilleInterpolator();
		try {
			double[] x = new double[]{ 1.0 , 2.0 , 2.0 , 4.0 };
			double[] y = new double[]{ 0.0 , 4.0 , 4.0 , 2.5 };
			org.apache.commons.math.analysis.UnivariateRealFunction p = interpolator.interpolate(x, y);
			p.value(0.0);
			junit.framework.Assert.fail("Expecting MathException - bad abscissas array");
		} catch (org.apache.commons.math.MathException ex) {
		}
	}

	protected double partialerror(double[] x, double z) throws java.lang.IllegalArgumentException {
		if ((x.length) < 1) {
			throw new java.lang.IllegalArgumentException("Interpolation array cannot be empty.");
		} 
		double out = 1;
		for (int i = 0 ; i < (x.length) ; i++) {
			out *= (z - (x[i])) / (i + 1);
		}
		return out;
	}
}

