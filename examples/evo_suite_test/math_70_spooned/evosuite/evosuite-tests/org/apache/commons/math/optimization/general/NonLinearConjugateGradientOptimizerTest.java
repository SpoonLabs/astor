package org.apache.commons.math.optimization.general;


public class NonLinearConjugateGradientOptimizerTest extends junit.framework.TestCase {
	public NonLinearConjugateGradientOptimizerTest(java.lang.String name) {
		super(name);
	}

	public void testTrivial() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem(new double[][]{ new double[]{ 2 } } , new double[]{ 3 });
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer optimizer = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer(org.apache.commons.math.optimization.general.ConjugateGradientFormula.POLAK_RIBIERE);
		optimizer.setMaxIterations(100);
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(1.0E-6 , 1.0E-6));
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(problem, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 0 });
		junit.framework.Assert.assertEquals(1.5, optimum.getPoint()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(0.0, optimum.getValue(), 1.0E-10);
	}

	public void testColumnsPermutation() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem(new double[][]{ new double[]{ 1.0 , -1.0 } , new double[]{ 0.0 , 2.0 } , new double[]{ 1.0 , -2.0 } } , new double[]{ 4.0 , 6.0 , 1.0 });
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer optimizer = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer(org.apache.commons.math.optimization.general.ConjugateGradientFormula.POLAK_RIBIERE);
		optimizer.setMaxIterations(100);
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(1.0E-6 , 1.0E-6));
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(problem, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 0 , 0 });
		junit.framework.Assert.assertEquals(7.0, optimum.getPoint()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(3.0, optimum.getPoint()[1], 1.0E-10);
		junit.framework.Assert.assertEquals(0.0, optimum.getValue(), 1.0E-10);
	}

	public void testNoDependency() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem(new double[][]{ new double[]{ 2 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 2 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 2 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 2 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 2 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 2 } } , new double[]{ 0.0 , 1.1 , 2.2 , 3.3 , 4.4 , 5.5 });
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer optimizer = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer(org.apache.commons.math.optimization.general.ConjugateGradientFormula.POLAK_RIBIERE);
		optimizer.setMaxIterations(100);
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(1.0E-6 , 1.0E-6));
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(problem, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 0 , 0 , 0 , 0 , 0 , 0 });
		for (int i = 0 ; i < (problem.target.length) ; ++i) {
			junit.framework.Assert.assertEquals((0.55 * i), optimum.getPoint()[i], 1.0E-10);
		}
	}

	public void testOneSet() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem(new double[][]{ new double[]{ 1 , 0 , 0 } , new double[]{ -1 , 1 , 0 } , new double[]{ 0 , -1 , 1 } } , new double[]{ 1 , 1 , 1 });
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer optimizer = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer(org.apache.commons.math.optimization.general.ConjugateGradientFormula.POLAK_RIBIERE);
		optimizer.setMaxIterations(100);
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(1.0E-6 , 1.0E-6));
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(problem, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 0 , 0 , 0 });
		junit.framework.Assert.assertEquals(1.0, optimum.getPoint()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(2.0, optimum.getPoint()[1], 1.0E-10);
		junit.framework.Assert.assertEquals(3.0, optimum.getPoint()[2], 1.0E-10);
	}

	public void testTwoSets() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		final double epsilon = 1.0E-7;
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem(new double[][]{ new double[]{ 2 , 1 , 0 , 4 , 0 , 0 } , new double[]{ -4 , -2 , 3 , -7 , 0 , 0 } , new double[]{ 4 , 1 , -2 , 8 , 0 , 0 } , new double[]{ 0 , -3 , -12 , -1 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , epsilon , 1 } , new double[]{ 0 , 0 , 0 , 0 , 1 , 1 } } , new double[]{ 2 , -9 , 2 , 2 , 1 + (epsilon * epsilon) , 2 });
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer optimizer = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer(org.apache.commons.math.optimization.general.ConjugateGradientFormula.POLAK_RIBIERE);
		optimizer.setMaxIterations(100);
		optimizer.setPreconditioner(new org.apache.commons.math.optimization.general.Preconditioner() {
			public double[] precondition(double[] point, double[] r) {
				double[] d = r.clone();
				d[0] /= 72.0;
				d[1] /= 30.0;
				d[2] /= 314.0;
				d[3] /= 260.0;
				d[4] /= 2 * (1 + (epsilon * epsilon));
				d[5] /= 4.0;
				return d;
			}
		});
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(1.0E-13 , 1.0E-13));
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(problem, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 0 , 0 , 0 , 0 , 0 , 0 });
		junit.framework.Assert.assertEquals(3.0, optimum.getPoint()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(4.0, optimum.getPoint()[1], 1.0E-10);
		junit.framework.Assert.assertEquals(-1.0, optimum.getPoint()[2], 1.0E-10);
		junit.framework.Assert.assertEquals(-2.0, optimum.getPoint()[3], 1.0E-10);
		junit.framework.Assert.assertEquals((1.0 + epsilon), optimum.getPoint()[4], 1.0E-10);
		junit.framework.Assert.assertEquals((1.0 - epsilon), optimum.getPoint()[5], 1.0E-10);
	}

	public void testNonInversible() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem(new double[][]{ new double[]{ 1 , 2 , -3 } , new double[]{ 2 , 1 , 3 } , new double[]{ -3 , 0 , -9 } } , new double[]{ 1 , 1 , 1 });
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer optimizer = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer(org.apache.commons.math.optimization.general.ConjugateGradientFormula.POLAK_RIBIERE);
		optimizer.setMaxIterations(100);
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(1.0E-6 , 1.0E-6));
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(problem, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 0 , 0 , 0 });
		junit.framework.Assert.assertTrue(((optimum.getValue()) > 0.5));
	}

	public void testIllConditioned() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem problem1 = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem(new double[][]{ new double[]{ 10.0 , 7.0 , 8.0 , 7.0 } , new double[]{ 7.0 , 5.0 , 6.0 , 5.0 } , new double[]{ 8.0 , 6.0 , 10.0 , 9.0 } , new double[]{ 7.0 , 5.0 , 9.0 , 10.0 } } , new double[]{ 32 , 23 , 33 , 31 });
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer optimizer = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer(org.apache.commons.math.optimization.general.ConjugateGradientFormula.POLAK_RIBIERE);
		optimizer.setMaxIterations(100);
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(1.0E-13 , 1.0E-13));
		org.apache.commons.math.analysis.solvers.BrentSolver solver = new org.apache.commons.math.analysis.solvers.BrentSolver();
		solver.setAbsoluteAccuracy(1.0E-15);
		solver.setRelativeAccuracy(1.0E-15);
		optimizer.setLineSearchSolver(solver);
		org.apache.commons.math.optimization.RealPointValuePair optimum1 = optimizer.optimize(problem1, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 0 , 1 , 2 , 3 });
		junit.framework.Assert.assertEquals(1.0, optimum1.getPoint()[0], 1.0E-5);
		junit.framework.Assert.assertEquals(1.0, optimum1.getPoint()[1], 1.0E-5);
		junit.framework.Assert.assertEquals(1.0, optimum1.getPoint()[2], 1.0E-5);
		junit.framework.Assert.assertEquals(1.0, optimum1.getPoint()[3], 1.0E-5);
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem problem2 = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem(new double[][]{ new double[]{ 10.0 , 7.0 , 8.1 , 7.2 } , new double[]{ 7.08 , 5.04 , 6.0 , 5.0 } , new double[]{ 8.0 , 5.98 , 9.89 , 9.0 } , new double[]{ 6.99 , 4.99 , 9.0 , 9.98 } } , new double[]{ 32 , 23 , 33 , 31 });
		org.apache.commons.math.optimization.RealPointValuePair optimum2 = optimizer.optimize(problem2, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 0 , 1 , 2 , 3 });
		junit.framework.Assert.assertEquals(-81.0, optimum2.getPoint()[0], 0.1);
		junit.framework.Assert.assertEquals(137.0, optimum2.getPoint()[1], 0.1);
		junit.framework.Assert.assertEquals(-34.0, optimum2.getPoint()[2], 0.1);
		junit.framework.Assert.assertEquals(22.0, optimum2.getPoint()[3], 0.1);
	}

	public void testMoreEstimatedParametersSimple() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem(new double[][]{ new double[]{ 3.0 , 2.0 , 0.0 , 0.0 } , new double[]{ 0.0 , 1.0 , -1.0 , 1.0 } , new double[]{ 2.0 , 0.0 , 1.0 , 0.0 } } , new double[]{ 7.0 , 3.0 , 5.0 });
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer optimizer = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer(org.apache.commons.math.optimization.general.ConjugateGradientFormula.POLAK_RIBIERE);
		optimizer.setMaxIterations(100);
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(1.0E-6 , 1.0E-6));
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(problem, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 7 , 6 , 5 , 4 });
		junit.framework.Assert.assertEquals(0, optimum.getValue(), 1.0E-10);
	}

	public void testMoreEstimatedParametersUnsorted() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem(new double[][]{ new double[]{ 1.0 , 1.0 , 0.0 , 0.0 , 0.0 , 0.0 } , new double[]{ 0.0 , 0.0 , 1.0 , 1.0 , 1.0 , 0.0 } , new double[]{ 0.0 , 0.0 , 0.0 , 0.0 , 1.0 , -1.0 } , new double[]{ 0.0 , 0.0 , -1.0 , 1.0 , 0.0 , 1.0 } , new double[]{ 0.0 , 0.0 , 0.0 , -1.0 , 1.0 , 0.0 } } , new double[]{ 3.0 , 12.0 , -1.0 , 7.0 , 1.0 });
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer optimizer = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer(org.apache.commons.math.optimization.general.ConjugateGradientFormula.POLAK_RIBIERE);
		optimizer.setMaxIterations(100);
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(1.0E-6 , 1.0E-6));
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(problem, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 2 , 2 , 2 , 2 , 2 , 2 });
		junit.framework.Assert.assertEquals(0, optimum.getValue(), 1.0E-10);
	}

	public void testRedundantEquations() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem(new double[][]{ new double[]{ 1.0 , 1.0 } , new double[]{ 1.0 , -1.0 } , new double[]{ 1.0 , 3.0 } } , new double[]{ 3.0 , 1.0 , 5.0 });
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer optimizer = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer(org.apache.commons.math.optimization.general.ConjugateGradientFormula.POLAK_RIBIERE);
		optimizer.setMaxIterations(100);
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(1.0E-6 , 1.0E-6));
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(problem, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 1 , 1 });
		junit.framework.Assert.assertEquals(2.0, optimum.getPoint()[0], 1.0E-8);
		junit.framework.Assert.assertEquals(1.0, optimum.getPoint()[1], 1.0E-8);
	}

	public void testInconsistentEquations() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem problem = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.LinearProblem(new double[][]{ new double[]{ 1.0 , 1.0 } , new double[]{ 1.0 , -1.0 } , new double[]{ 1.0 , 3.0 } } , new double[]{ 3.0 , 1.0 , 4.0 });
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer optimizer = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer(org.apache.commons.math.optimization.general.ConjugateGradientFormula.POLAK_RIBIERE);
		optimizer.setMaxIterations(100);
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(1.0E-6 , 1.0E-6));
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(problem, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 1 , 1 });
		junit.framework.Assert.assertTrue(((optimum.getValue()) > 0.1));
	}

	public void testCircleFitting() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.Circle circle = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizerTest.Circle();
		circle.addPoint(30.0, 68.0);
		circle.addPoint(50.0, -6.0);
		circle.addPoint(110.0, -20.0);
		circle.addPoint(35.0, 15.0);
		circle.addPoint(45.0, 97.0);
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer optimizer = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer(org.apache.commons.math.optimization.general.ConjugateGradientFormula.POLAK_RIBIERE);
		optimizer.setMaxIterations(100);
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(1.0E-30 , 1.0E-30));
		org.apache.commons.math.analysis.solvers.BrentSolver solver = new org.apache.commons.math.analysis.solvers.BrentSolver();
		solver.setAbsoluteAccuracy(1.0E-13);
		solver.setRelativeAccuracy(1.0E-15);
		optimizer.setLineSearchSolver(solver);
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(circle, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 98.68 , 47.345 });
		java.awt.geom.Point2D.Double center = new java.awt.geom.Point2D.Double(optimum.getPointRef()[0] , optimum.getPointRef()[1]);
		junit.framework.Assert.assertEquals(69.960161753, circle.getRadius(center), 1.0E-8);
		junit.framework.Assert.assertEquals(96.075902096, center.x, 1.0E-8);
		junit.framework.Assert.assertEquals(48.135167894, center.y, 1.0E-8);
	}

	private static class LinearProblem implements java.io.Serializable , org.apache.commons.math.analysis.DifferentiableMultivariateRealFunction {
		private static final long serialVersionUID = 703247177355019415L;

		final org.apache.commons.math.linear.RealMatrix factors;

		final double[] target;

		public LinearProblem(double[][] factors ,double[] target) {
			this.factors = new org.apache.commons.math.linear.BlockRealMatrix(factors);
			this.target = target;
		}

		private double[] gradient(double[] point) {
			double[] r = factors.operate(point);
			for (int i = 0 ; i < (r.length) ; ++i) {
				r[i] -= target[i];
			}
			double[] p = factors.transpose().operate(r);
			for (int i = 0 ; i < (p.length) ; ++i) {
				p[i] *= 2;
			}
			return p;
		}

		public double value(double[] variables) throws org.apache.commons.math.FunctionEvaluationException {
			double[] y = factors.operate(variables);
			double sum = 0;
			for (int i = 0 ; i < (y.length) ; ++i) {
				double ri = (y[i]) - (target[i]);
				sum += ri * ri;
			}
			return sum;
		}

		public org.apache.commons.math.analysis.MultivariateVectorialFunction gradient() {
			return new org.apache.commons.math.analysis.MultivariateVectorialFunction() {
				private static final long serialVersionUID = 2621997811350805819L;

				public double[] value(double[] point) {
					return gradient(point);
				}
			};
		}

		public org.apache.commons.math.analysis.MultivariateRealFunction partialDerivative(final int k) {
			return new org.apache.commons.math.analysis.MultivariateRealFunction() {
				private static final long serialVersionUID = -6186178619133562011L;

				public double value(double[] point) {
					return gradient(point)[k];
				}
			};
		}
	}

	private static class Circle implements java.io.Serializable , org.apache.commons.math.analysis.DifferentiableMultivariateRealFunction {
		private static final long serialVersionUID = -4711170319243817874L;

		private java.util.ArrayList<java.awt.geom.Point2D.Double> points;

		public Circle() {
			points = new java.util.ArrayList<java.awt.geom.Point2D.Double>();
		}

		public void addPoint(double px, double py) {
			points.add(new java.awt.geom.Point2D.Double(px , py));
		}

		public double getRadius(java.awt.geom.Point2D.Double center) {
			double r = 0;
			for (java.awt.geom.Point2D.Double point : points) {
				r += point.distance(center);
			}
			return r / (points.size());
		}

		private double[] gradient(double[] point) {
			java.awt.geom.Point2D.Double center = new java.awt.geom.Point2D.Double(point[0] , point[1]);
			double radius = getRadius(center);
			double dJdX = 0;
			double dJdY = 0;
			for (java.awt.geom.Point2D.Double pk : points) {
				double dk = pk.distance(center);
				dJdX += (((center.x) - (pk.x)) * (dk - radius)) / dk;
				dJdY += (((center.y) - (pk.y)) * (dk - radius)) / dk;
			}
			dJdX *= 2;
			dJdY *= 2;
			return new double[]{ dJdX , dJdY };
		}

		public double value(double[] variables) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException {
			java.awt.geom.Point2D.Double center = new java.awt.geom.Point2D.Double(variables[0] , variables[1]);
			double radius = getRadius(center);
			double sum = 0;
			for (java.awt.geom.Point2D.Double point : points) {
				double di = (point.distance(center)) - radius;
				sum += di * di;
			}
			return sum;
		}

		public org.apache.commons.math.analysis.MultivariateVectorialFunction gradient() {
			return new org.apache.commons.math.analysis.MultivariateVectorialFunction() {
				private static final long serialVersionUID = 3174909643301201710L;

				public double[] value(double[] point) {
					return gradient(point);
				}
			};
		}

		public org.apache.commons.math.analysis.MultivariateRealFunction partialDerivative(final int k) {
			return new org.apache.commons.math.analysis.MultivariateRealFunction() {
				private static final long serialVersionUID = 3073956364104833888L;

				public double value(double[] point) {
					return gradient(point)[k];
				}
			};
		}
	}
}

