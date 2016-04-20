package org.apache.commons.math.stat.regression;


public interface MultipleLinearRegression {
	double[] estimateRegressionParameters();

	double[][] estimateRegressionParametersVariance();

	double[] estimateResiduals();

	double estimateRegressandVariance();

	double[] estimateRegressionParametersStandardErrors();
}

