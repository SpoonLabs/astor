package org.apache.commons.math.ode.nonstiff;


public class HighamHall54IntegratorTest extends junit.framework.TestCase {
	public HighamHall54IntegratorTest(java.lang.String name) {
		super(name);
	}

	public void testWrongDerivative() {
		try {
			org.apache.commons.math.ode.nonstiff.HighamHall54Integrator integrator = new org.apache.commons.math.ode.nonstiff.HighamHall54Integrator(0.0 , 1.0 , 1.0E-10 , 1.0E-10);
			org.apache.commons.math.ode.FirstOrderDifferentialEquations equations = new org.apache.commons.math.ode.FirstOrderDifferentialEquations() {
				private static final long serialVersionUID = -1157081786301178032L;

				public void computeDerivatives(double t, double[] y, double[] dot) throws org.apache.commons.math.ode.DerivativeException {
					if (t < (-0.5)) {
						throw new org.apache.commons.math.ode.DerivativeException("{0}" , "oops");
					} else {
						throw new org.apache.commons.math.ode.DerivativeException(new java.lang.RuntimeException("oops"));
					}
				}

				public int getDimension() {
					return 1;
				}
			};
			try {
				integrator.integrate(equations, -1.0, new double[1], 0.0, new double[1]);
				junit.framework.Assert.fail("an exception should have been thrown");
			} catch (org.apache.commons.math.ode.DerivativeException de) {
			}
			try {
				integrator.integrate(equations, 0.0, new double[1], 1.0, new double[1]);
				junit.framework.Assert.fail("an exception should have been thrown");
			} catch (org.apache.commons.math.ode.DerivativeException de) {
			}
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail(("wrong exception caught: " + (e.getMessage())));
		}
	}

	public void testMinStep() {
		try {
			org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
			double minStep = 0.1 * ((pb.getFinalTime()) - (pb.getInitialTime()));
			double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
			double[] vecAbsoluteTolerance = new double[]{ 1.0E-15 , 1.0E-16 };
			double[] vecRelativeTolerance = new double[]{ 1.0E-15 , 1.0E-16 };
			org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.HighamHall54Integrator(minStep , maxStep , vecAbsoluteTolerance , vecRelativeTolerance);
			org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
			integ.addStepHandler(handler);
			integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.ode.DerivativeException de) {
			junit.framework.Assert.fail("wrong exception caught");
		} catch (org.apache.commons.math.ode.IntegratorException ie) {
		}
	}

