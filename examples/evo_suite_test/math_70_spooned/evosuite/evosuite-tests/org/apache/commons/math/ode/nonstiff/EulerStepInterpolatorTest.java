package org.apache.commons.math.ode.nonstiff;


public class EulerStepInterpolatorTest {
	@org.junit.Test
	public void noReset() throws org.apache.commons.math.ode.DerivativeException {
		double[] y = new double[]{ 0.0 , 1.0 , -2.0 };
		double[][] yDot = new double[][]{ new double[]{ 1.0 , 2.0 , -2.0 } };
		org.apache.commons.math.ode.nonstiff.EulerStepInterpolator interpolator = new org.apache.commons.math.ode.nonstiff.EulerStepInterpolator();
		interpolator.reinitialize(new org.apache.commons.math.ode.nonstiff.EulerStepInterpolatorTest.DummyIntegrator(interpolator), y, yDot, true);
		interpolator.storeTime(0);
		interpolator.shift();
		interpolator.storeTime(1);
		double[] result = interpolator.getInterpolatedState();
		for (int i = 0 ; i < (result.length) ; ++i) {
			org.junit.Assert.assertTrue(((java.lang.Math.abs(((result[i]) - (y[i])))) < 1.0E-10));
		}
	}

	@org.junit.Test
	public void interpolationAtBounds() throws org.apache.commons.math.ode.DerivativeException {
		double t0 = 0;
		double[] y0 = new double[]{ 0.0 , 1.0 , -2.0 };
		double[] y = y0.clone();
		double[][] yDot = new double[][]{ new double[y0.length] };
		org.apache.commons.math.ode.nonstiff.EulerStepInterpolator interpolator = new org.apache.commons.math.ode.nonstiff.EulerStepInterpolator();
		interpolator.reinitialize(new org.apache.commons.math.ode.nonstiff.EulerStepInterpolatorTest.DummyIntegrator(interpolator), y, yDot, true);
		interpolator.storeTime(t0);
		double dt = 1.0;
		y[0] = 1.0;
		y[1] = 3.0;
		y[2] = -4.0;
		yDot[0][0] = ((y[0]) - (y0[0])) / dt;
		yDot[0][1] = ((y[1]) - (y0[1])) / dt;
		yDot[0][2] = ((y[2]) - (y0[2])) / dt;
		interpolator.shift();
		interpolator.storeTime((t0 + dt));
		interpolator.setInterpolatedTime(interpolator.getPreviousTime());
		double[] result = interpolator.getInterpolatedState();
		for (int i = 0 ; i < (result.length) ; ++i) {
			org.junit.Assert.assertTrue(((java.lang.Math.abs(((result[i]) - (y0[i])))) < 1.0E-10));
		}
		interpolator.setInterpolatedTime(interpolator.getCurrentTime());
		result = interpolator.getInterpolatedState();
		for (int i = 0 ; i < (result.length) ; ++i) {
			org.junit.Assert.assertTrue(((java.lang.Math.abs(((result[i]) - (y[i])))) < 1.0E-10));
		}
	}

	@org.junit.Test
	public void interpolationInside() throws org.apache.commons.math.ode.DerivativeException {
		double[] y = new double[]{ 1.0 , 3.0 , -4.0 };
		double[][] yDot = new double[][]{ new double[]{ 1.0 , 2.0 , -2.0 } };
		org.apache.commons.math.ode.nonstiff.EulerStepInterpolator interpolator = new org.apache.commons.math.ode.nonstiff.EulerStepInterpolator();
		interpolator.reinitialize(new org.apache.commons.math.ode.nonstiff.EulerStepInterpolatorTest.DummyIntegrator(interpolator), y, yDot, true);
		interpolator.storeTime(0);
		interpolator.shift();
		interpolator.storeTime(1);
		interpolator.setInterpolatedTime(0.1);
		double[] result = interpolator.getInterpolatedState();
		org.junit.Assert.assertTrue(((java.lang.Math.abs(((result[0]) - 0.1))) < 1.0E-10));
		org.junit.Assert.assertTrue(((java.lang.Math.abs(((result[1]) - 1.2))) < 1.0E-10));
		org.junit.Assert.assertTrue(((java.lang.Math.abs(((result[2]) + 2.2))) < 1.0E-10));
		interpolator.setInterpolatedTime(0.5);
		result = interpolator.getInterpolatedState();
		org.junit.Assert.assertTrue(((java.lang.Math.abs(((result[0]) - 0.5))) < 1.0E-10));
		org.junit.Assert.assertTrue(((java.lang.Math.abs(((result[1]) - 2.0))) < 1.0E-10));
		org.junit.Assert.assertTrue(((java.lang.Math.abs(((result[2]) + 3.0))) < 1.0E-10));
	}

	@org.junit.Test
	public void derivativesConsistency() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem3 pb = new org.apache.commons.math.ode.TestProblem3();
		double step = ((pb.getFinalTime()) - (pb.getInitialTime())) * 0.001;
		org.apache.commons.math.ode.nonstiff.EulerIntegrator integ = new org.apache.commons.math.ode.nonstiff.EulerIntegrator(step);
		org.apache.commons.math.ode.sampling.StepInterpolatorTestUtils.checkDerivativesConsistency(integ, pb, 1.0E-10);
	}

	@org.junit.Test
	public void serialization() throws java.io.IOException, java.lang.ClassNotFoundException, org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem1 pb = new org.apache.commons.math.ode.TestProblem1();
		double step = ((pb.getFinalTime()) - (pb.getInitialTime())) * 0.001;
		org.apache.commons.math.ode.nonstiff.EulerIntegrator integ = new org.apache.commons.math.ode.nonstiff.EulerIntegrator(step);
		integ.addStepHandler(new org.apache.commons.math.ode.ContinuousOutputModel());
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
		java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bos);
		for (org.apache.commons.math.ode.sampling.StepHandler handler : integ.getStepHandlers()) {
			oos.writeObject(handler);
		}
		java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(bos.toByteArray());
		java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bis);
		org.apache.commons.math.ode.ContinuousOutputModel cm = ((org.apache.commons.math.ode.ContinuousOutputModel)(ois.readObject()));
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
		org.junit.Assert.assertTrue((maxError < 0.001));
	}

	private static class DummyIntegrator extends org.apache.commons.math.ode.nonstiff.RungeKuttaIntegrator {
		protected DummyIntegrator(org.apache.commons.math.ode.nonstiff.RungeKuttaStepInterpolator prototype) {
			super("dummy", new double[0], new double[0][0], new double[0], prototype, java.lang.Double.NaN);
		}
	}
}

