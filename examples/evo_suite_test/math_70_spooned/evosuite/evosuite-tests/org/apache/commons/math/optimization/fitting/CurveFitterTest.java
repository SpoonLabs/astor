package org.apache.commons.math.optimization.fitting;


public class CurveFitterTest {
	@org.junit.Test
	public void testMath303() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer optimizer = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer();
		org.apache.commons.math.optimization.fitting.CurveFitter fitter = new org.apache.commons.math.optimization.fitting.CurveFitter(optimizer);
		fitter.addObservedPoint(2.805, 0.6934785852953367);
		fitter.addObservedPoint(2.74333333333333, 0.6306772025518496);
		fitter.addObservedPoint(1.655, 0.9474675497289684);
		fitter.addObservedPoint(1.725, 0.9013594835804194);
		org.apache.commons.math.optimization.fitting.ParametricRealFunction sif = new org.apache.commons.math.optimization.fitting.CurveFitterTest.SimpleInverseFunction();
		double[] initialguess1 = new double[1];
		initialguess1[0] = 1.0;
		org.junit.Assert.assertEquals(1, fitter.fit(sif, initialguess1).length);
		double[] initialguess2 = new double[2];
		initialguess2[0] = 1.0;
		initialguess2[1] = 0.5;
		org.junit.Assert.assertEquals(2, fitter.fit(sif, initialguess2).length);
	}

	@org.junit.Test
	public void testMath304() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer optimizer = new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer();
		org.apache.commons.math.optimization.fitting.CurveFitter fitter = new org.apache.commons.math.optimization.fitting.CurveFitter(optimizer);
		fitter.addObservedPoint(2.805, 0.6934785852953367);
		fitter.addObservedPoint(2.74333333333333, 0.6306772025518496);
		fitter.addObservedPoint(1.655, 0.9474675497289684);
		fitter.addObservedPoint(1.725, 0.9013594835804194);
		org.apache.commons.math.optimization.fitting.ParametricRealFunction sif = new org.apache.commons.math.optimization.fitting.CurveFitterTest.SimpleInverseFunction();
		double[] initialguess1 = new double[1];
		initialguess1[0] = 1.0;
		org.junit.Assert.assertEquals(1.6357215104109237, fitter.fit(sif, initialguess1)[0], 1.0E-14);
		double[] initialguess2 = new double[1];
		initialguess2[0] = 10.0;
		org.junit.Assert.assertEquals(1.6357215104109237, fitter.fit(sif, initialguess1)[0], 1.0E-14);
	}

	private static class SimpleInverseFunction implements org.apache.commons.math.optimization.fitting.ParametricRealFunction {
		public double value(double x, double[] parameters) {
			return ((parameters[0]) / x) + ((parameters.length) < 2 ? 0 : parameters[1]);
		}

		public double[] gradient(double x, double[] doubles) {
			double[] gradientVector = new double[doubles.length];
			gradientVector[0] = 1 / x;
			if ((doubles.length) >= 2) {
				gradientVector[1] = 1;
			} 
			return gradientVector;
		}
	}
}

