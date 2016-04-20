package org.apache.commons.math.optimization.general;


public class LevenbergMarquardtOptimizerTest extends junit.framework.TestCase {
	public LevenbergMarquardtOptimizerTest(java.lang.String name) {
		super(name);
	}

	public void testTrivial() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem(new double[][]{ new double[]{ 2 } } , new double[]{ 3 });
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer optimizer = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer();
		org.apache.commons.math.optimization.VectorialPointValuePair optimum = optimizer.optimize(problem, problem.target, new double[]{ 1 }, new double[]{ 0 });
		junit.framework.Assert.assertEquals(0, optimizer.getRMS(), 1.0E-10);
		try {
			optimizer.guessParametersErrors();
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.optimization.OptimizationException ee) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		junit.framework.Assert.assertEquals(1.5, optimum.getPoint()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(3.0, optimum.getValue()[0], 1.0E-10);
	}

	public void testQRColumnsPermutation() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem(new double[][]{ new double[]{ 1.0 , -1.0 } , new double[]{ 0.0 , 2.0 } , new double[]{ 1.0 , -2.0 } } , new double[]{ 4.0 , 6.0 , 1.0 });
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer optimizer = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer();
		org.apache.commons.math.optimization.VectorialPointValuePair optimum = optimizer.optimize(problem, problem.target, new double[]{ 1 , 1 , 1 }, new double[]{ 0 , 0 });
		junit.framework.Assert.assertEquals(0, optimizer.getRMS(), 1.0E-10);
		junit.framework.Assert.assertEquals(7.0, optimum.getPoint()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(3.0, optimum.getPoint()[1], 1.0E-10);
		junit.framework.Assert.assertEquals(4.0, optimum.getValue()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(6.0, optimum.getValue()[1], 1.0E-10);
		junit.framework.Assert.assertEquals(1.0, optimum.getValue()[2], 1.0E-10);
	}

	public void testNoDependency() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem(new double[][]{ new double[]{ 2 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 2 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 2 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 2 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 2 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 2 } } , new double[]{ 0.0 , 1.1 , 2.2 , 3.3 , 4.4 , 5.5 });
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer optimizer = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer();
		org.apache.commons.math.optimization.VectorialPointValuePair optimum = optimizer.optimize(problem, problem.target, new double[]{ 1 , 1 , 1 , 1 , 1 , 1 }, new double[]{ 0 , 0 , 0 , 0 , 0 , 0 });
		junit.framework.Assert.assertEquals(0, optimizer.getRMS(), 1.0E-10);
		for (int i = 0 ; i < (problem.target.length) ; ++i) {
			junit.framework.Assert.assertEquals((0.55 * i), optimum.getPoint()[i], 1.0E-10);
		}
	}

	public void testOneSet() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem(new double[][]{ new double[]{ 1 , 0 , 0 } , new double[]{ -1 , 1 , 0 } , new double[]{ 0 , -1 , 1 } } , new double[]{ 1 , 1 , 1 });
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer optimizer = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer();
		org.apache.commons.math.optimization.VectorialPointValuePair optimum = optimizer.optimize(problem, problem.target, new double[]{ 1 , 1 , 1 }, new double[]{ 0 , 0 , 0 });
		junit.framework.Assert.assertEquals(0, optimizer.getRMS(), 1.0E-10);
		junit.framework.Assert.assertEquals(1.0, optimum.getPoint()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(2.0, optimum.getPoint()[1], 1.0E-10);
		junit.framework.Assert.assertEquals(3.0, optimum.getPoint()[2], 1.0E-10);
	}

