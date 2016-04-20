package org.apache.commons.math.ode.nonstiff;


class DormandPrince54StepInterpolator extends org.apache.commons.math.ode.nonstiff.RungeKuttaStepInterpolator {
	private static final double A70 = 35.0 / 384.0;

	private static final double A72 = 500.0 / 1113.0;

	private static final double A73 = 125.0 / 192.0;

	private static final double A74 = (-2187.0) / 6784.0;

	private static final double A75 = 11.0 / 84.0;

	private static final double D0 = (-1.2715105075E10) / 1.1282082432E10;

	private static final double D2 = 8.74874797E10 / 3.2700410799E10;

	private static final double D3 = (-1.0690763975E10) / 1.880347072E9;

	private static final double D4 = 7.01980252875E11 / 1.99316789632E11;

	private static final double D5 = (-1.453857185E9) / 8.22651844E8;

	private static final double D6 = 6.9997945E7 / 2.9380423E7;

	private static final long serialVersionUID = 4104157279605906956L;

	private double[] v1;

	private double[] v2;

	private double[] v3;

	private double[] v4;

	private boolean vectorsInitialized;

	public DormandPrince54StepInterpolator() {
		super();
		v1 = null;
		v2 = null;
		v3 = null;
		v4 = null;
		vectorsInitialized = false;
	}

	public DormandPrince54StepInterpolator(final org.apache.commons.math.ode.nonstiff.DormandPrince54StepInterpolator interpolator) {
		super(interpolator);
		if ((interpolator.v1) == null) {
			v1 = null;
			v2 = null;
			v3 = null;
			v4 = null;
			vectorsInitialized = false;
		} else {
			v1 = interpolator.v1.clone();
			v2 = interpolator.v2.clone();
			v3 = interpolator.v3.clone();
			v4 = interpolator.v4.clone();
			vectorsInitialized = interpolator.vectorsInitialized;
		}
	}

	@java.lang.Override
	protected org.apache.commons.math.ode.sampling.StepInterpolator doCopy() {
		return new org.apache.commons.math.ode.nonstiff.DormandPrince54StepInterpolator(this);
	}

	@java.lang.Override
	public void reinitialize(final org.apache.commons.math.ode.AbstractIntegrator integrator, final double[] y, final double[][] yDotK, final boolean forward) {
		super.reinitialize(integrator, y, yDotK, forward);
		v1 = null;
		v2 = null;
		v3 = null;
		v4 = null;
		vectorsInitialized = false;
	}

	@java.lang.Override
	public void storeTime(final double t) {
		super.storeTime(t);
		vectorsInitialized = false;
	}

	@java.lang.Override
	protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) throws org.apache.commons.math.ode.DerivativeException {
		if (!(vectorsInitialized)) {
			if ((v1) == null) {
				v1 = new double[interpolatedState.length];
				v2 = new double[interpolatedState.length];
				v3 = new double[interpolatedState.length];
				v4 = new double[interpolatedState.length];
			} 
			for (int i = 0 ; i < (interpolatedState.length) ; ++i) {
				final double yDot0 = yDotK[0][i];
				final double yDot2 = yDotK[2][i];
				final double yDot3 = yDotK[3][i];
				final double yDot4 = yDotK[4][i];
				final double yDot5 = yDotK[5][i];
				final double yDot6 = yDotK[6][i];
				v1[i] = (((((A70) * yDot0) + ((A72) * yDot2)) + ((A73) * yDot3)) + ((A74) * yDot4)) + ((A75) * yDot5);
				v2[i] = yDot0 - (v1[i]);
				v3[i] = ((v1[i]) - (v2[i])) - yDot6;
				v4[i] = ((((((D0) * yDot0) + ((D2) * yDot2)) + ((D3) * yDot3)) + ((D4) * yDot4)) + ((D5) * yDot5)) + ((D6) * yDot6);
			}
			vectorsInitialized = true;
		} 
		final double eta = 1 - theta;
		final double twoTheta = 2 * theta;
		final double dot2 = 1 - twoTheta;
		final double dot3 = theta * (2 - (3 * theta));
		final double dot4 = twoTheta * (1 + (theta * (twoTheta - 3)));
		for (int i = 0 ; i < (interpolatedState.length) ; ++i) {
			interpolatedState[i] = (currentState[i]) - (oneMinusThetaH * ((v1[i]) - (theta * ((v2[i]) + (theta * ((v3[i]) + (eta * (v4[i]))))))));
			interpolatedDerivatives[i] = (((v1[i]) + (dot2 * (v2[i]))) + (dot3 * (v3[i]))) + (dot4 * (v4[i]));
		}
	}
}

