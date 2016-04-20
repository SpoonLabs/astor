package org.apache.commons.math.optimization;


public class MultiStartDifferentiableMultivariateVectorialOptimizerTest {
	@org.junit.Test
	public void testTrivial() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.MultiStartDifferentiableMultivariateVectorialOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.MultiStartDifferentiableMultivariateVectorialOptimizerTest.LinearProblem(new double[][]{ new double[]{ 2 } } , new double[]{ 3 });
		org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer underlyingOptimizer = new org.apache.commons.math.optimization.general.GaussNewtonOptimizer(true);
		org.apache.commons.math.random.JDKRandomGenerator g = new org.apache.commons.math.random.JDKRandomGenerator();
		g.setSeed(16069223052L);
		org.apache.commons.math.random.RandomVectorGenerator generator = new org.apache.commons.math.random.UncorrelatedRandomVectorGenerator(1 , new org.apache.commons.math.random.GaussianRandomGenerator(g));
		org.apache.commons.math.optimization.MultiStartDifferentiableMultivariateVectorialOptimizer optimizer = new org.apache.commons.math.optimization.MultiStartDifferentiableMultivariateVectorialOptimizer(underlyingOptimizer , 10 , generator);
		optimizer.setMaxIterations(100);
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleVectorialValueChecker(1.0E-6 , 1.0E-6));
		try {
			optimizer.getOptima();
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalStateException ise) {
		}
		org.apache.commons.math.optimization.VectorialPointValuePair optimum = optimizer.optimize(problem, problem.target, new double[]{ 1 }, new double[]{ 0 });
		org.junit.Assert.assertEquals(1.5, optimum.getPoint()[0], 1.0E-10);
		org.junit.Assert.assertEquals(3.0, optimum.getValue()[0], 1.0E-10);
		org.apache.commons.math.optimization.VectorialPointValuePair[] optima = optimizer.getOptima();
		org.junit.Assert.assertEquals(10, optima.length);
		for (int i = 0 ; i < (optima.length) ; ++i) {
			org.junit.Assert.assertEquals(1.5, optima[i].getPoint()[0], 1.0E-10);
			org.junit.Assert.assertEquals(3.0, optima[i].getValue()[0], 1.0E-10);
		}
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) > 20));
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) < 50));
		org.junit.Assert.assertTrue(((optimizer.getIterations()) > 20));
		org.junit.Assert.assertTrue(((optimizer.getIterations()) < 50));
		org.junit.Assert.assertTrue(((optimizer.getJacobianEvaluations()) > 20));
		org.junit.Assert.assertTrue(((optimizer.getJacobianEvaluations()) < 50));
		org.junit.Assert.assertEquals(100, optimizer.getMaxIterations());
	}

	@org.junit.Test(expected = org.apache.commons.math.optimization.OptimizationException.class)
	public void testNoOptimum() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer underlyingOptimizer = new org.apache.commons.math.optimization.general.GaussNewtonOptimizer(true);
		org.apache.commons.math.random.JDKRandomGenerator g = new org.apache.commons.math.random.JDKRandomGenerator();
		g.setSeed(12373523445L);
		org.apache.commons.math.random.RandomVectorGenerator generator = new org.apache.commons.math.random.UncorrelatedRandomVectorGenerator(1 , new org.apache.commons.math.random.GaussianRandomGenerator(g));
		org.apache.commons.math.optimization.MultiStartDifferentiableMultivariateVectorialOptimizer optimizer = new org.apache.commons.math.optimization.MultiStartDifferentiableMultivariateVectorialOptimizer(underlyingOptimizer , 10 , generator);
		optimizer.setMaxIterations(100);
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleVectorialValueChecker(1.0E-6 , 1.0E-6));
		optimizer.optimize(new org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction() {
			public org.apache.commons.math.analysis.MultivariateMatrixFunction jacobian() {
				return null;
			}

			public double[] value(double[] point) throws org.apache.commons.math.FunctionEvaluationException {
				throw new org.apache.commons.math.FunctionEvaluationException(point[0]);
			}
		}, new double[]{ 2 }, new double[]{ 1 }, new double[]{ 0 });
	}

	private static class LinearProblem implements java.io.Serializable , org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction {
		private static final long serialVersionUID = -8804268799379350190L;

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
				private static final long serialVersionUID = -8387467946663627585L;

				public double[][] value(double[] point) {
					return factors.getData();
				}
			};
		}
	}
}

