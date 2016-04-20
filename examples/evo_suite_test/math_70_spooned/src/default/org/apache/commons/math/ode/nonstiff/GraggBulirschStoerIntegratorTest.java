package org.apache.commons.math.ode.nonstiff;


public class GraggBulirschStoerIntegratorTest extends junit.framework.TestCase {
	public GraggBulirschStoerIntegratorTest(java.lang.String name) {
		super(name);
	}

	public void testDimensionCheck() {
		try {
			org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
			org.apache.commons.math.ode.nonstiff.AdaptiveStepsizeIntegrator integrator = new org.apache.commons.math.ode.nonstiff.GraggBulirschStoerIntegrator(0.0 , 1.0 , 1.0E-10 , 1.0E-10);
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
			org.apache.commons.math.ode.nonstiff.GraggBulirschStoerIntegrator integrator = new org.apache.commons.math.ode.nonstiff.GraggBulirschStoerIntegrator(0.0 , 1.0 , 1.0E-10 , 1.0E-10);
			integrator.integrate(pb, 0.0, new double[pb.getDimension()], 0.0, new double[pb.getDimension()]);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.ode.DerivativeException de) {
			junit.framework.Assert.fail("wrong exception caught");
		} catch (org.apache.commons.math.ode.IntegratorException ie) {
		}
	}

	public void testMinStep() {
		try {
			org.apache.commons.math.ode.TestProblem5 pb = new org.apache.commons.math.ode.TestProblem5();
			double minStep = 0.1 * (java.lang.Math.abs(((pb.getFinalTime()) - (pb.getInitialTime()))));
			double maxStep = java.lang.Math.abs(((pb.getFinalTime()) - (pb.getInitialTime())));
			double[] vecAbsoluteTolerance = new double[]{ 1.0E-20 , 1.0E-21 };
			double[] vecRelativeTolerance = new double[]{ 1.0E-20 , 1.0E-21 };
			org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.GraggBulirschStoerIntegrator(minStep , maxStep , vecAbsoluteTolerance , vecRelativeTolerance);
			org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
			integ.addStepHandler(handler);
			integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.ode.DerivativeException de) {
			junit.framework.Assert.fail("wrong exception caught");
		} catch (org.apache.commons.math.ode.IntegratorException ie) {
		}
	}

