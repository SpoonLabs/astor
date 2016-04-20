package org.apache.commons.math.optimization;


public class MultiStartDifferentiableMultivariateRealOptimizerTest {
	@org.junit.Test
	public void testCircleFitting() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.MultiStartDifferentiableMultivariateRealOptimizerTest.Circle circle = new org.apache.commons.math.optimization.MultiStartDifferentiableMultivariateRealOptimizerTest.Circle();
		circle.addPoint(30.0, 68.0);
		circle.addPoint(50.0, -6.0);
		circle.addPoint(110.0, -20.0);
		circle.addPoint(35.0, 15.0);
		circle.addPoint(45.0, 97.0);
		org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer underlying = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer(org.apache.commons.math.optimization.general.ConjugateGradientFormula.POLAK_RIBIERE);
		org.apache.commons.math.random.JDKRandomGenerator g = new org.apache.commons.math.random.JDKRandomGenerator();
		g.setSeed(753289573253L);
		org.apache.commons.math.random.RandomVectorGenerator generator = new org.apache.commons.math.random.UncorrelatedRandomVectorGenerator(new double[]{ 50.0 , 50.0 } , new double[]{ 10.0 , 10.0 } , new org.apache.commons.math.random.GaussianRandomGenerator(g));
		org.apache.commons.math.optimization.MultiStartDifferentiableMultivariateRealOptimizer optimizer = new org.apache.commons.math.optimization.MultiStartDifferentiableMultivariateRealOptimizer(underlying , 10 , generator);
		optimizer.setMaxIterations(100);
		org.junit.Assert.assertEquals(100, optimizer.getMaxIterations());
		optimizer.setMaxEvaluations(100);
		org.junit.Assert.assertEquals(100, optimizer.getMaxEvaluations());
		optimizer.setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker(1.0E-10 , 1.0E-10));
		org.apache.commons.math.analysis.solvers.BrentSolver solver = new org.apache.commons.math.analysis.solvers.BrentSolver();
		solver.setAbsoluteAccuracy(1.0E-13);
		solver.setRelativeAccuracy(1.0E-15);
		org.apache.commons.math.optimization.RealPointValuePair optimum = optimizer.optimize(circle, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[]{ 98.68 , 47.345 });
		org.apache.commons.math.optimization.RealPointValuePair[] optima = optimizer.getOptima();
		for (org.apache.commons.math.optimization.RealPointValuePair o : optima) {
			java.awt.geom.Point2D.Double center = new java.awt.geom.Point2D.Double(o.getPointRef()[0] , o.getPointRef()[1]);
			org.junit.Assert.assertEquals(69.960161753, circle.getRadius(center), 1.0E-8);
			org.junit.Assert.assertEquals(96.075902096, center.x, 1.0E-8);
			org.junit.Assert.assertEquals(48.135167894, center.y, 1.0E-8);
		}
		org.junit.Assert.assertTrue(((optimizer.getGradientEvaluations()) > 650));
		org.junit.Assert.assertTrue(((optimizer.getGradientEvaluations()) < 700));
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) > 70));
		org.junit.Assert.assertTrue(((optimizer.getEvaluations()) < 90));
		org.junit.Assert.assertTrue(((optimizer.getIterations()) > 70));
		org.junit.Assert.assertTrue(((optimizer.getIterations()) < 90));
		org.junit.Assert.assertEquals(3.1267527, optimum.getValue(), 1.0E-8);
	}

	private static class Circle implements org.apache.commons.math.analysis.DifferentiableMultivariateRealFunction {
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
				public double[] value(double[] point) {
					return gradient(point);
				}
			};
		}

		public org.apache.commons.math.analysis.MultivariateRealFunction partialDerivative(final int k) {
			return new org.apache.commons.math.analysis.MultivariateRealFunction() {
				public double value(double[] point) {
					return gradient(point)[k];
				}
			};
		}
	}
}

