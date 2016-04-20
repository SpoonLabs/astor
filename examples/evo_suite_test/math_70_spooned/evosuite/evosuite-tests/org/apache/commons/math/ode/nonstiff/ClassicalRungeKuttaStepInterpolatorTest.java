package org.apache.commons.math.ode.nonstiff;


public class ClassicalRungeKuttaStepInterpolatorTest {
	@org.junit.Test
	public void derivativesConsistency() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem3 pb = new org.apache.commons.math.ode.TestProblem3();
		double step = ((pb.getFinalTime()) - (pb.getInitialTime())) * 0.001;
		org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator integ = new org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator(step);
		org.apache.commons.math.ode.sampling.StepInterpolatorTestUtils.checkDerivativesConsistency(integ, pb, 1.0E-10);
	}

	@org.junit.Test
	public void serialization() throws java.io.IOException, java.lang.ClassNotFoundException, org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.TestProblem3 pb = new org.apache.commons.math.ode.TestProblem3(0.9);
		double step = ((pb.getFinalTime()) - (pb.getInitialTime())) * 3.0E-4;
		org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator integ = new org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator(step);
		integ.addStepHandler(new org.apache.commons.math.ode.ContinuousOutputModel());
		integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(), pb.getFinalTime(), new double[pb.getDimension()]);
		java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
		java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bos);
		for (org.apache.commons.math.ode.sampling.StepHandler handler : integ.getStepHandlers()) {
			oos.writeObject(handler);
		}
		org.junit.Assert.assertTrue(((bos.size()) > 700000));
		org.junit.Assert.assertTrue(((bos.size()) < 701000));
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
		org.junit.Assert.assertTrue((maxError > 0.005));
	}
}

