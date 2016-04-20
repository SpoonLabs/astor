package org.apache.commons.math.distribution;


public abstract class ContinuousDistributionAbstractTest extends junit.framework.TestCase {
	private org.apache.commons.math.distribution.ContinuousDistribution distribution;

	private double tolerance = 1.0E-4;

	private double[] cumulativeTestPoints;

	private double[] cumulativeTestValues;

	private double[] inverseCumulativeTestPoints;

	private double[] inverseCumulativeTestValues;

	private double[] densityTestValues;

	public ContinuousDistributionAbstractTest(java.lang.String name) {
		super(name);
	}

	public abstract org.apache.commons.math.distribution.ContinuousDistribution makeDistribution();

	public abstract double[] makeCumulativeTestPoints();

	public abstract double[] makeCumulativeTestValues();

	public abstract double[] makeDensityTestValues();

	public double[] makeInverseCumulativeTestPoints() {
		return makeCumulativeTestValues();
	}

	public double[] makeInverseCumulativeTestValues() {
		return makeCumulativeTestPoints();
	}

	@java.lang.Override
	protected void setUp() throws java.lang.Exception {
		super.setUp();
		distribution = makeDistribution();
		cumulativeTestPoints = makeCumulativeTestPoints();
		cumulativeTestValues = makeCumulativeTestValues();
		inverseCumulativeTestPoints = makeInverseCumulativeTestPoints();
		inverseCumulativeTestValues = makeInverseCumulativeTestValues();
		densityTestValues = makeDensityTestValues();
	}

	@java.lang.Override
	protected void tearDown() throws java.lang.Exception {
		super.tearDown();
		distribution = null;
		cumulativeTestPoints = null;
		cumulativeTestValues = null;
		inverseCumulativeTestPoints = null;
		inverseCumulativeTestValues = null;
		densityTestValues = null;
	}

	protected void verifyCumulativeProbabilities() throws java.lang.Exception {
		for (int i = 0 ; i < (cumulativeTestPoints.length) ; i++) {
			org.apache.commons.math.TestUtils.assertEquals(("Incorrect cumulative probability value returned for " + (cumulativeTestPoints[i])), cumulativeTestValues[i], distribution.cumulativeProbability(cumulativeTestPoints[i]), getTolerance());
		}
	}

	protected void verifyInverseCumulativeProbabilities() throws java.lang.Exception {
		for (int i = 0 ; i < (inverseCumulativeTestPoints.length) ; i++) {
			org.apache.commons.math.TestUtils.assertEquals(("Incorrect inverse cumulative probability value returned for " + (inverseCumulativeTestPoints[i])), inverseCumulativeTestValues[i], distribution.inverseCumulativeProbability(inverseCumulativeTestPoints[i]), getTolerance());
		}
	}

	protected void verifyDensities() throws java.lang.Exception {
		for (int i = 0 ; i < (cumulativeTestPoints.length) ; i++) {
			org.apache.commons.math.TestUtils.assertEquals(("Incorrect probability density value returned for " + (cumulativeTestPoints[i])), densityTestValues[i], ((org.apache.commons.math.distribution.AbstractContinuousDistribution)(distribution)).density(cumulativeTestPoints[i]), getTolerance());
		}
	}

	public void testCumulativeProbabilities() throws java.lang.Exception {
		verifyCumulativeProbabilities();
	}

	public void testInverseCumulativeProbabilities() throws java.lang.Exception {
		verifyInverseCumulativeProbabilities();
	}

	public void testDensities() throws java.lang.Exception {
		verifyDensities();
	}

	public void testConsistency() throws java.lang.Exception {
		for (int i = 1 ; i < (cumulativeTestPoints.length) ; i++) {
			org.apache.commons.math.TestUtils.assertEquals(0.0, distribution.cumulativeProbability(cumulativeTestPoints[i], cumulativeTestPoints[i]), tolerance);
			double upper = java.lang.Math.max(cumulativeTestPoints[i], cumulativeTestPoints[(i - 1)]);
			double lower = java.lang.Math.min(cumulativeTestPoints[i], cumulativeTestPoints[(i - 1)]);
			double diff = (distribution.cumulativeProbability(upper)) - (distribution.cumulativeProbability(lower));
			double direct = distribution.cumulativeProbability(lower, upper);
			org.apache.commons.math.TestUtils.assertEquals((((("Inconsistent cumulative probabilities for (" + lower) + ",") + upper) + ")"), diff, direct, tolerance);
		}
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

	protected double[] getCumulativeTestPoints() {
		return cumulativeTestPoints;
	}

	protected void setCumulativeTestPoints(double[] cumulativeTestPoints) {
		this.cumulativeTestPoints = cumulativeTestPoints;
	}

	protected double[] getCumulativeTestValues() {
		return cumulativeTestValues;
	}

	protected void setCumulativeTestValues(double[] cumulativeTestValues) {
		this.cumulativeTestValues = cumulativeTestValues;
	}

	protected double[] getDensityTestValues() {
		return densityTestValues;
	}

	protected void setDensityTestValues(double[] densityTestValues) {
		this.densityTestValues = densityTestValues;
	}

	protected org.apache.commons.math.distribution.ContinuousDistribution getDistribution() {
		return distribution;
	}

	protected void setDistribution(org.apache.commons.math.distribution.AbstractContinuousDistribution distribution) {
		this.distribution = distribution;
	}

	protected double[] getInverseCumulativeTestPoints() {
		return inverseCumulativeTestPoints;
	}

	protected void setInverseCumulativeTestPoints(double[] inverseCumulativeTestPoints) {
		this.inverseCumulativeTestPoints = inverseCumulativeTestPoints;
	}

	protected double[] getInverseCumulativeTestValues() {
		return inverseCumulativeTestValues;
	}

	protected void setInverseCumulativeTestValues(double[] inverseCumulativeTestValues) {
		this.inverseCumulativeTestValues = inverseCumulativeTestValues;
	}

	protected double getTolerance() {
		return tolerance;
	}

	protected void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}
}

