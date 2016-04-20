package org.apache.commons.math.analysis;


public class Expm1Function implements org.apache.commons.math.analysis.DifferentiableUnivariateRealFunction {
	public double value(double x) throws org.apache.commons.math.FunctionEvaluationException {
		return (java.lang.Math.exp(x)) - 1.0;
	}

	public org.apache.commons.math.analysis.UnivariateRealFunction derivative() {
		return new org.apache.commons.math.analysis.UnivariateRealFunction() {
			public double value(double x) throws org.apache.commons.math.FunctionEvaluationException {
				return java.lang.Math.exp(x);
			}
		};
	}
}

