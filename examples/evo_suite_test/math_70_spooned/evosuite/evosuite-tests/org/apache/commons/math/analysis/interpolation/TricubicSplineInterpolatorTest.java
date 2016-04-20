package org.apache.commons.math.analysis.interpolation;


public final class TricubicSplineInterpolatorTest {
	@org.junit.Test
	public void testPreconditions() throws org.apache.commons.math.MathException {
		double[] xval = new double[]{ 3 , 4 , 5 , 6.5 };
		double[] yval = new double[]{ -4 , -3 , -1 , 2.5 };
		double[] zval = new double[]{ -12 , -8 , -5.5 , -3 , 0 , 2.5 };
		double[][][] fval = new double[xval.length][yval.length][zval.length];
		org.apache.commons.math.analysis.interpolation.TrivariateRealGridInterpolator interpolator = new org.apache.commons.math.analysis.interpolation.TricubicSplineInterpolator();
		@java.lang.SuppressWarnings(value = "unused")
		org.apache.commons.math.analysis.TrivariateRealFunction p = interpolator.interpolate(xval, yval, zval, fval);
		double[] wxval = new double[]{ 3 , 2 , 5 , 6.5 };
		try {
			p = interpolator.interpolate(wxval, yval, zval, fval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException e) {
		}
		double[] wyval = new double[]{ -4 , -3 , -1 , -1 };
		try {
			p = interpolator.interpolate(xval, wyval, zval, fval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException e) {
		}
		double[] wzval = new double[]{ -12 , -8 , -5.5 , -3 , -4 , 2.5 };
		try {
			p = interpolator.interpolate(xval, yval, wzval, fval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException e) {
		}
		double[][][] wfval = new double[xval.length][(yval.length) + 1][zval.length];
		try {
			p = interpolator.interpolate(xval, yval, zval, wfval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.DimensionMismatchException e) {
		}
		wfval = new double[(xval.length) - 1][yval.length][zval.length];
		try {
			p = interpolator.interpolate(xval, yval, zval, wfval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.DimensionMismatchException e) {
		}
		wfval = new double[xval.length][yval.length][(zval.length) - 1];
		try {
			p = interpolator.interpolate(xval, yval, zval, wfval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.DimensionMismatchException e) {
		}
	}

	@org.junit.Test
	public void testPlane() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.TrivariateRealFunction f = new org.apache.commons.math.analysis.TrivariateRealFunction() {
			public double value(double x, double y, double z) {
				return (((2 * x) - (3 * y)) - z) + 5;
			}
		};
		org.apache.commons.math.analysis.interpolation.TrivariateRealGridInterpolator interpolator = new org.apache.commons.math.analysis.interpolation.TricubicSplineInterpolator();
		double[] xval = new double[]{ 3 , 4 , 5 , 6.5 };
		double[] yval = new double[]{ -4 , -3 , -1 , 2 , 2.5 };
		double[] zval = new double[]{ -12 , -8 , -5.5 , -3 , 0 , 2.5 };
		double[][][] fval = new double[xval.length][yval.length][zval.length];
		for (int i = 0 ; i < (xval.length) ; i++) {
			for (int j = 0 ; j < (yval.length) ; j++) {
				for (int k = 0 ; k < (zval.length) ; k++) {
					fval[i][j][k] = f.value(xval[i], yval[j], zval[k]);
				}
			}
		}
		org.apache.commons.math.analysis.TrivariateRealFunction p = interpolator.interpolate(xval, yval, zval, fval);
		double x;
		double y;
		double z;
		double expected;
		double result;
		x = 4;
		y = -3;
		z = 0;
		expected = f.value(x, y, z);
		result = p.value(x, y, z);
		org.junit.Assert.assertEquals("On sample point", expected, result, 1.0E-15);
		x = 4.5;
		y = -1.5;
		z = -4.25;
		expected = f.value(x, y, z);
		result = p.value(x, y, z);
		org.junit.Assert.assertEquals("half-way between sample points (middle of the patch)", expected, result, 0.3);
		x = 3.5;
		y = -3.5;
		z = -10;
		expected = f.value(x, y, z);
		result = p.value(x, y, z);
		org.junit.Assert.assertEquals("half-way between sample points (border of the patch)", expected, result, 0.3);
	}

	@org.junit.Test
	public void testWave() throws org.apache.commons.math.MathException {
		double[] xval = new double[]{ 3 , 4 , 5 , 6.5 };
		double[] yval = new double[]{ -4 , -3 , -1 , 2 , 2.5 };
		double[] zval = new double[]{ -12 , -8 , -5.5 , -3 , 0 , 4 };
		final double a = 0.2;
		final double omega = 0.5;
		final double kx = 2;
		final double ky = 1;
		org.apache.commons.math.analysis.TrivariateRealFunction f = new org.apache.commons.math.analysis.TrivariateRealFunction() {
			public double value(double x, double y, double z) {
				return a * (java.lang.Math.cos((((omega * z) - (kx * x)) - (ky * y))));
			}
		};
		double[][][] fval = new double[xval.length][yval.length][zval.length];
		for (int i = 0 ; i < (xval.length) ; i++) {
			for (int j = 0 ; j < (yval.length) ; j++) {
				for (int k = 0 ; k < (zval.length) ; k++) {
					fval[i][j][k] = f.value(xval[i], yval[j], zval[k]);
				}
			}
		}
		org.apache.commons.math.analysis.interpolation.TrivariateRealGridInterpolator interpolator = new org.apache.commons.math.analysis.interpolation.TricubicSplineInterpolator();
		org.apache.commons.math.analysis.TrivariateRealFunction p = interpolator.interpolate(xval, yval, zval, fval);
		double x;
		double y;
		double z;
		double expected;
		double result;
		x = 4;
		y = -3;
		z = 0;
		expected = f.value(x, y, z);
		result = p.value(x, y, z);
		org.junit.Assert.assertEquals("On sample point", expected, result, 1.0E-12);
		x = 4.5;
		y = -1.5;
		z = -4.25;
		expected = f.value(x, y, z);
		result = p.value(x, y, z);
		org.junit.Assert.assertEquals("Half-way between sample points (middle of the patch)", expected, result, 0.1);
		x = 3.5;
		y = -3.5;
		z = -10;
		expected = f.value(x, y, z);
		result = p.value(x, y, z);
		org.junit.Assert.assertEquals("Half-way between sample points (border of the patch)", expected, result, 0.1);
	}
}

