package org.apache.commons.math.analysis.interpolation;


public class TricubicSplineInterpolator implements org.apache.commons.math.analysis.interpolation.TrivariateRealGridInterpolator {
	public org.apache.commons.math.analysis.interpolation.TricubicSplineInterpolatingFunction interpolate(final double[] xval, final double[] yval, final double[] zval, final double[][][] fval) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		if (((((xval.length) == 0) || ((yval.length) == 0)) || ((zval.length) == 0)) || ((fval.length) == 0)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("no data");
		} 
		if ((xval.length) != (fval.length)) {
			throw new org.apache.commons.math.DimensionMismatchException(xval.length , fval.length);
		} 
		org.apache.commons.math.util.MathUtils.checkOrder(xval, 1, true);
		org.apache.commons.math.util.MathUtils.checkOrder(yval, 1, true);
		org.apache.commons.math.util.MathUtils.checkOrder(zval, 1, true);
		final int xLen = xval.length;
		final int yLen = yval.length;
		final int zLen = zval.length;
		final double[][][] fvalXY = new double[zLen][xLen][yLen];
		final double[][][] fvalZX = new double[yLen][zLen][xLen];
		for (int i = 0 ; i < xLen ; i++) {
			if ((fval[i].length) != yLen) {
				throw new org.apache.commons.math.DimensionMismatchException(fval[i].length , yLen);
			} 
			for (int j = 0 ; j < yLen ; j++) {
				if ((fval[i][j].length) != zLen) {
					throw new org.apache.commons.math.DimensionMismatchException(fval[i][j].length , zLen);
				} 
				for (int k = 0 ; k < zLen ; k++) {
					final double v = fval[i][j][k];
					fvalXY[k][i][j] = v;
					fvalZX[j][k][i] = v;
				}
			}
		}
		final org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolator bsi = new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolator();
		final org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction[] xSplineYZ = new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction[xLen];
		for (int i = 0 ; i < xLen ; i++) {
			xSplineYZ[i] = bsi.interpolate(yval, zval, fval[i]);
		}
		final org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction[] ySplineZX = new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction[yLen];
		for (int j = 0 ; j < yLen ; j++) {
			ySplineZX[j] = bsi.interpolate(zval, xval, fvalZX[j]);
		}
		final org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction[] zSplineXY = new org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction[zLen];
		for (int k = 0 ; k < zLen ; k++) {
			zSplineXY[k] = bsi.interpolate(xval, yval, fvalXY[k]);
		}
		final double[][][] dFdX = new double[xLen][yLen][zLen];
		final double[][][] dFdY = new double[xLen][yLen][zLen];
		final double[][][] d2FdXdY = new double[xLen][yLen][zLen];
		for (int k = 0 ; k < zLen ; k++) {
			final org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction f = zSplineXY[k];
			for (int i = 0 ; i < xLen ; i++) {
				final double x = xval[i];
				for (int j = 0 ; j < yLen ; j++) {
					final double y = yval[j];
					dFdX[i][j][k] = f.partialDerivativeX(x, y);
					dFdY[i][j][k] = f.partialDerivativeY(x, y);
					d2FdXdY[i][j][k] = f.partialDerivativeXY(x, y);
				}
			}
		}
		final double[][][] dFdZ = new double[xLen][yLen][zLen];
		final double[][][] d2FdYdZ = new double[xLen][yLen][zLen];
		for (int i = 0 ; i < xLen ; i++) {
			final org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction f = xSplineYZ[i];
			for (int j = 0 ; j < yLen ; j++) {
				final double y = yval[j];
				for (int k = 0 ; k < zLen ; k++) {
					final double z = zval[k];
					dFdZ[i][j][k] = f.partialDerivativeY(y, z);
					d2FdYdZ[i][j][k] = f.partialDerivativeXY(y, z);
				}
			}
		}
		final double[][][] d2FdZdX = new double[xLen][yLen][zLen];
		for (int j = 0 ; j < yLen ; j++) {
			final org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolatingFunction f = ySplineZX[j];
			for (int k = 0 ; k < zLen ; k++) {
				final double z = zval[k];
				for (int i = 0 ; i < xLen ; i++) {
					final double x = xval[i];
					d2FdZdX[i][j][k] = f.partialDerivativeXY(z, x);
				}
			}
		}
		final double[][][] d3FdXdYdZ = new double[xLen][yLen][zLen];
		for (int i = 0 ; i < xLen ; i++) {
			final int nI = nextIndex(i, xLen);
			final int pI = previousIndex(i);
			for (int j = 0 ; j < yLen ; j++) {
				final int nJ = nextIndex(j, yLen);
				final int pJ = previousIndex(j);
				for (int k = 0 ; k < zLen ; k++) {
					final int nK = nextIndex(k, zLen);
					final int pK = previousIndex(k);
					d3FdXdYdZ[i][j][k] = ((((((((fval[nI][nJ][nK]) - (fval[nI][pJ][nK])) - (fval[pI][nJ][nK])) + (fval[pI][pJ][nK])) - (fval[nI][nJ][pK])) + (fval[nI][pJ][pK])) + (fval[pI][nJ][pK])) - (fval[pI][pJ][pK])) / ((((xval[nI]) - (xval[pI])) * ((yval[nJ]) - (yval[pJ]))) * ((zval[nK]) - (zval[pK])));
				}
			}
		}
		return new org.apache.commons.math.analysis.interpolation.TricubicSplineInterpolatingFunction(xval , yval , zval , fval , dFdX , dFdY , dFdZ , d2FdXdY , d2FdZdX , d2FdYdZ , d3FdXdYdZ);
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

