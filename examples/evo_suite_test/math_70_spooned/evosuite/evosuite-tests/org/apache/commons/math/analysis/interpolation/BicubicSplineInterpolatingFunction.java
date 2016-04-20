package org.apache.commons.math.analysis.interpolation;


public class BicubicSplineInterpolatingFunction implements org.apache.commons.math.analysis.BivariateRealFunction {
	private static final double[][] AINV = new double[][]{ new double[]{ 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ -3 , 3 , 0 , 0 , -2 , -1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 2 , -2 , 0 , 0 , 1 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -3 , 3 , 0 , 0 , -2 , -1 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , -2 , 0 , 0 , 1 , 1 , 0 , 0 } , new double[]{ -3 , 0 , 3 , 0 , 0 , 0 , 0 , 0 , -2 , 0 , -1 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , -3 , 0 , 3 , 0 , 0 , 0 , 0 , 0 , -2 , 0 , -1 , 0 } , new double[]{ 9 , -9 , -9 , 9 , 6 , 3 , -6 , -3 , 6 , -6 , 3 , -3 , 4 , 2 , 2 , 1 } , new double[]{ -6 , 6 , 6 , -6 , -3 , -3 , 3 , 3 , -4 , 4 , -2 , 2 , -2 , -2 , -1 , -1 } , new double[]{ 2 , 0 , -2 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 1 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 2 , 0 , -2 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 1 , 0 } , new double[]{ -6 , 6 , 6 , -6 , -4 , -2 , 4 , 2 , -3 , 3 , -3 , 3 , -2 , -1 , -2 , -1 } , new double[]{ 4 , -4 , -4 , 4 , 2 , 2 , -2 , -2 , 2 , -2 , 2 , -2 , 1 , 1 , 1 , 1 } };

	private final double[] xval;

	private final double[] yval;

	private final org.apache.commons.math.analysis.interpolation.BicubicSplineFunction[][] splines;

	private org.apache.commons.math.analysis.BivariateRealFunction[][][] partialDerivatives = null;

	public BicubicSplineInterpolatingFunction(double[] x ,double[] y ,double[][] f ,double[][] dFdX ,double[][] dFdY ,double[][] d2FdXdY) throws org.apache.commons.math.DimensionMismatchException {
		final int xLen = x.length;
		final int yLen = y.length;
		if ((((xLen == 0) || (yLen == 0)) || ((f.length) == 0)) || ((f[0].length) == 0)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("no data");
		} 
		if (xLen != (f.length)) {
			throw new org.apache.commons.math.DimensionMismatchException(xLen , f.length);
		} 
		if (xLen != (dFdX.length)) {
			throw new org.apache.commons.math.DimensionMismatchException(xLen , dFdX.length);
		} 
		if (xLen != (dFdY.length)) {
			throw new org.apache.commons.math.DimensionMismatchException(xLen , dFdY.length);
		} 
		if (xLen != (d2FdXdY.length)) {
			throw new org.apache.commons.math.DimensionMismatchException(xLen , d2FdXdY.length);
		} 
		org.apache.commons.math.util.MathUtils.checkOrder(x, 1, true);
		org.apache.commons.math.util.MathUtils.checkOrder(y, 1, true);
		xval = x.clone();
		yval = y.clone();
		final int lastI = xLen - 1;
		final int lastJ = yLen - 1;
		splines = new org.apache.commons.math.analysis.interpolation.BicubicSplineFunction[lastI][lastJ];
		for (int i = 0 ; i < lastI ; i++) {
			if ((f[i].length) != yLen) {
				throw new org.apache.commons.math.DimensionMismatchException(f[i].length , yLen);
			} 
			if ((dFdX[i].length) != yLen) {
				throw new org.apache.commons.math.DimensionMismatchException(dFdX[i].length , yLen);
			} 
			if ((dFdY[i].length) != yLen) {
				throw new org.apache.commons.math.DimensionMismatchException(dFdY[i].length , yLen);
			} 
			if ((d2FdXdY[i].length) != yLen) {
				throw new org.apache.commons.math.DimensionMismatchException(d2FdXdY[i].length , yLen);
			} 
			final int ip1 = i + 1;
			for (int j = 0 ; j < lastJ ; j++) {
				final int jp1 = j + 1;
				final double[] beta = new double[]{ f[i][j] , f[ip1][j] , f[i][jp1] , f[ip1][jp1] , dFdX[i][j] , dFdX[ip1][j] , dFdX[i][jp1] , dFdX[ip1][jp1] , dFdY[i][j] , dFdY[ip1][j] , dFdY[i][jp1] , dFdY[ip1][jp1] , d2FdXdY[i][j] , d2FdXdY[ip1][j] , d2FdXdY[i][jp1] , d2FdXdY[ip1][jp1] };
				splines[i][j] = new org.apache.commons.math.analysis.interpolation.BicubicSplineFunction(computeSplineCoefficients(beta));
			}
		}
	}

