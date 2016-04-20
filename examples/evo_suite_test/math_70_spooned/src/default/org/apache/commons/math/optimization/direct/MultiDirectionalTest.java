package org.apache.commons.math.optimization.direct;


public class MultiDirectionalTest {
	@org.junit.Test
	public void testFunctionEvaluationExceptions() {
		org.apache.commons.math.analysis.MultivariateRealFunction wrong = new org.apache.commons.math.analysis.MultivariateRealFunction() {
			private static final long serialVersionUID = 4751314470965489371L;

			public double value(double[] x) throws org.apache.commons.math.FunctionEvaluationException {
				if ((x[0]) < 0) {
					throw new org.apache.commons.math.FunctionEvaluationException(x , "{0}" , "oops");
				} else if ((x[0]) > 1) {
					throw new org.apache.commons.math.FunctionEvaluationException(new java.lang.RuntimeException("oops") , x);
				} else {
					return (x[0]) * (1 - (x[0]));
				}
			}
		};
		try {
			org.apache.commons.math.optimization.direct.MultiDirectional optimizer = new org.apache.commons.math.optimization.direct.MultiDirectional(0.9 , 1.9);
			optimizer.optimize(wrong, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ -1.0 });
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.FunctionEvaluationException ce) {
			org.junit.Assert.assertNull(ce.getCause());
		} catch (java.lang.Exception e) {
			org.junit.Assert.fail(("wrong exception caught: " + (e.getMessage())));
		}
		try {
			org.apache.commons.math.optimization.direct.MultiDirectional optimizer = new org.apache.commons.math.optimization.direct.MultiDirectional(0.9 , 1.9);
			optimizer.optimize(wrong, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ +2.0 });
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.FunctionEvaluationException ce) {
			org.junit.Assert.assertNotNull(ce.getCause());
		} catch (java.lang.Exception e) {
			org.junit.Assert.fail(("wrong exception caught: " + (e.getMessage())));
		}
	}

	@org.junit.Test
	public void testMinimizeMaximize() throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		final double xM = -3.841947088256864;
		final double yM = -1.391745200270735;
		final double xP = 0.22866822373490592;
		final double yP = -yM;
		final double valueXmYm = 0.2373295333134217;
		final double valueXmYp = -valueXmYm;
		final double valueXpYm = -0.7290400707055187;
		final double valueXpYp = -valueXpYm;
		org.apache.commons.math.analysis.MultivariateRealFunction fourExtrema = new org.apache.commons.math.analysis.MultivariateRealFunction() {
			private static final long serialVersionUID = -7039124064449091152L;

			public double value(double[] variables) throws org.apache.commons.math.FunctionEvaluationException {
				final double x = variables[0];
				final double y = variables[1];
				return (x == 0) || (y == 0) ? 0 : ((((java.lang.Math.atan(x)) * (java.lang.Math.atan((x + 2)))) * (java.lang.Math.atan(y))) * (java.lang.Math.atan(y))) / (x * y);
			}
		};
		org.apache.commons.math.optimization.direct.MultiDirectional optimizer = new org.apache.commons.math.optimization.direct.MultiDirectional();
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(1.0E-11 , 1.0E-30));
		optimizer.setMaxIterations(200);
		optimizer.setStartConfiguration(new double[]{ 0.2 , 0.2 });
		org.apache.commons.math.optimization.RealPointValuePair optimum;
		optimum = optimizer.optimize(fourExtrema, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ -3.0 , 0 });
		org.junit.Assert.assertEquals(xM, optimum.getPoint()[0], 4.0E-6);
		org.junit.Assert.assertEquals(yP, optimum.getPoint()[1], 3.0E-6);
		org.junit.Assert.assertEquals(valueXmYp, optimum.getValue(), 8.0E-13);
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) > 120));
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) < 150));
		optimum = optimizer.optimize(fourExtrema, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ +1 , 0 });
		org.junit.Assert.assertEquals(xP, optimum.getPoint()[0], 2.0E-8);
		org.junit.Assert.assertEquals(yM, optimum.getPoint()[1], 3.0E-6);
		org.junit.Assert.assertEquals(valueXpYm, optimum.getValue(), 2.0E-12);
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) > 120));
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) < 150));
		optimum = optimizer.optimize(fourExtrema, org.apache.commons.math.optimization.GoalType.MAXIMIZE, new double[]{ -3.0 , 0.0 });
		org.junit.Assert.assertEquals(xM, optimum.getPoint()[0], 7.0E-7);
		org.junit.Assert.assertEquals(yM, optimum.getPoint()[1], 3.0E-7);
		org.junit.Assert.assertEquals(valueXmYm, optimum.getValue(), 2.0E-14);
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) > 120));
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) < 150));
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(1.0E-15 , 1.0E-30));
		optimum = optimizer.optimize(fourExtrema, org.apache.commons.math.optimization.GoalType.MAXIMIZE, new double[]{ +1 , 0 });
		org.junit.Assert.assertEquals(xP, optimum.getPoint()[0], 2.0E-8);
		org.junit.Assert.assertEquals(yP, optimum.getPoint()[1], 3.0E-6);
		org.junit.Assert.assertEquals(valueXpYp, optimum.getValue(), 2.0E-12);
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) > 180));
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) < 220));
	}

	@org.junit.Test
	public void testRosenbrock() throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		org.apache.commons.math.analysis.MultivariateRealFunction rosenbrock = new org.apache.commons.math.analysis.MultivariateRealFunction() {
			private static final long serialVersionUID = -9044950469615237490L;

			public double value(double[] x) throws org.apache.commons.math.FunctionEvaluationException {
				++(count);
				double a = (x[1]) - ((x[0]) * (x[0]));
				double b = 1.0 - (x[0]);
				return ((100 * a) * a) + (b * b);
			}
		};
		count = 0;
		org.apache.commons.math.optimization.direct.MultiDirectional optimizer = new org.apache.commons.math.optimization.direct.MultiDirectional();
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(-1 , 0.001));
		optimizer.setMaxIterations(100);
		optimizer.setStartConfiguration(new double[][]{ new double[]{ -1.2 , 1.0 } , new double[]{ 0.9 , 1.2 } , new double[]{ 3.5 , -2.3 } });
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(rosenbrock, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ -1.2 , 1.0 });
		org.junit.Assert.assertEquals(count, optimizer.getEvaluations());
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) > 50));
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) < 100));
		org.junit.Assert.assertTrue(((optimum.getValue()) > 0.01));
	}

	@org.junit.Test
	public void testPowell() throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		org.apache.commons.math.analysis.MultivariateRealFunction powell = new org.apache.commons.math.analysis.MultivariateRealFunction() {
			private static final long serialVersionUID = -832162886102041840L;

			public double value(double[] x) throws org.apache.commons.math.FunctionEvaluationException {
				++(count);
				double a = (x[0]) + (10 * (x[1]));
				double b = (x[2]) - (x[3]);
				double c = (x[1]) - (2 * (x[2]));
				double d = (x[0]) - (x[3]);
				return (((a * a) + ((5 * b) * b)) + (((c * c) * c) * c)) + ((((10 * d) * d) * d) * d);
			}
		};
		count = 0;
		org.apache.commons.math.optimization.direct.MultiDirectional optimizer = new org.apache.commons.math.optimization.direct.MultiDirectional();
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(-1.0 , 0.001));
		optimizer.setMaxIterations(1000);
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(powell, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 3.0 , -1.0 , 0.0 , 1.0 });
		org.junit.Assert.assertEquals(count, optimizer.getEvaluations());
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) > 800));
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) < 900));
		org.junit.Assert.assertTrue(((optimum.getValue()) > 0.01));
	}

	@org.junit.Test
	public void testMath283() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.direct.MultiDirectional multiDirectional = new org.apache.commons.math.optimization.direct.MultiDirectional();
		multiDirectional.setMaxIterations(100);
		multiDirectional.setMaxEvaluations(1000);
		final org.apache.commons.math.optimization.direct.MultiDirectionalTest.Gaussian2D function = new org.apache.commons.math.optimization.direct.MultiDirectionalTest.Gaussian2D(0.0 , 0.0 , 1.0);
		org.apache.commons.math.optimization.RealPointValuePair estimate = multiDirectional.optimize(function, org.apache.commons.math.optimization.GoalType.MAXIMIZE, function.getMaximumPosition());
		final double EPSILON = 1.0E-5;
		final double expectedMaximum = function.getMaximum();
		final double actualMaximum = estimate.getValue();
		org.junit.Assert.assertEquals(expectedMaximum, actualMaximum, EPSILON);
		final double[] expectedPosition = function.getMaximumPosition();
		final double[] actualPosition = estimate.getPoint();
		org.junit.Assert.assertEquals(expectedPosition[0], actualPosition[0], EPSILON);
		org.junit.Assert.assertEquals(expectedPosition[1], actualPosition[1], EPSILON);
	}

	private static class Gaussian2D implements org.apache.commons.math.analysis.MultivariateRealFunction {
		private final double[] maximumPosition;

		private final double std;

		public Gaussian2D(double xOpt ,double yOpt ,double std) {
			maximumPosition = new double[]{ xOpt , yOpt };
			this.std = std;
		}

		public double getMaximum() {
			return value(maximumPosition);
		}

		public double[] getMaximumPosition() {
			return maximumPosition.clone();
		}

		public double value(double[] point) {
			final double x = point[0];
			final double y = point[1];
			final double twoS2 = (2.0 * (std)) * (std);
			return (1.0 / (twoS2 * (java.lang.Math.PI))) * (java.lang.Math.exp(((-((x * x) + (y * y))) / twoS2)));
		}
	}

	private int count;
}

