package org.apache.commons.math.analysis.integration;


public final class SimpsonIntegratorTest extends junit.framework.TestCase {
	public void testSinFunction() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.analysis.integration.UnivariateRealIntegrator integrator = new org.apache.commons.math.analysis.integration.SimpsonIntegrator();
		double min;
		double max;
		double expected;
		double result;
		double tolerance;
		min = 0;
		max = java.lang.Math.PI;
		expected = 2;
		tolerance = java.lang.Math.abs((expected * (integrator.getRelativeAccuracy())));
		result = integrator.integrate(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = (-(java.lang.Math.PI)) / 3;
		max = 0;
		expected = -0.5;
		tolerance = java.lang.Math.abs((expected * (integrator.getRelativeAccuracy())));
		result = integrator.integrate(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
	}

	public void testQuinticFunction() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.QuinticFunction();
		org.apache.commons.math.analysis.integration.UnivariateRealIntegrator integrator = new org.apache.commons.math.analysis.integration.SimpsonIntegrator();
		double min;
		double max;
		double expected;
		double result;
		double tolerance;
		min = 0;
		max = 1;
		expected = (-1.0) / 48;
		tolerance = java.lang.Math.abs((expected * (integrator.getRelativeAccuracy())));
		result = integrator.integrate(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = 0;
		max = 0.5;
		expected = 11.0 / 768;
		tolerance = java.lang.Math.abs((expected * (integrator.getRelativeAccuracy())));
		result = integrator.integrate(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = -1;
		max = 4;
		expected = ((2048 / 3.0) - 78) + (1.0 / 48);
		tolerance = java.lang.Math.abs((expected * (integrator.getRelativeAccuracy())));
		result = integrator.integrate(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
	}

	public void testParameters() throws java.lang.Exception {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.analysis.integration.UnivariateRealIntegrator integrator = new org.apache.commons.math.analysis.integration.SimpsonIntegrator();
		try {
			integrator.integrate(f, 1, -1);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - bad interval");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			integrator.setMinimalIterationCount(5);
			integrator.setMaximalIterationCount(4);
			integrator.integrate(f, -1, 1);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - bad iteration limits");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			integrator.setMinimalIterationCount(10);
			integrator.setMaximalIterationCount(99);
			integrator.integrate(f, -1, 1);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - bad iteration limits");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}
}