	public void testBackward() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem5 pb = new org.apache.commons.math.ode.TestProblem5();
		double minStep = 0;
		double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
		double scalAbsoluteTolerance = 1.0E-8;
		double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.GraggBulirschStoerIntegrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertTrue(((handler.getLastError()) < 9.0E-10));
		junit.framework.Assert.assertTrue(((handler.getMaximalValueError()) < 9.0E-10));
		junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
		junit.framework.Assert.assertEquals("Gragg-Bulirsch-Stoer", integ.getName());
	}

	public void testIncreasingTolerance() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		int previousCalls = java.lang.Integer.MAX_VALUE;
		for (int i = -12 ; i < (-4) ; ++i) {
			org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
			double minStep = 0;
			double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
			double absTolerance = java.lang.Math.pow(10.0, i);
			double relTolerance = absTolerance;
			org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.GraggBulirschStoerIntegrator(minStep , maxStep , absTolerance , relTolerance);
			org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
			integ.addStepHandler(handler);
			integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
			double ratio = (handler.getMaximalValueError()) / absTolerance;
			junit.framework.Assert.assertTrue((ratio < 2.4));
			junit.framework.Assert.assertTrue((ratio > 0.02));
			junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
			int calls = pb.getCalls();
			junit.framework.Assert.assertEquals(integ.getEvaluations(), calls);
			junit.framework.Assert.assertTrue((calls <= previousCalls));
			previousCalls = calls;
		}
	}

	public void testIntegratorControls() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem3 pb = new org.apache.commons.math.ode.TestProblem3(0.999);
		org.apache.commons.math.ode.nonstiff.GraggBulirschStoerIntegrator integ = new org.apache.commons.math.ode.nonstiff.GraggBulirschStoerIntegrator(0 , ((pb.getFinalTime()) - (pb.getInitialTime())) , 1.0E-8 , 1.0E-10);
		double errorWithDefaultSettings = getMaxError(integ, pb);
		integ.setStabilityCheck(true, 2, 1, 0.99);
		junit.framework.Assert.assertTrue((errorWithDefaultSettings < (getMaxError(integ, pb))));
		integ.setStabilityCheck(true, -1, -1, -1);
		integ.setStepsizeControl(0.5, 0.99, 0.1, 2.5);
		junit.framework.Assert.assertTrue((errorWithDefaultSettings < (getMaxError(integ, pb))));
		integ.setStepsizeControl(-1, -1, -1, -1);
		integ.setOrderControl(10, 0.7, 0.95);
		junit.framework.Assert.assertTrue((errorWithDefaultSettings < (getMaxError(integ, pb))));
		integ.setOrderControl(-1, -1, -1);
		integ.setInterpolationControl(true, 3);
		junit.framework.Assert.assertTrue((errorWithDefaultSettings < (getMaxError(integ, pb))));
		integ.setInterpolationControl(true, -1);
	}

	private double getMaxError(org.apache.commons.math.ode.FirstOrderIntegrator integrator, org.apache.commons.math.ode.TestProblemAbstract pb) throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integrator);
		integrator.addStepHandler(handler);
		integrator.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		return handler.getMaximalValueError();
	}

	public void testEvents() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem4 pb = new org.apache.commons.math.ode.TestProblem4();
		double minStep = 0;
		double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
		double scalAbsoluteTolerance = 1.0E-10;
		double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.GraggBulirschStoerIntegrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
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
		double absTolerance = 1.0E-6;
		double relTolerance = 1.0E-6;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.GraggBulirschStoerIntegrator(minStep , maxStep , absTolerance , relTolerance);
		integ.addStepHandler(new org.apache.commons.math.ode.nonstiff.GraggBulirschStoerIntegratorTest.KeplerStepHandler(pb));
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertEquals(integ.getEvaluations(), pb.getCalls());
		junit.framework.Assert.assertTrue(((pb.getCalls()) < 2150));
	}

	public void testVariableSteps() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		final org.apache.commons.math.ode.TestProblem3 pb = new org.apache.commons.math.ode.TestProblem3(0.9);
		double minStep = 0;
		double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
		double absTolerance = 1.0E-8;
		double relTolerance = 1.0E-8;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.GraggBulirschStoerIntegrator(minStep , maxStep , absTolerance , relTolerance);
		integ.addStepHandler(new org.apache.commons.math.ode.nonstiff.GraggBulirschStoerIntegratorTest.VariableStepHandler());
		double stopTime = integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertEquals(pb.getFinalTime(), stopTime, 1.0E-10);
		junit.framework.Assert.assertEquals("Gragg-Bulirsch-Stoer", integ.getName());
	}

	public void testUnstableDerivative() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		final org.apache.commons.math.ode.nonstiff.StepProblem stepProblem = new org.apache.commons.math.ode.nonstiff.StepProblem(0.0 , 1.0 , 2.0);
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.GraggBulirschStoerIntegrator(0.1 , 10 , 1.0E-12 , 0.0);
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
			return true;
		}

		public void reset() {
			nbSteps = 0;
			maxError = 0;
		}

		public void handleStep(org.apache.commons.math.ode.sampling.StepInterpolator interpolator, boolean isLast) throws org.apache.commons.math.ode.DerivativeException {
			++(nbSteps);
			for (int a = 1 ; a < 100 ; ++a) {
				double prev = interpolator.getPreviousTime();
				double curr = interpolator.getCurrentTime();
				double interp = (((100 - a) * prev) + (a * curr)) / 100;
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
				junit.framework.Assert.assertTrue(((maxError) < 2.7E-6));
				junit.framework.Assert.assertTrue(((nbSteps) < 80));
			} 
		}

		private int nbSteps;

		private double maxError;

		private org.apache.commons.math.ode.TestProblem3 pb;
	}

	public static class VariableStepHandler implements org.apache.commons.math.ode.sampling.StepHandler {
		public VariableStepHandler() {
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
				junit.framework.Assert.assertTrue(((minStep) < 0.0082));
				junit.framework.Assert.assertTrue(((maxStep) > 1.7));
			} 
		}

		private boolean firstTime;

		private double minStep;

		private double maxStep;
	}
}

