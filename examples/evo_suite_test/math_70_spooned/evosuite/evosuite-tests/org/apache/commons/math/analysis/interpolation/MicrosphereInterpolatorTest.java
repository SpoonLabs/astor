package org.apache.commons.math.analysis.interpolation;


public final class MicrosphereInterpolatorTest {
	@org.junit.Test
	public void testLinearFunction2D() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.MultivariateRealFunction f = new org.apache.commons.math.analysis.MultivariateRealFunction() {
			public double value(double[] x) {
				if ((x.length) != 2) {
					throw new java.lang.IllegalArgumentException();
				} 
				return ((2 * (x[0])) - (3 * (x[1]))) + 5;
			}
		};
		org.apache.commons.math.analysis.interpolation.MultivariateRealInterpolator interpolator = new org.apache.commons.math.analysis.interpolation.MicrosphereInterpolator();
		final int n = 9;
		final int dim = 2;
		double[][] x = new double[n][dim];
		double[] y = new double[n];
		int index = 0;
		for (int i = -1 ; i <= 1 ; i++) {
			for (int j = -1 ; j <= 1 ; j++) {
				x[index][0] = i;
				x[index][1] = j;
				y[index] = f.value(x[index]);
				++index;
			}
		}
		org.apache.commons.math.analysis.MultivariateRealFunction p = interpolator.interpolate(x, y);
		double[] c = new double[dim];
		double expected;
		double result;
		c[0] = 0;
		c[1] = 0;
		expected = f.value(c);
		result = p.value(c);
		org.junit.Assert.assertEquals("On sample point", expected, result, java.lang.Math.ulp(1.0));
		c[0] = 0 + 1.0E-5;
		c[1] = 1 - 1.0E-5;
		expected = f.value(c);
		result = p.value(c);
		org.junit.Assert.assertEquals("1e-5 away from sample point", expected, result, 1.0E-4);
	}

	@org.junit.Test
	public void testParaboloid2D() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.MultivariateRealFunction f = new org.apache.commons.math.analysis.MultivariateRealFunction() {
			public double value(double[] x) {
				if ((x.length) != 2) {
					throw new java.lang.IllegalArgumentException();
				} 
				return ((((2 * (x[0])) * (x[0])) - ((3 * (x[1])) * (x[1]))) + ((4 * (x[0])) * (x[1]))) - 5;
			}
		};
		org.apache.commons.math.analysis.interpolation.MultivariateRealInterpolator interpolator = new org.apache.commons.math.analysis.interpolation.MicrosphereInterpolator();
		final int n = 121;
		final int dim = 2;
		double[][] x = new double[n][dim];
		double[] y = new double[n];
		int index = 0;
		for (int i = -10 ; i <= 10 ; i += 2) {
			for (int j = -10 ; j <= 10 ; j += 2) {
				x[index][0] = i;
				x[index][1] = j;
				y[index] = f.value(x[index]);
				++index;
			}
		}
		org.apache.commons.math.analysis.MultivariateRealFunction p = interpolator.interpolate(x, y);
		double[] c = new double[dim];
		double expected;
		double result;
		c[0] = 0;
		c[1] = 0;
		expected = f.value(c);
		result = p.value(c);
		org.junit.Assert.assertEquals("On sample point", expected, result, java.lang.Math.ulp(1.0));
		c[0] = 2 + 1.0E-5;
		c[1] = 2 - 1.0E-5;
		expected = f.value(c);
		result = p.value(c);
		org.junit.Assert.assertEquals("1e-5 away from sample point", expected, result, 0.001);
	}
}

