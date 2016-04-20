package org.apache.commons.math.optimization.direct;


public class NelderMeadTest {
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
			org.apache.commons.math.optimization.direct.NelderMead optimizer = new org.apache.commons.math.optimization.direct.NelderMead(0.9 , 1.9 , 0.4 , 0.6);
			optimizer.optimize(wrong, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ -1.0 });
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.FunctionEvaluationException ce) {
			org.junit.Assert.assertNull(ce.getCause());
		} catch (java.lang.Exception e) {
			org.junit.Assert.fail(("wrong exception caught: " + (e.getMessage())));
		}
		try {
			org.apache.commons.math.optimization.direct.NelderMead optimizer = new org.apache.commons.math.optimization.direct.NelderMead(0.9 , 1.9 , 0.4 , 0.6);
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
		org.apache.commons.math.optimization.direct.NelderMead optimizer = new org.apache.commons.math.optimization.direct.NelderMead();
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(1.0E-10 , 1.0E-30));
		optimizer.setMaxIterations(100);
		optimizer.setStartConfiguration(new double[]{ 0.2 , 0.2 });
		org.apache.commons.math.optimization.RealPointValuePair optimum;
		optimum = optimizer.optimize(fourExtrema, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ -3.0 , 0 });
		org.junit.Assert.assertEquals(xM, optimum.getPoint()[0], 2.0E-7);
		org.junit.Assert.assertEquals(yP, optimum.getPoint()[1], 2.0E-5);
		org.junit.Assert.assertEquals(valueXmYp, optimum.getValue(), 6.0E-12);
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) > 60));
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) < 90));
		optimum = optimizer.optimize(fourExtrema, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ +1 , 0 });
		org.junit.Assert.assertEquals(xP, optimum.getPoint()[0], 5.0E-6);
		org.junit.Assert.assertEquals(yM, optimum.getPoint()[1], 6.0E-6);
		org.junit.Assert.assertEquals(valueXpYm, optimum.getValue(), 1.0E-11);
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) > 60));
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) < 90));
		optimum = optimizer.optimize(fourExtrema, org.apache.commons.math.optimization.GoalType.MAXIMIZE, new double[]{ -3.0 , 0.0 });
		org.junit.Assert.assertEquals(xM, optimum.getPoint()[0], 1.0E-5);
		org.junit.Assert.assertEquals(yM, optimum.getPoint()[1], 3.0E-6);
		org.junit.Assert.assertEquals(valueXmYm, optimum.getValue(), 3.0E-12);
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) > 60));
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) < 90));
		optimum = optimizer.optimize(fourExtrema, org.apache.commons.math.optimization.GoalType.MAXIMIZE, new double[]{ +1 , 0 });
		org.junit.Assert.assertEquals(xP, optimum.getPoint()[0], 4.0E-6);
		org.junit.Assert.assertEquals(yP, optimum.getPoint()[1], 5.0E-6);
		org.junit.Assert.assertEquals(valueXpYp, optimum.getValue(), 7.0E-12);
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) > 60));
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) < 90));
	}

	@org.junit.Test
	public void testRosenbrock() throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		org.apache.commons.math.optimization.direct.NelderMeadTest.Rosenbrock rosenbrock = new org.apache.commons.math.optimization.direct.NelderMeadTest.Rosenbrock();
		org.apache.commons.math.optimization.direct.NelderMead optimizer = new org.apache.commons.math.optimization.direct.NelderMead();
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(-1 , 0.001));
		optimizer.setMaxIterations(100);
		optimizer.setStartConfiguration(new double[][]{ new double[]{ -1.2 , 1.0 } , new double[]{ 0.9 , 1.2 } , new double[]{ 3.5 , -2.3 } });
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(rosenbrock, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ -1.2 , 1.0 });
		org.junit.Assert.assertEquals(rosenbrock.getCount(), optimizer.getEvaluations());
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) > 40));
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) < 50));
		org.junit.Assert.assertTrue(((optimum.getValue()) < 8.0E-4));
	}

	@org.junit.Test
	public void testPowell() throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		org.apache.commons.math.optimization.direct.NelderMeadTest.Powell powell = new org.apache.commons.math.optimization.direct.NelderMeadTest.Powell();
		org.apache.commons.math.optimization.direct.NelderMead optimizer = new org.apache.commons.math.optimization.direct.NelderMead();
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(-1.0 , 0.001));
		optimizer.setMaxIterations(200);
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(powell, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 3.0 , -1.0 , 0.0 , 1.0 });
		org.junit.Assert.assertEquals(powell.getCount(), optimizer.getEvaluations());
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) > 110));
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) < 130));
		org.junit.Assert.assertTrue(((optimum.getValue()) < 0.002));
	}

	@org.junit.Test
	public void testLeastSquares1() throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		final org.apache.commons.math.linear.RealMatrix factors = new org.apache.commons.math.linear.Array2DRowRealMatrix(new double[][]{ new double[]{ 1.0 , 0.0 } , new double[]{ 0.0 , 1.0 } } , false);
		org.apache.commons.math.optimization.LeastSquaresConverter ls = new org.apache.commons.math.optimization.LeastSquaresConverter(new org.apache.commons.math.analysis.MultivariateVectorialFunction() {
			public double[] value(double[] variables) {
				return factors.operate(variables);
			}
		} , new double[]{ 2.0 , -3.0 });
		org.apache.commons.math.optimization.direct.NelderMead optimizer = new org.apache.commons.math.optimization.direct.NelderMead();
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(-1.0 , 1.0E-6));
		optimizer.setMaxIterations(200);
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(ls, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 10.0 , 10.0 });
		org.junit.Assert.assertEquals(2.0, optimum.getPointRef()[0], 3.0E-5);
		org.junit.Assert.assertEquals(-3.0, optimum.getPointRef()[1], 4.0E-4);
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) > 60));
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) < 80));
		org.junit.Assert.assertTrue(((optimum.getValue()) < 1.0E-6));
	}

	@org.junit.Test
	public void testLeastSquares2() throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		final org.apache.commons.math.linear.RealMatrix factors = new org.apache.commons.math.linear.Array2DRowRealMatrix(new double[][]{ new double[]{ 1.0 , 0.0 } , new double[]{ 0.0 , 1.0 } } , false);
		org.apache.commons.math.optimization.LeastSquaresConverter ls = new org.apache.commons.math.optimization.LeastSquaresConverter(new org.apache.commons.math.analysis.MultivariateVectorialFunction() {
			public double[] value(double[] variables) {
				return factors.operate(variables);
			}
		} , new double[]{ 2.0 , -3.0 } , new double[]{ 10.0 , 0.1 });
		org.apache.commons.math.optimization.direct.NelderMead optimizer = new org.apache.commons.math.optimization.direct.NelderMead();
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(-1.0 , 1.0E-6));
		optimizer.setMaxIterations(200);
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(ls, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 10.0 , 10.0 });
		org.junit.Assert.assertEquals(2.0, optimum.getPointRef()[0], 5.0E-5);
		org.junit.Assert.assertEquals(-3.0, optimum.getPointRef()[1], 8.0E-4);
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) > 60));
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) < 80));
		org.junit.Assert.assertTrue(((optimum.getValue()) < 1.0E-6));
	}

	@org.junit.Test
	public void testLeastSquares3() throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		final org.apache.commons.math.linear.RealMatrix factors = new org.apache.commons.math.linear.Array2DRowRealMatrix(new double[][]{ new double[]{ 1.0 , 0.0 } , new double[]{ 0.0 , 1.0 } } , false);
		org.apache.commons.math.optimization.LeastSquaresConverter ls = new org.apache.commons.math.optimization.LeastSquaresConverter(new org.apache.commons.math.analysis.MultivariateVectorialFunction() {
			public double[] value(double[] variables) {
				return factors.operate(variables);
			}
		} , new double[]{ 2.0 , -3.0 } , new org.apache.commons.math.linear.Array2DRowRealMatrix(new double[][]{ new double[]{ 1.0 , 1.2 } , new double[]{ 1.2 , 2.0 } }));
		org.apache.commons.math.optimization.direct.NelderMead optimizer = new org.apache.commons.math.optimization.direct.NelderMead();
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(-1.0 , 1.0E-6));
		optimizer.setMaxIterations(200);
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(ls, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 10.0 , 10.0 });
		org.junit.Assert.assertEquals(2.0, optimum.getPointRef()[0], 0.002);
		org.junit.Assert.assertEquals(-3.0, optimum.getPointRef()[1], 8.0E-4);
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) > 60));
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) < 80));
		org.junit.Assert.assertTrue(((optimum.getValue()) < 1.0E-6));
	}

	@org.junit.Test(expected = org.apache.commons.math.MaxIterationsExceededException.class)
	public void testMaxIterations() throws org.apache.commons.math.MathException {
		try {
			org.apache.commons.math.optimization.direct.NelderMeadTest.Powell powell = new org.apache.commons.math.optimization.direct.NelderMeadTest.Powell();
			org.apache.commons.math.optimization.direct.NelderMead optimizer = new org.apache.commons.math.optimization.direct.NelderMead();
			optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(-1.0 , 0.001));
			optimizer.setMaxIterations(20);
			optimizer.optimize(powell, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 3.0 , -1.0 , 0.0 , 1.0 });
		} catch (org.apache.commons.math.optimization.OptimizationException oe) {
			if ((oe.getCause()) instanceof org.apache.commons.math.ConvergenceException) {
				throw ((org.apache.commons.math.ConvergenceException)(oe.getCause()));
			} 
			throw oe;
		}
	}

	@org.junit.Test(expected = org.apache.commons.math.MaxEvaluationsExceededException.class)
	public void testMaxEvaluations() throws org.apache.commons.math.MathException {
		try {
			org.apache.commons.math.optimization.direct.NelderMeadTest.Powell powell = new org.apache.commons.math.optimization.direct.NelderMeadTest.Powell();
			org.apache.commons.math.optimization.direct.NelderMead optimizer = new org.apache.commons.math.optimization.direct.NelderMead();
			optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleRealPointChecker(-1.0 , 0.001));
			optimizer.setMaxEvaluations(20);
			optimizer.optimize(powell, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 3.0 , -1.0 , 0.0 , 1.0 });
		} catch (org.apache.commons.math.FunctionEvaluationException fee) {
			if ((fee.getCause()) instanceof org.apache.commons.math.ConvergenceException) {
				throw ((org.apache.commons.math.ConvergenceException)(fee.getCause()));
			} 
			throw fee;
		}
	}

	private static class Rosenbrock implements org.apache.commons.math.analysis.MultivariateRealFunction {
		private int count;

		public Rosenbrock() {
			count = 0;
		}

		public double value(double[] x) throws org.apache.commons.math.FunctionEvaluationException {
			++(count);
			double a = (x[1]) - ((x[0]) * (x[0]));
			double b = 1.0 - (x[0]);
			return ((100 * a) * a) + (b * b);
		}

		public int getCount() {
			return count;
		}
	}

	private static class Powell implements org.apache.commons.math.analysis.MultivariateRealFunction {
		private int count;

		public Powell() {
			count = 0;
		}

		public double value(double[] x) throws org.apache.commons.math.FunctionEvaluationException {
			++(count);
			double a = (x[0]) + (10 * (x[1]));
			double b = (x[2]) - (x[3]);
			double c = (x[1]) - (2 * (x[2]));
			double d = (x[0]) - (x[3]);
			return (((a * a) + ((5 * b) * b)) + (((c * c) * c) * c)) + ((((10 * d) * d) * d) * d);
		}

		public int getCount() {
			return count;
		}
	}
}

