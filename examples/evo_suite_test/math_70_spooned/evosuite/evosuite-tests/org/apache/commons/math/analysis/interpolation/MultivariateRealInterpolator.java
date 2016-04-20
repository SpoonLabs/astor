package org.apache.commons.math.analysis.interpolation;


public interface MultivariateRealInterpolator {
	org.apache.commons.math.analysis.MultivariateRealFunction interpolate(double[][] xval, double[] yval) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;
}

