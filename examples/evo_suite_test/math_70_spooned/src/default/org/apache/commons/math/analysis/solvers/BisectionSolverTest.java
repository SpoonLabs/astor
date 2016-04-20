package org.apache.commons.math.analysis.solvers;


public final class BisectionSolverTest extends junit.framework.TestCase {
	@java.lang.Deprecated
	public void testDeprecated() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		double result;
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.BisectionSolver(f);
		result = solver.solve(3, 4);
		junit.framework.Assert.assertEquals(result, java.lang.Math.PI, solver.getAbsoluteAccuracy());
		result = solver.solve(1, 4);
		junit.framework.Assert.assertEquals(result, java.lang.Math.PI, solver.getAbsoluteAccuracy());
	}

	public void testSinZero() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		double result;
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		result = solver.solve(f, 3, 4);
		junit.framework.Assert.assertEquals(result, java.lang.Math.PI, solver.getAbsoluteAccuracy());
		result = solver.solve(f, 1, 4);
		junit.framework.Assert.assertEquals(result, java.lang.Math.PI, solver.getAbsoluteAccuracy());
	}

	public void testQuinticZero() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.QuinticFunction();
		double result;
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		result = solver.solve(f, -0.2, 0.2);
		junit.framework.Assert.assertEquals(result, 0, solver.getAbsoluteAccuracy());
		result = solver.solve(f, -0.1, 0.3);
		junit.framework.Assert.assertEquals(result, 0, solver.getAbsoluteAccuracy());
		result = solver.solve(f, -0.3, 0.45);
		junit.framework.Assert.assertEquals(result, 0, solver.getAbsoluteAccuracy());
		result = solver.solve(f, 0.3, 0.7);
		junit.framework.Assert.assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
		result = solver.solve(f, 0.2, 0.6);
		junit.framework.Assert.assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
		result = solver.solve(f, 0.05, 0.95);
		junit.framework.Assert.assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
		result = solver.solve(f, 0.85, 1.25);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		result = solver.solve(f, 0.8, 1.2);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		result = solver.solve(f, 0.85, 1.75);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		result = solver.solve(f, 0.55, 1.45);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		result = solver.solve(f, 0.85, 5);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertEquals(result, solver.getResult(), 0);
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) > 0));
	}

	public void testMath369() throws java.lang.Exception {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		junit.framework.Assert.assertEquals(java.lang.Math.PI, solver.solve(f, 3.0, 3.2, 3.1), solver.getAbsoluteAccuracy());
	}

	public void testSetFunctionValueAccuracy() {
		double expected = 0.01;
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		solver.setFunctionValueAccuracy(expected);
		junit.framework.Assert.assertEquals(expected, solver.getFunctionValueAccuracy(), 0.01);
	}

	public void testResetFunctionValueAccuracy() {
		double newValue = 0.01;
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		double oldValue = solver.getFunctionValueAccuracy();
		solver.setFunctionValueAccuracy(newValue);
		solver.resetFunctionValueAccuracy();
		junit.framework.Assert.assertEquals(oldValue, solver.getFunctionValueAccuracy(), 0.01);
	}

	public void testSetAbsoluteAccuracy() {
		double expected = 0.01;
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		solver.setAbsoluteAccuracy(expected);
		junit.framework.Assert.assertEquals(expected, solver.getAbsoluteAccuracy(), 0.01);
	}

	public void testResetAbsoluteAccuracy() {
		double newValue = 0.01;
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		double oldValue = solver.getAbsoluteAccuracy();
		solver.setAbsoluteAccuracy(newValue);
		solver.resetAbsoluteAccuracy();
		junit.framework.Assert.assertEquals(oldValue, solver.getAbsoluteAccuracy(), 0.01);
	}

	public void testSetMaximalIterationCount() {
		int expected = 100;
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		solver.setMaximalIterationCount(expected);
		junit.framework.Assert.assertEquals(expected, solver.getMaximalIterationCount());
	}

	public void testResetMaximalIterationCount() {
		int newValue = 10000;
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		int oldValue = solver.getMaximalIterationCount();
		solver.setMaximalIterationCount(newValue);
		solver.resetMaximalIterationCount();
		junit.framework.Assert.assertEquals(oldValue, solver.getMaximalIterationCount());
	}

	public void testSetRelativeAccuracy() {
		double expected = 0.01;
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		solver.setRelativeAccuracy(expected);
		junit.framework.Assert.assertEquals(expected, solver.getRelativeAccuracy(), 0.01);
	}

	public void testResetRelativeAccuracy() {
		double newValue = 0.01;
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		double oldValue = solver.getRelativeAccuracy();
		solver.setRelativeAccuracy(newValue);
		solver.resetRelativeAccuracy();
		junit.framework.Assert.assertEquals(oldValue, solver.getRelativeAccuracy(), 0.01);
	}
}

