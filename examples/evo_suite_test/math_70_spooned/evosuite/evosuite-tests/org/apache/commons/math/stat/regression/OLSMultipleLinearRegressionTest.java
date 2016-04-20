package org.apache.commons.math.stat.regression;


public class OLSMultipleLinearRegressionTest extends org.apache.commons.math.stat.regression.MultipleLinearRegressionAbstractTest {
	private double[] y;

	private double[][] x;

	@org.junit.Before
	@java.lang.Override
	public void setUp() {
		y = new double[]{ 11.0 , 12.0 , 13.0 , 14.0 , 15.0 , 16.0 };
		x = new double[6][];
		x[0] = new double[]{ 1.0 , 0 , 0 , 0 , 0 , 0 };
		x[1] = new double[]{ 1.0 , 2.0 , 0 , 0 , 0 , 0 };
		x[2] = new double[]{ 1.0 , 0 , 3.0 , 0 , 0 , 0 };
		x[3] = new double[]{ 1.0 , 0 , 0 , 4.0 , 0 , 0 };
		x[4] = new double[]{ 1.0 , 0 , 0 , 0 , 5.0 , 0 };
		x[5] = new double[]{ 1.0 , 0 , 0 , 0 , 0 , 6.0 };
		super.setUp();
	}

	@java.lang.Override
	protected org.apache.commons.math.stat.regression.OLSMultipleLinearRegression createRegression() {
		org.apache.commons.math.stat.regression.OLSMultipleLinearRegression regression = new org.apache.commons.math.stat.regression.OLSMultipleLinearRegression();
		regression.newSampleData(y, x);
		return regression;
	}

	@java.lang.Override
	protected int getNumberOfRegressors() {
		return x[0].length;
	}

	@java.lang.Override
	protected int getSampleSize() {
		return y.length;
	}

	@org.junit.Test(expected = java.lang.IllegalArgumentException.class)
	public void cannotAddXSampleData() {
		createRegression().newSampleData(new double[]{  }, null);
	}

	@org.junit.Test(expected = java.lang.IllegalArgumentException.class)
	public void cannotAddNullYSampleData() {
		createRegression().newSampleData(null, new double[][]{  });
	}

	@org.junit.Test(expected = java.lang.IllegalArgumentException.class)
	public void cannotAddSampleDataWithSizeMismatch() {
		double[] y = new double[]{ 1.0 , 2.0 };
		double[][] x = new double[1][];
		x[0] = new double[]{ 1.0 , 0 };
		createRegression().newSampleData(y, x);
	}

	@org.junit.Test
	public void testPerfectFit() {
		double[] betaHat = regression.estimateRegressionParameters();
		org.apache.commons.math.TestUtils.assertEquals(betaHat, new double[]{ 11.0 , 1.0 / 2.0 , 2.0 / 3.0 , 3.0 / 4.0 , 4.0 / 5.0 , 5.0 / 6.0 }, 1.0E-14);
		double[] residuals = regression.estimateResiduals();
		org.apache.commons.math.TestUtils.assertEquals(residuals, new double[]{ 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 }, 1.0E-14);
		org.apache.commons.math.linear.RealMatrix errors = new org.apache.commons.math.linear.Array2DRowRealMatrix(regression.estimateRegressionParametersVariance() , false);
		final double[] s = new double[]{ 1.0 , (-1.0) / 2.0 , (-1.0) / 3.0 , (-1.0) / 4.0 , (-1.0) / 5.0 , (-1.0) / 6.0 };
		org.apache.commons.math.linear.RealMatrix referenceVariance = new org.apache.commons.math.linear.Array2DRowRealMatrix(s.length , s.length);
		referenceVariance.walkInOptimizedOrder(new org.apache.commons.math.linear.DefaultRealMatrixChangingVisitor() {
			@java.lang.Override
			public double visit(int row, int column, double value) throws org.apache.commons.math.linear.MatrixVisitorException {
				if (row == 0) {
					return s[column];
				} 
				double x = (s[row]) * (s[column]);
				return row == column ? 2 * x : x;
			}
		});
		org.junit.Assert.assertEquals(0.0, errors.subtract(referenceVariance).getNorm(), (5.0E-16 * (referenceVariance.getNorm())));
	}

