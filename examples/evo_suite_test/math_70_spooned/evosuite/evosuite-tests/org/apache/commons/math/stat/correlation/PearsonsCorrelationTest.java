package org.apache.commons.math.stat.correlation;


public class PearsonsCorrelationTest extends junit.framework.TestCase {
	protected final double[] longleyData = new double[]{ 60323 , 83.0 , 234289 , 2356 , 1590 , 107608 , 1947 , 61122 , 88.5 , 259426 , 2325 , 1456 , 108632 , 1948 , 60171 , 88.2 , 258054 , 3682 , 1616 , 109773 , 1949 , 61187 , 89.5 , 284599 , 3351 , 1650 , 110929 , 1950 , 63221 , 96.2 , 328975 , 2099 , 3099 , 112075 , 1951 , 63639 , 98.1 , 346999 , 1932 , 3594 , 113270 , 1952 , 64989 , 99.0 , 365385 , 1870 , 3547 , 115094 , 1953 , 63761 , 100.0 , 363112 , 3578 , 3350 , 116219 , 1954 , 66019 , 101.2 , 397469 , 2904 , 3048 , 117388 , 1955 , 67857 , 104.6 , 419180 , 2822 , 2857 , 118734 , 1956 , 68169 , 108.4 , 442769 , 2936 , 2798 , 120445 , 1957 , 66513 , 110.8 , 444546 , 4681 , 2637 , 121950 , 1958 , 68655 , 112.6 , 482704 , 3813 , 2552 , 123366 , 1959 , 69564 , 114.2 , 502601 , 3931 , 2514 , 125368 , 1960 , 69331 , 115.7 , 518173 , 4806 , 2572 , 127852 , 1961 , 70551 , 116.9 , 554894 , 4007 , 2827 , 130081 , 1962 };

	protected final double[] swissData = new double[]{ 80.2 , 17.0 , 15 , 12 , 9.96 , 83.1 , 45.1 , 6 , 9 , 84.84 , 92.5 , 39.7 , 5 , 5 , 93.4 , 85.8 , 36.5 , 12 , 7 , 33.77 , 76.9 , 43.5 , 17 , 15 , 5.16 , 76.1 , 35.3 , 9 , 7 , 90.57 , 83.8 , 70.2 , 16 , 7 , 92.85 , 92.4 , 67.8 , 14 , 8 , 97.16 , 82.4 , 53.3 , 12 , 7 , 97.67 , 82.9 , 45.2 , 16 , 13 , 91.38 , 87.1 , 64.5 , 14 , 6 , 98.61 , 64.1 , 62.0 , 21 , 12 , 8.52 , 66.9 , 67.5 , 14 , 7 , 2.27 , 68.9 , 60.7 , 19 , 12 , 4.43 , 61.7 , 69.3 , 22 , 5 , 2.82 , 68.3 , 72.6 , 18 , 2 , 24.2 , 71.7 , 34.0 , 17 , 8 , 3.3 , 55.7 , 19.4 , 26 , 28 , 12.11 , 54.3 , 15.2 , 31 , 20 , 2.15 , 65.1 , 73.0 , 19 , 9 , 2.84 , 65.5 , 59.8 , 22 , 10 , 5.23 , 65.0 , 55.1 , 14 , 3 , 4.52 , 56.6 , 50.9 , 22 , 12 , 15.14 , 57.4 , 54.1 , 20 , 6 , 4.2 , 72.5 , 71.2 , 12 , 1 , 2.4 , 74.2 , 58.1 , 14 , 8 , 5.23 , 72.0 , 63.5 , 6 , 3 , 2.56 , 60.5 , 60.8 , 16 , 10 , 7.72 , 58.3 , 26.8 , 25 , 19 , 18.46 , 65.4 , 49.5 , 15 , 8 , 6.1 , 75.5 , 85.9 , 3 , 2 , 99.71 , 69.3 , 84.9 , 7 , 6 , 99.68 , 77.3 , 89.7 , 5 , 2 , 100.0 , 70.5 , 78.2 , 12 , 6 , 98.96 , 79.4 , 64.9 , 7 , 3 , 98.22 , 65.0 , 75.9 , 9 , 9 , 99.06 , 92.2 , 84.6 , 3 , 3 , 99.46 , 79.3 , 63.1 , 13 , 13 , 96.83 , 70.4 , 38.4 , 26 , 12 , 5.62 , 65.7 , 7.7 , 29 , 11 , 13.79 , 72.7 , 16.7 , 22 , 13 , 11.22 , 64.4 , 17.6 , 35 , 32 , 16.92 , 77.6 , 37.6 , 15 , 7 , 4.97 , 67.6 , 18.7 , 25 , 7 , 8.65 , 35.0 , 1.2 , 37 , 53 , 42.34 , 44.7 , 46.6 , 16 , 29 , 50.43 , 42.8 , 27.7 , 22 , 29 , 58.33 };

