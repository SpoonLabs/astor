package org.apache.commons.math.ode.nonstiff;


public class AdamsMoultonIntegratorTest {
	@org.junit.Test(expected = org.apache.commons.math.ode.IntegratorException.class)
	public void dimensionCheck() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.AdamsMoultonIntegrator(2 , 0.0 , 1.0 , 1.0E-10 , 1.0E-10);
		integ.integrate(pb, 0.0, new double[(pb.getDimension()) + 10], 1.0, new double[(pb.getDimension()) + 10]);
	}

	@org.junit.Test(expected = org.apache.commons.math.ode.IntegratorException.class)
	public void testMinStep() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
		double minStep = 0.1 * ((pb.getFinalTime()) - (pb.getInitialTime()));
		double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
		double[] vecAbsoluteTolerance = new double[]{ 1.0E-15 , 1.0E-16 };
		double[] vecRelativeTolerance = new double[]{ 1.0E-15 , 1.0E-16 };
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.AdamsMoultonIntegrator(4 , minStep , maxStep , vecAbsoluteTolerance , vecRelativeTolerance);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
	}

	@org.junit.Test
	public void testIncreasingTolerance() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		int previousCalls = java.lang.Integer.MAX_VALUE;
		for (int i = -12 ; i < (-2) ; ++i) {
			org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
			double minStep = 0;
			double maxStep = (pb.getFinalTime()) - (pb.getInitialTime());
			double scalAbsoluteTolerance = java.lang.Math.pow(10.0, i);
			double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;
			org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.AdamsMoultonIntegrator(4 , minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
			org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
			integ.addStepHandler(handler);
			integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
			org.junit.Assert.assertTrue(((handler.getMaximalValueError()) > (0.15 * scalAbsoluteTolerance)));
			org.junit.Assert.assertTrue(((handler.getMaximalValueError()) < (3.0 * scalAbsoluteTolerance)));
			org.junit.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-16);
			int calls = pb.getCalls();
			org.junit.Assert.assertEquals(integ.getEvaluations(), calls);
			org.junit.Assert.assertTrue((calls <= previousCalls));
			previousCalls = calls;
		}
	}

	@org.junit.Test(expected = org.apache.commons.math.ode.DerivativeException.class)
	public void exceedMaxEvaluations() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
		double range = (pb.getFinalTime()) - (pb.getInitialTime());
		org.apache.commons.math.ode.nonstiff.AdamsMoultonIntegrator integ = new org.apache.commons.math.ode.nonstiff.AdamsMoultonIntegrator(2 , 0 , range , 1.0E-12 , 1.0E-12);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		integ.setMaxEvaluations(650);
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
	}

	@org.junit.Test
	public void backward() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem5 pb = new org.apache.commons.math.ode.TestProblem5();
		double range = java.lang.Math.abs(((pb.getFinalTime()) - (pb.getInitialTime())));
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.AdamsMoultonIntegrator(4 , 0 , range , 1.0E-12 , 1.0E-12);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		org.junit.Assert.assertTrue(((handler.getLastError()) < 1.0E-9));
		org.junit.Assert.assertTrue(((handler.getMaximalValueError()) < 1.0E-9));
		org.junit.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-16);
		org.junit.Assert.assertEquals("Adams-Moulton", integ.getName());
	}

	@org.junit.Test
	public void polynomial() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem6 pb = new org.apache.commons.math.ode.TestProblem6();
		double range = java.lang.Math.abs(((pb.getFinalTime()) - (pb.getInitialTime())));
		for (int nSteps = 1 ; nSteps < 7 ; ++nSteps) {
			org.apache.commons.math.ode.nonstiff.AdamsMoultonIntegrator integ = new org.apache.commons.math.ode.nonstiff.AdamsMoultonIntegrator(nSteps , (1.0E-6 * range) , (0.1 * range) , 1.0E-9 , 1.0E-9);
			org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
			integ.addStepHandler(handler);
			integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
			if (nSteps < 4) {
				org.junit.Assert.assertTrue(((integ.getEvaluations()) > 140));
			} else {
				org.junit.Assert.assertTrue(((integ.getEvaluations()) < 90));
			}
		}
	}
}

