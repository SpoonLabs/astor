package org.apache.commons.math.stat.regression;


public final class SimpleRegressionTest extends junit.framework.TestCase {
	private double[][] data = new double[][]{ new double[]{ 0.1 , 0.2 } , new double[]{ 338.8 , 337.4 } , new double[]{ 118.1 , 118.2 } , new double[]{ 888.0 , 884.6 } , new double[]{ 9.2 , 10.1 } , new double[]{ 228.1 , 226.5 } , new double[]{ 668.5 , 666.3 } , new double[]{ 998.5 , 996.3 } , new double[]{ 449.1 , 448.6 } , new double[]{ 778.9 , 777.0 } , new double[]{ 559.2 , 558.2 } , new double[]{ 0.3 , 0.4 } , new double[]{ 0.1 , 0.6 } , new double[]{ 778.1 , 775.5 } , new double[]{ 668.8 , 666.9 } , new double[]{ 339.3 , 338.0 } , new double[]{ 448.9 , 447.5 } , new double[]{ 10.8 , 11.6 } , new double[]{ 557.7 , 556.0 } , new double[]{ 228.3 , 228.1 } , new double[]{ 998.0 , 995.8 } , new double[]{ 888.8 , 887.6 } , new double[]{ 119.6 , 120.2 } , new double[]{ 0.3 , 0.3 } , new double[]{ 0.6 , 0.3 } , new double[]{ 557.6 , 556.8 } , new double[]{ 339.3 , 339.1 } , new double[]{ 888.0 , 887.2 } , new double[]{ 998.5 , 999.0 } , new double[]{ 778.9 , 779.0 } , new double[]{ 10.2 , 11.1 } , new double[]{ 117.6 , 118.3 } , new double[]{ 228.9 , 229.2 } , new double[]{ 668.4 , 669.1 } , new double[]{ 449.2 , 448.9 } , new double[]{ 0.2 , 0.5 } };

	private double[][] corrData = new double[][]{ new double[]{ 101.0 , 99.2 } , new double[]{ 100.1 , 99.0 } , new double[]{ 100.0 , 100.0 } , new double[]{ 90.6 , 111.6 } , new double[]{ 86.5 , 122.2 } , new double[]{ 89.7 , 117.6 } , new double[]{ 90.6 , 121.1 } , new double[]{ 82.8 , 136.0 } , new double[]{ 70.1 , 154.2 } , new double[]{ 65.4 , 153.6 } , new double[]{ 61.3 , 158.5 } , new double[]{ 62.5 , 140.6 } , new double[]{ 63.6 , 136.2 } , new double[]{ 52.6 , 168.0 } , new double[]{ 59.7 , 154.3 } , new double[]{ 59.5 , 149.0 } , new double[]{ 61.3 , 165.5 } };

	private double[][] infData = new double[][]{ new double[]{ 15.6 , 5.2 } , new double[]{ 26.8 , 6.1 } , new double[]{ 37.8 , 8.7 } , new double[]{ 36.4 , 8.5 } , new double[]{ 35.5 , 8.8 } , new double[]{ 18.6 , 4.9 } , new double[]{ 15.3 , 4.5 } , new double[]{ 7.9 , 2.5 } , new double[]{ 0.0 , 1.1 } };

	private double[][] removeSingle = new double[][]{ infData[1] };

	private double[][] removeMultiple = new double[][]{ infData[1] , infData[2] };

	private double removeX = infData[0][0];

	private double removeY = infData[0][1];

	private double[][] infData2 = new double[][]{ new double[]{ 1 , 1 } , new double[]{ 2 , 0 } , new double[]{ 3 , 5 } , new double[]{ 4 , 2 } , new double[]{ 5 , -1 } , new double[]{ 6 , 12 } };

	public SimpleRegressionTest(java.lang.String name) {
		super(name);
	}

