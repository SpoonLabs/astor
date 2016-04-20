package org.apache.commons.math.analysis.interpolation;


public interface BivariateRealGridInterpolator {
	org.apache.commons.math.analysis.BivariateRealFunction interpolate(double[] xval, double[] yval, double[][] fval) throws org.apache.commons.math.MathException;
}