	public double value(double x, double y) {
		final int i = searchIndex(x, xval);
		if (i == (-1)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range", x, xval[0], xval[((xval.length) - 1)]);
		} 
		final int j = searchIndex(y, yval);
		if (j == (-1)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range", y, yval[0], yval[((yval.length) - 1)]);
		} 
		final double xN = (x - (xval[i])) / ((xval[(i + 1)]) - (xval[i]));
		final double yN = (y - (yval[j])) / ((yval[(j + 1)]) - (yval[j]));
		return splines[i][j].value(xN, yN);
	}

	public double partialDerivativeX(double x, double y) {
		return partialDerivative(0, x, y);
	}

	public double partialDerivativeY(double x, double y) {
		return partialDerivative(1, x, y);
	}

	public double partialDerivativeXX(double x, double y) {
		return partialDerivative(2, x, y);
	}

	public double partialDerivativeYY(double x, double y) {
		return partialDerivative(3, x, y);
	}

	public double partialDerivativeXY(double x, double y) {
		return partialDerivative(4, x, y);
	}

	private double partialDerivative(int which, double x, double y) {
		if ((partialDerivatives) == null) {
			computePartialDerivatives();
		} 
		final int i = searchIndex(x, xval);
		if (i == (-1)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range", x, xval[0], xval[((xval.length) - 1)]);
		} 
		final int j = searchIndex(y, yval);
		if (j == (-1)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range", y, yval[0], yval[((yval.length) - 1)]);
		} 
		final double xN = (x - (xval[i])) / ((xval[(i + 1)]) - (xval[i]));
		final double yN = (y - (yval[j])) / ((yval[(j + 1)]) - (yval[j]));
		double result = java.lang.Double.NaN;
		try {
			result = partialDerivatives[which][i][j].value(xN, yN);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
		}
		return result;
	}

	private void computePartialDerivatives() {
		final int lastI = (xval.length) - 1;
		final int lastJ = (yval.length) - 1;
		partialDerivatives = new org.apache.commons.math.analysis.BivariateRealFunction[5][lastI][lastJ];
		for (int i = 0 ; i < lastI ; i++) {
			for (int j = 0 ; j < lastJ ; j++) {
				final org.apache.commons.math.analysis.interpolation.BicubicSplineFunction f = splines[i][j];
				partialDerivatives[0][i][j] = f.partialDerivativeX();
				partialDerivatives[1][i][j] = f.partialDerivativeY();
				partialDerivatives[2][i][j] = f.partialDerivativeXX();
				partialDerivatives[3][i][j] = f.partialDerivativeYY();
				partialDerivatives[4][i][j] = f.partialDerivativeXY();
			}
		}
	}

	private int searchIndex(double c, double[] val) {
		if (c < (val[0])) {
			return -1;
		} 
		final int max = val.length;
		for (int i = 1 ; i < max ; i++) {
			if (c <= (val[i])) {
				return i - 1;
			} 
		}
		return -1;
	}

	private double[] computeSplineCoefficients(double[] beta) {
		final double[] a = new double[16];
		for (int i = 0 ; i < 16 ; i++) {
			double result = 0;
			final double[] row = AINV[i];
			for (int j = 0 ; j < 16 ; j++) {
				result += (row[j]) * (beta[j]);
			}
			a[i] = result;
		}
		return a;
	}
}

class BicubicSplineFunction implements org.apache.commons.math.analysis.BivariateRealFunction {
	private static final short N = 4;

	private final double[][] a = new double[N][N];

	org.apache.commons.math.analysis.BivariateRealFunction partialDerivativeX = null;

	org.apache.commons.math.analysis.BivariateRealFunction partialDerivativeY = null;

	org.apache.commons.math.analysis.BivariateRealFunction partialDerivativeXX = null;

	org.apache.commons.math.analysis.BivariateRealFunction partialDerivativeYY = null;

	org.apache.commons.math.analysis.BivariateRealFunction partialDerivativeXY = null;