	public void testIncreasingTolerance() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		int previousCalls = java.lang.Integer.MAX_VALUE;
		for (int i = -12 ; i < (-2) ; ++i) {
			org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
			double minStep = 0;
			double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
			double scalAbsoluteTolerance = java.lang.Math.pow(10.0, i);
			double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;
			org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.HighamHall54Integrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
			org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
			integ.addStepHandler(handler);
			integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
			junit.framework.Assert.assertTrue(((handler.getMaximalValueError()) < (1.3 * scalAbsoluteTolerance)));
			junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
			int calls = pb.getCalls();
			junit.framework.Assert.assertEquals(integ.getEvaluations(), calls);
			junit.framework.Assert.assertTrue((calls <= previousCalls));
			previousCalls = calls;
		}
	}

	public void testBackward() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem5 pb = new org.apache.commons.math.ode.TestProblem5();
		double minStep = 0;
		double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
		double scalAbsoluteTolerance = 1.0E-8;
		double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.HighamHall54Integrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertTrue(((handler.getLastError()) < 5.0E-7));
		junit.framework.Assert.assertTrue(((handler.getMaximalValueError()) < 5.0E-7));
		junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
		junit.framework.Assert.assertEquals("Higham-Hall 5(4)", integ.getName());
	}

	public void testEvents() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem4 pb = new org.apache.commons.math.ode.TestProblem4();
		double minStep = 0;
		double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
		double scalAbsoluteTolerance = 1.0E-8;
		double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.HighamHall54Integrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		org.apache.commons.math.ode.events.EventHandler[] functions = pb.getEventsHandlers();
		for (int l = 0 ; l < (functions.length) ; ++l) {
			integ.addEventHandler(functions[l], java.lang.Double.POSITIVE_INFINITY, (1.0E-8 * maxStep), 1000);
		}
		junit.framework.Assert.assertEquals(functions.length, integ.getEventHandlers().size());
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertTrue(((handler.getMaximalValueError()) < 1.0E-7));
		junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
		junit.framework.Assert.assertEquals(12.0, handler.getLastTime(), (1.0E-8 * maxStep));
		integ.clearEventHandlers();
		junit.framework.Assert.assertEquals(0, integ.getEventHandlers().size());
	}

	public void testEventsErrors() {
		final org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
		double minStep = 0;
		double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
		double scalAbsoluteTolerance = 1.0E-8;
		double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.HighamHall54Integrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		integ.addEventHandler(new org.apache.commons.math.ode.events.EventHandler() {
			public int eventOccurred(double t, double[] y, boolean increasing) {
				return org.apache.commons.math.ode.events.EventHandler.CONTINUE;
			}

			public double g(double t, double[] y) throws org.apache.commons.math.ode.events.EventException {
				double middle = ((pb.getInitialTime()) + (pb.getFinalTime())) / 2;
				double offset = t - middle;
				if (offset > 0) {
					throw new org.apache.commons.math.ode.events.EventException("Evaluation failed for argument = {0}" , t);
				} 
				return offset;
			}

			public void resetState(double t, double[] y) {
			}

			private static final long serialVersionUID = 935652725339916361L;
		}, java.lang.Double.POSITIVE_INFINITY, (1.0E-8 * maxStep), 1000);
		try {
			integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.ode.IntegratorException ie) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception type caught");
		}
	}

	public void testEventsNoConvergence() {
		final org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
		double minStep = 0;
		double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
		double scalAbsoluteTolerance = 1.0E-8;
		double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.HighamHall54Integrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		integ.addEventHandler(new org.apache.commons.math.ode.events.EventHandler() {
			public int eventOccurred(double t, double[] y, boolean increasing) {
				return org.apache.commons.math.ode.events.EventHandler.CONTINUE;
			}

			public double g(double t, double[] y) {
				double middle = ((pb.getInitialTime()) + (pb.getFinalTime())) / 2;
				double offset = t - middle;
				return offset > 0 ? offset + 0.5 : offset - 0.5;
			}

			public void resetState(double t, double[] y) {
			}

			private static final long serialVersionUID = 935652725339916361L;
		}, java.lang.Double.POSITIVE_INFINITY, (1.0E-8 * maxStep), 3);
		try {
			integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.ode.IntegratorException ie) {
			junit.framework.Assert.assertTrue(((ie.getCause()) != null));
			junit.framework.Assert.assertTrue(((ie.getCause()) instanceof org.apache.commons.math.ConvergenceException));
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception type caught");
		}
	}

	public void testSanityChecks() {
		try {
			final org.apache.commons.math.ode.TestProblem3 pb = new org.apache.commons.math.ode.TestProblem3(0.9);
			double minStep = 0;
			double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
			try {
				org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.HighamHall54Integrator(minStep , maxStep , new double[4] , new double[4]);
				integ.integrate(pb, pb.getInitialTime(), new double[6], pb.getFinalTime(), new double[pb.getDimension()]);
				junit.framework.Assert.fail("an exception should have been thrown");
			} catch (org.apache.commons.math.ode.IntegratorException ie) {
			}
			try {
				org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.HighamHall54Integrator(minStep , maxStep , new double[4] , new double[4]);
				integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[6]);
				junit.framework.Assert.fail("an exception should have been thrown");
			} catch (org.apache.commons.math.ode.IntegratorException ie) {
			}
			try {
				org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.HighamHall54Integrator(minStep , maxStep , new double[2] , new double[4]);
				integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
				junit.framework.Assert.fail("an exception should have been thrown");
			} catch (org.apache.commons.math.ode.IntegratorException ie) {
			}
			try {
				org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.HighamHall54Integrator(minStep , maxStep , new double[4] , new double[2]);
				integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
				junit.framework.Assert.fail("an exception should have been thrown");
			} catch (org.apache.commons.math.ode.IntegratorException ie) {
			}
			try {
				org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.HighamHall54Integrator(minStep , maxStep , new double[4] , new double[4]);
				integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getInitialTime(), new double[pb.getDimension()]);
				junit.framework.Assert.fail("an exception should have been thrown");
			} catch (org.apache.commons.math.ode.IntegratorException ie) {
			}
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail(("wrong exception caught: " + (e.getMessage())));
		}
	}

	public void testKepler() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		final org.apache.commons.math.ode.TestProblem3 pb = new org.apache.commons.math.ode.TestProblem3(0.9);
		double minStep = 0;
		double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
		double[] vecAbsoluteTolerance = new double[]{ 1.0E-8 , 1.0E-8 , 1.0E-10 , 1.0E-10 };
		double[] vecRelativeTolerance = new double[]{ 1.0E-10 , 1.0E-10 , 1.0E-8 , 1.0E-8 };
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.HighamHall54Integrator(minStep , maxStep , vecAbsoluteTolerance , vecRelativeTolerance);
		integ.addStepHandler(new org.apache.commons.math.ode.nonstiff.HighamHall54IntegratorTest.KeplerHandler(pb));
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertEquals("Higham-Hall 5(4)", integ.getName());
	}

	private static class KeplerHandler implements org.apache.commons.math.ode.sampling.StepHandler {
		public KeplerHandler(org.apache.commons.math.ode.TestProblem3 pb) {
			this.pb = pb;
			nbSteps = 0;
			maxError = 0;
		}

		public boolean requiresDenseOutput() {
			return false;
		}

		public void reset() {
			nbSteps = 0;
			maxError = 0;
		}

		public void handleStep(org.apache.commons.math.ode.sampling.StepInterpolator interpolator, boolean isLast) throws org.apache.commons.math.ode.DerivativeException {
			++(nbSteps);
			double[] interpolatedY = interpolator.getInterpolatedState();
			double[] theoreticalY = pb.computeTheoreticalState(interpolator.getCurrentTime());
			double dx = (interpolatedY[0]) - (theoreticalY[0]);
			double dy = (interpolatedY[1]) - (theoreticalY[1]);
			double error = (dx * dx) + (dy * dy);
			if (error > (maxError)) {
				maxError = error;
			} 
			if (isLast) {
				junit.framework.Assert.assertTrue(((maxError) < 4.2E-11));
				junit.framework.Assert.assertTrue(((nbSteps) < 670));
			} 
		}

		private org.apache.commons.math.ode.TestProblem3 pb;

		private int nbSteps;

		private double maxError;
	}
}

