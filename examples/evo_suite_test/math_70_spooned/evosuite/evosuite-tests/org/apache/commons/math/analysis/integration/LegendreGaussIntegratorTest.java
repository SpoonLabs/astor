package org.apache.commons.math.analysis.integration;


public class LegendreGaussIntegratorTest extends junit.framework.TestCase {
	public LegendreGaussIntegratorTest(java.lang.String name) {
		super(name);
	}

	public void testSinFunction() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.analysis.integration.UnivariateRealIntegrator integrator = new org.apache.commons.math.analysis.integration.LegendreGaussIntegrator(5 , 64);
		integrator.setAbsoluteAccuracy(1.0E-10);
		integrator.setRelativeAccuracy(1.0E-14);
		integrator.setMinimalIterationCount(2);
		integrator.setMaximalIterationCount(15);
		double min;
		double max;
		double expected;
		double result;
		double tolerance;
		min = 0;
		max = java.lang.Math.PI;
		expected = 2;
		tolerance = java.lang.Math.max(integrator.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (integrator.getRelativeAccuracy()))));
		result = integrator.integrate(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = (-(java.lang.Math.PI)) / 3;
		max = 0;
		expected = -0.5;
		tolerance = java.lang.Math.max(integrator.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (integrator.getRelativeAccuracy()))));
		result = integrator.integrate(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
	}

	public void testQuinticFunction() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.QuinticFunction();
		org.apache.commons.math.analysis.integration.UnivariateRealIntegrator integrator = new org.apache.commons.math.analysis.integration.LegendreGaussIntegrator(3 , 64);
		double min;
		double max;
		double expected;
		double result;
		min = 0;
		max = 1;
		expected = (-1.0) / 48;
		result = integrator.integrate(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, 1.0E-16);
		min = 0;
		max = 0.5;
		expected = 11.0 / 768;
		result = integrator.integrate(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, 1.0E-16);
		min = -1;
		max = 4;
		expected = ((2048 / 3.0) - 78) + (1.0 / 48);
		result = integrator.integrate(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, 1.0E-16);
	}

	public void testExactIntegration() throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		java.util.Random random = new java.util.Random(86343623467878363L);
		for (int n = 2 ; n < 6 ; ++n) {
			org.apache.commons.math.analysis.integration.LegendreGaussIntegrator integrator = new org.apache.commons.math.analysis.integration.LegendreGaussIntegrator(n , 64);
			for (int degree = 0 ; degree <= ((2 * n) - 1) ; ++degree) {
				for (int i = 0 ; i < 10 ; ++i) {
					double[] coeff = new double[degree + 1];
					for (int k = 0 ; k < (coeff.length) ; ++k) {
						coeff[k] = (2 * (random.nextDouble())) - 1;
					}
					org.apache.commons.math.analysis.polynomials.PolynomialFunction p = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(coeff);
					double result = integrator.integrate(p, -5.0, 15.0);
					double reference = exactIntegration(p, -5.0, 15.0);
					junit.framework.Assert.assertEquals(((((n + " ") + degree) + " ") + i), reference, result, (1.0E-12 * (1.0 + (java.lang.Math.abs(reference)))));
				}
			}
		}
	}

	private double exactIntegration(org.apache.commons.math.analysis.polynomials.PolynomialFunction p, double a, double b) {
		final double[] coeffs = p.getCoefficients();
		double yb = (coeffs[((coeffs.length) - 1)]) / (coeffs.length);
		double ya = yb;
		for (int i = (coeffs.length) - 2 ; i >= 0 ; --i) {
			yb = (yb * b) + ((coeffs[i]) / (i + 1));
			ya = (ya * a) + ((coeffs[i]) / (i + 1));
		}
		return (yb * b) - (ya * a);
	}
}

