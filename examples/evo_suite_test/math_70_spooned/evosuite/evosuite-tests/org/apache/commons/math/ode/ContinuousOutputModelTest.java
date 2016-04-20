package org.apache.commons.math.ode;


public class ContinuousOutputModelTest extends junit.framework.TestCase {
	public ContinuousOutputModelTest(java.lang.String name) {
		super(name);
		pb = null;
		integ = null;
	}

	public void testBoundaries() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		integ.addStepHandler(new org.apache.commons.math.ode.ContinuousOutputModel());
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		org.apache.commons.math.ode.ContinuousOutputModel cm = ((org.apache.commons.math.ode.ContinuousOutputModel)(integ.getStepHandlers().iterator().next()));
		cm.setInterpolatedTime(((2.0 * (pb.getInitialTime())) - (pb.getFinalTime())));
		cm.setInterpolatedTime(((2.0 * (pb.getFinalTime())) - (pb.getInitialTime())));
		cm.setInterpolatedTime((0.5 * ((pb.getFinalTime()) + (pb.getInitialTime()))));
	}

	public void testRandomAccess() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.ContinuousOutputModel cm = new org.apache.commons.math.ode.ContinuousOutputModel();
		integ.addStepHandler(cm);
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		java.util.Random random = new java.util.Random(347588535632L);
		double maxError = 0.0;
		for (int i = 0 ; i < 1000 ; ++i) {
			double r = random.nextDouble();
			double time = (r * (pb.getInitialTime())) + ((1.0 - r) * (pb.getFinalTime()));
			cm.setInterpolatedTime(time);
			double[] interpolatedY = cm.getInterpolatedState();
			double[] theoreticalY = pb.computeTheoreticalState(time);
			double dx = (interpolatedY[0]) - (theoreticalY[0]);
			double dy = (interpolatedY[1]) - (theoreticalY[1]);
			double error = (dx * dx) + (dy * dy);
			if (error > maxError) {
				maxError = error;
			} 
		}
		junit.framework.Assert.assertTrue((maxError < 1.0E-9));
	}

	public void testModelsMerging() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.FirstOrderDifferentialEquations problem = new org.apache.commons.math.ode.FirstOrderDifferentialEquations() {
			private static final long serialVersionUID = 2472449657345878299L;

			public void computeDerivatives(double t, double[] y, double[] dot) throws org.apache.commons.math.ode.DerivativeException {
				dot[0] = -(y[1]);
				dot[1] = y[0];
			}

			public int getDimension() {
				return 2;
			}
		};
		org.apache.commons.math.ode.ContinuousOutputModel cm1 = new org.apache.commons.math.ode.ContinuousOutputModel();
		org.apache.commons.math.ode.FirstOrderIntegrator integ1 = new org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator(0 , 1.0 , 1.0E-8 , 1.0E-8);
		integ1.addStepHandler(cm1);
		integ1.integrate(problem, java.lang.Math.PI, new double[]{ -1.0 , 0.0 }, 0, new double[2]);
		org.apache.commons.math.ode.ContinuousOutputModel cm2 = new org.apache.commons.math.ode.ContinuousOutputModel();
		org.apache.commons.math.ode.FirstOrderIntegrator integ2 = new org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator(0 , 0.1 , 1.0E-12 , 1.0E-12);
		integ2.addStepHandler(cm2);
		integ2.integrate(problem, (2.0 * (java.lang.Math.PI)), new double[]{ 1.0 , 0.0 }, java.lang.Math.PI, new double[2]);
		org.apache.commons.math.ode.ContinuousOutputModel cm = new org.apache.commons.math.ode.ContinuousOutputModel();
		cm.append(cm2);
		cm.append(new org.apache.commons.math.ode.ContinuousOutputModel());
		cm.append(cm1);
		junit.framework.Assert.assertEquals((2.0 * (java.lang.Math.PI)), cm.getInitialTime(), 1.0E-12);
		junit.framework.Assert.assertEquals(0, cm.getFinalTime(), 1.0E-12);
		junit.framework.Assert.assertEquals(cm.getFinalTime(), cm.getInterpolatedTime(), 1.0E-12);
		for (double t = 0 ; t < (2.0 * (java.lang.Math.PI)) ; t += 0.1) {
			cm.setInterpolatedTime(t);
			double[] y = cm.getInterpolatedState();
			junit.framework.Assert.assertEquals(java.lang.Math.cos(t), y[0], 1.0E-7);
			junit.framework.Assert.assertEquals(java.lang.Math.sin(t), y[1], 1.0E-7);
		}
	}

	public void testErrorConditions() throws org.apache.commons.math.ode.DerivativeException {
		org.apache.commons.math.ode.ContinuousOutputModel cm = new org.apache.commons.math.ode.ContinuousOutputModel();
		cm.handleStep(buildInterpolator(0, new double[]{ 0.0 , 1.0 , -2.0 }, 1), true);
		junit.framework.Assert.assertTrue(checkAppendError(cm, 1.0, new double[]{ 0.0 , 1.0 }, 2.0));
		junit.framework.Assert.assertTrue(checkAppendError(cm, 10.0, new double[]{ 0.0 , 1.0 , -2.0 }, 20.0));
		junit.framework.Assert.assertTrue(checkAppendError(cm, 1.0, new double[]{ 0.0 , 1.0 , -2.0 }, 0.0));
		junit.framework.Assert.assertFalse(checkAppendError(cm, 1.0, new double[]{ 0.0 , 1.0 , -2.0 }, 2.0));
	}

	private boolean checkAppendError(org.apache.commons.math.ode.ContinuousOutputModel cm, double t0, double[] y0, double t1) throws org.apache.commons.math.ode.DerivativeException {
		try {
			org.apache.commons.math.ode.ContinuousOutputModel otherCm = new org.apache.commons.math.ode.ContinuousOutputModel();
			otherCm.handleStep(buildInterpolator(t0, y0, t1), true);
			cm.append(otherCm);
		} catch (java.lang.IllegalArgumentException iae) {
			return true;
		}
		return false;
	}

	private org.apache.commons.math.ode.sampling.StepInterpolator buildInterpolator(double t0, double[] y0, double t1) {
		org.apache.commons.math.ode.sampling.DummyStepInterpolator interpolator = new org.apache.commons.math.ode.sampling.DummyStepInterpolator(y0 , new double[y0.length] , (t1 >= t0));
		interpolator.storeTime(t0);
		interpolator.shift();
		interpolator.storeTime(t1);
		return interpolator;
	}

	public void checkValue(double value, double reference) {
		junit.framework.Assert.assertTrue(((java.lang.Math.abs((value - reference))) < 1.0E-10));
	}

	@java.lang.Override
	public void setUp() {
		pb = new org.apache.commons.math.ode.TestProblem3(0.9);
		double minStep = 0;
		double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
		integ = new org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator(minStep , maxStep , 1.0E-8 , 1.0E-8);
	}

	@java.lang.Override
	public void tearDown() {
		pb = null;
		integ = null;
	}

	org.apache.commons.math.ode.TestProblem3 pb;

	org.apache.commons.math.ode.FirstOrderIntegrator integ;
}

