package org.apache.commons.math.analysis.interpolation;


public class DividedDifferenceInterpolator implements java.io.Serializable , org.apache.commons.math.analysis.interpolation.UnivariateRealInterpolator {
	private static final long serialVersionUID = 107049519551235069L;

	public org.apache.commons.math.analysis.polynomials.PolynomialFunctionNewtonForm interpolate(double[] x, double[] y) throws org.apache.commons.math.DuplicateSampleAbscissaException {
		org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm.verifyInterpolationArray(x, y);
		final double[] c = new double[(x.length) - 1];
		java.lang.System.arraycopy(x, 0, c, 0, c.length);
		final double[] a = org.apache.commons.math.analysis.interpolation.DividedDifferenceInterpolator.computeDividedDifference(x, y);
		return new org.apache.commons.math.analysis.polynomials.PolynomialFunctionNewtonForm(a , c);
	}

	protected static double[] computeDividedDifference(final double[] x, final double[] y) throws org.apache.commons.math.DuplicateSampleAbscissaException {
		org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm.verifyInterpolationArray(x, y);
		final double[] divdiff = y.clone();
		final int n = x.length;
		final double[] a = new double[n];
		a[0] = divdiff[0];
		for (int i = 1 ; i < n ; i++) {
			for (int j = 0 ; j < (n - i) ; j++) {
				final double denominator = (x[(j + i)]) - (x[j]);
				if (denominator == 0.0) {
					throw new org.apache.commons.math.DuplicateSampleAbscissaException(x[j] , j , (j + i));
				} 
				divdiff[j] = ((divdiff[(j + 1)]) - (divdiff[j])) / denominator;
			}
			a[i] = divdiff[0];
		}
		return a;
	}
}

