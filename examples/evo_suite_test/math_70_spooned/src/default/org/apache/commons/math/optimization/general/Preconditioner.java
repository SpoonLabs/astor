package org.apache.commons.math.optimization.general;


public interface Preconditioner {
	double[] precondition(double[] point, double[] r) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException;
}

