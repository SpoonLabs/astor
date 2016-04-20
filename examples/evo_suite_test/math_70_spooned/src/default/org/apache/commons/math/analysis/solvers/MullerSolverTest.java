package org.apache.commons.math.analysis.solvers;


public final class MullerSolverTest extends junit.framework.TestCase {
	@java.lang.Deprecated
	public void testDeprecated() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.MullerSolver(f);
		double min;
		double max;
		double expected;
		double result;
		double tolerance;
		min = 3.0;
		max = 4.0;
		expected = java.lang.Math.PI;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve(min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = -1.0;
		max = 1.5;
		expected = 0.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve(min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
	}

	@java.lang.Deprecated
	public void testDeprecated2() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.QuinticFunction();
		org.apache.commons.math.analysis.solvers.MullerSolver solver = new org.apache.commons.math.analysis.solvers.MullerSolver(f);
		double min;
		double max;
		double expected;
		double result;
		double tolerance;
		min = -0.4;
		max = 0.2;
		expected = 0.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve2(min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = 0.75;
		max = 1.5;
		expected = 1.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve2(min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = -0.9;
		max = -0.2;
		expected = -0.5;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve2(min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
	}

	public void testSinFunction() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.MullerSolver();
		double min;
		double max;
		double expected;
		double result;
		double tolerance;
		min = 3.0;
		max = 4.0;
		expected = java.lang.Math.PI;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = -1.0;
		max = 1.5;
		expected = 0.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
	}

	public void testSinFunction2() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.analysis.solvers.MullerSolver solver = new org.apache.commons.math.analysis.solvers.MullerSolver();
		double min;
		double max;
		double expected;
		double result;
		double tolerance;
		min = 3.0;
		max = 4.0;
		expected = java.lang.Math.PI;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve2(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = -1.0;
		max = 1.5;
		expected = 0.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve2(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
	}

	public void testQuinticFunction() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.QuinticFunction();
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.MullerSolver();
		double min;
		double max;
		double expected;
		double result;
		double tolerance;
		min = -0.4;
		max = 0.2;
		expected = 0.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = 0.75;
		max = 1.5;
		expected = 1.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = -0.9;
		max = -0.2;
		expected = -0.5;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
	}

	public void testQuinticFunction2() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.QuinticFunction();
		org.apache.commons.math.analysis.solvers.MullerSolver solver = new org.apache.commons.math.analysis.solvers.MullerSolver();
		double min;
		double max;
		double expected;
		double result;
		double tolerance;
		min = -0.4;
		max = 0.2;
		expected = 0.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve2(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = 0.75;
		max = 1.5;
		expected = 1.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve2(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = -0.9;
		max = -0.2;
		expected = -0.5;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve2(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
	}

	public void testExpm1Function() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.MullerSolver();
		double min;
		double max;
		double expected;
		double result;
		double tolerance;
		min = -1.0;
		max = 2.0;
		expected = 0.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = -20.0;
		max = 10.0;
		expected = 0.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = -50.0;
		max = 100.0;
		expected = 0.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
	}

	public void testExpm1Function2() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.solvers.MullerSolver solver = new org.apache.commons.math.analysis.solvers.MullerSolver();
		double min;
		double max;
		double expected;
		double result;
		double tolerance;
		min = -1.0;
		max = 2.0;
		expected = 0.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve2(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = -20.0;
		max = 10.0;
		expected = 0.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve2(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		min = -50.0;
		max = 100.0;
		expected = 0.0;
		tolerance = java.lang.Math.max(solver.getAbsoluteAccuracy(), java.lang.Math.abs((expected * (solver.getRelativeAccuracy()))));
		result = solver.solve2(f, min, max);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
	}

	public void testParameters() throws java.lang.Exception {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.MullerSolver();
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
	}
}