	public void testLongly() throws java.lang.Exception {
		org.apache.commons.math.linear.RealMatrix matrix = createRealMatrix(longleyData, 16, 7);
		org.apache.commons.math.stat.correlation.PearsonsCorrelation corrInstance = new org.apache.commons.math.stat.correlation.PearsonsCorrelation(matrix);
		org.apache.commons.math.linear.RealMatrix correlationMatrix = corrInstance.getCorrelationMatrix();
		double[] rData = new double[]{ 1.0 , 0.970898525061056 , 0.9835516111796693 , 0.5024980838759942 , 0.4573073999764817 , 0.960390571594376 , 0.9713294591921188 , 0.970898525061056 , 1.0 , 0.9915891780247822 , 0.6206333925590966 , 0.4647441876006747 , 0.979163432977498 , 0.9911491900672053 , 0.983551611179669 , 0.9915891780247822 , 1.0 , 0.604260939889558 , 0.4464367918926265 , 0.991090069458478 , 0.9952734837647849 , 0.502498083875994 , 0.6206333925590966 , 0.604260939889558 , 1.0 , -0.1774206295018783 , 0.686551516365312 , 0.6682566045621746 , 0.457307399976482 , 0.4647441876006747 , 0.4464367918926265 , -0.1774206295018783 , 1.0 , 0.364416267189032 , 0.4172451498349454 , 0.960390571594376 , 0.9791634329774981 , 0.9910900694584777 , 0.686551516365312 , 0.364416267189032 , 1.0 , 0.9939528462329257 , 0.971329459192119 , 0.9911491900672053 , 0.9952734837647849 , 0.6682566045621746 , 0.4172451498349454 , 0.993952846232926 , 1.0 };
		org.apache.commons.math.TestUtils.assertEquals("correlation matrix", createRealMatrix(rData, 7, 7), correlationMatrix, 1.0E-14);
		double[] rPvalues = new double[]{ 4.38904690369668E-10 , 8.36353208910623E-12 , 7.8159700933611E-14 , 0.0472894097790304 , 0.01030636128354301 , 0.01316878049026582 , 0.0749178049642416 , 0.06971758330341182 , 0.0830166169296545 , 0.510948586323452 , 3.693245043123738E-9 , 4.327782576751815E-11 , 1.167954621905665E-13 , 0.00331028281967516 , 0.1652293725106684 , 3.95834476307755E-10 , 1.114663916723657E-13 , 1.332267629550188E-15 , 0.00466039138541463 , 0.1078477071581498 , 7.771561172376096E-15 };
		org.apache.commons.math.linear.RealMatrix rPMatrix = createLowerTriangularRealMatrix(rPvalues, 7);
		fillUpper(rPMatrix, 0.0);
		org.apache.commons.math.TestUtils.assertEquals("correlation p values", rPMatrix, corrInstance.getCorrelationPValues(), 1.0E-14);
	}

