package org.apache.commons.math.distribution;


public abstract class IntegerDistributionAbstractTest extends junit.framework.TestCase {
	private org.apache.commons.math.distribution.IntegerDistribution distribution;

	private double tolerance = 1.0E-4;

	private int[] densityTestPoints;

	private double[] densityTestValues;

	private int[] cumulativeTestPoints;

	private double[] cumulativeTestValues;

	private double[] inverseCumulativeTestPoints;

	private int[] inverseCumulativeTestValues;

	public IntegerDistributionAbstractTest(java.lang.String name) {
		super(name);
	}

	public abstract org.apache.commons.math.distribution.IntegerDistribution makeDistribution();

	public abstract int[] makeDensityTestPoints();

	public abstract double[] makeDensityTestValues();

	public abstract int[] makeCumulativeTestPoints();

	public abstract double[] makeCumulativeTestValues();

	public abstract double[] makeInverseCumulativeTestPoints();

	public abstract int[] makeInverseCumulativeTestValues();

	@java.lang.Override
	protected void setUp() throws java.lang.Exception {
		super.setUp();
		distribution = makeDistribution();
		densityTestPoints = makeDensityTestPoints();
		densityTestValues = makeDensityTestValues();
		cumulativeTestPoints = makeCumulativeTestPoints();
		cumulativeTestValues = makeCumulativeTestValues();
		inverseCumulativeTestPoints = makeInverseCumulativeTestPoints();
		inverseCumulativeTestValues = makeInverseCumulativeTestValues();
	}

	@java.lang.Override
	protected void tearDown() throws java.lang.Exception {
		super.tearDown();
		distribution = null;
		densityTestPoints = null;
		densityTestValues = null;
		cumulativeTestPoints = null;
		cumulativeTestValues = null;
		inverseCumulativeTestPoints = null;
		inverseCumulativeTestValues = null;
	}

	protected void verifyDensities() throws java.lang.Exception {
		for (int i = 0 ; i < (densityTestPoints.length) ; i++) {
			junit.framework.Assert.assertEquals(("Incorrect density value returned for " + (densityTestPoints[i])), densityTestValues[i], distribution.probability(densityTestPoints[i]), tolerance);
		}
	}

	protected void verifyCumulativeProbabilities() throws java.lang.Exception {
		for (int i = 0 ; i < (cumulativeTestPoints.length) ; i++) {
			junit.framework.Assert.assertEquals(("Incorrect cumulative probability value returned for " + (cumulativeTestPoints[i])), cumulativeTestValues[i], distribution.cumulativeProbability(cumulativeTestPoints[i]), tolerance);
		}
	}

	protected void verifyInverseCumulativeProbabilities() throws java.lang.Exception {
		for (int i = 0 ; i < (inverseCumulativeTestPoints.length) ; i++) {
			junit.framework.Assert.assertEquals(("Incorrect inverse cumulative probability value returned for " + (inverseCumulativeTestPoints[i])), inverseCumulativeTestValues[i], distribution.inverseCumulativeProbability(inverseCumulativeTestPoints[i]));
		}
	}

	public void testDensities() throws java.lang.Exception {
		verifyDensities();
	}

	public void testCumulativeProbabilities() throws java.lang.Exception {
		verifyCumulativeProbabilities();
	}

