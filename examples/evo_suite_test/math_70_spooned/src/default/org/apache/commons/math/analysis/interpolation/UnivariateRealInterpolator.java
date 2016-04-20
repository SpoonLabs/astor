package org.apache.commons.math.analysis.interpolation;


public interface UnivariateRealInterpolator {
	org.apache.commons.math.analysis.UnivariateRealFunction interpolate(double[] xval, double[] yval) throws org.apache.commons.math.MathException;
}