	public void testNorris() {
		org.apache.commons.math.stat.regression.SimpleRegression regression = new org.apache.commons.math.stat.regression.SimpleRegression();
		for (int i = 0 ; i < (data.length) ; i++) {
			regression.addData(data[i][1], data[i][0]);
		}
		junit.framework.Assert.assertEquals("slope", 1.00211681802045, regression.getSlope(), 1.0E-11);
		junit.framework.Assert.assertEquals("slope std err", 4.29796848199937E-4, regression.getSlopeStdErr(), 1.0E-11);
		junit.framework.Assert.assertEquals("number of observations", 36, regression.getN());
		junit.framework.Assert.assertEquals("intercept", -0.262323073774029, regression.getIntercept(), 1.0E-11);
		junit.framework.Assert.assertEquals("std err intercept", 0.232818234301152, regression.getInterceptStdErr(), 1.0E-11);
		junit.framework.Assert.assertEquals("r-square", 0.999993745883712, regression.getRSquare(), 1.0E-11);
		junit.framework.Assert.assertEquals("SSR", 4255954.13232369, regression.getRegressionSumSquares(), 1.0E-8);
		junit.framework.Assert.assertEquals("MSE", 0.782864662630069, regression.getMeanSquareError(), 1.0E-9);
		junit.framework.Assert.assertEquals("SSE", 26.6173985294224, regression.getSumSquaredErrors(), 1.0E-8);
		junit.framework.Assert.assertEquals("predict(0)", -0.262323073774029, regression.predict(0), 1.0E-11);
		junit.framework.Assert.assertEquals("predict(1)", (1.00211681802045 - 0.262323073774029), regression.predict(1), 1.0E-11);
	}

	public void testCorr() {
		org.apache.commons.math.stat.regression.SimpleRegression regression = new org.apache.commons.math.stat.regression.SimpleRegression();
		regression.addData(corrData);
		junit.framework.Assert.assertEquals("number of observations", 17, regression.getN());
		junit.framework.Assert.assertEquals("r-square", 0.896123, regression.getRSquare(), 1.0E-5);
		junit.framework.Assert.assertEquals("r", -0.94663767742, regression.getR(), 1.0E-10);
	}

