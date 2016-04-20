package org.apache.commons.math.optimization.fitting;


public class PolynomialFitterTest {
	@org.junit.Test
	public void testNoError() throws org.apache.commons.math.optimization.OptimizationException {
		java.util.Random randomizer = new java.util.Random(64925784252L);
		for (int degree = 1 ; degree < 10 ; ++degree) {
			org.apache.commons.math.analysis.polynomials.PolynomialFunction p = buildRandomPolynomial(degree, randomizer);
			org.apache.commons.math.optimization.fitting.PolynomialFitter fitter = new org.apache.commons.math.optimization.fitting.PolynomialFitter(degree , new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer());
			for (int i = 0 ; i <= degree ; ++i) {
				fitter.addObservedPoint(1.0, i, p.value(i));
			}
			org.apache.commons.math.analysis.polynomials.PolynomialFunction fitted = fitter.fit();
			for (double x = -1.0 ; x < 1.0 ; x += 0.01) {
				double error = (java.lang.Math.abs(((p.value(x)) - (fitted.value(x))))) / (1.0 + (java.lang.Math.abs(p.value(x))));
				org.junit.Assert.assertEquals(0.0, error, 1.0E-6);
			}
		}
	}

	@org.junit.Test
	public void testSmallError() throws org.apache.commons.math.optimization.OptimizationException {
		java.util.Random randomizer = new java.util.Random(53882150042L);
		double maxError = 0;
		for (int degree = 0 ; degree < 10 ; ++degree) {
			org.apache.commons.math.analysis.polynomials.PolynomialFunction p = buildRandomPolynomial(degree, randomizer);
			org.apache.commons.math.optimization.fitting.PolynomialFitter fitter = new org.apache.commons.math.optimization.fitting.PolynomialFitter(degree , new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer());
			for (double x = -1.0 ; x < 1.0 ; x += 0.01) {
				fitter.addObservedPoint(1.0, x, ((p.value(x)) + (0.1 * (randomizer.nextGaussian()))));
			}
			org.apache.commons.math.analysis.polynomials.PolynomialFunction fitted = fitter.fit();
			for (double x = -1.0 ; x < 1.0 ; x += 0.01) {
				double error = (java.lang.Math.abs(((p.value(x)) - (fitted.value(x))))) / (1.0 + (java.lang.Math.abs(p.value(x))));
				maxError = java.lang.Math.max(maxError, error);
				org.junit.Assert.assertTrue(((java.lang.Math.abs(error)) < 0.1));
			}
		}
		org.junit.Assert.assertTrue((maxError > 0.01));
	}

	@org.junit.Test
	public void testRedundantSolvable() {
		checkUnsolvableProblem(new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer(), true);
	}

	@org.junit.Test
	public void testRedundantUnsolvable() {
		org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer optimizer = new org.apache.commons.math.optimization.general.GaussNewtonOptimizer(true);
		checkUnsolvableProblem(optimizer, false);
	}

	private void checkUnsolvableProblem(org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer optimizer, boolean solvable) {
		java.util.Random randomizer = new java.util.Random(1248788532L);
		for (int degree = 0 ; degree < 10 ; ++degree) {
			org.apache.commons.math.analysis.polynomials.PolynomialFunction p = buildRandomPolynomial(degree, randomizer);
			org.apache.commons.math.optimization.fitting.PolynomialFitter fitter = new org.apache.commons.math.optimization.fitting.PolynomialFitter(degree , optimizer);
			for (double x = -1.0 ; x < 1.0 ; x += 0.01) {
				fitter.addObservedPoint(1.0, 0.0, p.value(0.0));
			}
			try {
				fitter.fit();
				org.junit.Assert.assertTrue((solvable || (degree == 0)));
			} catch (org.apache.commons.math.optimization.OptimizationException e) {
				org.junit.Assert.assertTrue(((!solvable) && (degree > 0)));
			}
		}
	}

	private org.apache.commons.math.analysis.polynomials.PolynomialFunction buildRandomPolynomial(int degree, java.util.Random randomizer) {
		final double[] coefficients = new double[degree + 1];
		for (int i = 0 ; i <= degree ; ++i) {
			coefficients[i] = randomizer.nextGaussian();
		}
		return new org.apache.commons.math.analysis.polynomials.PolynomialFunction(coefficients);
	}
}

