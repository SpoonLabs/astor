package org.apache.commons.math.ode.nonstiff;


public class DormandPrince54Integrator extends org.apache.commons.math.ode.nonstiff.EmbeddedRungeKuttaIntegrator {
	private static final java.lang.String METHOD_NAME = "Dormand-Prince 5(4)";

	private static final double[] STATIC_C = new double[]{ 1.0 / 5.0 , 3.0 / 10.0 , 4.0 / 5.0 , 8.0 / 9.0 , 1.0 , 1.0 };

	private static final double[][] STATIC_A = new double[][]{ new double[]{ 1.0 / 5.0 } , new double[]{ 3.0 / 40.0 , 9.0 / 40.0 } , new double[]{ 44.0 / 45.0 , (-56.0) / 15.0 , 32.0 / 9.0 } , new double[]{ 19372.0 / 6561.0 , (-25360.0) / 2187.0 , 64448.0 / 6561.0 , (-212.0) / 729.0 } , new double[]{ 9017.0 / 3168.0 , (-355.0) / 33.0 , 46732.0 / 5247.0 , 49.0 / 176.0 , (-5103.0) / 18656.0 } , new double[]{ 35.0 / 384.0 , 0.0 , 500.0 / 1113.0 , 125.0 / 192.0 , (-2187.0) / 6784.0 , 11.0 / 84.0 } };

	private static final double[] STATIC_B = new double[]{ 35.0 / 384.0 , 0.0 , 500.0 / 1113.0 , 125.0 / 192.0 , (-2187.0) / 6784.0 , 11.0 / 84.0 , 0.0 };

	private static final double E1 = 71.0 / 57600.0;

	private static final double E3 = (-71.0) / 16695.0;

	private static final double E4 = 71.0 / 1920.0;

	private static final double E5 = (-17253.0) / 339200.0;

	private static final double E6 = 22.0 / 525.0;

	private static final double E7 = (-1.0) / 40.0;

	public DormandPrince54Integrator(final double minStep ,final double maxStep ,final double scalAbsoluteTolerance ,final double scalRelativeTolerance) {
		super(METHOD_NAME, true, STATIC_C, STATIC_A, STATIC_B, new org.apache.commons.math.ode.nonstiff.DormandPrince54StepInterpolator(), minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
	}

	public DormandPrince54Integrator(final double minStep ,final double maxStep ,final double[] vecAbsoluteTolerance ,final double[] vecRelativeTolerance) {
		super(METHOD_NAME, true, STATIC_C, STATIC_A, STATIC_B, new org.apache.commons.math.ode.nonstiff.DormandPrince54StepInterpolator(), minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
	}

	@java.lang.Override
	public int getOrder() {
		return 5;
	}

	@java.lang.Override
	protected double estimateError(final double[][] yDotK, final double[] y0, final double[] y1, final double h) {
		double error = 0;
		for (int j = 0 ; j < (y0.length) ; ++j) {
			final double errSum = ((((((E1) * (yDotK[0][j])) + ((E3) * (yDotK[2][j]))) + ((E4) * (yDotK[3][j]))) + ((E5) * (yDotK[4][j]))) + ((E6) * (yDotK[5][j]))) + ((E7) * (yDotK[6][j]));
			final double yScale = java.lang.Math.max(java.lang.Math.abs(y0[j]), java.lang.Math.abs(y1[j]));
			final double tol = (vecAbsoluteTolerance) == null ? (scalAbsoluteTolerance) + ((scalRelativeTolerance) * yScale) : (vecAbsoluteTolerance[j]) + ((vecRelativeTolerance[j]) * yScale);
			final double ratio = (h * errSum) / tol;
			error += ratio * ratio;
		}
		return java.lang.Math.sqrt((error / (y0.length)));
	}
}

