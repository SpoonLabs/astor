package org.apache.commons.math.analysis.solvers;


public final class BrentSolverTest extends junit.framework.TestCase {
	public BrentSolverTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Deprecated
	public void testDeprecated() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		double result;
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.BrentSolver(f);
		result = solver.solve(3, 4);
		junit.framework.Assert.assertEquals(result, java.lang.Math.PI, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 5));
		result = solver.solve(1, 4);
		junit.framework.Assert.assertEquals(result, java.lang.Math.PI, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 6));
		solver = new org.apache.commons.math.analysis.solvers.SecantSolver(f);
		result = solver.solve(3, 4);
		junit.framework.Assert.assertEquals(result, java.lang.Math.PI, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 5));
		result = solver.solve(1, 4);
		junit.framework.Assert.assertEquals(result, java.lang.Math.PI, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 6));
		junit.framework.Assert.assertEquals(result, solver.getResult(), 0);
	}

	public void testSinZero() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		double result;
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.BrentSolver();
		result = solver.solve(f, 3, 4);
		junit.framework.Assert.assertEquals(result, java.lang.Math.PI, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 5));
		result = solver.solve(f, 1, 4);
		junit.framework.Assert.assertEquals(result, java.lang.Math.PI, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 6));
		solver = new org.apache.commons.math.analysis.solvers.SecantSolver();
		result = solver.solve(f, 3, 4);
		junit.framework.Assert.assertEquals(result, java.lang.Math.PI, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 5));
		result = solver.solve(f, 1, 4);
		junit.framework.Assert.assertEquals(result, java.lang.Math.PI, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 6));
		junit.framework.Assert.assertEquals(result, solver.getResult(), 0);
	}

	public void testQuinticZero() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.QuinticFunction();
		double result;
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.BrentSolver();
		result = solver.solve(f, -0.2, 0.2);
		junit.framework.Assert.assertEquals(result, 0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 2));
		result = solver.solve(f, -0.1, 0.3);
		junit.framework.Assert.assertEquals(result, 0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 6));
		result = solver.solve(f, -0.3, 0.45);
		junit.framework.Assert.assertEquals(result, 0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 7));
		result = solver.solve(f, 0.3, 0.7);
		junit.framework.Assert.assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 7));
		result = solver.solve(f, 0.2, 0.6);
		junit.framework.Assert.assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 7));
		result = solver.solve(f, 0.05, 0.95);
		junit.framework.Assert.assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 9));
		result = solver.solve(f, 0.85, 1.25);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 9));
		result = solver.solve(f, 0.8, 1.2);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 9));
		result = solver.solve(f, 0.85, 1.75);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 11));
		result = solver.solve(f, 0.55, 1.45);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 8));
		result = solver.solve(f, 0.85, 5);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 13));
		solver = new org.apache.commons.math.analysis.solvers.SecantSolver();
		result = solver.solve(f, -0.2, 0.2);
		junit.framework.Assert.assertEquals(result, 0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 2));
		result = solver.solve(f, -0.1, 0.3);
		junit.framework.Assert.assertEquals(result, 0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 6));
		result = solver.solve(f, -0.3, 0.45);
		junit.framework.Assert.assertEquals(result, 0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 7));
		result = solver.solve(f, 0.3, 0.7);
		junit.framework.Assert.assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 8));
		result = solver.solve(f, 0.2, 0.6);
		junit.framework.Assert.assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 7));
		result = solver.solve(f, 0.05, 0.95);
		junit.framework.Assert.assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 9));
		result = solver.solve(f, 0.85, 1.25);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 11));
		result = solver.solve(f, 0.8, 1.2);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 9));
		result = solver.solve(f, 0.85, 1.75);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 15));
		result = solver.solve(f, 0.55, 1.45);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 8));
		result = solver.solve(f, 0.85, 5);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((solver.getIterationCount()) <= 15));
		result = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(f, -0.2, 0.2);
		junit.framework.Assert.assertEquals(result, 0, solver.getAbsoluteAccuracy());
		result = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(f, -0.1, 0.3);
		junit.framework.Assert.assertEquals(result, 0, 1.0E-8);
		result = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(f, -0.3, 0.45);
		junit.framework.Assert.assertEquals(result, 0, 1.0E-6);
		result = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(f, 0.3, 0.7);
		junit.framework.Assert.assertEquals(result, 0.5, 1.0E-6);
		result = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(f, 0.2, 0.6);
		junit.framework.Assert.assertEquals(result, 0.5, 1.0E-6);
		result = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(f, 0.05, 0.95);
		junit.framework.Assert.assertEquals(result, 0.5, 1.0E-6);
		result = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(f, 0.85, 1.25);
		junit.framework.Assert.assertEquals(result, 1.0, 1.0E-6);
		result = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(f, 0.8, 1.2);
		junit.framework.Assert.assertEquals(result, 1.0, 1.0E-6);
		result = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(f, 0.85, 1.75);
		junit.framework.Assert.assertEquals(result, 1.0, 1.0E-6);
		result = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(f, 0.55, 1.45);
		junit.framework.Assert.assertEquals(result, 1.0, 1.0E-6);
		result = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(f, 0.85, 5);
		junit.framework.Assert.assertEquals(result, 1.0, 1.0E-6);
	}

	public void testRootEndpoints() throws java.lang.Exception {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.BrentSolver();
		double result = solver.solve(f, java.lang.Math.PI, 4);
		junit.framework.Assert.assertEquals(java.lang.Math.PI, result, solver.getAbsoluteAccuracy());
		result = solver.solve(f, 3, java.lang.Math.PI);
		junit.framework.Assert.assertEquals(java.lang.Math.PI, result, solver.getAbsoluteAccuracy());
		result = solver.solve(f, java.lang.Math.PI, 4, 3.5);
		junit.framework.Assert.assertEquals(java.lang.Math.PI, result, solver.getAbsoluteAccuracy());
		result = solver.solve(f, 3, java.lang.Math.PI, 3.07);
		junit.framework.Assert.assertEquals(java.lang.Math.PI, result, solver.getAbsoluteAccuracy());
	}

	public void testBadEndpoints() throws java.lang.Exception {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.BrentSolver();
		try {
			solver.solve(f, 1, -1);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - bad interval");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			solver.solve(f, 1, 1.5);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - non-bracketing");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			solver.solve(f, 1, 1.5, 1.2);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - non-bracketing");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testInitialGuess() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.MonitoredFunction f = new org.apache.commons.math.analysis.MonitoredFunction(new org.apache.commons.math.analysis.QuinticFunction());
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = new org.apache.commons.math.analysis.solvers.BrentSolver();
		double result;
		result = solver.solve(f, 0.6, 7.0);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		int referenceCallsCount = f.getCallsCount();
		junit.framework.Assert.assertTrue((referenceCallsCount >= 13));
		try {
			result = solver.solve(f, 0.6, 7.0, 0.0);
			junit.framework.Assert.fail("an IllegalArgumentException was expected");
		} catch (java.lang.IllegalArgumentException iae) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail(("wrong exception caught: " + (e.getMessage())));
		}
		f.setCallsCount(0);
		result = solver.solve(f, 0.6, 7.0, 0.61);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((f.getCallsCount()) > referenceCallsCount));
		f.setCallsCount(0);
		result = solver.solve(f, 0.6, 7.0, 0.999999);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertTrue(((f.getCallsCount()) < referenceCallsCount));
		f.setCallsCount(0);
		result = solver.solve(f, 0.6, 7.0, 1.0);
		junit.framework.Assert.assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
		junit.framework.Assert.assertEquals(0, solver.getIterationCount());
		junit.framework.Assert.assertEquals(1, f.getCallsCount());
	}
}

