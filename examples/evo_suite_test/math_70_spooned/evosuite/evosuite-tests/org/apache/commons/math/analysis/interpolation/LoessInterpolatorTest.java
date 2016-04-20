package org.apache.commons.math.analysis.interpolation;


public class LoessInterpolatorTest {
	@org.junit.Test
	public void testOnOnePoint() throws org.apache.commons.math.MathException {
		double[] xval = new double[]{ 0.5 };
		double[] yval = new double[]{ 0.7 };
		double[] res = new org.apache.commons.math.analysis.interpolation.LoessInterpolator().smooth(xval, yval);
		org.junit.Assert.assertEquals(1, res.length);
		org.junit.Assert.assertEquals(0.7, res[0], 0.0);
	}

	@org.junit.Test
	public void testOnTwoPoints() throws org.apache.commons.math.MathException {
		double[] xval = new double[]{ 0.5 , 0.6 };
		double[] yval = new double[]{ 0.7 , 0.8 };
		double[] res = new org.apache.commons.math.analysis.interpolation.LoessInterpolator().smooth(xval, yval);
		org.junit.Assert.assertEquals(2, res.length);
		org.junit.Assert.assertEquals(0.7, res[0], 0.0);
		org.junit.Assert.assertEquals(0.8, res[1], 0.0);
	}

	@org.junit.Test
	public void testOnStraightLine() throws org.apache.commons.math.MathException {
		double[] xval = new double[]{ 1 , 2 , 3 , 4 , 5 };
		double[] yval = new double[]{ 2 , 4 , 6 , 8 , 10 };
		org.apache.commons.math.analysis.interpolation.LoessInterpolator li = new org.apache.commons.math.analysis.interpolation.LoessInterpolator(0.6 , 2 , 1.0E-12);
		double[] res = li.smooth(xval, yval);
		org.junit.Assert.assertEquals(5, res.length);
		for (int i = 0 ; i < 5 ; ++i) {
			org.junit.Assert.assertEquals(yval[i], res[i], 1.0E-8);
		}
	}

	@org.junit.Test
	public void testOnDistortedSine() throws org.apache.commons.math.MathException {
		int numPoints = 100;
		double[] xval = new double[numPoints];
		double[] yval = new double[numPoints];
		double xnoise = 0.1;
		double ynoise = 0.2;
		generateSineData(xval, yval, xnoise, ynoise);
		org.apache.commons.math.analysis.interpolation.LoessInterpolator li = new org.apache.commons.math.analysis.interpolation.LoessInterpolator(0.3 , 4 , 1.0E-12);
		double[] res = li.smooth(xval, yval);
		double noisyResidualSum = 0;
		double fitResidualSum = 0;
		for (int i = 0 ; i < numPoints ; ++i) {
			double expected = java.lang.Math.sin(xval[i]);
			double noisy = yval[i];
			double fit = res[i];
			noisyResidualSum += java.lang.Math.pow((noisy - expected), 2);
			fitResidualSum += java.lang.Math.pow((fit - expected), 2);
		}
		org.junit.Assert.assertTrue((fitResidualSum < noisyResidualSum));
	}

	@org.junit.Test
	public void testIncreasingBandwidthIncreasesSmoothness() throws org.apache.commons.math.MathException {
		int numPoints = 100;
		double[] xval = new double[numPoints];
		double[] yval = new double[numPoints];
		double xnoise = 0.1;
		double ynoise = 0.1;
		generateSineData(xval, yval, xnoise, ynoise);
		double[] bandwidths = new double[]{ 0.1 , 0.5 , 1.0 };
		double[] variances = new double[bandwidths.length];
		for (int i = 0 ; i < (bandwidths.length) ; i++) {
			double bw = bandwidths[i];
			org.apache.commons.math.analysis.interpolation.LoessInterpolator li = new org.apache.commons.math.analysis.interpolation.LoessInterpolator(bw , 4 , 1.0E-12);
			double[] res = li.smooth(xval, yval);
			for (int j = 1 ; j < (res.length) ; ++j) {
				variances[i] += java.lang.Math.pow(((res[j]) - (res[(j - 1)])), 2);
			}
		}
		for (int i = 1 ; i < (variances.length) ; ++i) {
			org.junit.Assert.assertTrue(((variances[i]) < (variances[(i - 1)])));
		}
	}

