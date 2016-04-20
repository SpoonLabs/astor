package org.apache.commons.math.analysis.interpolation;


public final class BicubicSplineInterpolatingFunctionTest {
	@org.junit.Test
	public void testPreconditions() throws org.apache.commons.math.MathException {
		double[] xval = new double[]{ 3 , 4 , 5 , 6.5 };
		double[] yval = new double[]{ -4 , -3 , -1 , 2.5 };
		double[][] zval = new double[xval.length][yval.length];
		@java.lang.SuppressWarnings(value = "unused")
		org.apache.commons.math.analysis.BivariateRealFunction bcf = new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction(xval , yval , zval , zval , zval , zval);
		double[] wxval = new double[]{ 3 , 2 , 5 , 6.5 };
		try {
			bcf = new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction(wxval , yval , zval , zval , zval , zval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException e) {
		}
		double[] wyval = new double[]{ -4 , -1 , -1 , 2.5 };
		try {
			bcf = new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction(xval , wyval , zval , zval , zval , zval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException e) {
		}
		double[][] wzval = new double[xval.length][(yval.length) - 1];
		try {
			bcf = new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction(xval , yval , wzval , zval , zval , zval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.DimensionMismatchException e) {
		}
		try {
			bcf = new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction(xval , yval , zval , wzval , zval , zval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.DimensionMismatchException e) {
		}
		try {
			bcf = new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction(xval , yval , zval , zval , wzval , zval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.DimensionMismatchException e) {
		}
		try {
			bcf = new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction(xval , yval , zval , zval , zval , wzval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.DimensionMismatchException e) {
		}
		wzval = new double[(xval.length) - 1][yval.length];
		try {
			bcf = new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction(xval , yval , wzval , zval , zval , zval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.DimensionMismatchException e) {
		}
		try {
			bcf = new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction(xval , yval , zval , wzval , zval , zval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.DimensionMismatchException e) {
		}
		try {
			bcf = new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction(xval , yval , zval , zval , wzval , zval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.DimensionMismatchException e) {
		}
		try {
			bcf = new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction(xval , yval , zval , zval , zval , wzval);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.DimensionMismatchException e) {
		}
	}

	@org.junit.Test
	public void testPlane() throws org.apache.commons.math.MathException {
		double[] xval = new double[]{ 3 , 4 , 5 , 6.5 };
		double[] yval = new double[]{ -4 , -3 , -1 , 2 , 2.5 };
		org.apache.commons.math.analysis.BivariateRealFunction f = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				return ((2 * x) - (3 * y)) + 5;
			}
		};
		double[][] zval = new double[xval.length][yval.length];
		for (int i = 0 ; i < (xval.length) ; i++) {
			for (int j = 0 ; j < (yval.length) ; j++) {
				zval[i][j] = f.value(xval[i], yval[j]);
			}
		}
		double[][] dZdX = new double[xval.length][yval.length];
		for (int i = 0 ; i < (xval.length) ; i++) {
			for (int j = 0 ; j < (yval.length) ; j++) {
				dZdX[i][j] = 2;
			}
		}
		double[][] dZdY = new double[xval.length][yval.length];
		for (int i = 0 ; i < (xval.length) ; i++) {
			for (int j = 0 ; j < (yval.length) ; j++) {
				dZdY[i][j] = -3;
			}
		}
		double[][] dZdXdY = new double[xval.length][yval.length];
		for (int i = 0 ; i < (xval.length) ; i++) {
			for (int j = 0 ; j < (yval.length) ; j++) {
				dZdXdY[i][j] = 0;
			}
		}
		org.apache.commons.math.analysis.BivariateRealFunction bcf = new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction(xval , yval , zval , dZdX , dZdY , dZdXdY);
		double x;
		double y;
		double expected;
		double result;
		x = 4;
		y = -3;
		expected = f.value(x, y);
		result = bcf.value(x, y);
		org.junit.Assert.assertEquals("On sample point", expected, result, 1.0E-15);
		x = 4.5;
		y = -1.5;
		expected = f.value(x, y);
		result = bcf.value(x, y);
		org.junit.Assert.assertEquals("Half-way between sample points (middle of the patch)", expected, result, 0.3);
		x = 3.5;
		y = -3.5;
		expected = f.value(x, y);
		result = bcf.value(x, y);
		org.junit.Assert.assertEquals("Half-way between sample points (border of the patch)", expected, result, 0.3);
	}

	@org.junit.Test
	public void testParaboloid() throws org.apache.commons.math.MathException {
		double[] xval = new double[]{ 3 , 4 , 5 , 6.5 };
		double[] yval = new double[]{ -4 , -3 , -1 , 2 , 2.5 };
		org.apache.commons.math.analysis.BivariateRealFunction f = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				return ((((2 * x) * x) - ((3 * y) * y)) + ((4 * x) * y)) - 5;
			}
		};
		double[][] zval = new double[xval.length][yval.length];
		for (int i = 0 ; i < (xval.length) ; i++) {
			for (int j = 0 ; j < (yval.length) ; j++) {
				zval[i][j] = f.value(xval[i], yval[j]);
			}
		}
		double[][] dZdX = new double[xval.length][yval.length];
		org.apache.commons.math.analysis.BivariateRealFunction dfdX = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				return 4 * (x + y);
			}
		};
		for (int i = 0 ; i < (xval.length) ; i++) {
			for (int j = 0 ; j < (yval.length) ; j++) {
				dZdX[i][j] = dfdX.value(xval[i], yval[j]);
			}
		}
		double[][] dZdY = new double[xval.length][yval.length];
		org.apache.commons.math.analysis.BivariateRealFunction dfdY = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				return (4 * x) - (6 * y);
			}
		};
		for (int i = 0 ; i < (xval.length) ; i++) {
			for (int j = 0 ; j < (yval.length) ; j++) {
				dZdY[i][j] = dfdY.value(xval[i], yval[j]);
			}
		}
		double[][] dZdXdY = new double[xval.length][yval.length];
		for (int i = 0 ; i < (xval.length) ; i++) {
			for (int j = 0 ; j < (yval.length) ; j++) {
				dZdXdY[i][j] = 4;
			}
		}
		org.apache.commons.math.analysis.BivariateRealFunction bcf = new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction(xval , yval , zval , dZdX , dZdY , dZdXdY);
		double x;
		double y;
		double expected;
		double result;
		x = 4;
		y = -3;
		expected = f.value(x, y);
		result = bcf.value(x, y);
		org.junit.Assert.assertEquals("On sample point", expected, result, 1.0E-15);
		x = 4.5;
		y = -1.5;
		expected = f.value(x, y);
		result = bcf.value(x, y);
		org.junit.Assert.assertEquals("Half-way between sample points (middle of the patch)", expected, result, 2);
		x = 3.5;
		y = -3.5;
		expected = f.value(x, y);
		result = bcf.value(x, y);
		org.junit.Assert.assertEquals("Half-way between sample points (border of the patch)", expected, result, 2);
	}

	@org.junit.Test
	public void testSplinePartialDerivatives() throws org.apache.commons.math.MathException {
		final int N = 4;
		final double[] coeff = new double[16];
		for (int i = 0 ; i < N ; i++) {
			for (int j = 0 ; j < N ; j++) {
				final int index = i + (N * j);
				coeff[(i + (N * j))] = (i + 1) * (j + 2);
			}
		}
		final org.apache.commons.math.analysis.interpolation.BicubicSplineFunction f = new org.apache.commons.math.analysis.interpolation.BicubicSplineFunction(coeff);
		org.apache.commons.math.analysis.BivariateRealFunction derivative;
		final double x = 0.435;
		final double y = 0.776;
		final double tol = 1.0E-13;
		derivative = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				final double x2 = x * x;
				final double y2 = y * y;
				final double y3 = y2 * y;
				final double yFactor = ((2 + (3 * y)) + (4 * y2)) + (5 * y3);
				return yFactor * ((2 + (6 * x)) + (12 * x2));
			}
		};
		org.junit.Assert.assertEquals("dFdX", derivative.value(x, y), f.partialDerivativeX().value(x, y), tol);
		derivative = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				final double x2 = x * x;
				final double x3 = x2 * x;
				final double y2 = y * y;
				final double xFactor = ((1 + (2 * x)) + (3 * x2)) + (4 * x3);
				return xFactor * ((3 + (8 * y)) + (15 * y2));
			}
		};
		org.junit.Assert.assertEquals("dFdY", derivative.value(x, y), f.partialDerivativeY().value(x, y), tol);
		derivative = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				final double y2 = y * y;
				final double y3 = y2 * y;
				final double yFactor = ((2 + (3 * y)) + (4 * y2)) + (5 * y3);
				return yFactor * (6 + (24 * x));
			}
		};
		org.junit.Assert.assertEquals("d2FdX2", derivative.value(x, y), f.partialDerivativeXX().value(x, y), tol);
		derivative = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				final double x2 = x * x;
				final double x3 = x2 * x;
				final double xFactor = ((1 + (2 * x)) + (3 * x2)) + (4 * x3);
				return xFactor * (8 + (30 * y));
			}
		};
		org.junit.Assert.assertEquals("d2FdY2", derivative.value(x, y), f.partialDerivativeYY().value(x, y), tol);
		derivative = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				final double x2 = x * x;
				final double y2 = y * y;
				final double yFactor = (3 + (8 * y)) + (15 * y2);
				return yFactor * ((2 + (6 * x)) + (12 * x2));
			}
		};
		org.junit.Assert.assertEquals("d2FdXdY", derivative.value(x, y), f.partialDerivativeXY().value(x, y), tol);
	}

	@org.junit.Test
	public void testMatchingPartialDerivatives() throws org.apache.commons.math.MathException {
		final int sz = 21;
		double[] val = new double[sz];
		final double delta = 1.0 / (sz - 1);
		for (int i = 0 ; i < sz ; i++) {
			val[i] = i * delta;
		}
		org.apache.commons.math.analysis.BivariateRealFunction f = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				final double x2 = x * x;
				final double x3 = x2 * x;
				final double y2 = y * y;
				final double y3 = y2 * y;
				return ((((((((5 - (3 * x)) + (2 * y)) - (x * y)) + (2 * x2)) - (3 * y2)) + ((4 * x2) * y)) - (x * y2)) - (3 * x3)) + y3;
			}
		};
		double[][] fval = new double[sz][sz];
		for (int i = 0 ; i < sz ; i++) {
			for (int j = 0 ; j < sz ; j++) {
				fval[i][j] = f.value(val[i], val[j]);
			}
		}
		double[][] dFdX = new double[sz][sz];
		org.apache.commons.math.analysis.BivariateRealFunction dfdX = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				final double x2 = x * x;
				final double y2 = y * y;
				return (((((-3) - y) + (4 * x)) + ((8 * x) * y)) - y2) - (9 * x2);
			}
		};
		for (int i = 0 ; i < sz ; i++) {
			for (int j = 0 ; j < sz ; j++) {
				dFdX[i][j] = dfdX.value(val[i], val[j]);
			}
		}
		double[][] dFdY = new double[sz][sz];
		org.apache.commons.math.analysis.BivariateRealFunction dfdY = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				final double x2 = x * x;
				final double y2 = y * y;
				return ((((2 - x) - (6 * y)) + (4 * x2)) - ((2 * x) * y)) + (3 * y2);
			}
		};
		for (int i = 0 ; i < sz ; i++) {
			for (int j = 0 ; j < sz ; j++) {
				dFdY[i][j] = dfdY.value(val[i], val[j]);
			}
		}
		double[][] d2FdXdY = new double[sz][sz];
		org.apache.commons.math.analysis.BivariateRealFunction d2fdXdY = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				return ((-1) + (8 * x)) - (2 * y);
			}
		};
		for (int i = 0 ; i < sz ; i++) {
			for (int j = 0 ; j < sz ; j++) {
				d2FdXdY[i][j] = d2fdXdY.value(val[i], val[j]);
			}
		}
		org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction bcf = new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction(val , val , fval , dFdX , dFdY , d2FdXdY);
		double x;
		double y;
		double expected;
		double result;
		final double tol = 1.0E-12;
		for (int i = 0 ; i < sz ; i++) {
			x = val[i];
			for (int j = 0 ; j < sz ; j++) {
				y = val[j];
				expected = dfdX.value(x, y);
				result = bcf.partialDerivativeX(x, y);
				org.junit.Assert.assertEquals((((x + " ") + y) + " dFdX"), expected, result, tol);
				expected = dfdY.value(x, y);
				result = bcf.partialDerivativeY(x, y);
				org.junit.Assert.assertEquals((((x + " ") + y) + " dFdY"), expected, result, tol);
				expected = d2fdXdY.value(x, y);
				result = bcf.partialDerivativeXY(x, y);
				org.junit.Assert.assertEquals((((x + " ") + y) + " d2FdXdY"), expected, result, tol);
			}
		}
	}
}