	public BicubicSplineFunction(double[] aV) {
		for (int i = 0 ; i < (N) ; i++) {
			for (int j = 0 ; j < (N) ; j++) {
				a[i][j] = aV[(i + ((N) * j))];
			}
		}
	}

	public double value(double x, double y) {
		if ((x < 0) || (x > 1)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range", x, 0, 1);
		} 
		if ((y < 0) || (y > 1)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range", y, 0, 1);
		} 
		final double x2 = x * x;
		final double x3 = x2 * x;
		final double[] pX = new double[]{ 1 , x , x2 , x3 };
		final double y2 = y * y;
		final double y3 = y2 * y;
		final double[] pY = new double[]{ 1 , y , y2 , y3 };
		return apply(pX, pY, a);
	}

	private double apply(double[] pX, double[] pY, double[][] coeff) {
		double result = 0;
		for (int i = 0 ; i < (N) ; i++) {
			for (int j = 0 ; j < (N) ; j++) {
				result += ((coeff[i][j]) * (pX[i])) * (pY[j]);
			}
		}
		return result;
	}

	public org.apache.commons.math.analysis.BivariateRealFunction partialDerivativeX() {
		if ((partialDerivativeX) == null) {
			computePartialDerivatives();
		} 
		return partialDerivativeX;
	}

	public org.apache.commons.math.analysis.BivariateRealFunction partialDerivativeY() {
		if ((partialDerivativeY) == null) {
			computePartialDerivatives();
		} 
		return partialDerivativeY;
	}

	public org.apache.commons.math.analysis.BivariateRealFunction partialDerivativeXX() {
		if ((partialDerivativeXX) == null) {
			computePartialDerivatives();
		} 
		return partialDerivativeXX;
	}

	public org.apache.commons.math.analysis.BivariateRealFunction partialDerivativeYY() {
		if ((partialDerivativeYY) == null) {
			computePartialDerivatives();
		} 
		return partialDerivativeYY;
	}

	public org.apache.commons.math.analysis.BivariateRealFunction partialDerivativeXY() {
		if ((partialDerivativeXY) == null) {
			computePartialDerivatives();
		} 
		return partialDerivativeXY;
	}

	private void computePartialDerivatives() {
		final double[][] aX = new double[N][N];
		final double[][] aY = new double[N][N];
		final double[][] aXX = new double[N][N];
		final double[][] aYY = new double[N][N];
		final double[][] aXY = new double[N][N];
		for (int i = 0 ; i < (N) ; i++) {
			for (int j = 0 ; j < (N) ; j++) {
				final double c = a[i][j];
				aX[i][j] = i * c;
				aY[i][j] = j * c;
				aXX[i][j] = (i - 1) * (aX[i][j]);
				aYY[i][j] = (j - 1) * (aY[i][j]);
				aXY[i][j] = j * (aX[i][j]);
			}
		}
		partialDerivativeX = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				final double x2 = x * x;
				final double[] pX = new double[]{ 0 , 1 , x , x2 };
				final double y2 = y * y;
				final double y3 = y2 * y;
				final double[] pY = new double[]{ 1 , y , y2 , y3 };
				return apply(pX, pY, aX);
			}
		};
		partialDerivativeY = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				final double x2 = x * x;
				final double x3 = x2 * x;
				final double[] pX = new double[]{ 1 , x , x2 , x3 };
				final double y2 = y * y;
				final double[] pY = new double[]{ 0 , 1 , y , y2 };
				return apply(pX, pY, aY);
			}
		};
		partialDerivativeXX = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				final double[] pX = new double[]{ 0 , 0 , 1 , x };
				final double y2 = y * y;
				final double y3 = y2 * y;
				final double[] pY = new double[]{ 1 , y , y2 , y3 };
				return apply(pX, pY, aXX);
			}
		};
		partialDerivativeYY = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				final double x2 = x * x;
				final double x3 = x2 * x;
				final double[] pX = new double[]{ 1 , x , x2 , x3 };
				final double[] pY = new double[]{ 0 , 0 , 1 , y };
				return apply(pX, pY, aYY);
			}
		};
		partialDerivativeXY = new org.apache.commons.math.analysis.BivariateRealFunction() {
			public double value(double x, double y) {
				final double x2 = x * x;
				final double[] pX = new double[]{ 0 , 1 , x , x2 };
				final double y2 = y * y;
				final double[] pY = new double[]{ 0 , 1 , y , y2 };
				return apply(pX, pY, aXY);
			}
		};
	}
}

