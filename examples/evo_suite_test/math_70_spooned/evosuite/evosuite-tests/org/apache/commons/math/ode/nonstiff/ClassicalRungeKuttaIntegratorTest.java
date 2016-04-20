package org.apache.commons.math.ode.nonstiff;


public class ClassicalRungeKuttaIntegratorTest extends junit.framework.TestCase {
	public ClassicalRungeKuttaIntegratorTest(java.lang.String name) {
		super(name);
	}

	public void testMissedEndEvent() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		final double t0 = 1.8782503200000029E9;
		final double tEvent = 1.8782503799999986E9;
		final double[] k = new double[]{ 1.0E-4 , 1.0E-5 , 1.0E-6 };
		org.apache.commons.math.ode.FirstOrderDifferentialEquations ode = new org.apache.commons.math.ode.FirstOrderDifferentialEquations() {
			public int getDimension() {
				return k.length;
			}

			public void computeDerivatives(double t, double[] y, double[] yDot) {
				for (int i = 0 ; i < (y.length) ; ++i) {
					yDot[i] = (k[i]) * (y[i]);
				}
			}
		};
		org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator integrator = new org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator(60.0);
		double[] y0 = new double[k.length];
		for (int i = 0 ; i < (y0.length) ; ++i) {
			y0[i] = i + 1;
		}
		double[] y = new double[k.length];
		double finalT = integrator.integrate(ode, t0, y0, tEvent, y);
		junit.framework.Assert.assertEquals(tEvent, finalT, 5.0E-6);
		for (int i = 0 ; i < (y.length) ; ++i) {
			junit.framework.Assert.assertEquals(((y0[i]) * (java.lang.Math.exp(((k[i]) * (finalT - t0))))), y[i], 1.0E-9);
		}
		integrator.addEventHandler(new org.apache.commons.math.ode.events.EventHandler() {
			public void resetState(double t, double[] y) {
			}

			public double g(double t, double[] y) {
				return t - tEvent;
			}

			public int eventOccurred(double t, double[] y, boolean increasing) {
				junit.framework.Assert.assertEquals(tEvent, t, 5.0E-6);
				return org.apache.commons.math.ode.events.EventHandler.CONTINUE;
			}
		}, java.lang.Double.POSITIVE_INFINITY, 1.0E-20, 100);
		finalT = integrator.integrate(ode, t0, y0, (tEvent + 120), y);
		junit.framework.Assert.assertEquals((tEvent + 120), finalT, 5.0E-6);
		for (int i = 0 ; i < (y.length) ; ++i) {
			junit.framework.Assert.assertEquals(((y0[i]) * (java.lang.Math.exp(((k[i]) * (finalT - t0))))), y[i], 1.0E-9);
		}
	}

	public void testSanityChecks() {
		try {
			org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
			new org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator(0.01).integrate(pb, 0.0, new double[(pb.getDimension()) + 10], 1.0, new double[pb.getDimension()]);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.ode.DerivativeException de) {
			junit.framework.Assert.fail("wrong exception caught");
		} catch (org.apache.commons.math.ode.IntegratorException ie) {
		}
		try {
			org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
			new org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator(0.01).integrate(pb, 0.0, new double[pb.getDimension()], 1.0, new double[(pb.getDimension()) + 10]);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.ode.DerivativeException de) {
			junit.framework.Assert.fail("wrong exception caught");
		} catch (org.apache.commons.math.ode.IntegratorException ie) {
		}
		try {
			org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
			new org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator(0.01).integrate(pb, 0.0, new double[pb.getDimension()], 0.0, new double[pb.getDimension()]);
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
			for (int i = 4 ; i < 10 ; ++i) {
				org.apache.commons.math.ode.TestProblemAbstract pb = problems[k].copy();
				double step = ((pb.getFinalTime()) - (pb.getInitialTime())) * (java.lang.Math.pow(2.0, -i));
				org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator(step);
				org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
				integ.addStepHandler(handler);
				org.apache.commons.math.ode.events.EventHandler[] functions = pb.getEventsHandlers();
				for (int l = 0 ; l < (functions.length) ; ++l) {
					integ.addEventHandler(functions[l], java.lang.Double.POSITIVE_INFINITY, (1.0E-6 * step), 1000);
				}
				junit.framework.Assert.assertEquals(functions.length, integ.getEventHandlers().size());
				double stopTime = integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
				if ((functions.length) == 0) {
					junit.framework.Assert.assertEquals(pb.getFinalTime(), stopTime, 1.0E-10);
				} 
				double error = handler.getMaximalValueError();
				if (i > 4) {
					junit.framework.Assert.assertTrue((error < (java.lang.Math.abs(previousError))));
				} 
				previousError = error;
				junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
				integ.clearEventHandlers();
				junit.framework.Assert.assertEquals(0, integ.getEventHandlers().size());
			}
		}
	}

	public void testSmallStep() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
		double step = ((pb.getFinalTime()) - (pb.getInitialTime())) * 0.001;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator(step);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertTrue(((handler.getLastError()) < 2.0E-13));
		junit.framework.Assert.assertTrue(((handler.getMaximalValueError()) < 4.0E-12));
		junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
		junit.framework.Assert.assertEquals("classical Runge-Kutta", integ.getName());
	}

	public void testBigStep() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
		double step = ((pb.getFinalTime()) - (pb.getInitialTime())) * 0.2;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator(step);
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
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator(step);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertTrue(((handler.getLastError()) < 5.0E-10));
		junit.framework.Assert.assertTrue(((handler.getMaximalValueError()) < 7.0E-10));
		junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
		junit.framework.Assert.assertEquals("classical Runge-Kutta", integ.getName());
	}

	public void testKepler() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		final org.apache.commons.math.ode.TestProblem3 pb = new org.apache.commons.math.ode.TestProblem3(0.9);
		double step = ((pb.getFinalTime()) - (pb.getInitialTime())) * 3.0E-4;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator(step);
		integ.addStepHandler(new org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegratorTest.KeplerHandler(pb));
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
	}

	private static class KeplerHandler implements org.apache.commons.math.ode.sampling.StepHandler {
		public KeplerHandler(org.apache.commons.math.ode.TestProblem3 pb) {
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
				junit.framework.Assert.assertTrue(((maxError) > 0.005));
			} 
		}

		private double maxError = 0;

		private org.apache.commons.math.ode.TestProblem3 pb;
	}

	public void testStepSize() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		final double step = 1.23456;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator(step);
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