	public void testNaNs() {
		org.apache.commons.math.stat.regression.SimpleRegression regression = new org.apache.commons.math.stat.regression.SimpleRegression();
		junit.framework.Assert.assertTrue("intercept not NaN", java.lang.Double.isNaN(regression.getIntercept()));
		junit.framework.Assert.assertTrue("slope not NaN", java.lang.Double.isNaN(regression.getSlope()));
		junit.framework.Assert.assertTrue("slope std err not NaN", java.lang.Double.isNaN(regression.getSlopeStdErr()));
		junit.framework.Assert.assertTrue("intercept std err not NaN", java.lang.Double.isNaN(regression.getInterceptStdErr()));
		junit.framework.Assert.assertTrue("MSE not NaN", java.lang.Double.isNaN(regression.getMeanSquareError()));
		junit.framework.Assert.assertTrue("e not NaN", java.lang.Double.isNaN(regression.getR()));
		junit.framework.Assert.assertTrue("r-square not NaN", java.lang.Double.isNaN(regression.getRSquare()));
		junit.framework.Assert.assertTrue("RSS not NaN", java.lang.Double.isNaN(regression.getRegressionSumSquares()));
		junit.framework.Assert.assertTrue("SSE not NaN", java.lang.Double.isNaN(regression.getSumSquaredErrors()));
		junit.framework.Assert.assertTrue("SSTO not NaN", java.lang.Double.isNaN(regression.getTotalSumSquares()));
		junit.framework.Assert.assertTrue("predict not NaN", java.lang.Double.isNaN(regression.predict(0)));
		regression.addData(1, 2);
		regression.addData(1, 3);
		junit.framework.Assert.assertTrue("intercept not NaN", java.lang.Double.isNaN(regression.getIntercept()));
		junit.framework.Assert.assertTrue("slope not NaN", java.lang.Double.isNaN(regression.getSlope()));
		junit.framework.Assert.assertTrue("slope std err not NaN", java.lang.Double.isNaN(regression.getSlopeStdErr()));
		junit.framework.Assert.assertTrue("intercept std err not NaN", java.lang.Double.isNaN(regression.getInterceptStdErr()));
		junit.framework.Assert.assertTrue("MSE not NaN", java.lang.Double.isNaN(regression.getMeanSquareError()));
		junit.framework.Assert.assertTrue("e not NaN", java.lang.Double.isNaN(regression.getR()));
		junit.framework.Assert.assertTrue("r-square not NaN", java.lang.Double.isNaN(regression.getRSquare()));
		junit.framework.Assert.assertTrue("RSS not NaN", java.lang.Double.isNaN(regression.getRegressionSumSquares()));
		junit.framework.Assert.assertTrue("SSE not NaN", java.lang.Double.isNaN(regression.getSumSquaredErrors()));
		junit.framework.Assert.assertTrue("predict not NaN", java.lang.Double.isNaN(regression.predict(0)));
		junit.framework.Assert.assertTrue("SSTO NaN", !(java.lang.Double.isNaN(regression.getTotalSumSquares())));
		regression = new org.apache.commons.math.stat.regression.SimpleRegression();
		regression.addData(1, 2);
		regression.addData(3, 3);
		junit.framework.Assert.assertTrue("interceptNaN", !(java.lang.Double.isNaN(regression.getIntercept())));
		junit.framework.Assert.assertTrue("slope NaN", !(java.lang.Double.isNaN(regression.getSlope())));
		junit.framework.Assert.assertTrue("slope std err not NaN", java.lang.Double.isNaN(regression.getSlopeStdErr()));
		junit.framework.Assert.assertTrue("intercept std err not NaN", java.lang.Double.isNaN(regression.getInterceptStdErr()));
		junit.framework.Assert.assertTrue("MSE not NaN", java.lang.Double.isNaN(regression.getMeanSquareError()));
		junit.framework.Assert.assertTrue("r NaN", !(java.lang.Double.isNaN(regression.getR())));
		junit.framework.Assert.assertTrue("r-square NaN", !(java.lang.Double.isNaN(regression.getRSquare())));
		junit.framework.Assert.assertTrue("RSS NaN", !(java.lang.Double.isNaN(regression.getRegressionSumSquares())));
		junit.framework.Assert.assertTrue("SSE NaN", !(java.lang.Double.isNaN(regression.getSumSquaredErrors())));
		junit.framework.Assert.assertTrue("SSTO NaN", !(java.lang.Double.isNaN(regression.getTotalSumSquares())));
		junit.framework.Assert.assertTrue("predict NaN", !(java.lang.Double.isNaN(regression.predict(0))));
		regression.addData(1, 4);
		junit.framework.Assert.assertTrue("MSE NaN", !(java.lang.Double.isNaN(regression.getMeanSquareError())));
		junit.framework.Assert.assertTrue("slope std err NaN", !(java.lang.Double.isNaN(regression.getSlopeStdErr())));
		junit.framework.Assert.assertTrue("intercept std err NaN", !(java.lang.Double.isNaN(regression.getInterceptStdErr())));
	}

	public void testClear() {
		org.apache.commons.math.stat.regression.SimpleRegression regression = new org.apache.commons.math.stat.regression.SimpleRegression();
		regression.addData(corrData);
		junit.framework.Assert.assertEquals("number of observations", 17, regression.getN());
		regression.clear();
		junit.framework.Assert.assertEquals("number of observations", 0, regression.getN());
		regression.addData(corrData);
		junit.framework.Assert.assertEquals("r-square", 0.896123, regression.getRSquare(), 1.0E-5);
		regression.addData(data);
		junit.framework.Assert.assertEquals("number of observations", 53, regression.getN());
	}

