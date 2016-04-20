package org.apache.commons.math.analysis.interpolation;


public final class SmoothingBicubicSplineInterpolatorTest {
	@org.junit.Test
	public void testPreconditions() throws org.apache.commons.math.MathException {
		double[] xval = new double[]{ 3 , 4 , 5 , 6.5 };
		double[] yval = new double[]{ -4 , -3 , -1 , 2.5 };
		double[][] zval = new double[xval.length][yval.length];
		org.apache.commons.math.analysis.interpolation.BivariateRealGridInterpolator interpolator = new org.apache.commons.math.analysis.interpolation.SmoothingBicubicSplineInterpolator();
		@java.lang.SuppressWarnings(value = "unused")
		org.apache.commons.math.analysis.BivariateRealFunction p = interpolator.interpolate(xval, yval, zval);
		double[] wxval = new double[]{ 3 , 2 , 5 , 6.5 };
		try {
			p = interpolator.interpolate(wxval, yval, zval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException e) {
		}
		double[] wyval = new double[]{ -4 , -3 , -1 , -1 };
		try {
			p = interpolator.interpolate(xval, wyval, zval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException e) {
		}
		double[][] wzval = new double[xval.length][(yval.length) + 1];
		try {
			p = interpolator.interpolate(xval, yval, wzval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.DimensionMismatchException e) {
		}
		wzval = new double[(xval.length) - 1][yval.length];
		try {
			p = interpolator.interpolate(xval, yval, wzval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.DimensionMismatchException e) {
		}
		wzval = new double[xval.length][(yval.length) - 1];
		try {
			p = interpolator.interpolate(xval, yval, wzval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.DimensionMismatchException e) {
		}
	}

	@org.junit.Test
	public void testPlane() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.BivariateRealFunction f = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				return ((2 * x) - (3 * y)) + 5;
			}
		};
		org.apache.commons.math.analysis.interpolation.BivariateRealGridInterpolator interpolator = new org.apache.commons.math.analysis.interpolation.SmoothingBicubicSplineInterpolator();
		double[] xval = new double[]{ 3 , 4 , 5 , 6.5 };
		double[] yval = new double[]{ -4 , -3 , -1 , 2 , 2.5 };
		double[][] zval = new double[xval.length][yval.length];
		for (int i = 0 ; i < (xval.length) ; i++) {
			for (int j = 0 ; j < (yval.length) ; j++) {
				zval[i][j] = f.value(xval[i], yval[j]);
			}
		}
		org.apache.commons.math.analysis.BivariateRealFunction p = interpolator.interpolate(xval, yval, zval);
		double x;
		double y;
		double expected;
		double result;
		x = 4;
		y = -3;
		expected = f.value(x, y);
		result = p.value(x, y);
		org.junit.Assert.assertEquals("On sample point", expected, result, 1.0E-15);
		x = 4.5;
		y = -1.5;
		expected = f.value(x, y);
		result = p.value(x, y);
		org.junit.Assert.assertEquals("half-way between sample points (middle of the patch)", expected, result, 0.3);
		x = 3.5;
		y = -3.5;
		expected = f.value(x, y);
		result = p.value(x, y);
		org.junit.Assert.assertEquals("half-way between sample points (border of the patch)", expected, result, 0.3);
	}

	@org.junit.Test
	public void testParaboloid() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.BivariateRealFunction f = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				return ((((2 * x) * x) - ((3 * y) * y)) + ((4 * x) * y)) - 5;
			}
		};
		org.apache.commons.math.analysis.interpolation.BivariateRealGridInterpolator interpolator = new org.apache.commons.math.analysis.interpolation.SmoothingBicubicSplineInterpolator();
		double[] xval = new double[]{ 3 , 4 , 5 , 6.5 };
		double[] yval = new double[]{ -4 , -3 , -2 , -1 , 0.5 , 2.5 };
		double[][] zval = new double[xval.length][yval.length];
		for (int i = 0 ; i < (xval.length) ; i++) {
			for (int j = 0 ; j < (yval.length) ; j++) {
				zval[i][j] = f.value(xval[i], yval[j]);
			}
		}
		org.apache.commons.math.analysis.BivariateRealFunction p = interpolator.interpolate(xval, yval, zval);
		double x;
		double y;
		double expected;
		double result;
		x = 5;
		y = 0.5;
		expected = f.value(x, y);
		result = p.value(x, y);
		org.junit.Assert.assertEquals("On sample point", expected, result, 1.0E-13);
		x = 4.5;
		y = -1.5;
		expected = f.value(x, y);
		result = p.value(x, y);
		org.junit.Assert.assertEquals("half-way between sample points (middle of the patch)", expected, result, 0.2);
		x = 3.5;
		y = -3.5;
		expected = f.value(x, y);
		result = p.value(x, y);
		org.junit.Assert.assertEquals("half-way between sample points (border of the patch)", expected, result, 0.2);
	}
}

