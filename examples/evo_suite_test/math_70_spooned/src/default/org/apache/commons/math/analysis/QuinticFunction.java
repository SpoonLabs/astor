package org.apache.commons.math.analysis;


public class QuinticFunction implements org.apache.commons.math.analysis.DifferentiableUnivariateRealFunction {
	public double value(double x) throws org.apache.commons.math.FunctionEvaluationException {
		return ((((x - 1) * (x - 0.5)) * x) * (x + 0.5)) * (x + 1);
	}

	public org.apache.commons.math.analysis.UnivariateRealFunction derivative() {
		return new org.apache.commons.math.analysis.UnivariateRealFunction() {
			public double value(double x) throws org.apache.commons.math.FunctionEvaluationException {
				return (((((5 * x) * x) - 3.75) * x) * x) + 0.25;
			}
		};
	}
}

