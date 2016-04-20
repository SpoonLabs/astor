package org.apache.commons.math.analysis.interpolation;


public class SmoothingPolynomialBicubicSplineInterpolator extends org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolator {
	private final org.apache.commons.math.optimization.fitting.PolynomialFitter xFitter;

	private final org.apache.commons.math.optimization.fitting.PolynomialFitter yFitter;

	public SmoothingPolynomialBicubicSplineInterpolator() {
		this(3);
	}

	public SmoothingPolynomialBicubicSplineInterpolator(int degree) {
		this(degree, degree);
	}

	public SmoothingPolynomialBicubicSplineInterpolator(int xDegree ,int yDegree) {
		xFitter = new org.apache.commons.math.optimization.fitting.PolynomialFitter(xDegree , new org.apache.commons.math.optimization.general.GaussNewtonOptimizer(false));
		yFitter = new org.apache.commons.math.optimization.fitting.PolynomialFitter(yDegree , new org.apache.commons.math.optimization.general.GaussNewtonOptimizer(false));
	}

	public org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction interpolate(final double[] xval, final double[] yval, final double[][] fval) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		if ((((xval.length) == 0) || ((yval.length) == 0)) || ((fval.length) == 0)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("no data");
		} 
		if ((xval.length) != (fval.length)) {
			throw new org.apache.commons.math.DimensionMismatchException(xval.length , fval.length);
		} 
		final int xLen = xval.length;
		final int yLen = yval.length;
		for (int i = 0 ; i < xLen ; i++) {
			if ((fval[i].length) != yLen) {
				throw new org.apache.commons.math.DimensionMismatchException(fval[i].length , yLen);
			} 
		}
		org.apache.commons.math.util.MathUtils.checkOrder(xval, 1, true);
		org.apache.commons.math.util.MathUtils.checkOrder(yval, 1, true);
		final org.apache.commons.math.analysis.polynomials.PolynomialFunction[] yPolyX = new org.apache.commons.math.analysis.polynomials.PolynomialFunction[yLen];
		for (int j = 0 ; j < yLen ; j++) {
			xFitter.clearObservations();
			for (int i = 0 ; i < xLen ; i++) {
				xFitter.addObservedPoint(1, xval[i], fval[i][j]);
			}
			yPolyX[j] = xFitter.fit();
		}
		final double[][] fval_1 = new double[xLen][yLen];
		for (int j = 0 ; j < yLen ; j++) {
			final org.apache.commons.math.analysis.polynomials.PolynomialFunction f = yPolyX[j];
			for (int i = 0 ; i < xLen ; i++) {
				fval_1[i][j] = f.value(xval[i]);
			}
		}
		final org.apache.commons.math.analysis.polynomials.PolynomialFunction[] xPolyY = new org.apache.commons.math.analysis.polynomials.PolynomialFunction[xLen];
		for (int i = 0 ; i < xLen ; i++) {
			yFitter.clearObservations();
			for (int j = 0 ; j < yLen ; j++) {
				yFitter.addObservedPoint(1, yval[j], fval_1[i][j]);
			}
			xPolyY[i] = yFitter.fit();
		}
		final double[][] fval_2 = new double[xLen][yLen];
		for (int i = 0 ; i < xLen ; i++) {
			final org.apache.commons.math.analysis.polynomials.PolynomialFunction f = xPolyY[i];
			for (int j = 0 ; j < yLen ; j++) {
				fval_2[i][j] = f.value(yval[j]);
			}
		}
		return super.interpolate(xval, yval, fval_2);
	}
}