	public void testFloatingPointArguments() throws java.lang.Exception {
		for (int i = 0 ; i < (cumulativeTestPoints.length) ; i++) {
			double arg = cumulativeTestPoints[i];
			junit.framework.Assert.assertEquals(("Incorrect cumulative probability value returned for " + (cumulativeTestPoints[i])), cumulativeTestValues[i], distribution.cumulativeProbability(arg), tolerance);
			if (i < ((cumulativeTestPoints.length) - 1)) {
				double arg2 = cumulativeTestPoints[(i + 1)];
				junit.framework.Assert.assertEquals(((((("Inconsistent probability for discrete range " + "[ ") + arg) + ",") + arg2) + " ]"), distribution.cumulativeProbability(cumulativeTestPoints[i], cumulativeTestPoints[(i + 1)]), distribution.cumulativeProbability(arg, arg2), tolerance);
				arg = arg - (java.lang.Math.random());
				arg2 = arg2 + (java.lang.Math.random());
				junit.framework.Assert.assertEquals(((((("Inconsistent probability for discrete range " + "[ ") + arg) + ",") + arg2) + " ]"), distribution.cumulativeProbability(cumulativeTestPoints[i], cumulativeTestPoints[(i + 1)]), distribution.cumulativeProbability(arg, arg2), tolerance);
			} 
		}
		int one = 1;
		int ten = 10;
		int two = 2;
		double oned = one;
		double twod = two;
		double tend = ten;
		junit.framework.Assert.assertEquals(distribution.cumulativeProbability(one, two), distribution.cumulativeProbability(oned, twod), tolerance);
		junit.framework.Assert.assertEquals(distribution.cumulativeProbability(one, two), distribution.cumulativeProbability((oned - (tolerance)), (twod + 0.9)), tolerance);
		junit.framework.Assert.assertEquals(distribution.cumulativeProbability(two, ten), distribution.cumulativeProbability(twod, tend), tolerance);
		junit.framework.Assert.assertEquals(distribution.cumulativeProbability(two, ten), distribution.cumulativeProbability((twod - (tolerance)), (tend + 0.9)), tolerance);
	}

	public void testInverseCumulativeProbabilities() throws java.lang.Exception {
		verifyInverseCumulativeProbabilities();
	}

	public void testIllegalArguments() throws java.lang.Exception {
		try {
			distribution.cumulativeProbability(1, 0);
			junit.framework.Assert.fail("Expecting IllegalArgumentException for bad cumulativeProbability interval");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			distribution.inverseCumulativeProbability(-1);
			junit.framework.Assert.fail("Expecting IllegalArgumentException for p = -1");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			distribution.inverseCumulativeProbability(2);
			junit.framework.Assert.fail("Expecting IllegalArgumentException for p = 2");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	protected int[] getCumulativeTestPoints() {
		return cumulativeTestPoints;
	}

	protected void setCumulativeTestPoints(int[] cumulativeTestPoints) {
		this.cumulativeTestPoints = cumulativeTestPoints;
	}

	protected double[] getCumulativeTestValues() {
		return cumulativeTestValues;
	}

	protected void setCumulativeTestValues(double[] cumulativeTestValues) {
		this.cumulativeTestValues = cumulativeTestValues;
	}

	protected int[] getDensityTestPoints() {
		return densityTestPoints;
	}

	protected void setDensityTestPoints(int[] densityTestPoints) {
		this.densityTestPoints = densityTestPoints;
	}

	protected double[] getDensityTestValues() {
		return densityTestValues;
	}

	protected void setDensityTestValues(double[] densityTestValues) {
		this.densityTestValues = densityTestValues;
	}

	protected org.apache.commons.math.distribution.IntegerDistribution getDistribution() {
		return distribution;
	}

	protected void setDistribution(org.apache.commons.math.distribution.IntegerDistribution distribution) {
		this.distribution = distribution;
	}

	protected double[] getInverseCumulativeTestPoints() {
		return inverseCumulativeTestPoints;
	}

	protected void setInverseCumulativeTestPoints(double[] inverseCumulativeTestPoints) {
		this.inverseCumulativeTestPoints = inverseCumulativeTestPoints;
	}

	protected int[] getInverseCumulativeTestValues() {
		return inverseCumulativeTestValues;
	}

	protected void setInverseCumulativeTestValues(int[] inverseCumulativeTestValues) {
		this.inverseCumulativeTestValues = inverseCumulativeTestValues;
	}

	protected double getTolerance() {
		return tolerance;
	}

	protected void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}
}

