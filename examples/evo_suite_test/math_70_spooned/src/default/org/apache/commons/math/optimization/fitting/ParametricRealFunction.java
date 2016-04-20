package org.apache.commons.math.optimization.fitting;


public interface ParametricRealFunction {
	double value(double x, double[] parameters) throws org.apache.commons.math.FunctionEvaluationException;

	double[] gradient(double x, double[] parameters) throws org.apache.commons.math.FunctionEvaluationException;
}

