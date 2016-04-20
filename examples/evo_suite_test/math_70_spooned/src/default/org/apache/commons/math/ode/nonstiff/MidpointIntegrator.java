package org.apache.commons.math.ode.nonstiff;


public class MidpointIntegrator extends org.apache.commons.math.ode.nonstiff.RungeKuttaIntegrator {
	private static final double[] STATIC_C = new double[]{ 1.0 / 2.0 };

	private static final double[][] STATIC_A = new double[][]{ new double[]{ 1.0 / 2.0 } };

	private static final double[] STATIC_B = new double[]{ 0.0 , 1.0 };

	public MidpointIntegrator(final double step) {
		super("midpoint", STATIC_C, STATIC_A, STATIC_B, new org.apache.commons.math.ode.nonstiff.MidpointStepInterpolator(), step);
	}
}

