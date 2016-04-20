package org.apache.commons.math.estimation;


@java.lang.Deprecated
public interface Estimator {
	void estimate(org.apache.commons.math.estimation.EstimationProblem problem) throws org.apache.commons.math.estimation.EstimationException;

	double getRMS(org.apache.commons.math.estimation.EstimationProblem problem);

	double[][] getCovariances(org.apache.commons.math.estimation.EstimationProblem problem) throws org.apache.commons.math.estimation.EstimationException;

	double[] guessParametersErrors(org.apache.commons.math.estimation.EstimationProblem problem) throws org.apache.commons.math.estimation.EstimationException;
}