	@org.junit.Test
	public void testLongly() {
		double[] design = new double[]{ 60323 , 83.0 , 234289 , 2356 , 1590 , 107608 , 1947 , 61122 , 88.5 , 259426 , 2325 , 1456 , 108632 , 1948 , 60171 , 88.2 , 258054 , 3682 , 1616 , 109773 , 1949 , 61187 , 89.5 , 284599 , 3351 , 1650 , 110929 , 1950 , 63221 , 96.2 , 328975 , 2099 , 3099 , 112075 , 1951 , 63639 , 98.1 , 346999 , 1932 , 3594 , 113270 , 1952 , 64989 , 99.0 , 365385 , 1870 , 3547 , 115094 , 1953 , 63761 , 100.0 , 363112 , 3578 , 3350 , 116219 , 1954 , 66019 , 101.2 , 397469 , 2904 , 3048 , 117388 , 1955 , 67857 , 104.6 , 419180 , 2822 , 2857 , 118734 , 1956 , 68169 , 108.4 , 442769 , 2936 , 2798 , 120445 , 1957 , 66513 , 110.8 , 444546 , 4681 , 2637 , 121950 , 1958 , 68655 , 112.6 , 482704 , 3813 , 2552 , 123366 , 1959 , 69564 , 114.2 , 502601 , 3931 , 2514 , 125368 , 1960 , 69331 , 115.7 , 518173 , 4806 , 2572 , 127852 , 1961 , 70551 , 116.9 , 554894 , 4007 , 2827 , 130081 , 1962 };
		int nobs = 16;
		int nvars = 6;
		org.apache.commons.math.stat.regression.OLSMultipleLinearRegression model = new org.apache.commons.math.stat.regression.OLSMultipleLinearRegression();
		model.newSampleData(design, nobs, nvars);
		double[] betaHat = model.estimateRegressionParameters();
		org.apache.commons.math.TestUtils.assertEquals(betaHat, new double[]{ -3482258.63459582 , 15.0618722713733 , -0.035819179292591 , -2.02022980381683 , -1.03322686717359 , -0.0511041056535807 , 1829.15146461355 }, 2.0E-8);
		double[] residuals = model.estimateResiduals();
		org.apache.commons.math.TestUtils.assertEquals(residuals, new double[]{ 267.340029759711 , -94.0139423988359 , 46.28716775752924 , -410.114621930906 , 309.7145907602313 , -249.3112153297231 , -164.0489563956039 , -13.18035686637081 , 14.30477260005235 , 455.394094551857 , -17.26892711483297 , -39.0550425226967 , -155.5499735953195 , -85.6713080421283 , 341.9315139607727 , -206.7578251937366 }, 1.0E-8);
		double[] errors = model.estimateRegressionParametersStandardErrors();
		org.apache.commons.math.TestUtils.assertEquals(new double[]{ 890420.383607373 , 84.9149257747669 , 0.0334910077722432 , 0.488399681651699 , 0.214274163161675 , 0.22607320006937 , 455.478499142212 }, errors, 1.0E-6);
	}

	@org.junit.Test
	public void testSwissFertility() {
		double[] design = new double[]{ 80.2 , 17.0 , 15 , 12 , 9.96 , 83.1 , 45.1 , 6 , 9 , 84.84 , 92.5 , 39.7 , 5 , 5 , 93.4 , 85.8 , 36.5 , 12 , 7 , 33.77 , 76.9 , 43.5 , 17 , 15 , 5.16 , 76.1 , 35.3 , 9 , 7 , 90.57 , 83.8 , 70.2 , 16 , 7 , 92.85 , 92.4 , 67.8 , 14 , 8 , 97.16 , 82.4 , 53.3 , 12 , 7 , 97.67 , 82.9 , 45.2 , 16 , 13 , 91.38 , 87.1 , 64.5 , 14 , 6 , 98.61 , 64.1 , 62.0 , 21 , 12 , 8.52 , 66.9 , 67.5 , 14 , 7 , 2.27 , 68.9 , 60.7 , 19 , 12 , 4.43 , 61.7 , 69.3 , 22 , 5 , 2.82 , 68.3 , 72.6 , 18 , 2 , 24.2 , 71.7 , 34.0 , 17 , 8 , 3.3 , 55.7 , 19.4 , 26 , 28 , 12.11 , 54.3 , 15.2 , 31 , 20 , 2.15 , 65.1 , 73.0 , 19 , 9 , 2.84 , 65.5 , 59.8 , 22 , 10 , 5.23 , 65.0 , 55.1 , 14 , 3 , 4.52 , 56.6 , 50.9 , 22 , 12 , 15.14 , 57.4 , 54.1 , 20 , 6 , 4.2 , 72.5 , 71.2 , 12 , 1 , 2.4 , 74.2 , 58.1 , 14 , 8 , 5.23 , 72.0 , 63.5 , 6 , 3 , 2.56 , 60.5 , 60.8 , 16 , 10 , 7.72 , 58.3 , 26.8 , 25 , 19 , 18.46 , 65.4 , 49.5 , 15 , 8 , 6.1 , 75.5 , 85.9 , 3 , 2 , 99.71 , 69.3 , 84.9 , 7 , 6 , 99.68 , 77.3 , 89.7 , 5 , 2 , 100.0 , 70.5 , 78.2 , 12 , 6 , 98.96 , 79.4 , 64.9 , 7 , 3 , 98.22 , 65.0 , 75.9 , 9 , 9 , 99.06 , 92.2 , 84.6 , 3 , 3 , 99.46 , 79.3 , 63.1 , 13 , 13 , 96.83 , 70.4 , 38.4 , 26 , 12 , 5.62 , 65.7 , 7.7 , 29 , 11 , 13.79 , 72.7 , 16.7 , 22 , 13 , 11.22 , 64.4 , 17.6 , 35 , 32 , 16.92 , 77.6 , 37.6 , 15 , 7 , 4.97 , 67.6 , 18.7 , 25 , 7 , 8.65 , 35.0 , 1.2 , 37 , 53 , 42.34 , 44.7 , 46.6 , 16 , 29 , 50.43 , 42.8 , 27.7 , 22 , 29 , 58.33 };
		int nobs = 47;
		int nvars = 4;
		org.apache.commons.math.stat.regression.OLSMultipleLinearRegression model = new org.apache.commons.math.stat.regression.OLSMultipleLinearRegression();
		model.newSampleData(design, nobs, nvars);
		double[] betaHat = model.estimateRegressionParameters();
		org.apache.commons.math.TestUtils.assertEquals(betaHat, new double[]{ 91.05542390271397 , -0.22064551045715 , -0.26058239824328 , -0.9616123845603 , 0.12441843147162 }, 1.0E-12);
		double[] residuals = model.estimateResiduals();
		org.apache.commons.math.TestUtils.assertEquals(residuals, new double[]{ 7.104426785973051 , 1.6580347433531366 , 4.694495277002964 , 8.454802269016616 , 13.654743234318621 , -9.358686445850077 , 7.5822446330520386 , 15.556899556385929 , 0.811309073659898 , 7.118676273248431 , 7.425137877122872 , 2.676131687323411 , 0.8351584810309354 , 7.176999111961518 , -3.8746753206299553 , -3.133777947638725 , -0.1412575244091504 , 1.118680917046978 , -6.358809734681659 , 3.4039270429434074 , 2.3374058329820175 , -7.92723685769005 , -7.836101096849796 , -11.259736926935707 , 0.9445333697827101 , 6.654424510138033 , -0.9146136301118665 , -4.315244940384857 , -4.353693204700918 , -3.890788516930466 , -6.302764392630219 , -7.830898218928909 , -3.179228001533275 , -6.716729877115823 , -4.846994671804175 , -10.633566435363369 , 11.103113436203696 , 6.008403264181173 , 5.432623083018848 , -7.237557862969223 , 2.167155081444822 , 15.014757465276311 , 4.8625103516321015 , -7.159725641390771 , -0.4515205619767598 , -10.291687090383759 , -15.781298457190006 }, 1.0E-12);
		double[] errors = model.estimateRegressionParametersStandardErrors();
		org.apache.commons.math.TestUtils.assertEquals(new double[]{ 6.94881329475087 , 0.0736000897234 , 0.27410957467466 , 0.19454551679325 , 0.03726654773803 }, errors, 1.0E-10);
	}