	@org.junit.Test
	public void testIncreasingRobustnessItersIncreasesSmoothnessWithOutliers() throws org.apache.commons.math.MathException {
		int numPoints = 100;
		double[] xval = new double[numPoints];
		double[] yval = new double[numPoints];
		double xnoise = 0.1;
		double ynoise = 0.1;
		generateSineData(xval, yval, xnoise, ynoise);
		yval[(numPoints / 3)] *= 100;
		yval[((2 * numPoints) / 3)] *= -100;
		double[] variances = new double[4];
		for (int i = 0 ; i < 4 ; i++) {
			org.apache.commons.math.analysis.interpolation.LoessInterpolator li = new org.apache.commons.math.analysis.interpolation.LoessInterpolator(0.3 , i , 1.0E-12);
			double[] res = li.smooth(xval, yval);
			for (int j = 1 ; j < (res.length) ; ++j) {
				variances[i] += java.lang.Math.abs(((res[j]) - (res[(j - 1)])));
			}
		}
		for (int i = 1 ; i < (variances.length) ; ++i) {
			org.junit.Assert.assertTrue(((variances[i]) < (variances[(i - 1)])));
		}
	}

	@org.junit.Test(expected = org.apache.commons.math.MathException.class)
	public void testUnequalSizeArguments() throws org.apache.commons.math.MathException {
		new org.apache.commons.math.analysis.interpolation.LoessInterpolator().smooth(new double[]{ 1 , 2 , 3 }, new double[]{ 1 , 2 , 3 , 4 });
	}

	@org.junit.Test(expected = org.apache.commons.math.MathException.class)
	public void testEmptyData() throws org.apache.commons.math.MathException {
		new org.apache.commons.math.analysis.interpolation.LoessInterpolator().smooth(new double[]{  }, new double[]{  });
	}

	@org.junit.Test(expected = org.apache.commons.math.MathException.class)
	public void testNonStrictlyIncreasing1() throws org.apache.commons.math.MathException {
		new org.apache.commons.math.analysis.interpolation.LoessInterpolator().smooth(new double[]{ 4 , 3 , 1 , 2 }, new double[]{ 3 , 4 , 5 , 6 });
	}

	@org.junit.Test(expected = org.apache.commons.math.MathException.class)
	public void testNonStrictlyIncreasing2() throws org.apache.commons.math.MathException {
		new org.apache.commons.math.analysis.interpolation.LoessInterpolator().smooth(new double[]{ 1 , 2 , 2 , 3 }, new double[]{ 3 , 4 , 5 , 6 });
	}

	@org.junit.Test(expected = org.apache.commons.math.MathException.class)
	public void testNotAllFiniteReal1() throws org.apache.commons.math.MathException {
		new org.apache.commons.math.analysis.interpolation.LoessInterpolator().smooth(new double[]{ 1 , 2 , java.lang.Double.NaN }, new double[]{ 3 , 4 , 5 });
	}

	@org.junit.Test(expected = org.apache.commons.math.MathException.class)
	public void testNotAllFiniteReal2() throws org.apache.commons.math.MathException {
		new org.apache.commons.math.analysis.interpolation.LoessInterpolator().smooth(new double[]{ 1 , 2 , java.lang.Double.POSITIVE_INFINITY }, new double[]{ 3 , 4 , 5 });
	}

	@org.junit.Test(expected = org.apache.commons.math.MathException.class)
	public void testNotAllFiniteReal3() throws org.apache.commons.math.MathException {
		new org.apache.commons.math.analysis.interpolation.LoessInterpolator().smooth(new double[]{ 1 , 2 , java.lang.Double.NEGATIVE_INFINITY }, new double[]{ 3 , 4 , 5 });
	}

	@org.junit.Test(expected = org.apache.commons.math.MathException.class)
	public void testNotAllFiniteReal4() throws org.apache.commons.math.MathException {
		new org.apache.commons.math.analysis.interpolation.LoessInterpolator().smooth(new double[]{ 3 , 4 , 5 }, new double[]{ 1 , 2 , java.lang.Double.NaN });
	}

