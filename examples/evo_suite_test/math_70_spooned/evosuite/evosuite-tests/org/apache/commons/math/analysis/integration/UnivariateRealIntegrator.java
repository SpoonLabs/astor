package org.apache.commons.math.analysis.integration;


public interface UnivariateRealIntegrator extends org.apache.commons.math.ConvergingAlgorithm {
	void setMinimalIterationCount(int count);

	int getMinimalIterationCount();

	void resetMinimalIterationCount();

	@java.lang.Deprecated
	double integrate(double min, double max) throws java.lang.IllegalArgumentException, org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException;

	double integrate(org.apache.commons.math.analysis.UnivariateRealFunction f, double min, double max) throws java.lang.IllegalArgumentException, org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException;

	double getResult() throws java.lang.IllegalStateException;
}

