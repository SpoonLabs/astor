package org.apache.commons.math.ode.nonstiff;


public class DormandPrince853IntegratorTest extends junit.framework.TestCase {
	public DormandPrince853IntegratorTest(java.lang.String name) {
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
		org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator integrator = new org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator(0.0 , 100.0 , 1.0E-10 , 1.0E-10);
		double[] y0 = new double[k.length];
		for (int i = 0 ; i < (y0.length) ; ++i) {
			y0[i] = i + 1;
		}
		double[] y = new double[k.length];
		integrator.setInitialStepSize(60.0);
		double finalT = integrator.integrate(ode, t0, y0, tEvent, y);
		junit.framework.Assert.assertEquals(tEvent, finalT, 5.0E-6);
		for (int i = 0 ; i < (y.length) ; ++i) {
			junit.framework.Assert.assertEquals(((y0[i]) * (java.lang.Math.exp(((k[i]) * (finalT - t0))))), y[i], 1.0E-9);
		}
		integrator.setInitialStepSize(60.0);
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

	public void testDimensionCheck() {
		try {
			org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
			org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator integrator = new org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator(0.0 , 1.0 , 1.0E-10 , 1.0E-10);
			integrator.integrate(pb, 0.0, new double[(pb.getDimension()) + 10], 1.0, new double[(pb.getDimension()) + 10]);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.ode.DerivativeException de) {
			junit.framework.Assert.fail("wrong exception caught");
		} catch (org.apache.commons.math.ode.IntegratorException ie) {
		}
	}

	public void testNullIntervalCheck() {
		try {
			org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
			org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator integrator = new org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator(0.0 , 1.0 , 1.0E-10 , 1.0E-10);
			integrator.integrate(pb, 0.0, new double[pb.getDimension()], 0.0, new double[pb.getDimension()]);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.ode.DerivativeException de) {
			junit.framework.Assert.fail("wrong exception caught");
		} catch (org.apache.commons.math.ode.IntegratorException ie) {
		}
	}

	public void testMinStep() {
		try {
			org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
			double minStep = 0.1 * ((pb.getFinalTime()) - (pb.getInitialTime()));
			double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
			double[] vecAbsoluteTolerance = new double[]{ 1.0E-15 , 1.0E-16 };
			double[] vecRelativeTolerance = new double[]{ 1.0E-15 , 1.0E-16 };
			org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator(minStep , maxStep , vecAbsoluteTolerance , vecRelativeTolerance);
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
			org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
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
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertTrue(((handler.getLastError()) < 8.1E-8));
		junit.framework.Assert.assertTrue(((handler.getMaximalValueError()) < 1.1E-7));
		junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
		junit.framework.Assert.assertEquals("Dormand-Prince 8 (5, 3)", integ.getName());
	}

	public void testEvents() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem4 pb = new org.apache.commons.math.ode.TestProblem4();
		double minStep = 0;
		double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
		double scalAbsoluteTolerance = 1.0E-9;
		double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		org.apache.commons.math.ode.events.EventHandler[] functions = pb.getEventsHandlers();
		for (int l = 0 ; l < (functions.length) ; ++l) {
			integ.addEventHandler(functions[l], java.lang.Double.POSITIVE_INFINITY, (1.0E-8 * maxStep), 1000);
		}
		junit.framework.Assert.assertEquals(functions.length, integ.getEventHandlers().size());
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertTrue(((handler.getMaximalValueError()) < 5.0E-8));
		junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
		junit.framework.Assert.assertEquals(12.0, handler.getLastTime(), (1.0E-8 * maxStep));
		integ.clearEventHandlers();
		junit.framework.Assert.assertEquals(0, integ.getEventHandlers().size());
	}

	public void testKepler() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		final org.apache.commons.math.ode.TestProblem3 pb = new org.apache.commons.math.ode.TestProblem3(0.9);
		double minStep = 0;
		double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
		double scalAbsoluteTolerance = 1.0E-8;
		double scalRelativeTolerance = scalAbsoluteTolerance;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
		integ.addStepHandler(new org.apache.commons.math.ode.nonstiff.DormandPrince853IntegratorTest.KeplerHandler(pb));
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertEquals(integ.getEvaluations(), pb.getCalls());
		junit.framework.Assert.assertTrue(((pb.getCalls()) < 3300));
	}