	@org.junit.Test(expected = org.apache.commons.math.MathException.class)
	public void testNotAllFiniteReal5() throws org.apache.commons.math.MathException {
		new org.apache.commons.math.analysis.interpolation.LoessInterpolator().smooth(new double[]{ 3 , 4 , 5 }, new double[]{ 1 , 2 , java.lang.Double.POSITIVE_INFINITY });
	}

	@org.junit.Test(expected = org.apache.commons.math.MathException.class)
	public void testNotAllFiniteReal6() throws org.apache.commons.math.MathException {
		new org.apache.commons.math.analysis.interpolation.LoessInterpolator().smooth(new double[]{ 3 , 4 , 5 }, new double[]{ 1 , 2 , java.lang.Double.NEGATIVE_INFINITY });
	}

	@org.junit.Test(expected = org.apache.commons.math.MathException.class)
	public void testInsufficientBandwidth() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.interpolation.LoessInterpolator li = new org.apache.commons.math.analysis.interpolation.LoessInterpolator(0.1 , 3 , 1.0E-12);
		li.smooth(new double[]{ 1 , 2 , 3 , 4 , 5 , 6 , 7 , 8 , 9 , 10 , 11 , 12 }, new double[]{ 1 , 2 , 3 , 4 , 5 , 6 , 7 , 8 , 9 , 10 , 11 , 12 });
	}

	@org.junit.Test(expected = org.apache.commons.math.MathException.class)
	public void testCompletelyIncorrectBandwidth1() throws org.apache.commons.math.MathException {
		new org.apache.commons.math.analysis.interpolation.LoessInterpolator(-0.2 , 3 , 1.0E-12);
	}

	@org.junit.Test(expected = org.apache.commons.math.MathException.class)
	public void testCompletelyIncorrectBandwidth2() throws org.apache.commons.math.MathException {
		new org.apache.commons.math.analysis.interpolation.LoessInterpolator(1.1 , 3 , 1.0E-12);
	}

	@org.junit.Test
	public void testMath296withoutWeights() throws org.apache.commons.math.MathException {
		double[] xval = new double[]{ 0.1 , 0.2 , 0.3 , 0.4 , 0.5 , 0.6 , 0.7 , 0.8 , 0.9 , 1.0 , 1.1 , 1.2 , 1.3 , 1.4 , 1.5 , 1.6 , 1.7 , 1.8 , 1.9 , 2.0 };
		double[] yval = new double[]{ 0.47 , 0.48 , 0.55 , 0.56 , -0.08 , -0.04 , -0.07 , -0.07 , -0.56 , -0.46 , -0.56 , -0.52 , -3.03 , -3.08 , -3.09 , -3.04 , 3.54 , 3.46 , 3.36 , 3.35 };
		double[] yref = new double[]{ 0.461 , 0.499 , 0.541 , 0.308 , 0.175 , -0.042 , -0.072 , -0.196 , -0.311 , -0.446 , -0.557 , -1.497 , -2.133 , -3.08 , -3.09 , -0.621 , 0.982 , 3.449 , 3.389 , 3.336 };
		org.apache.commons.math.analysis.interpolation.LoessInterpolator li = new org.apache.commons.math.analysis.interpolation.LoessInterpolator(0.3 , 4 , 1.0E-12);
		double[] res = li.smooth(xval, yval);
		org.junit.Assert.assertEquals(xval.length, res.length);
		for (int i = 0 ; i < (res.length) ; ++i) {
			org.junit.Assert.assertEquals(yref[i], res[i], 0.02);
		}
	}

	private void generateSineData(double[] xval, double[] yval, double xnoise, double ynoise) {
		double dx = (2 * (java.lang.Math.PI)) / (xval.length);
		double x = 0;
		for (int i = 0 ; i < (xval.length) ; ++i) {
			xval[i] = x;
			yval[i] = (java.lang.Math.sin(x)) + (((2 * (java.lang.Math.random())) - 1) * ynoise);
			x += dx * (1 + (((2 * (java.lang.Math.random())) - 1) * xnoise));
		}
	}
}

