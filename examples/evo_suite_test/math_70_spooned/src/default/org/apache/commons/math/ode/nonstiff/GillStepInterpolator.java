package org.apache.commons.math.ode.nonstiff;


class GillStepInterpolator extends org.apache.commons.math.ode.nonstiff.RungeKuttaStepInterpolator {
	private static final double TWO_MINUS_SQRT_2 = 2 - (java.lang.Math.sqrt(2.0));

	private static final double TWO_PLUS_SQRT_2 = 2 + (java.lang.Math.sqrt(2.0));

	private static final long serialVersionUID = -107804074496313322L;

	public GillStepInterpolator() {
	}

	public GillStepInterpolator(final org.apache.commons.math.ode.nonstiff.GillStepInterpolator interpolator) {
		super(interpolator);
	}

	@java.lang.Override
	protected org.apache.commons.math.ode.sampling.StepInterpolator doCopy() {
		return new org.apache.commons.math.ode.nonstiff.GillStepInterpolator(this);
	}

	@java.lang.Override
	protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) throws org.apache.commons.math.ode.DerivativeException {
		final double twoTheta = 2 * theta;
		final double fourTheta = 4 * theta;
		final double s = oneMinusThetaH / 6.0;
		final double oMt = 1 - theta;
		final double soMt = s * oMt;
		final double c23 = soMt * (1 + twoTheta);
		final double coeff1 = soMt * (1 - fourTheta);
		final double coeff2 = c23 * (TWO_MINUS_SQRT_2);
		final double coeff3 = c23 * (TWO_PLUS_SQRT_2);
		final double coeff4 = s * (1 + (theta * (1 + fourTheta)));
		final double coeffDot1 = (theta * (twoTheta - 3)) + 1;
		final double cDot23 = theta * oMt;
		final double coeffDot2 = cDot23 * (TWO_MINUS_SQRT_2);
		final double coeffDot3 = cDot23 * (TWO_PLUS_SQRT_2);
		final double coeffDot4 = theta * (twoTheta - 1);
		for (int i = 0 ; i < (interpolatedState.length) ; ++i) {
			final double yDot1 = yDotK[0][i];
			final double yDot2 = yDotK[1][i];
			final double yDot3 = yDotK[2][i];
			final double yDot4 = yDotK[3][i];
			interpolatedState[i] = ((((currentState[i]) - (coeff1 * yDot1)) - (coeff2 * yDot2)) - (coeff3 * yDot3)) - (coeff4 * yDot4);
			interpolatedDerivatives[i] = (((coeffDot1 * yDot1) + (coeffDot2 * yDot2)) + (coeffDot3 * yDot3)) + (coeffDot4 * yDot4);
		}
	}
}

