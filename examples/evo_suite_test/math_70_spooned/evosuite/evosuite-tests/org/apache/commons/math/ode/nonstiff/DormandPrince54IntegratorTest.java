package org.apache.commons.math.ode.nonstiff;


public class DormandPrince54IntegratorTest extends junit.framework.TestCase {
	public DormandPrince54IntegratorTest(java.lang.String name) {
		super(name);
	}

	public void testDimensionCheck() {
		try {
			org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
			org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator integrator = new org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator(0.0 , 1.0 , 1.0E-10 , 1.0E-10);
			integrator.integrate(pb, 0.0, new double[(pb.getDimension()) + 10], 1.0, new double[(pb.getDimension()) + 10]);
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
			org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator(minStep , maxStep , vecAbsoluteTolerance , vecRelativeTolerance);
			org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
			integ.addStepHandler(handler);
			integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.ode.DerivativeException de) {
			junit.framework.Assert.fail("wrong exception caught");
		} catch (org.apache.commons.math.ode.IntegratorException ie) {
		}
	}

	public void testSmallLastStep() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblemAbstract pb = new org.apache.commons.math.ode.TestProblem5();
		double minStep = 1.25;
		double maxStep = java.lang.Math.abs(((pb.getFinalTime()) - (pb.getInitialTime())));
		double scalAbsoluteTolerance = 6.0E-4;
		double scalRelativeTolerance = 6.0E-4;
		org.apache.commons.math.ode.nonstiff.AdaptiveStepsizeIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
		org.apache.commons.math.ode.nonstiff.DormandPrince54IntegratorTest.DP54SmallLastHandler handler = new org.apache.commons.math.ode.nonstiff.DormandPrince54IntegratorTest.DP54SmallLastHandler(minStep);
		integ.addStepHandler(handler);
		integ.setInitialStepSize(1.7);
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertTrue(handler.wasLastSeen());
		junit.framework.Assert.assertEquals("Dormand-Prince 5(4)", integ.getName());
	}

	public void testBackward() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem5 pb = new org.apache.commons.math.ode.TestProblem5();
		double minStep = 0;
		double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
		double scalAbsoluteTolerance = 1.0E-8;
		double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertTrue(((handler.getLastError()) < 2.0E-7));
		junit.framework.Assert.assertTrue(((handler.getMaximalValueError()) < 2.0E-7));
		junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
		junit.framework.Assert.assertEquals("Dormand-Prince 5(4)", integ.getName());
	}

	private static class DP54SmallLastHandler implements org.apache.commons.math.ode.sampling.StepHandler {
		public DP54SmallLastHandler(double minStep) {
			lastSeen = false;
			this.minStep = minStep;
		}

		public boolean requiresDenseOutput() {
			return false;
		}

		public void reset() {
		}

		public void handleStep(org.apache.commons.math.ode.sampling.StepInterpolator interpolator, boolean isLast) {
			if (isLast) {
				lastSeen = true;
				double h = (interpolator.getCurrentTime()) - (interpolator.getPreviousTime());
				junit.framework.Assert.assertTrue(((java.lang.Math.abs(h)) < (minStep)));
			} 
		}

		public boolean wasLastSeen() {
			return lastSeen;
		}

		private boolean lastSeen;

		private double minStep;
	}

	public void testIncreasingTolerance() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		int previousCalls = java.lang.Integer.MAX_VALUE;
		for (int i = -12 ; i < (-2) ; ++i) {
			org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
			double minStep = 0;
			double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
			double scalAbsoluteTolerance = java.lang.Math.pow(10.0, i);
			double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;
			org.apache.commons.math.ode.nonstiff.EmbeddedRungeKuttaIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
			org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
			integ.setSafety(0.8);
			integ.setMaxGrowth(5.0);
			integ.setMinReduction(0.3);
			integ.addStepHandler(handler);
			integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
			junit.framework.Assert.assertEquals(0.8, integ.getSafety(), 1.0E-12);
			junit.framework.Assert.assertEquals(5.0, integ.getMaxGrowth(), 1.0E-12);
			junit.framework.Assert.assertEquals(0.3, integ.getMinReduction(), 1.0E-12);
			junit.framework.Assert.assertTrue(((handler.getMaximalValueError()) < (0.7 * scalAbsoluteTolerance)));
			junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
			int calls = pb.getCalls();
			junit.framework.Assert.assertEquals(integ.getEvaluations(), calls);
			junit.framework.Assert.assertTrue((calls <= previousCalls));
			previousCalls = calls;
		}
	}

	public void testEvents() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem4 pb = new org.apache.commons.math.ode.TestProblem4();
		double minStep = 0;
		double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
		double scalAbsoluteTolerance = 1.0E-8;
		double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		org.apache.commons.math.ode.events.EventHandler[] functions = pb.getEventsHandlers();
		for (int l = 0 ; l < (functions.length) ; ++l) {
			integ.addEventHandler(functions[l], java.lang.Double.POSITIVE_INFINITY, (1.0E-8 * maxStep), 1000);
		}
		junit.framework.Assert.assertEquals(functions.length, integ.getEventHandlers().size());
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertTrue(((handler.getMaximalValueError()) < 5.0E-6));
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
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
		integ.addStepHandler(new org.apache.commons.math.ode.nonstiff.DormandPrince54IntegratorTest.KeplerHandler(pb));
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertEquals(integ.getEvaluations(), pb.getCalls());
		junit.framework.Assert.assertTrue(((pb.getCalls()) < 2800));
	}

	public void testVariableSteps() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		final org.apache.commons.math.ode.TestProblem3 pb = new org.apache.commons.math.ode.TestProblem3(0.9);
		double minStep = 0;
		double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
		double scalAbsoluteTolerance = 1.0E-8;
		double scalRelativeTolerance = scalAbsoluteTolerance;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
		integ.addStepHandler(new org.apache.commons.math.ode.nonstiff.DormandPrince54IntegratorTest.VariableHandler());
		double stopTime = integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertEquals(pb.getFinalTime(), stopTime, 1.0E-10);
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
				junit.framework.Assert.assertTrue(((maxError) < 7.0E-10));
				junit.framework.Assert.assertTrue(((nbSteps) < 400));
			} 
		}

		private int nbSteps;

		private double maxError;

		private org.apache.commons.math.ode.TestProblem3 pb;
	}

	private static class VariableHandler implements org.apache.commons.math.ode.sampling.StepHandler {
		public VariableHandler() {
			firstTime = true;
			minStep = 0;
			maxStep = 0;
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
				junit.framework.Assert.assertTrue(((minStep) < (1.0 / 450.0)));
				junit.framework.Assert.assertTrue(((maxStep) > (1.0 / 4.2)));
			} 
		}

		private boolean firstTime;

		private double minStep;

		private double maxStep;
	}
}

