package org.apache.commons.math.optimization.univariate;


public final class BrentMinimizerTest {
	@org.junit.Test
	public void testSinMin() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.optimization.UnivariateRealOptimizer minimizer = new org.apache.commons.math.optimization.univariate.BrentOptimizer();
		minimizer.setMaxEvaluations(200);
		org.junit.Assert.assertEquals(200, minimizer.getMaxEvaluations());
		try {
			minimizer.getResult();
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalStateException ise) {
		} catch (java.lang.Exception e) {
			org.junit.Assert.fail("wrong exception caught");
		}
		org.junit.Assert.assertEquals(((3 * (java.lang.Math.PI)) / 2), minimizer.optimize(f, org.apache.commons.math.optimization.GoalType.MINIMIZE, 4, 5), (70 * (minimizer.getAbsoluteAccuracy())));
		org.junit.Assert.assertTrue(((minimizer.getIterationCount()) <= 50));
		org.junit.Assert.assertEquals(((3 * (java.lang.Math.PI)) / 2), minimizer.optimize(f, org.apache.commons.math.optimization.GoalType.MINIMIZE, 1, 5), (70 * (minimizer.getAbsoluteAccuracy())));
		org.junit.Assert.assertTrue(((minimizer.getIterationCount()) <= 50));
		org.junit.Assert.assertTrue(((minimizer.getEvaluations()) <= 100));
		org.junit.Assert.assertTrue(((minimizer.getEvaluations()) >= 90));
		minimizer.setMaxEvaluations(50);
		try {
			minimizer.optimize(f, org.apache.commons.math.optimization.GoalType.MINIMIZE, 4, 5);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.FunctionEvaluationException fee) {
		} catch (java.lang.Exception e) {
			org.junit.Assert.fail("wrong exception caught");
		}
	}

	@org.junit.Test
	public void testQuinticMin() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.QuinticFunction();
		org.apache.commons.math.optimization.UnivariateRealOptimizer minimizer = new org.apache.commons.math.optimization.univariate.BrentOptimizer();
		org.junit.Assert.assertEquals(-0.27195613, minimizer.optimize(f, org.apache.commons.math.optimization.GoalType.MINIMIZE, -0.3, -0.2), 1.0E-8);
		org.junit.Assert.assertEquals(0.82221643, minimizer.optimize(f, org.apache.commons.math.optimization.GoalType.MINIMIZE, 0.3, 0.9), 1.0E-8);
		org.junit.Assert.assertTrue(((minimizer.getIterationCount()) <= 50));
		org.junit.Assert.assertEquals(-0.27195613, minimizer.optimize(f, org.apache.commons.math.optimization.GoalType.MINIMIZE, -1.0, 0.2), 1.0E-8);
		org.junit.Assert.assertTrue(((minimizer.getIterationCount()) <= 50));
	}

	@org.junit.Test
	public void testQuinticMax() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.QuinticFunction();
		org.apache.commons.math.optimization.UnivariateRealOptimizer minimizer = new org.apache.commons.math.optimization.univariate.BrentOptimizer();
		org.junit.Assert.assertEquals(0.27195613, minimizer.optimize(f, org.apache.commons.math.optimization.GoalType.MAXIMIZE, 0.2, 0.3), 1.0E-8);
		minimizer.setMaximalIterationCount(30);
		try {
			minimizer.optimize(f, org.apache.commons.math.optimization.GoalType.MAXIMIZE, 0.2, 0.3);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.MaxIterationsExceededException miee) {
		} catch (java.lang.Exception e) {
			org.junit.Assert.fail("wrong exception caught");
		}
	}

	@org.junit.Test
	public void testMinEndpoints() throws java.lang.Exception {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.optimization.UnivariateRealOptimizer solver = new org.apache.commons.math.optimization.univariate.BrentOptimizer();
		double result = solver.optimize(f, org.apache.commons.math.optimization.GoalType.MINIMIZE, ((3 * (java.lang.Math.PI)) / 2), 5);
		org.junit.Assert.assertEquals(((3 * (java.lang.Math.PI)) / 2), result, (70 * (solver.getAbsoluteAccuracy())));
		result = solver.optimize(f, org.apache.commons.math.optimization.GoalType.MINIMIZE, 4, ((3 * (java.lang.Math.PI)) / 2));
		org.junit.Assert.assertEquals(((3 * (java.lang.Math.PI)) / 2), result, (70 * (solver.getAbsoluteAccuracy())));
	}
}