	public void testSwissFertility() throws java.lang.Exception {
		org.apache.commons.math.linear.RealMatrix matrix = createRealMatrix(swissData, 47, 5);
		org.apache.commons.math.stat.correlation.PearsonsCorrelation corrInstance = new org.apache.commons.math.stat.correlation.PearsonsCorrelation(matrix);
		org.apache.commons.math.linear.RealMatrix correlationMatrix = corrInstance.getCorrelationMatrix();
		double[] rData = new double[]{ 1.0 , 0.3530791836199747 , -0.6458827064572875 , -0.6637888570350691 , 0.4636847006517939 , 0.3530791836199747 , 1.0 , -0.6865422086171366 , -0.6395225189483201 , 0.4010950530487398 , -0.6458827064572875 , -0.6865422086171366 , 1.0 , 0.698415296288483 , -0.5727418060641666 , -0.6637888570350691 , -0.6395225189483201 , 0.698415296288483 , 1.0 , -0.1538589170909148 , 0.4636847006517939 , 0.4010950530487398 , -0.5727418060641666 , -0.1538589170909148 , 1.0 };
		org.apache.commons.math.TestUtils.assertEquals("correlation matrix", createRealMatrix(rData, 5, 5), correlationMatrix, 1.0E-14);
		double[] rPvalues = new double[]{ 0.01491720061472623 , 9.45043734069043E-7 , 9.95151527133974E-8 , 3.658616965962355E-7 , 1.304590105694471E-6 , 4.811397236181847E-8 , 0.001028523190118147 , 0.005204433539191644 , 2.588307925380906E-5 , 0.301807756132683 };
		org.apache.commons.math.linear.RealMatrix rPMatrix = createLowerTriangularRealMatrix(rPvalues, 5);
		fillUpper(rPMatrix, 0.0);
		org.apache.commons.math.TestUtils.assertEquals("correlation p values", rPMatrix, corrInstance.getCorrelationPValues(), 1.0E-14);
	}