	public void testInference() throws java.lang.Exception {
		org.apache.commons.math.stat.regression.SimpleRegression regression = new org.apache.commons.math.stat.regression.SimpleRegression();
		regression.addData(infData);
		junit.framework.Assert.assertEquals("slope std err", 0.011448491, regression.getSlopeStdErr(), 1.0E-10);
		junit.framework.Assert.assertEquals("std err intercept", 0.286036932, regression.getInterceptStdErr(), 1.0E-8);
		junit.framework.Assert.assertEquals("significance", 4.596E-7, regression.getSignificance(), 1.0E-8);
		junit.framework.Assert.assertEquals("slope conf interval half-width", 0.0270713794287, regression.getSlopeConfidenceInterval(), 1.0E-8);
		regression = new org.apache.commons.math.stat.regression.SimpleRegression();
		regression.addData(infData2);
		junit.framework.Assert.assertEquals("slope std err", 1.07260253, regression.getSlopeStdErr(), 1.0E-8);
		junit.framework.Assert.assertEquals("std err intercept", 4.17718672, regression.getInterceptStdErr(), 1.0E-8);
		junit.framework.Assert.assertEquals("significance", 0.261829133982, regression.getSignificance(), 1.0E-11);
		junit.framework.Assert.assertEquals("slope conf interval half-width", 2.97802204827, regression.getSlopeConfidenceInterval(), 1.0E-8);
		junit.framework.Assert.assertTrue("tighter means wider", ((regression.getSlopeConfidenceInterval()) < (regression.getSlopeConfidenceInterval(0.01))));
		try {
			regression.getSlopeConfidenceInterval(1);
			junit.framework.Assert.fail("expecting IllegalArgumentException for alpha = 1");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testPerfect() throws java.lang.Exception {
		org.apache.commons.math.stat.regression.SimpleRegression regression = new org.apache.commons.math.stat.regression.SimpleRegression();
		int n = 100;
		for (int i = 0 ; i < n ; i++) {
			regression.addData((((double)(i)) / (n - 1)), i);
		}
		junit.framework.Assert.assertEquals(0.0, regression.getSignificance(), 1.0E-5);
		junit.framework.Assert.assertTrue(((regression.getSlope()) > 0.0));
		junit.framework.Assert.assertTrue(((regression.getSumSquaredErrors()) >= 0.0));
	}

	public void testPerfectNegative() throws java.lang.Exception {
		org.apache.commons.math.stat.regression.SimpleRegression regression = new org.apache.commons.math.stat.regression.SimpleRegression();
		int n = 100;
		for (int i = 0 ; i < n ; i++) {
			regression.addData(((-((double)(i))) / (n - 1)), i);
		}
		junit.framework.Assert.assertEquals(0.0, regression.getSignificance(), 1.0E-5);
		junit.framework.Assert.assertTrue(((regression.getSlope()) < 0.0));
	}

	public void testRandom() throws java.lang.Exception {
		org.apache.commons.math.stat.regression.SimpleRegression regression = new org.apache.commons.math.stat.regression.SimpleRegression();
		java.util.Random random = new java.util.Random(1);
		int n = 100;
		for (int i = 0 ; i < n ; i++) {
			regression.addData((((double)(i)) / (n - 1)), random.nextDouble());
		}
		junit.framework.Assert.assertTrue(((0.0 < (regression.getSignificance())) && ((regression.getSignificance()) < 1.0)));
	}

	public void testSSENonNegative() {
		double[] y = new double[]{ 8915.102 , 8919.302 , 8923.502 };
		double[] x = new double[]{ 110.7178495 , 110.7264895 , 110.7351295 };
		org.apache.commons.math.stat.regression.SimpleRegression reg = new org.apache.commons.math.stat.regression.SimpleRegression();
		for (int i = 0 ; i < (x.length) ; i++) {
			reg.addData(x[i], y[i]);
		}
		junit.framework.Assert.assertTrue(((reg.getSumSquaredErrors()) >= 0.0));
	}

	public void testRemoveXY() throws java.lang.Exception {
		org.apache.commons.math.stat.regression.SimpleRegression regression = new org.apache.commons.math.stat.regression.SimpleRegression();
		regression.addData(infData);
		regression.removeData(removeX, removeY);
		regression.addData(removeX, removeY);
		junit.framework.Assert.assertEquals("slope std err", 0.011448491, regression.getSlopeStdErr(), 1.0E-10);
		junit.framework.Assert.assertEquals("std err intercept", 0.286036932, regression.getInterceptStdErr(), 1.0E-8);
		junit.framework.Assert.assertEquals("significance", 4.596E-7, regression.getSignificance(), 1.0E-8);
		junit.framework.Assert.assertEquals("slope conf interval half-width", 0.0270713794287, regression.getSlopeConfidenceInterval(), 1.0E-8);
	}

	public void testRemoveSingle() throws java.lang.Exception {
		org.apache.commons.math.stat.regression.SimpleRegression regression = new org.apache.commons.math.stat.regression.SimpleRegression();
		regression.addData(infData);
		regression.removeData(removeSingle);
		regression.addData(removeSingle);
		junit.framework.Assert.assertEquals("slope std err", 0.011448491, regression.getSlopeStdErr(), 1.0E-10);
		junit.framework.Assert.assertEquals("std err intercept", 0.286036932, regression.getInterceptStdErr(), 1.0E-8);
		junit.framework.Assert.assertEquals("significance", 4.596E-7, regression.getSignificance(), 1.0E-8);
		junit.framework.Assert.assertEquals("slope conf interval half-width", 0.0270713794287, regression.getSlopeConfidenceInterval(), 1.0E-8);
	}

	public void testRemoveMultiple() throws java.lang.Exception {
		org.apache.commons.math.stat.regression.SimpleRegression regression = new org.apache.commons.math.stat.regression.SimpleRegression();
		regression.addData(infData);
		regression.removeData(removeMultiple);
		regression.addData(removeMultiple);
		junit.framework.Assert.assertEquals("slope std err", 0.011448491, regression.getSlopeStdErr(), 1.0E-10);
		junit.framework.Assert.assertEquals("std err intercept", 0.286036932, regression.getInterceptStdErr(), 1.0E-8);
		junit.framework.Assert.assertEquals("significance", 4.596E-7, regression.getSignificance(), 1.0E-8);
		junit.framework.Assert.assertEquals("slope conf interval half-width", 0.0270713794287, regression.getSlopeConfidenceInterval(), 1.0E-8);
	}

	public void testRemoveObsFromEmpty() {
		org.apache.commons.math.stat.regression.SimpleRegression regression = new org.apache.commons.math.stat.regression.SimpleRegression();
		regression.removeData(removeX, removeY);
		junit.framework.Assert.assertEquals(regression.getN(), 0);
	}

	public void testRemoveObsFromSingle() {
		org.apache.commons.math.stat.regression.SimpleRegression regression = new org.apache.commons.math.stat.regression.SimpleRegression();
		regression.addData(removeX, removeY);
		regression.removeData(removeX, removeY);
		junit.framework.Assert.assertEquals(regression.getN(), 0);
	}

	public void testRemoveMultipleToEmpty() {
		org.apache.commons.math.stat.regression.SimpleRegression regression = new org.apache.commons.math.stat.regression.SimpleRegression();
		regression.addData(removeMultiple);
		regression.removeData(removeMultiple);
		junit.framework.Assert.assertEquals(regression.getN(), 0);
	}

	public void testRemoveMultiplePastEmpty() {
		org.apache.commons.math.stat.regression.SimpleRegression regression = new org.apache.commons.math.stat.regression.SimpleRegression();
		regression.addData(removeX, removeY);
		regression.removeData(removeMultiple);
		junit.framework.Assert.assertEquals(regression.getN(), 0);
	}
}

