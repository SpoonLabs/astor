package org.apache.commons.math.stat.regression;


public abstract class MultipleLinearRegressionAbstractTest {
	protected org.apache.commons.math.stat.regression.MultipleLinearRegression regression;

	@org.junit.Before
	public void setUp() {
		regression = createRegression();
	}

	protected abstract org.apache.commons.math.stat.regression.MultipleLinearRegression createRegression();

	protected abstract int getNumberOfRegressors();

	protected abstract int getSampleSize();

	@org.junit.Test
	public void canEstimateRegressionParameters() {
		double[] beta = regression.estimateRegressionParameters();
		org.junit.Assert.assertEquals(getNumberOfRegressors(), beta.length);
	}

	@org.junit.Test
	public void canEstimateResiduals() {
		double[] e = regression.estimateResiduals();
		org.junit.Assert.assertEquals(getSampleSize(), e.length);
	}

	@org.junit.Test
	public void canEstimateRegressionParametersVariance() {
		double[][] variance = regression.estimateRegressionParametersVariance();
		org.junit.Assert.assertEquals(getNumberOfRegressors(), variance.length);
	}

	@org.junit.Test
	public void canEstimateRegressandVariance() {
		if ((getSampleSize()) > (getNumberOfRegressors())) {
			double variance = regression.estimateRegressandVariance();
			org.junit.Assert.assertTrue((variance > 0.0));
		} 
	}
}

