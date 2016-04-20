package org.apache.commons.math.analysis.solvers;


public final class NewtonSolverTest extends junit.framework.TestCase {
	@java.lang.Deprecated
	public void testDeprecated() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.DifferentiableUnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		double result;
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.NewtonSolver(f);
		result = solver.solve(3, 4);
		junit.framework.Assert.assertEquals(result, java.lang.Math.PI, solver.getAbsoluteAccuracy());
		result = solver.solve(1, 4);
		junit.framework.Assert.assertEquals(result, java.lang.Math.PI, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertEquals(result, solver.getResult(), 0);
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) > 0));
	}

	public void testSinZero() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.DifferentiableUnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		double result;
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.NewtonSolver();
		result = solver.solve(f, 3, 4);
		junit.framework.Assert.assertEquals(result, java.lang.Math.PI, solver.getAbsoluteAccuracy());
		result = solver.solve(f, 1, 4);
		junit.framework.Assert.assertEquals(result, java.lang.Math.PI, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertEquals(result, solver.getResult(), 0);
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) > 0));
	}

	public void testQuinticZero() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.DifferentiableUnivariateRealFunction f = new org.apache.commons.math.analysis.QuinticFunction();
		double result;
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.NewtonSolver();
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
	}
}