	public void testTwoSets() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		double epsilon = 1.0E-7;
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem(new double[][]{ new double[]{ 2 , 1 , 0 , 4 , 0 , 0 } , new double[]{ -4 , -2 , 3 , -7 , 0 , 0 } , new double[]{ 4 , 1 , -2 , 8 , 0 , 0 } , new double[]{ 0 , -3 , -12 , -1 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , epsilon , 1 } , new double[]{ 0 , 0 , 0 , 0 , 1 , 1 } } , new double[]{ 2 , -9 , 2 , 2 , 1 + (epsilon * epsilon) , 2 });
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer optimizer = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer();
		org.apache.commons.math.optimization.VectorialPointValuePair optimum = optimizer.optimize(problem, problem.target, new double[]{ 1 , 1 , 1 , 1 , 1 , 1 }, new double[]{ 0 , 0 , 0 , 0 , 0 , 0 });
		junit.framework.Assert.assertEquals(0, optimizer.getRMS(), 1.0E-10);
		junit.framework.Assert.assertEquals(3.0, optimum.getPoint()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(4.0, optimum.getPoint()[1], 1.0E-10);
		junit.framework.Assert.assertEquals(-1.0, optimum.getPoint()[2], 1.0E-10);
		junit.framework.Assert.assertEquals(-2.0, optimum.getPoint()[3], 1.0E-10);
		junit.framework.Assert.assertEquals((1.0 + epsilon), optimum.getPoint()[4], 1.0E-10);
		junit.framework.Assert.assertEquals((1.0 - epsilon), optimum.getPoint()[5], 1.0E-10);
	}

	public void testNonInversible() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem(new double[][]{ new double[]{ 1 , 2 , -3 } , new double[]{ 2 , 1 , 3 } , new double[]{ -3 , 0 , -9 } } , new double[]{ 1 , 1 , 1 });
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer optimizer = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer();
		optimizer.optimize(problem, problem.target, new double[]{ 1 , 1 , 1 }, new double[]{ 0 , 0 , 0 });
		junit.framework.Assert.assertTrue((((java.lang.Math.sqrt(problem.target.length)) * (optimizer.getRMS())) > 0.6));
		try {
			optimizer.getCovariances();
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.optimization.OptimizationException ee) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testIllConditioned() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem problem1 = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem(new double[][]{ new double[]{ 10.0 , 7.0 , 8.0 , 7.0 } , new double[]{ 7.0 , 5.0 , 6.0 , 5.0 } , new double[]{ 8.0 , 6.0 , 10.0 , 9.0 } , new double[]{ 7.0 , 5.0 , 9.0 , 10.0 } } , new double[]{ 32 , 23 , 33 , 31 });
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer optimizer = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer();
		org.apache.commons.math.optimization.VectorialPointValuePair optimum1 = optimizer.optimize(problem1, problem1.target, new double[]{ 1 , 1 , 1 , 1 }, new double[]{ 0 , 1 , 2 , 3 });
		junit.framework.Assert.assertEquals(0, optimizer.getRMS(), 1.0E-10);
		junit.framework.Assert.assertEquals(1.0, optimum1.getPoint()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(1.0, optimum1.getPoint()[1], 1.0E-10);
		junit.framework.Assert.assertEquals(1.0, optimum1.getPoint()[2], 1.0E-10);
		junit.framework.Assert.assertEquals(1.0, optimum1.getPoint()[3], 1.0E-10);
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem problem2 = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem(new double[][]{ new double[]{ 10.0 , 7.0 , 8.1 , 7.2 } , new double[]{ 7.08 , 5.04 , 6.0 , 5.0 } , new double[]{ 8.0 , 5.98 , 9.89 , 9.0 } , new double[]{ 6.99 , 4.99 , 9.0 , 9.98 } } , new double[]{ 32 , 23 , 33 , 31 });
		org.apache.commons.math.optimization.VectorialPointValuePair optimum2 = optimizer.optimize(problem2, problem2.target, new double[]{ 1 , 1 , 1 , 1 }, new double[]{ 0 , 1 , 2 , 3 });
		junit.framework.Assert.assertEquals(0, optimizer.getRMS(), 1.0E-10);
		junit.framework.Assert.assertEquals(-81.0, optimum2.getPoint()[0], 1.0E-8);
		junit.framework.Assert.assertEquals(137.0, optimum2.getPoint()[1], 1.0E-8);
		junit.framework.Assert.assertEquals(-34.0, optimum2.getPoint()[2], 1.0E-8);
		junit.framework.Assert.assertEquals(22.0, optimum2.getPoint()[3], 1.0E-8);
	}

