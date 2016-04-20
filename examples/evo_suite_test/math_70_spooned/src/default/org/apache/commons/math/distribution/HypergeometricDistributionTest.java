package org.apache.commons.math.distribution;


public class HypergeometricDistributionTest extends org.apache.commons.math.distribution.IntegerDistributionAbstractTest {
	public HypergeometricDistributionTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.distribution.IntegerDistribution makeDistribution() {
		return new org.apache.commons.math.distribution.HypergeometricDistributionImpl(10 , 5 , 5);
	}

	@java.lang.Override
	public int[] makeDensityTestPoints() {
		return new int[]{ -1 , 0 , 1 , 2 , 3 , 4 , 5 , 10 };
	}

	@java.lang.Override
	public double[] makeDensityTestValues() {
		return new double[]{ 0.0 , 0.003968 , 0.099206 , 0.396825 , 0.396825 , 0.099206 , 0.003968 , 0.0 };
	}

	@java.lang.Override
	public int[] makeCumulativeTestPoints() {
		return makeDensityTestPoints();
	}

	@java.lang.Override
	public double[] makeCumulativeTestValues() {
		return new double[]{ 0.0 , 0.003968 , 0.103175 , 0.5 , 0.896825 , 0.996032 , 1.0 , 1.0 };
	}

	@java.lang.Override
	public double[] makeInverseCumulativeTestPoints() {
		return new double[]{ 0.0 , 0.001 , 0.01 , 0.025 , 0.05 , 0.1 , 0.999 , 0.99 , 0.975 , 0.95 , 0.9 , 1.0 };
	}

	@java.lang.Override
	public int[] makeInverseCumulativeTestValues() {
		return new int[]{ -1 , -1 , 0 , 0 , 0 , 0 , 4 , 3 , 3 , 3 , 3 , 5 };
	}

	public void testDegenerateNoFailures() throws java.lang.Exception {
		setDistribution(new org.apache.commons.math.distribution.HypergeometricDistributionImpl(5 , 5 , 3));
		setCumulativeTestPoints(new int[]{ -1 , 0 , 1 , 3 , 10 });
		setCumulativeTestValues(new double[]{ 0.0 , 0.0 , 0.0 , 1.0 , 1.0 });
		setDensityTestPoints(new int[]{ -1 , 0 , 1 , 3 , 10 });
		setDensityTestValues(new double[]{ 0.0 , 0.0 , 0.0 , 1.0 , 0.0 });
		setInverseCumulativeTestPoints(new double[]{ 0.1 , 0.5 });
		setInverseCumulativeTestValues(new int[]{ 2 , 2 });
		verifyDensities();
		verifyCumulativeProbabilities();
		verifyInverseCumulativeProbabilities();
	}

	public void testDegenerateNoSuccesses() throws java.lang.Exception {
		setDistribution(new org.apache.commons.math.distribution.HypergeometricDistributionImpl(5 , 0 , 3));
		setCumulativeTestPoints(new int[]{ -1 , 0 , 1 , 3 , 10 });
		setCumulativeTestValues(new double[]{ 0.0 , 1.0 , 1.0 , 1.0 , 1.0 });
		setDensityTestPoints(new int[]{ -1 , 0 , 1 , 3 , 10 });
		setDensityTestValues(new double[]{ 0.0 , 1.0 , 0.0 , 0.0 , 0.0 });
		setInverseCumulativeTestPoints(new double[]{ 0.1 , 0.5 });
		setInverseCumulativeTestValues(new int[]{ -1 , -1 });
		verifyDensities();
		verifyCumulativeProbabilities();
		verifyInverseCumulativeProbabilities();
	}

	public void testDegenerateFullSample() throws java.lang.Exception {
		setDistribution(new org.apache.commons.math.distribution.HypergeometricDistributionImpl(5 , 3 , 5));
		setCumulativeTestPoints(new int[]{ -1 , 0 , 1 , 3 , 10 });
		setCumulativeTestValues(new double[]{ 0.0 , 0.0 , 0.0 , 1.0 , 1.0 });
		setDensityTestPoints(new int[]{ -1 , 0 , 1 , 3 , 10 });
		setDensityTestValues(new double[]{ 0.0 , 0.0 , 0.0 , 1.0 , 0.0 });
		setInverseCumulativeTestPoints(new double[]{ 0.1 , 0.5 });
		setInverseCumulativeTestValues(new int[]{ 2 , 2 });
		verifyDensities();
		verifyCumulativeProbabilities();
		verifyInverseCumulativeProbabilities();
	}

