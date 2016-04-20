package org.apache.commons.math.ode.nonstiff;


public class HighamHall54Integrator extends org.apache.commons.math.ode.nonstiff.EmbeddedRungeKuttaIntegrator {
	private static final java.lang.String METHOD_NAME = "Higham-Hall 5(4)";

	private static final double[] STATIC_C = new double[]{ 2.0 / 9.0 , 1.0 / 3.0 , 1.0 / 2.0 , 3.0 / 5.0 , 1.0 , 1.0 };

	private static final double[][] STATIC_A = new double[][]{ new double[]{ 2.0 / 9.0 } , new double[]{ 1.0 / 12.0 , 1.0 / 4.0 } , new double[]{ 1.0 / 8.0 , 0.0 , 3.0 / 8.0 } , new double[]{ 91.0 / 500.0 , (-27.0) / 100.0 , 78.0 / 125.0 , 8.0 / 125.0 } , new double[]{ (-11.0) / 20.0 , 27.0 / 20.0 , 12.0 / 5.0 , (-36.0) / 5.0 , 5.0 } , new double[]{ 1.0 / 12.0 , 0.0 , 27.0 / 32.0 , (-4.0) / 3.0 , 125.0 / 96.0 , 5.0 / 48.0 } };

	private static final double[] STATIC_B = new double[]{ 1.0 / 12.0 , 0.0 , 27.0 / 32.0 , (-4.0) / 3.0 , 125.0 / 96.0 , 5.0 / 48.0 , 0.0 };

	private static final double[] STATIC_E = new double[]{ (-1.0) / 20.0 , 0.0 , 81.0 / 160.0 , (-6.0) / 5.0 , 25.0 / 32.0 , 1.0 / 16.0 , (-1.0) / 10.0 };

	public HighamHall54Integrator(final double minStep ,final double maxStep ,final double scalAbsoluteTolerance ,final double scalRelativeTolerance) {
		super(METHOD_NAME, false, STATIC_C, STATIC_A, STATIC_B, new org.apache.commons.math.ode.nonstiff.HighamHall54StepInterpolator(), minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
	}

	public HighamHall54Integrator(final double minStep ,final double maxStep ,final double[] vecAbsoluteTolerance ,final double[] vecRelativeTolerance) {
		super(METHOD_NAME, false, STATIC_C, STATIC_A, STATIC_B, new org.apache.commons.math.ode.nonstiff.HighamHall54StepInterpolator(), minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
	}

	@java.lang.Override
	public int getOrder() {
		return 5;
	}

	@java.lang.Override
	protected double estimateError(final double[][] yDotK, final double[] y0, final double[] y1, final double h) {
		double error = 0;
		for (int j = 0 ; j < (y0.length) ; ++j) {
			double errSum = (STATIC_E[0]) * (yDotK[0][j]);
			for (int l = 1 ; l < (STATIC_E.length) ; ++l) {
				errSum += (STATIC_E[l]) * (yDotK[l][j]);
			}
			final double yScale = java.lang.Math.max(java.lang.Math.abs(y0[j]), java.lang.Math.abs(y1[j]));
			final double tol = (vecAbsoluteTolerance) == null ? (scalAbsoluteTolerance) + ((scalRelativeTolerance) * yScale) : (vecAbsoluteTolerance[j]) + ((vecRelativeTolerance[j]) * yScale);
			final double ratio = (h * errSum) / tol;
			error += ratio * ratio;
		}
		return java.lang.Math.sqrt((error / (y0.length)));
	}
}

