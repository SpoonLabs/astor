package org.apache.commons.math.ode.nonstiff;


public class GillIntegrator extends org.apache.commons.math.ode.nonstiff.RungeKuttaIntegrator {
	private static final double[] STATIC_C = new double[]{ 1.0 / 2.0 , 1.0 / 2.0 , 1.0 };

	private static final double[][] STATIC_A = new double[][]{ new double[]{ 1.0 / 2.0 } , new double[]{ ((java.lang.Math.sqrt(2.0)) - 1.0) / 2.0 , (2.0 - (java.lang.Math.sqrt(2.0))) / 2.0 } , new double[]{ 0.0 , (-(java.lang.Math.sqrt(2.0))) / 2.0 , (2.0 + (java.lang.Math.sqrt(2.0))) / 2.0 } };

	private static final double[] STATIC_B = new double[]{ 1.0 / 6.0 , (2.0 - (java.lang.Math.sqrt(2.0))) / 6.0 , (2.0 + (java.lang.Math.sqrt(2.0))) / 6.0 , 1.0 / 6.0 };

	public GillIntegrator(final double step) {
		super("Gill", STATIC_C, STATIC_A, STATIC_B, new org.apache.commons.math.ode.nonstiff.GillStepInterpolator(), step);
	}
}