	public void testPopulationSize() {
		org.apache.commons.math.distribution.HypergeometricDistribution dist = new org.apache.commons.math.distribution.HypergeometricDistributionImpl(5 , 3 , 5);
		try {
			dist.setPopulationSize(-1);
			junit.framework.Assert.fail("negative population size.  IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		dist.setPopulationSize(10);
		junit.framework.Assert.assertEquals(10, dist.getPopulationSize());
	}

	public void testLargeValues() {
		int populationSize = 3456;
		int sampleSize = 789;
		int numberOfSucceses = 101;
		double[][] data = new double[][]{ new double[]{ 0.0 , 2.75646034603961E-12 , 2.75646034603961E-12 , 1.0 } , new double[]{ 1.0 , 8.55705370142386E-11 , 8.83269973602783E-11 , 0.999999999997244 } , new double[]{ 2.0 , 1.31288129219665E-9 , 1.40120828955693E-9 , 0.999999999911673 } , new double[]{ 3.0 , 1.32724172984193E-8 , 1.46736255879763E-8 , 0.999999998598792 } , new double[]{ 4.0 , 9.94501711734089E-8 , 1.14123796761385E-7 , 0.999999985326375 } , new double[]{ 5.0 , 5.89080768883643E-7 , 7.03204565645028E-7 , 0.999999885876203 } , new double[]{ 20.0 , 0.0760051397707708 , 0.27349758476299 , 0.802507555007781 } , new double[]{ 21.0 , 0.087144222047629 , 0.360641806810619 , 0.72650241523701 } , new double[]{ 22.0 , 0.0940378846881819 , 0.454679691498801 , 0.639358193189381 } , new double[]{ 23.0 , 0.0956897500614809 , 0.550369441560282 , 0.545320308501199 } , new double[]{ 24.0 , 0.0919766921922999 , 0.642346133752582 , 0.449630558439718 } , new double[]{ 25.0 , 0.083641637261095 , 0.725987771013677 , 0.357653866247418 } , new double[]{ 96.0 , 5.93849188852098E-57 , 1.0 , 6.01900244560712E-57 } , new double[]{ 97.0 , 7.96593036832547E-59 , 1.0 , 8.05105570861321E-59 } , new double[]{ 98.0 , 8.44582921934367E-61 , 1.0 , 8.5125340287733E-61 } , new double[]{ 99.0 , 6.63604297068222E-63 , 1.0 , 6.670480942963E-63 } , new double[]{ 100.0 , 3.43501099007557E-65 , 1.0 , 3.4437972280786E-65 } , new double[]{ 101.0 , 8.78623800302957E-68 , 1.0 , 8.78623800302957E-68 } };
		testHypergeometricDistributionProbabilities(populationSize, sampleSize, numberOfSucceses, data);
	}

	private void testHypergeometricDistributionProbabilities(int populationSize, int sampleSize, int numberOfSucceses, double[][] data) {
		org.apache.commons.math.distribution.HypergeometricDistributionImpl dist = new org.apache.commons.math.distribution.HypergeometricDistributionImpl(populationSize , numberOfSucceses , sampleSize);
		for (int i = 0 ; i < (data.length) ; ++i) {
			int x = ((int)(data[i][0]));
			double pdf = data[i][1];
			double actualPdf = dist.probability(x);
			org.apache.commons.math.TestUtils.assertRelativelyEquals((("Expected equals for <" + x) + "> pdf"), pdf, actualPdf, 1.0E-9);
			double cdf = data[i][2];
			double actualCdf = dist.cumulativeProbability(x);
			org.apache.commons.math.TestUtils.assertRelativelyEquals((("Expected equals for <" + x) + "> cdf"), cdf, actualCdf, 1.0E-9);
			double cdf1 = data[i][3];
			double actualCdf1 = dist.upperCumulativeProbability(x);
			org.apache.commons.math.TestUtils.assertRelativelyEquals((("Expected equals for <" + x) + "> cdf1"), cdf1, actualCdf1, 1.0E-9);
		}
	}

	public void testMoreLargeValues() {
		int populationSize = 26896;
		int sampleSize = 895;
		int numberOfSucceses = 55;
		double[][] data = new double[][]{ new double[]{ 0.0 , 0.155168304750504 , 0.155168304750504 , 1.0 } , new double[]{ 1.0 , 0.29437545000746 , 0.449543754757964 , 0.844831695249496 } , new double[]{ 2.0 , 0.273841321577003 , 0.723385076334967 , 0.550456245242036 } , new double[]{ 3.0 , 0.166488572570786 , 0.889873648905753 , 0.276614923665033 } , new double[]{ 4.0 , 0.0743969744713231 , 0.964270623377076 , 0.110126351094247 } , new double[]{ 5.0 , 0.0260542785784855 , 0.990324901955562 , 0.0357293766229237 } , new double[]{ 20.0 , 3.57101101678792E-16 , 1.0 , 3.78252101622096E-16 } , new double[]{ 21.0 , 2.00551638598312E-17 , 1.0 , 2.11509999433041E-17 } , new double[]{ 22.0 , 1.04317070180562E-18 , 1.0 , 1.09583608347287E-18 } , new double[]{ 23.0 , 5.03153504903308E-20 , 1.0 , 5.266538166725E-20 } , new double[]{ 24.0 , 2.2525984149695E-21 , 1.0 , 2.35003117691919E-21 } , new double[]{ 25.0 , 9.3677424515947E-23 , 1.0 , 9.74327619496943E-23 } , new double[]{ 50.0 , 9.83633962945521E-69 , 1.0 , 9.8677629437617E-69 } , new double[]{ 51.0 , 3.13448949497553E-71 , 1.0 , 3.14233143064882E-71 } , new double[]{ 52.0 , 7.82755221928122E-74 , 1.0 , 7.84193567329055E-74 } , new double[]{ 53.0 , 1.43662126065532E-76 , 1.0 , 1.43834540093295E-76 } , new double[]{ 54.0 , 1.72312692517348E-79 , 1.0 , 1.7241402776278E-79 } , new double[]{ 55.0 , 1.01335245432581E-82 , 1.0 , 1.01335245432581E-82 } };
		testHypergeometricDistributionProbabilities(populationSize, sampleSize, numberOfSucceses, data);
	}
}

