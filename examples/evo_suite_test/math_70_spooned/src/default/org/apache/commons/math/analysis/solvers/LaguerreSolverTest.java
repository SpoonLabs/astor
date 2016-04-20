package org.apache.commons.math.analysis.solvers;


public final class LaguerreSolverTest extends junit.framework.TestCase {
	@java.lang.Deprecated
	public void testDeprecated() throws org.apache.commons.math.MathException {
		double min;
		double max;
		double expected;
		double result;
		double tolerance;
		double[] coefficients = new double[]{ -1.0 , 4.0 };
		org.apache.commons.math.analysis.polynomials.PolynomialFunction f = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(coefficients);
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.LaguerreSolver(f);
		min = 0.0;
		max = 1.0;
		expected = 0.25;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve(min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
	}

	public void testLinearFunction() throws org.apache.commons.math.MathException {
		double min;
		double max;
		double expected;
		double result;
		double tolerance;
		double[] coefficients = new double[]{ -1.0 , 4.0 };
		org.apache.commons.math.analysis.polynomials.PolynomialFunction f = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(coefficients);
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.LaguerreSolver();
		min = 0.0;
		max = 1.0;
		expected = 0.25;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
	}

	public void testQuadraticFunction() throws org.apache.commons.math.MathException {
		double min;
		double max;
		double expected;
		double result;
		double tolerance;
		double[] coefficients = new double[]{ -3.0 , 5.0 , 2.0 };
		org.apache.commons.math.analysis.polynomials.PolynomialFunction f = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(coefficients);
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.LaguerreSolver();
		min = 0.0;
		max = 2.0;
		expected = 0.5;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = -4.0;
		max = -1.0;
		expected = -3.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
	}

	public void testQuinticFunction() throws org.apache.commons.math.MathException {
		double min;
		double max;
		double expected;
		double result;
		double tolerance;
		double[] coefficients = new double[]{ -12.0 , -1.0 , 1.0 , -12.0 , -1.0 , 1.0 };
		org.apache.commons.math.analysis.polynomials.PolynomialFunction f = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(coefficients);
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.LaguerreSolver();
		min = -2.0;
		max = 2.0;
		expected = -1.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = -5.0;
		max = -2.5;
		expected = -3.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = 3.0;
		max = 6.0;
		expected = 4.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
	}

	public void testQuinticFunction2() throws org.apache.commons.math.MathException {
		double initial = 0.0;
		double tolerance;
		org.apache.commons.math.complex.Complex expected;
		org.apache.commons.math.complex.Complex[] result;
		double[] coefficients = new double[]{ 4.0 , 0.0 , 1.0 , 4.0 , 0.0 , 1.0 };
		org.apache.commons.math.analysis.solvers.LaguerreSolver solver = new org.apache.commons.math.analysis.solvers.LaguerreSolver();
		result = solver.solveAll(coefficients, initial);
		expected = new org.apache.commons.math.complex.Complex(0.0 , -2.0);
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs(((expected.abs()) * (solver.getRelativeAccuracy()))));
		org.apache.commons.math.TestUtils.assertContains(result, expected, tolerance);
		expected = new org.apache.commons.math.complex.Complex(0.0 , 2.0);
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs(((expected.abs()) * (solver.getRelativeAccuracy()))));
		org.apache.commons.math.TestUtils.assertContains(result, expected, tolerance);
		expected = new org.apache.commons.math.complex.Complex(0.5 , (0.5 * (java.lang.Math.sqrt(3.0))));
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs(((expected.abs()) * (solver.getRelativeAccuracy()))));
		org.apache.commons.math.TestUtils.assertContains(result, expected, tolerance);
		expected = new org.apache.commons.math.complex.Complex(-1.0 , 0.0);
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs(((expected.abs()) * (solver.getRelativeAccuracy()))));
		org.apache.commons.math.TestUtils.assertContains(result, expected, tolerance);
		expected = new org.apache.commons.math.complex.Complex(0.5 , ((-0.5) * (java.lang.Math.sqrt(3.0))));
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs(((expected.abs()) * (solver.getRelativeAccuracy()))));
		org.apache.commons.math.TestUtils.assertContains(result, expected, tolerance);
	}

	public void testParameters() throws java.lang.Exception {
		double[] coefficients = new double[]{ -3.0 , 5.0 , 2.0 };
		org.apache.commons.math.analysis.polynomials.PolynomialFunction f = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(coefficients);
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.LaguerreSolver();
		try {
			solver.solve(f, 1, -1);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - bad interval");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			solver.solve(f, 2, 3);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - no bracketing");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			solver.solve(new org.apache.commons.math.analysis.SinFunction(), -1, 1);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - bad function");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}
}

