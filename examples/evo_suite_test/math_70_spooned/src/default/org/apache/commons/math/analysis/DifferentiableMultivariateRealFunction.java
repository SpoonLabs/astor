package org.apache.commons.math.analysis;


public interface DifferentiableMultivariateRealFunction extends org.apache.commons.math.analysis.MultivariateRealFunction {
	org.apache.commons.math.analysis.MultivariateRealFunction partialDerivative(int k);

	org.apache.commons.math.analysis.MultivariateVectorialFunction gradient();
}

