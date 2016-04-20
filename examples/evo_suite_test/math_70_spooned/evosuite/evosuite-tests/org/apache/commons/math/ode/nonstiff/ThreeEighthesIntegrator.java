package org.apache.commons.math.ode.nonstiff;


public class ThreeEighthesIntegrator extends org.apache.commons.math.ode.nonstiff.RungeKuttaIntegrator {
	private static final double[] STATIC_C = new double[]{ 1.0 / 3.0 , 2.0 / 3.0 , 1.0 };

	private static final double[][] STATIC_A = new double[][]{ new double[]{ 1.0 / 3.0 } , new double[]{ (-1.0) / 3.0 , 1.0 } , new double[]{ 1.0 , -1.0 , 1.0 } };

	private static final double[] STATIC_B = new double[]{ 1.0 / 8.0 , 3.0 / 8.0 , 3.0 / 8.0 , 1.0 / 8.0 };

	public ThreeEighthesIntegrator(final double step) {
		super("3/8", STATIC_C, STATIC_A, STATIC_B, new org.apache.commons.math.ode.nonstiff.ThreeEighthesStepInterpolator(), step);
	}
}