	public void testVariableSteps() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		final org.apache.commons.math.ode.TestProblem3 pb = new org.apache.commons.math.ode.TestProblem3(0.9);
		double minStep = 0;
		double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
		double scalAbsoluteTolerance = 1.0E-8;
		double scalRelativeTolerance = scalAbsoluteTolerance;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
		integ.addStepHandler(new org.apache.commons.math.ode.nonstiff.DormandPrince853IntegratorTest.VariableHandler());
		double stopTime = integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertEquals(pb.getFinalTime(), stopTime, 1.0E-10);
		junit.framework.Assert.assertEquals("Dormand-Prince 8 (5, 3)", integ.getName());
	}

	public void testNoDenseOutput() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem1 pb1 = new org.apache.commons.math.ode.TestProblem1();
		org.apache.commons.math.ode.TestProblem1 pb2 = pb1.copy();
		double minStep = 0.1 * ((pb1.getFinalTime()) - (pb1.getInitialTime()));
		double maxStep = (pb1.getFinalTime()) - (pb1.getInitialTime());
		double scalAbsoluteTolerance = 1.0E-4;
		double scalRelativeTolerance = 1.0E-4;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
		integ.addStepHandler(org.apache.commons.math.ode.sampling.DummyStepHandler.getInstance());
		integ.integrate(pb1, pb1.getInitialTime(), pb1.getInitialState(), pb1.getFinalTime(), new double[pb1.getDimension()]);
		int callsWithoutDenseOutput = pb1.getCalls();
		junit.framework.Assert.assertEquals(integ.getEvaluations(), callsWithoutDenseOutput);
		integ.addStepHandler(new org.apache.commons.math.ode.nonstiff.DormandPrince853IntegratorTest.InterpolatingStepHandler());
		integ.integrate(pb2, pb2.getInitialTime(), pb2.getInitialState(), pb2.getFinalTime(), new double[pb2.getDimension()]);
		int callsWithDenseOutput = pb2.getCalls();
		junit.framework.Assert.assertEquals(integ.getEvaluations(), callsWithDenseOutput);
		junit.framework.Assert.assertTrue((callsWithDenseOutput > callsWithoutDenseOutput));
	}

	public void testUnstableDerivative() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		final org.apache.commons.math.ode.nonstiff.StepProblem stepProblem = new org.apache.commons.math.ode.nonstiff.StepProblem(0.0 , 1.0 , 2.0);
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator(0.1 , 10 , 1.0E-12 , 0.0);
		integ.addEventHandler(stepProblem, 1.0, 1.0E-12, 1000);
		double[] y = new double[]{ java.lang.Double.NaN };
		integ.integrate(stepProblem, 0.0, new double[]{ 0.0 }, 10.0, y);
		junit.framework.Assert.assertEquals(8.0, y[0], 1.0E-12);
	}

	private static class KeplerHandler implements org.apache.commons.math.ode.sampling.StepHandler {
		public KeplerHandler(org.apache.commons.math.ode.TestProblem3 pb) {
			this.pb = pb;
			reset();
		}

		public boolean requiresDenseOutput() {
			return true;
		}

		public void reset() {
			nbSteps = 0;
			maxError = 0;
		}

		public void handleStep(org.apache.commons.math.ode.sampling.StepInterpolator interpolator, boolean isLast) throws org.apache.commons.math.ode.DerivativeException {
			++(nbSteps);
			for (int a = 1 ; a < 10 ; ++a) {
				double prev = interpolator.getPreviousTime();
				double curr = interpolator.getCurrentTime();
				double interp = (((10 - a) * prev) + (a * curr)) / 10;
				interpolator.setInterpolatedTime(interp);
				double[] interpolatedY = interpolator.getInterpolatedState();
				double[] theoreticalY = pb.computeTheoreticalState(interpolator.getInterpolatedTime());
				double dx = (interpolatedY[0]) - (theoreticalY[0]);
				double dy = (interpolatedY[1]) - (theoreticalY[1]);
				double error = (dx * dx) + (dy * dy);
				if (error > (maxError)) {
					maxError = error;
				} 
			}
			if (isLast) {
				junit.framework.Assert.assertTrue(((maxError) < 2.4E-10));
				junit.framework.Assert.assertTrue(((nbSteps) < 150));
			} 
		}

		private int nbSteps;

		private double maxError;

		private org.apache.commons.math.ode.TestProblem3 pb;
	}

	private static class VariableHandler implements org.apache.commons.math.ode.sampling.StepHandler {
		public VariableHandler() {
			reset();
		}

		public boolean requiresDenseOutput() {
			return false;
		}

		public void reset() {
			firstTime = true;
			minStep = 0;
			maxStep = 0;
		}

		public void handleStep(org.apache.commons.math.ode.sampling.StepInterpolator interpolator, boolean isLast) {
			double step = java.lang.Math.abs(((interpolator.getCurrentTime()) - (interpolator.getPreviousTime())));
			if (firstTime) {
				minStep = java.lang.Math.abs(step);
				maxStep = minStep;
				firstTime = false;
			} else {
				if (step < (minStep)) {
					minStep = step;
				} 
				if (step > (maxStep)) {
					maxStep = step;
				} 
			}
			if (isLast) {
				junit.framework.Assert.assertTrue(((minStep) < (1.0 / 100.0)));
				junit.framework.Assert.assertTrue(((maxStep) > (1.0 / 2.0)));
			} 
		}

		private boolean firstTime = true;

		private double minStep = 0;

		private double maxStep = 0;
	}

	private static class InterpolatingStepHandler implements org.apache.commons.math.ode.sampling.StepHandler {
		public boolean requiresDenseOutput() {
			return true;
		}

		public void reset() {
		}

		public void handleStep(org.apache.commons.math.ode.sampling.StepInterpolator interpolator, boolean isLast) throws org.apache.commons.math.ode.DerivativeException {
			double prev = interpolator.getPreviousTime();
			double curr = interpolator.getCurrentTime();
			interpolator.setInterpolatedTime((0.5 * (prev + curr)));
		}
	}
}