	public void testMoreEstimatedParametersSimple() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem(new double[][]{ new double[]{ 3.0 , 2.0 , 0.0 , 0.0 } , new double[]{ 0.0 , 1.0 , -1.0 , 1.0 } , new double[]{ 2.0 , 0.0 , 1.0 , 0.0 } } , new double[]{ 7.0 , 3.0 , 5.0 });
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer optimizer = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer();
		optimizer.optimize(problem, problem.target, new double[]{ 1 , 1 , 1 }, new double[]{ 7 , 6 , 5 , 4 });
		junit.framework.Assert.assertEquals(0, optimizer.getRMS(), 1.0E-10);
	}

	public void testMoreEstimatedParametersUnsorted() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem(new double[][]{ new double[]{ 1.0 , 1.0 , 0.0 , 0.0 , 0.0 , 0.0 } , new double[]{ 0.0 , 0.0 , 1.0 , 1.0 , 1.0 , 0.0 } , new double[]{ 0.0 , 0.0 , 0.0 , 0.0 , 1.0 , -1.0 } , new double[]{ 0.0 , 0.0 , -1.0 , 1.0 , 0.0 , 1.0 } , new double[]{ 0.0 , 0.0 , 0.0 , -1.0 , 1.0 , 0.0 } } , new double[]{ 3.0 , 12.0 , -1.0 , 7.0 , 1.0 });
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer optimizer = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer();
		org.apache.commons.math.optimization.VectorialPointValuePair optimum = optimizer.optimize(problem, problem.target, new double[]{ 1 , 1 , 1 , 1 , 1 }, new double[]{ 2 , 2 , 2 , 2 , 2 , 2 });
		junit.framework.Assert.assertEquals(0, optimizer.getRMS(), 1.0E-10);
		junit.framework.Assert.assertEquals(3.0, optimum.getPointRef()[2], 1.0E-10);
		junit.framework.Assert.assertEquals(4.0, optimum.getPointRef()[3], 1.0E-10);
		junit.framework.Assert.assertEquals(5.0, optimum.getPointRef()[4], 1.0E-10);
		junit.framework.Assert.assertEquals(6.0, optimum.getPointRef()[5], 1.0E-10);
	}

	public void testRedundantEquations() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem(new double[][]{ new double[]{ 1.0 , 1.0 } , new double[]{ 1.0 , -1.0 } , new double[]{ 1.0 , 3.0 } } , new double[]{ 3.0 , 1.0 , 5.0 });
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer optimizer = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer();
		org.apache.commons.math.optimization.VectorialPointValuePair optimum = optimizer.optimize(problem, problem.target, new double[]{ 1 , 1 , 1 }, new double[]{ 1 , 1 });
		junit.framework.Assert.assertEquals(0, optimizer.getRMS(), 1.0E-10);
		junit.framework.Assert.assertEquals(2.0, optimum.getPointRef()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(1.0, optimum.getPointRef()[1], 1.0E-10);
	}

	public void testInconsistentEquations() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem(new double[][]{ new double[]{ 1.0 , 1.0 } , new double[]{ 1.0 , -1.0 } , new double[]{ 1.0 , 3.0 } } , new double[]{ 3.0 , 1.0 , 4.0 });
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer optimizer = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer();
		optimizer.optimize(problem, problem.target, new double[]{ 1 , 1 , 1 }, new double[]{ 1 , 1 });
		junit.framework.Assert.assertTrue(((optimizer.getRMS()) > 0.1));
	}

	public void testInconsistentSizes() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.LinearProblem(new double[][]{ new double[]{ 1 , 0 } , new double[]{ 0 , 1 } } , new double[]{ -1 , 1 });
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer optimizer = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer();
		org.apache.commons.math.optimization.VectorialPointValuePair optimum = optimizer.optimize(problem, problem.target, new double[]{ 1 , 1 }, new double[]{ 0 , 0 });
		junit.framework.Assert.assertEquals(0, optimizer.getRMS(), 1.0E-10);
		junit.framework.Assert.assertEquals(-1, optimum.getPoint()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(+1, optimum.getPoint()[1], 1.0E-10);
		try {
			optimizer.optimize(problem, problem.target, new double[]{ 1 }, new double[]{ 0 , 0 });
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.optimization.OptimizationException oe) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		try {
			optimizer.optimize(problem, new double[]{ 1 }, new double[]{ 1 }, new double[]{ 0 , 0 });
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.FunctionEvaluationException oe) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testControlParameters() {
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.Circle circle = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.Circle();
		circle.addPoint(30.0, 68.0);
		circle.addPoint(50.0, -6.0);
		circle.addPoint(110.0, -20.0);
		circle.addPoint(35.0, 15.0);
		circle.addPoint(45.0, 97.0);
		checkEstimate(circle, 0.1, 10, 1.0E-14, 1.0E-16, 1.0E-10, false);
		checkEstimate(circle, 0.1, 10, 1.0E-15, 1.0E-17, 1.0E-10, true);
		checkEstimate(circle, 0.1, 5, 1.0E-15, 1.0E-16, 1.0E-10, true);
		circle.addPoint(300, -300);
		checkEstimate(circle, 0.1, 20, 1.0E-18, 1.0E-16, 1.0E-10, true);
	}

	private void checkEstimate(org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction problem, double initialStepBoundFactor, int maxCostEval, double costRelativeTolerance, double parRelativeTolerance, double orthoTolerance, boolean shouldFail) {
		try {
			org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer optimizer = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer();
			optimizer.setInitialStepBoundFactor(initialStepBoundFactor);
			optimizer.setMaxIterations(maxCostEval);
			optimizer.setCostRelativeTolerance(costRelativeTolerance);
			optimizer.setParRelativeTolerance(parRelativeTolerance);
			optimizer.setOrthoTolerance(orthoTolerance);
			optimizer.optimize(problem, new double[]{ 0 , 0 , 0 , 0 , 0 }, new double[]{ 1 , 1 , 1 , 1 , 1 }, new double[]{ 98.68 , 47.345 });
			junit.framework.Assert.assertTrue(!shouldFail);
		} catch (org.apache.commons.math.optimization.OptimizationException ee) {
			junit.framework.Assert.assertTrue(shouldFail);
		} catch (org.apache.commons.math.FunctionEvaluationException ee) {
			junit.framework.Assert.assertTrue(shouldFail);
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception type caught");
		}
	}

	public void testCircleFitting() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.Circle circle = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.Circle();
		circle.addPoint(30.0, 68.0);
		circle.addPoint(50.0, -6.0);
		circle.addPoint(110.0, -20.0);
		circle.addPoint(35.0, 15.0);
		circle.addPoint(45.0, 97.0);
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer optimizer = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer();
		org.apache.commons.math.optimization.VectorialPointValuePair optimum = optimizer.optimize(circle, new double[]{ 0 , 0 , 0 , 0 , 0 }, new double[]{ 1 , 1 , 1 , 1 , 1 }, new double[]{ 98.68 , 47.345 });
		junit.framework.Assert.assertTrue(((optimizer.getEvaluations()) < 10));
		junit.framework.Assert.assertTrue(((optimizer.getJacobianEvaluations()) < 10));
		double rms = optimizer.getRMS();
		junit.framework.Assert.assertEquals(1.768262623567235, ((java.lang.Math.sqrt(circle.getN())) * rms), 1.0E-10);
		java.awt.geom.Point2D.Double center = new java.awt.geom.Point2D.Double(optimum.getPointRef()[0] , optimum.getPointRef()[1]);
		junit.framework.Assert.assertEquals(69.96016176931406, circle.getRadius(center), 1.0E-10);
		junit.framework.Assert.assertEquals(96.07590211815305, center.x, 1.0E-10);
		junit.framework.Assert.assertEquals(48.13516790438953, center.y, 1.0E-10);
		double[][] cov = optimizer.getCovariances();
		junit.framework.Assert.assertEquals(1.839, cov[0][0], 0.001);
		junit.framework.Assert.assertEquals(0.731, cov[0][1], 0.001);
		junit.framework.Assert.assertEquals(cov[0][1], cov[1][0], 1.0E-14);
		junit.framework.Assert.assertEquals(0.786, cov[1][1], 0.001);
		double[] errors = optimizer.guessParametersErrors();
		junit.framework.Assert.assertEquals(1.384, errors[0], 0.001);
		junit.framework.Assert.assertEquals(0.905, errors[1], 0.001);
		double r = circle.getRadius(center);
		for (double d = 0 ; d < (2 * (java.lang.Math.PI)) ; d += 0.01) {
			circle.addPoint(((center.x) + (r * (java.lang.Math.cos(d)))), ((center.y) + (r * (java.lang.Math.sin(d)))));
		}
		double[] target = new double[circle.getN()];
		java.util.Arrays.fill(target, 0.0);
		double[] weights = new double[circle.getN()];
		java.util.Arrays.fill(weights, 2.0);
		optimizer.optimize(circle, target, weights, new double[]{ 98.68 , 47.345 });
		cov = optimizer.getCovariances();
		junit.framework.Assert.assertEquals(0.0016, cov[0][0], 0.001);
		junit.framework.Assert.assertEquals(3.2E-7, cov[0][1], 1.0E-9);
		junit.framework.Assert.assertEquals(cov[0][1], cov[1][0], 1.0E-14);
		junit.framework.Assert.assertEquals(0.0016, cov[1][1], 0.001);
		errors = optimizer.guessParametersErrors();
		junit.framework.Assert.assertEquals(0.002, errors[0], 0.001);
		junit.framework.Assert.assertEquals(0.002, errors[1], 0.001);
	}

	public void testCircleFittingBadInit() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.Circle circle = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.Circle();
		double[][] points = new double[][]{ new double[]{ -0.312967 , 0.072366 } , new double[]{ -0.339248 , 0.132965 } , new double[]{ -0.37978 , 0.202724 } , new double[]{ -0.390426 , 0.260487 } , new double[]{ -0.361212 , 0.328325 } , new double[]{ -0.346039 , 0.392619 } , new double[]{ -0.280579 , 0.444306 } , new double[]{ -0.216035 , 0.470009 } , new double[]{ -0.149127 , 0.493832 } , new double[]{ -0.075133 , 0.483271 } , new double[]{ -0.007759 , 0.45268 } , new double[]{ 0.060071 , 0.410235 } , new double[]{ 0.103037 , 0.341076 } , new double[]{ 0.118438 , 0.273884 } , new double[]{ 0.131293 , 0.192201 } , new double[]{ 0.115869 , 0.129797 } , new double[]{ 0.072223 , 0.058396 } , new double[]{ 0.022884 , 7.18E-4 } , new double[]{ -0.053355 , -0.020405 } , new double[]{ -0.123584 , -0.032451 } , new double[]{ -0.216248 , -0.032862 } , new double[]{ -0.278592 , -0.005008 } , new double[]{ -0.337655 , 0.056658 } , new double[]{ -0.385899 , 0.112526 } , new double[]{ -0.405517 , 0.186957 } , new double[]{ -0.415374 , 0.262071 } , new double[]{ -0.387482 , 0.343398 } , new double[]{ -0.347322 , 0.397943 } , new double[]{ -0.287623 , 0.458425 } , new double[]{ -0.223502 , 0.475513 } , new double[]{ -0.135352 , 0.478186 } , new double[]{ -0.061221 , 0.483371 } , new double[]{ 0.003711 , 0.422737 } , new double[]{ 0.065054 , 0.37583 } , new double[]{ 0.108108 , 0.297099 } , new double[]{ 0.123882 , 0.22285 } , new double[]{ 0.117729 , 0.134382 } , new double[]{ 0.085195 , 0.05682 } , new double[]{ 0.0298 , -0.019138 } , new double[]{ -0.02752 , -0.072374 } , new double[]{ -0.102268 , -0.091555 } , new double[]{ -0.200299 , -0.106578 } , new double[]{ -0.292731 , -0.091473 } , new double[]{ -0.356288 , -0.051108 } , new double[]{ -0.420561 , 0.014926 } , new double[]{ -0.471036 , 0.074716 } , new double[]{ -0.488638 , 0.182508 } , new double[]{ -0.48599 , 0.254068 } , new double[]{ -0.463943 , 0.338438 } , new double[]{ -0.406453 , 0.404704 } , new double[]{ -0.334287 , 0.466119 } , new double[]{ -0.254244 , 0.503188 } , new double[]{ -0.161548 , 0.495769 } , new double[]{ -0.075733 , 0.49556 } , new double[]{ 0.001375 , 0.434937 } , new double[]{ 0.082787 , 0.385806 } , new double[]{ 0.11549 , 0.323807 } , new double[]{ 0.141089 , 0.22345 } , new double[]{ 0.138693 , 0.131703 } , new double[]{ 0.126415 , 0.049174 } , new double[]{ 0.066518 , -0.010217 } , new double[]{ -0.005184 , -0.070647 } , new double[]{ -0.080985 , -0.103635 } , new double[]{ -0.177377 , -0.116887 } , new double[]{ -0.260628 , -0.100258 } , new double[]{ -0.335756 , -0.056251 } , new double[]{ -0.405195 , -8.95E-4 } , new double[]{ -0.444937 , 0.085456 } , new double[]{ -0.484357 , 0.175597 } , new double[]{ -0.472453 , 0.248681 } , new double[]{ -0.43858 , 0.347463 } , new double[]{ -0.402304 , 0.422428 } , new double[]{ -0.326777 , 0.479438 } , new double[]{ -0.247797 , 0.505581 } , new double[]{ -0.152676 , 0.51938 } , new double[]{ -0.071754 , 0.516264 } , new double[]{ 0.015942 , 0.472802 } , new double[]{ 0.076608 , 0.419077 } , new double[]{ 0.127673 , 0.330264 } , new double[]{ 0.159951 , 0.26215 } , new double[]{ 0.15353 , 0.172681 } , new double[]{ 0.140653 , 0.089229 } , new double[]{ 0.078666 , 0.024981 } , new double[]{ 0.023807 , -0.037022 } , new double[]{ -0.048837 , -0.077056 } , new double[]{ -0.127729 , -0.075338 } , new double[]{ -0.221271 , -0.067526 } };
		double[] target = new double[points.length];
		java.util.Arrays.fill(target, 0.0);
		double[] weights = new double[points.length];
		java.util.Arrays.fill(weights, 2.0);
		for (int i = 0 ; i < (points.length) ; ++i) {
			circle.addPoint(points[i][0], points[i][1]);
		}
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer optimizer = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer();
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleVectorialValueChecker(1.0E-10 , 1.0E-10));
		org.apache.commons.math.optimization.VectorialPointValuePair optimum = optimizer.optimize(circle, target, weights, new double[]{ -12 , -12 });
		java.awt.geom.Point2D.Double center = new java.awt.geom.Point2D.Double(optimum.getPointRef()[0] , optimum.getPointRef()[1]);
		junit.framework.Assert.assertTrue(((optimizer.getEvaluations()) < 25));
		junit.framework.Assert.assertTrue(((optimizer.getJacobianEvaluations()) < 20));
		junit.framework.Assert.assertEquals(0.043, optimizer.getRMS(), 0.001);
		junit.framework.Assert.assertEquals(0.292235, circle.getRadius(center), 1.0E-6);
		junit.framework.Assert.assertEquals(-0.151738, center.x, 1.0E-6);
		junit.framework.Assert.assertEquals(0.2075001, center.y, 1.0E-6);
	}

	public void testMath199() throws org.apache.commons.math.FunctionEvaluationException {
		try {
			org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.QuadraticProblem problem = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizerTest.QuadraticProblem();
			problem.addPoint(0, -3.182591015485607);
			problem.addPoint(1, -2.5581184967730577);
			problem.addPoint(2, -2.1488478161387325);
			problem.addPoint(3, -1.9122489313410047);
			problem.addPoint(4, 1.7785661310051026);
			new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer().optimize(problem, new double[]{ 0 , 0 , 0 , 0 , 0 }, new double[]{ 0.0 , 4.4E-323 , 1.0 , 4.4E-323 , 0.0 }, new double[]{ 0 , 0 , 0 });
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.optimization.OptimizationException ee) {
		}
	}

	private static class LinearProblem implements java.io.Serializable , org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction {
		private static final long serialVersionUID = 703247177355019415L;

		final org.apache.commons.math.linear.RealMatrix factors;

		final double[] target;

		public LinearProblem(double[][] factors ,double[] target) {
			this.factors = new org.apache.commons.math.linear.BlockRealMatrix(factors);
			this.target = target;
		}

		public double[] value(double[] variables) {
			return factors.operate(variables);
		}

		public org.apache.commons.math.analysis.MultivariateMatrixFunction jacobian() {
			return new org.apache.commons.math.analysis.MultivariateMatrixFunction() {
				private static final long serialVersionUID = 556396458721526234L;

				public double[][] value(double[] point) {
					return factors.getData();
				}
			};
		}
	}

	private static class Circle implements java.io.Serializable , org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction {
		private static final long serialVersionUID = -4711170319243817874L;

		private java.util.ArrayList<java.awt.geom.Point2D.Double> points;

		public Circle() {
			points = new java.util.ArrayList<java.awt.geom.Point2D.Double>();
		}

		public void addPoint(double px, double py) {
			points.add(new java.awt.geom.Point2D.Double(px , py));
		}

		public int getN() {
			return points.size();
		}

		public double getRadius(java.awt.geom.Point2D.Double center) {
			double r = 0;
			for (java.awt.geom.Point2D.Double point : points) {
				r += point.distance(center);
			}
			return r / (points.size());
		}

		private double[][] jacobian(double[] point) {
			int n = points.size();
			java.awt.geom.Point2D.Double center = new java.awt.geom.Point2D.Double(point[0] , point[1]);
			double dRdX = 0;
			double dRdY = 0;
			for (java.awt.geom.Point2D.Double pk : points) {
				double dk = pk.distance(center);
				dRdX += ((center.x) - (pk.x)) / dk;
				dRdY += ((center.y) - (pk.y)) / dk;
			}
			dRdX /= n;
			dRdY /= n;
			double[][] jacobian = new double[n][2];
			for (int i = 0 ; i < n ; ++i) {
				java.awt.geom.Point2D.Double pi = points.get(i);
				double di = pi.distance(center);
				jacobian[i][0] = (((center.x) - (pi.x)) / di) - dRdX;
				jacobian[i][1] = (((center.y) - (pi.y)) / di) - dRdY;
			}
			return jacobian;
		}

		public double[] value(double[] variables) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException {
			java.awt.geom.Point2D.Double center = new java.awt.geom.Point2D.Double(variables[0] , variables[1]);
			double radius = getRadius(center);
			double[] residuals = new double[points.size()];
			for (int i = 0 ; i < (residuals.length) ; ++i) {
				residuals[i] = (points.get(i).distance(center)) - radius;
			}
			return residuals;
		}

		public org.apache.commons.math.analysis.MultivariateMatrixFunction jacobian() {
			return new org.apache.commons.math.analysis.MultivariateMatrixFunction() {
				private static final long serialVersionUID = -4340046230875165095L;

				public double[][] value(double[] point) {
					return jacobian(point);
				}
			};
		}
	}

	private static class QuadraticProblem implements java.io.Serializable , org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction {
		private static final long serialVersionUID = 7072187082052755854L;

		private java.util.List<java.lang.Double> x;

		private java.util.List<java.lang.Double> y;

		public QuadraticProblem() {
			x = new java.util.ArrayList<java.lang.Double>();
			y = new java.util.ArrayList<java.lang.Double>();
		}

		public void addPoint(double x, double y) {
			this.x.add(x);
			this.y.add(y);
		}

		private double[][] jacobian(double[] variables) {
			double[][] jacobian = new double[x.size()][3];
			for (int i = 0 ; i < (jacobian.length) ; ++i) {
				jacobian[i][0] = (x.get(i)) * (x.get(i));
				jacobian[i][1] = x.get(i);
				jacobian[i][2] = 1.0;
			}
			return jacobian;
		}

		public double[] value(double[] variables) {
			double[] values = new double[x.size()];
			for (int i = 0 ; i < (values.length) ; ++i) {
				values[i] = ((((variables[0]) * (x.get(i))) + (variables[1])) * (x.get(i))) + (variables[2]);
			}
			return values;
		}

		public org.apache.commons.math.analysis.MultivariateMatrixFunction jacobian() {
			return new org.apache.commons.math.analysis.MultivariateMatrixFunction() {
				private static final long serialVersionUID = -8673650298627399464L;

				public double[][] value(double[] point) {
					return jacobian(point);
				}
			};
		}
	}
}

