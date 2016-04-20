package org.apache.commons.math.optimization.fitting;


public class HarmonicFitterTest {
	@org.junit.Test
	public void testNoError() throws org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.fitting.HarmonicFunction f = new org.apache.commons.math.optimization.fitting.HarmonicFunction(0.2 , 3.4 , 4.1);
		org.apache.commons.math.optimization.fitting.HarmonicFitter fitter = new org.apache.commons.math.optimization.fitting.HarmonicFitter(new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer());
		for (double x = 0.0 ; x < 1.3 ; x += 0.01) {
			fitter.addObservedPoint(1.0, x, f.value(x));
		}
		org.apache.commons.math.optimization.fitting.HarmonicFunction fitted = fitter.fit();
		org.junit.Assert.assertEquals(f.getAmplitude(), fitted.getAmplitude(), 1.0E-13);
		org.junit.Assert.assertEquals(f.getPulsation(), fitted.getPulsation(), 1.0E-13);
		org.junit.Assert.assertEquals(f.getPhase(), org.apache.commons.math.util.MathUtils.normalizeAngle(fitted.getPhase(), f.getPhase()), 1.0E-13);
		for (double x = -1.0 ; x < 1.0 ; x += 0.01) {
			org.junit.Assert.assertTrue(((java.lang.Math.abs(((f.value(x)) - (fitted.value(x))))) < 1.0E-13));
		}
	}

	@org.junit.Test
	public void test1PercentError() throws org.apache.commons.math.optimization.OptimizationException {
		java.util.Random randomizer = new java.util.Random(64925784252L);
		org.apache.commons.math.optimization.fitting.HarmonicFunction f = new org.apache.commons.math.optimization.fitting.HarmonicFunction(0.2 , 3.4 , 4.1);
		org.apache.commons.math.optimization.fitting.HarmonicFitter fitter = new org.apache.commons.math.optimization.fitting.HarmonicFitter(new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer());
		for (double x = 0.0 ; x < 10.0 ; x += 0.1) {
			fitter.addObservedPoint(1.0, x, ((f.value(x)) + (0.01 * (randomizer.nextGaussian()))));
		}
		org.apache.commons.math.optimization.fitting.HarmonicFunction fitted = fitter.fit();
		org.junit.Assert.assertEquals(f.getAmplitude(), fitted.getAmplitude(), 7.6E-4);
		org.junit.Assert.assertEquals(f.getPulsation(), fitted.getPulsation(), 0.0027);
		org.junit.Assert.assertEquals(f.getPhase(), org.apache.commons.math.util.MathUtils.normalizeAngle(fitted.getPhase(), f.getPhase()), 0.013);
	}

	@org.junit.Test
	public void testInitialGuess() throws org.apache.commons.math.optimization.OptimizationException {
		java.util.Random randomizer = new java.util.Random(45314242L);
		org.apache.commons.math.optimization.fitting.HarmonicFunction f = new org.apache.commons.math.optimization.fitting.HarmonicFunction(0.2 , 3.4 , 4.1);
		org.apache.commons.math.optimization.fitting.HarmonicFitter fitter = new org.apache.commons.math.optimization.fitting.HarmonicFitter(new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer() , new double[]{ 0.15 , 3.6 , 4.5 });
		for (double x = 0.0 ; x < 10.0 ; x += 0.1) {
			fitter.addObservedPoint(1.0, x, ((f.value(x)) + (0.01 * (randomizer.nextGaussian()))));
		}
		org.apache.commons.math.optimization.fitting.HarmonicFunction fitted = fitter.fit();
		org.junit.Assert.assertEquals(f.getAmplitude(), fitted.getAmplitude(), 0.0012);
		org.junit.Assert.assertEquals(f.getPulsation(), fitted.getPulsation(), 0.0033);
		org.junit.Assert.assertEquals(f.getPhase(), org.apache.commons.math.util.MathUtils.normalizeAngle(fitted.getPhase(), f.getPhase()), 0.017);
	}

	@org.junit.Test
	public void testUnsorted() throws org.apache.commons.math.optimization.OptimizationException {
		java.util.Random randomizer = new java.util.Random(64925784252L);
		org.apache.commons.math.optimization.fitting.HarmonicFunction f = new org.apache.commons.math.optimization.fitting.HarmonicFunction(0.2 , 3.4 , 4.1);
		org.apache.commons.math.optimization.fitting.HarmonicFitter fitter = new org.apache.commons.math.optimization.fitting.HarmonicFitter(new org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer());
		int size = 100;
		double[] xTab = new double[size];
		double[] yTab = new double[size];
		for (int i = 0 ; i < size ; ++i) {
			xTab[i] = 0.1 * i;
			yTab[i] = (f.value(xTab[i])) + (0.01 * (randomizer.nextGaussian()));
		}
		for (int i = 0 ; i < size ; ++i) {
			int i1 = randomizer.nextInt(size);
			int i2 = randomizer.nextInt(size);
			double xTmp = xTab[i1];
			double yTmp = yTab[i1];
			xTab[i1] = xTab[i2];
			yTab[i1] = yTab[i2];
			xTab[i2] = xTmp;
			yTab[i2] = yTmp;
		}
		for (int i = 0 ; i < size ; ++i) {
			fitter.addObservedPoint(1.0, xTab[i], yTab[i]);
		}
		org.apache.commons.math.optimization.fitting.HarmonicFunction fitted = fitter.fit();
		org.junit.Assert.assertEquals(f.getAmplitude(), fitted.getAmplitude(), 7.6E-4);
		org.junit.Assert.assertEquals(f.getPulsation(), fitted.getPulsation(), 0.0035);
		org.junit.Assert.assertEquals(f.getPhase(), org.apache.commons.math.util.MathUtils.normalizeAngle(fitted.getPhase(), f.getPhase()), 0.015);
	}
}