	public void testConstant() {
		double[] noVariance = new double[]{ 1 , 1 , 1 , 1 };
		double[] values = new double[]{ 1 , 2 , 3 , 4 };
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(new org.apache.commons.math.stat.correlation.PearsonsCorrelation().correlation(noVariance, values)));
	}

	public void testInsufficientData() {
		double[] one = new double[]{ 1 };
		double[] two = new double[]{ 2 };
		try {
			new org.apache.commons.math.stat.correlation.PearsonsCorrelation().correlation(one, two);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		org.apache.commons.math.linear.RealMatrix matrix = new org.apache.commons.math.linear.BlockRealMatrix(new double[][]{ new double[]{ 0 } , new double[]{ 1 } });
		try {
			new org.apache.commons.math.stat.correlation.PearsonsCorrelation(matrix);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testStdErrorConsistency() throws java.lang.Exception {
		org.apache.commons.math.distribution.TDistribution tDistribution = new org.apache.commons.math.distribution.TDistributionImpl(45);
		org.apache.commons.math.linear.RealMatrix matrix = createRealMatrix(swissData, 47, 5);
		org.apache.commons.math.stat.correlation.PearsonsCorrelation corrInstance = new org.apache.commons.math.stat.correlation.PearsonsCorrelation(matrix);
		org.apache.commons.math.linear.RealMatrix rValues = corrInstance.getCorrelationMatrix();
		org.apache.commons.math.linear.RealMatrix pValues = corrInstance.getCorrelationPValues();
		org.apache.commons.math.linear.RealMatrix stdErrors = corrInstance.getCorrelationStandardErrors();
		for (int i = 0 ; i < 5 ; i++) {
			for (int j = 0 ; j < i ; j++) {
				double t = (java.lang.Math.abs(rValues.getEntry(i, j))) / (stdErrors.getEntry(i, j));
				double p = 2 * (1 - (tDistribution.cumulativeProbability(t)));
				junit.framework.Assert.assertEquals(p, pValues.getEntry(i, j), 1.0E-14);
			}
		}
	}

	public void testCovarianceConsistency() throws java.lang.Exception {
		org.apache.commons.math.linear.RealMatrix matrix = createRealMatrix(longleyData, 16, 7);
		org.apache.commons.math.stat.correlation.PearsonsCorrelation corrInstance = new org.apache.commons.math.stat.correlation.PearsonsCorrelation(matrix);
		org.apache.commons.math.stat.correlation.Covariance covInstance = new org.apache.commons.math.stat.correlation.Covariance(matrix);
		org.apache.commons.math.stat.correlation.PearsonsCorrelation corrFromCovInstance = new org.apache.commons.math.stat.correlation.PearsonsCorrelation(covInstance);
		org.apache.commons.math.TestUtils.assertEquals("correlation values", corrInstance.getCorrelationMatrix(), corrFromCovInstance.getCorrelationMatrix(), 1.0E-14);
		org.apache.commons.math.TestUtils.assertEquals("p values", corrInstance.getCorrelationPValues(), corrFromCovInstance.getCorrelationPValues(), 1.0E-14);
		org.apache.commons.math.TestUtils.assertEquals("standard errors", corrInstance.getCorrelationStandardErrors(), corrFromCovInstance.getCorrelationStandardErrors(), 1.0E-14);
		org.apache.commons.math.stat.correlation.PearsonsCorrelation corrFromCovInstance2 = new org.apache.commons.math.stat.correlation.PearsonsCorrelation(covInstance.getCovarianceMatrix() , 16);
		org.apache.commons.math.TestUtils.assertEquals("correlation values", corrInstance.getCorrelationMatrix(), corrFromCovInstance2.getCorrelationMatrix(), 1.0E-14);
		org.apache.commons.math.TestUtils.assertEquals("p values", corrInstance.getCorrelationPValues(), corrFromCovInstance2.getCorrelationPValues(), 1.0E-14);
		org.apache.commons.math.TestUtils.assertEquals("standard errors", corrInstance.getCorrelationStandardErrors(), corrFromCovInstance2.getCorrelationStandardErrors(), 1.0E-14);
	}

	public void testConsistency() {
		org.apache.commons.math.linear.RealMatrix matrix = createRealMatrix(longleyData, 16, 7);
		org.apache.commons.math.stat.correlation.PearsonsCorrelation corrInstance = new org.apache.commons.math.stat.correlation.PearsonsCorrelation(matrix);
		double[][] data = matrix.getData();
		double[] x = matrix.getColumn(0);
		double[] y = matrix.getColumn(1);
		junit.framework.Assert.assertEquals(new org.apache.commons.math.stat.correlation.PearsonsCorrelation().correlation(x, y), corrInstance.getCorrelationMatrix().getEntry(0, 1), java.lang.Double.MIN_VALUE);
		org.apache.commons.math.TestUtils.assertEquals("Correlation matrix", corrInstance.getCorrelationMatrix(), new org.apache.commons.math.stat.correlation.PearsonsCorrelation().computeCorrelationMatrix(data), java.lang.Double.MIN_VALUE);
	}

	protected org.apache.commons.math.linear.RealMatrix createRealMatrix(double[] data, int nRows, int nCols) {
		double[][] matrixData = new double[nRows][nCols];
		int ptr = 0;
		for (int i = 0 ; i < nRows ; i++) {
			java.lang.System.arraycopy(data, ptr, matrixData[i], 0, nCols);
			ptr += nCols;
		}
		return new org.apache.commons.math.linear.BlockRealMatrix(matrixData);
	}

	protected org.apache.commons.math.linear.RealMatrix createLowerTriangularRealMatrix(double[] data, int dimension) {
		int ptr = 0;
		org.apache.commons.math.linear.RealMatrix result = new org.apache.commons.math.linear.BlockRealMatrix(dimension , dimension);
		for (int i = 1 ; i < dimension ; i++) {
			for (int j = 0 ; j < i ; j++) {
				result.setEntry(i, j, data[ptr]);
				ptr++;
			}
		}
		return result;
	}

	protected void fillUpper(org.apache.commons.math.linear.RealMatrix matrix, double diagonalValue) {
		int dimension = matrix.getColumnDimension();
		for (int i = 0 ; i < dimension ; i++) {
			matrix.setEntry(i, i, diagonalValue);
			for (int j = i + 1 ; j < dimension ; j++) {
				matrix.setEntry(i, j, matrix.getEntry(j, i));
			}
		}
	}
}

