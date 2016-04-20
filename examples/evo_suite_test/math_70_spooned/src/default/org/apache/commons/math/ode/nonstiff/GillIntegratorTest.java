package org.apache.commons.math.ode.nonstiff;


public class GillIntegratorTest extends junit.framework.TestCase {
	public GillIntegratorTest(java.lang.String name) {
		super(name);
	}

	public void testDimensionCheck() {
		try {
			org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
			new org.apache.commons.math.ode.nonstiff.GillIntegrator(0.01).integrate(pb, 0.0, new double[(pb.getDimension()) + 10], 1.0, new double[(pb.getDimension()) + 10]);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.ode.DerivativeException de) {
			junit.framework.Assert.fail("wrong exception caught");
		} catch (org.apache.commons.math.ode.IntegratorException ie) {
		}
	}

	public void testDecreasingSteps() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblemAbstract[] problems = org.apache.commons.math.ode.TestProblemFactory.getProblems();
		for (int k = 0 ; k < (problems.length) ; ++k) {
			double previousError = java.lang.Double.NaN;
			for (int i = 5 ; i < 10 ; ++i) {
				org.apache.commons.math.ode.TestProblemAbstract pb = problems[k].copy();
				double step = ((pb.getFinalTime()) - (pb.getInitialTime())) * (java.lang.Math.pow(2.0, -i));
				org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.GillIntegrator(step);
				org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
				integ.addStepHandler(handler);
				org.apache.commons.math.ode.events.EventHandler[] functions = pb.getEventsHandlers();
				for (int l = 0 ; l < (functions.length) ; ++l) {
					integ.addEventHandler(functions[l], java.lang.Double.POSITIVE_INFINITY, (1.0E-6 * step), 1000);
				}
				double stopTime = integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
				if ((functions.length) == 0) {
					junit.framework.Assert.assertEquals(pb.getFinalTime(), stopTime, 1.0E-10);
				} 
				double error = handler.getMaximalValueError();
				if (i > 5) {
					junit.framework.Assert.assertTrue((error < (java.lang.Math.abs(previousError))));
				} 
				previousError = error;
				junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
			}
		}
	}

	public void testSmallStep() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
		double step = ((pb.getFinalTime()) - (pb.getInitialTime())) * 0.001;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.GillIntegrator(step);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertTrue(((handler.getLastError()) < 2.0E-13));
		junit.framework.Assert.assertTrue(((handler.getMaximalValueError()) < 4.0E-12));
		junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
		junit.framework.Assert.assertEquals("Gill", integ.getName());
	}

	public void testBigStep() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
		double step = ((pb.getFinalTime()) - (pb.getInitialTime())) * 0.2;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.GillIntegrator(step);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertTrue(((handler.getLastError()) > 4.0E-4));
		junit.framework.Assert.assertTrue(((handler.getMaximalValueError()) > 0.005));
		junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
	}

	public void testBackward() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem5 pb = new org.apache.commons.math.ode.TestProblem5();
		double step = (java.lang.Math.abs(((pb.getFinalTime()) - (pb.getInitialTime())))) * 0.001;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.GillIntegrator(step);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertTrue(((handler.getLastError()) < 5.0E-10));
		junit.framework.Assert.assertTrue(((handler.getMaximalValueError()) < 7.0E-10));
		junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
		junit.framework.Assert.assertEquals("Gill", integ.getName());
	}

	public void testKepler() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		final org.apache.commons.math.ode.TestProblem3 pb = new org.apache.commons.math.ode.TestProblem3(0.9);
		double step = ((pb.getFinalTime()) - (pb.getInitialTime())) * 3.0E-4;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.GillIntegrator(step);
		integ.addStepHandler(new org.apache.commons.math.ode.nonstiff.GillIntegratorTest.KeplerStepHandler(pb));
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
	}

	public void testUnstableDerivative() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		final org.apache.commons.math.ode.nonstiff.StepProblem stepProblem = new org.apache.commons.math.ode.nonstiff.StepProblem(0.0 , 1.0 , 2.0);
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.GillIntegrator(0.3);
		integ.addEventHandler(stepProblem, 1.0, 1.0E-12, 1000);
		double[] y = new double[]{ java.lang.Double.NaN };
		integ.integrate(stepProblem, 0.0, new double[]{ 0.0 }, 10.0, y);
		junit.framework.Assert.assertEquals(8.0, y[0], 1.0E-12);
	}

	private static class KeplerStepHandler implements org.apache.commons.math.ode.sampling.StepHandler {
		public KeplerStepHandler(org.apache.commons.math.ode.TestProblem3 pb) {
			this.pb = pb;
			reset();
		}

		public boolean requiresDenseOutput() {
			return false;
		}

		public void reset() {
			maxError = 0;
		}

		public void handleStep(org.apache.commons.math.ode.sampling.StepInterpolator interpolator, boolean isLast) throws org.apache.commons.math.ode.DerivativeException {
			double[] interpolatedY = interpolator.getInterpolatedState();
			double[] theoreticalY = pb.computeTheoreticalState(interpolator.getCurrentTime());
			double dx = (interpolatedY[0]) - (theoreticalY[0]);
			double dy = (interpolatedY[1]) - (theoreticalY[1]);
			double error = (dx * dx) + (dy * dy);
			if (error > (maxError)) {
				maxError = error;
			} 
			if (isLast) {
				junit.framework.Assert.assertTrue(((maxError) > 0.001));
			} 
		}

		private double maxError;

		private org.apache.commons.math.ode.TestProblem3 pb;
	}

	public void testStepSize() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		final double step = 1.23456;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.GillIntegrator(step);
		integ.addStepHandler(new org.apache.commons.math.ode.sampling.StepHandler() {
			public void handleStep(org.apache.commons.math.ode.sampling.StepInterpolator interpolator, boolean isLast) {
				if (!isLast) {
					junit.framework.Assert.assertEquals(step, ((interpolator.getCurrentTime()) - (interpolator.getPreviousTime())), 1.0E-12);
				} 
			}

			public boolean requiresDenseOutput() {
				return false;
			}

			public void reset() {
			}
		});
		integ.integrate(new org.apache.commons.math.ode.FirstOrderDifferentialEquations() {
			private static final long serialVersionUID = 0L;

			public void computeDerivatives(double t, double[] y, double[] dot) {
				dot[0] = 1.0;
			}

			public int getDimension() {
				return 1;
			}
		}, 0.0, new double[]{ 0.0 }, 5.0, new double[1]);
	}
}

