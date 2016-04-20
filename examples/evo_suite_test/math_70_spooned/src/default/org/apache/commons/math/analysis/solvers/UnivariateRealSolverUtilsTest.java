package org.apache.commons.math.analysis.solvers;


public class UnivariateRealSolverUtilsTest extends junit.framework.TestCase {
	protected org.apache.commons.math.analysis.UnivariateRealFunction sin = new org.apache.commons.math.analysis.SinFunction();

	public void testSolveNull() throws org.apache.commons.math.MathException {
		try {
			org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(null, 0.0, 4.0);
			junit.framework.Assert.fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testSolveBadParameters() throws org.apache.commons.math.MathException {
		try {
			org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(sin, 0.0, 4.0, 4.0);
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(sin, 0.0, 4.0, 0.0);
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testSolveSin() throws org.apache.commons.math.MathException {
		double x = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(sin, 1.0, 4.0);
		junit.framework.Assert.assertEquals(java.lang.Math.PI, x, 1.0E-4);
	}

	public void testSolveAccuracyNull() throws org.apache.commons.math.MathException {
		try {
			double accuracy = 1.0E-6;
			org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(null, 0.0, 4.0, accuracy);
			junit.framework.Assert.fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testSolveAccuracySin() throws org.apache.commons.math.MathException {
		double accuracy = 1.0E-6;
		double x = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(sin, 1.0, 4.0, accuracy);
		junit.framework.Assert.assertEquals(java.lang.Math.PI, x, accuracy);
	}

	public void testSolveNoRoot() throws org.apache.commons.math.MathException {
		try {
			org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(sin, 1.0, 1.5);
			junit.framework.Assert.fail("Expecting IllegalArgumentException ");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testBracketSin() throws org.apache.commons.math.MathException {
		double[] result = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.bracket(sin, 0.0, -2.0, 2.0);
		junit.framework.Assert.assertTrue(((sin.value(result[0])) < 0));
		junit.framework.Assert.assertTrue(((sin.value(result[1])) > 0));
	}

	public void testBracketEndpointRoot() throws org.apache.commons.math.MathException {
		double[] result = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.bracket(sin, 1.5, 0, 2.0);
		junit.framework.Assert.assertEquals(0.0, sin.value(result[0]), 1.0E-15);
		junit.framework.Assert.assertTrue(((sin.value(result[1])) > 0));
	}

	public void testBadParameters() throws org.apache.commons.math.MathException {
		try {
			org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.bracket(null, 1.5, 0, 2.0);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.bracket(sin, 2.5, 0, 2.0);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.bracket(sin, 1.5, 2.0, 1.0);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.bracket(sin, 1.5, 0, 2.0, 0);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}
}

