package org.apache.commons.math.ode.nonstiff;


public class MidpointIntegratorTest extends junit.framework.TestCase {
	public MidpointIntegratorTest(java.lang.String name) {
		super(name);
	}

	public void testDimensionCheck() {
		try {
			org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
			new org.apache.commons.math.ode.nonstiff.MidpointIntegrator(0.01).integrate(pb, 0.0, new double[(pb.getDimension()) + 10], 1.0, new double[(pb.getDimension()) + 10]);
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
				org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.MidpointIntegrator(step);
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
				if (i > 4) {
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
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.MidpointIntegrator(step);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertTrue(((handler.getLastError()) < 2.0E-7));
		junit.framework.Assert.assertTrue(((handler.getMaximalValueError()) < 1.0E-6));
		junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
		junit.framework.Assert.assertEquals("midpoint", integ.getName());
	}

	public void testBigStep() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
		double step = ((pb.getFinalTime()) - (pb.getInitialTime())) * 0.2;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.MidpointIntegrator(step);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertTrue(((handler.getLastError()) > 0.01));
		junit.framework.Assert.assertTrue(((handler.getMaximalValueError()) > 0.05));
		junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
	}

	public void testBackward() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem5 pb = new org.apache.commons.math.ode.TestProblem5();
		double step = (java.lang.Math.abs(((pb.getFinalTime()) - (pb.getInitialTime())))) * 0.001;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.MidpointIntegrator(step);
		org.apache.commons.math.ode.TestProblemHandler handler = new org.apache.commons.math.ode.TestProblemHandler(pb , integ);
		integ.addStepHandler(handler);
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		junit.framework.Assert.assertTrue(((handler.getLastError()) < 6.0E-4));
		junit.framework.Assert.assertTrue(((handler.getMaximalValueError()) < 6.0E-4));
		junit.framework.Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0E-12);
		junit.framework.Assert.assertEquals("midpoint", integ.getName());
	}

	public void testStepSize() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		final double step = 1.23456;
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.MidpointIntegrator(step);
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

