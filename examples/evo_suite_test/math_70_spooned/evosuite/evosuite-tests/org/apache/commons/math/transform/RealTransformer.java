package org.apache.commons.math.transform;


public interface RealTransformer {
	double[] transform(double[] f) throws java.lang.IllegalArgumentException;

	double[] transform(org.apache.commons.math.analysis.UnivariateRealFunction f, double min, double max, int n) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException;

	double[] inversetransform(double[] f) throws java.lang.IllegalArgumentException;

	double[] inversetransform(org.apache.commons.math.analysis.UnivariateRealFunction f, double min, double max, int n) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException;
}