	@org.junit.Test
	public void testHat() throws java.lang.Exception {
		double[] design = new double[]{ 11.14 , 0.499 , 11.1 , 12.74 , 0.558 , 8.9 , 13.13 , 0.604 , 8.8 , 11.51 , 0.441 , 8.9 , 12.38 , 0.55 , 8.8 , 12.6 , 0.528 , 9.9 , 11.13 , 0.418 , 10.7 , 11.7 , 0.48 , 10.5 , 11.02 , 0.406 , 10.5 , 11.41 , 0.467 , 10.7 };
		int nobs = 10;
		int nvars = 2;
		org.apache.commons.math.stat.regression.OLSMultipleLinearRegression model = new org.apache.commons.math.stat.regression.OLSMultipleLinearRegression();
		model.newSampleData(design, nobs, nvars);
		org.apache.commons.math.linear.RealMatrix hat = model.calculateHat();
		double[] referenceData = new double[]{ 0.418 , -0.002 , 0.079 , -0.274 , -0.046 , 0.181 , 0.128 , 0.222 , 0.05 , 0.242 , 0.242 , 0.292 , 0.136 , 0.243 , 0.128 , -0.041 , 0.033 , -0.035 , 0.004 , 0.417 , -0.019 , 0.273 , 0.187 , -0.126 , 0.044 , -0.153 , 0.004 , 0.604 , 0.197 , -0.038 , 0.168 , -0.022 , 0.275 , -0.028 , 0.252 , 0.111 , -0.03 , 0.019 , -0.01 , -0.01 , 0.148 , 0.042 , 0.117 , 0.012 , 0.111 , 0.262 , 0.145 , 0.277 , 0.174 , 0.154 , 0.12 , 0.168 , 0.315 , 0.148 , 0.187 };
		int k = 0;
		for (int i = 0 ; i < 10 ; i++) {
			for (int j = i ; j < 10 ; j++) {
				org.junit.Assert.assertEquals(referenceData[k], hat.getEntry(i, j), 0.01);
				org.junit.Assert.assertEquals(hat.getEntry(i, j), hat.getEntry(j, i), 1.0E-11);
				k++;
			}
		}
		double[] residuals = model.estimateResiduals();
		org.apache.commons.math.linear.RealMatrix I = org.apache.commons.math.linear.MatrixUtils.createRealIdentityMatrix(10);
		double[] hatResiduals = I.subtract(hat).operate(model.Y).getData();
		org.apache.commons.math.TestUtils.assertEquals(residuals, hatResiduals, 1.0E-11);
	}
}

