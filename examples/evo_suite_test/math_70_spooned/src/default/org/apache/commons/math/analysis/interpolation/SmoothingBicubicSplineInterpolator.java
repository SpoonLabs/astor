package org.apache.commons.math.analysis.interpolation;


public class SmoothingBicubicSplineInterpolator implements org.apache.commons.math.analysis.interpolation.BivariateRealGridInterpolator {
	public org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction interpolate(final double[] xval, final double[] yval, final double[][] zval) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		if ((((xval.length) == 0) || ((yval.length) == 0)) || ((zval.length) == 0)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("no data");
		} 
		if ((xval.length) != (zval.length)) {
			throw new org.apache.commons.math.DimensionMismatchException(xval.length , zval.length);
		} 
		org.apache.commons.math.util.MathUtils.checkOrder(xval, 1, true);
		org.apache.commons.math.util.MathUtils.checkOrder(yval, 1, true);
		final int xLen = xval.length;
		final int yLen = yval.length;
		final double[][] zX = new double[yLen][xLen];
		for (int i = 0 ; i < xLen ; i++) {
			if ((zval[i].length) != yLen) {
				throw new org.apache.commons.math.DimensionMismatchException(zval[i].length , yLen);
			} 
			for (int j = 0 ; j < yLen ; j++) {
				zX[j][i] = zval[i][j];
			}
		}
		final org.apache.commons.math.analysis.interpolation.SplineInterpolator spInterpolator = new org.apache.commons.math.analysis.interpolation.SplineInterpolator();
		final org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction[] ySplineX = new org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction[yLen];
		for (int j = 0 ; j < yLen ; j++) {
			ySplineX[j] = spInterpolator.interpolate(xval, zX[j]);
		}
		final double[][] zY_1 = new double[xLen][yLen];
		for (int j = 0 ; j < yLen ; j++) {
			final org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction f = ySplineX[j];
			for (int i = 0 ; i < xLen ; i++) {
				zY_1[i][j] = f.value(xval[i]);
			}
		}
		final org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction[] xSplineY = new org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction[xLen];
		for (int i = 0 ; i < xLen ; i++) {
			xSplineY[i] = spInterpolator.interpolate(yval, zY_1[i]);
		}
		final double[][] zY_2 = new double[xLen][yLen];
		for (int i = 0 ; i < xLen ; i++) {
			final org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction f = xSplineY[i];
			for (int j = 0 ; j < yLen ; j++) {
				zY_2[i][j] = f.value(yval[j]);
			}
		}
		final double[][] dZdX = new double[xLen][yLen];
		for (int j = 0 ; j < yLen ; j++) {
			final org.apache.commons.math.analysis.UnivariateRealFunction f = ySplineX[j].derivative();
			for (int i = 0 ; i < xLen ; i++) {
				dZdX[i][j] = f.value(xval[i]);
			}
		}
		final double[][] dZdY = new double[xLen][yLen];
		for (int i = 0 ; i < xLen ; i++) {
			final org.apache.commons.math.analysis.UnivariateRealFunction f = xSplineY[i].derivative();
			for (int j = 0 ; j < yLen ; j++) {
				dZdY[i][j] = f.value(yval[j]);
			}
		}
		final double[][] dZdXdY = new double[xLen][yLen];
		for (int i = 0 ; i < xLen ; i++) {
			final int nI = nextIndex(i, xLen);
			final int pI = previousIndex(i);
			for (int j = 0 ; j < yLen ; j++) {
				final int nJ = nextIndex(j, yLen);
				final int pJ = previousIndex(j);
				dZdXdY[i][j] = ((((zY_2[nI][nJ]) - (zY_2[nI][pJ])) - (zY_2[pI][nJ])) + (zY_2[pI][pJ])) / (((xval[nI]) - (xval[pI])) * ((yval[nJ]) - (yval[pJ])));
			}
		}
		return new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction(xval , yval , zY_2 , dZdX , dZdY , dZdXdY);
	}

	private int nextIndex(int i, int max) {
		final int index = i + 1;
		return index < max ? index : index - 1;
	}

	private int previousIndex(int i) {
		final int index = i - 1;
		return index >= 0 ? index : 0;
	}
}

