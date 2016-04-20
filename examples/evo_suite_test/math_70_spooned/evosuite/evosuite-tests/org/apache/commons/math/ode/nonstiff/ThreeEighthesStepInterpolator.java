package org.apache.commons.math.ode.nonstiff;


class ThreeEighthesStepInterpolator extends org.apache.commons.math.ode.nonstiff.RungeKuttaStepInterpolator {
	private static final long serialVersionUID = -3345024435978721931L;

	public ThreeEighthesStepInterpolator() {
	}

	public ThreeEighthesStepInterpolator(final org.apache.commons.math.ode.nonstiff.ThreeEighthesStepInterpolator interpolator) {
		super(interpolator);
	}

	@java.lang.Override
	protected org.apache.commons.math.ode.sampling.StepInterpolator doCopy() {
		return new org.apache.commons.math.ode.nonstiff.ThreeEighthesStepInterpolator(this);
	}

	@java.lang.Override
	protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) throws org.apache.commons.math.ode.DerivativeException {
		final double fourTheta2 = (4 * theta) * theta;
		final double s = oneMinusThetaH / 8.0;
		final double coeff1 = s * ((1 - (7 * theta)) + (2 * fourTheta2));
		final double coeff2 = (3 * s) * ((1 + theta) - fourTheta2);
		final double coeff3 = (3 * s) * (1 + theta);
		final double coeff4 = s * ((1 + theta) + fourTheta2);
		final double coeffDot3 = 0.75 * theta;
		final double coeffDot1 = (coeffDot3 * ((4 * theta) - 5)) + 1;
		final double coeffDot2 = coeffDot3 * (5 - (6 * theta));
		final double coeffDot4 = coeffDot3 * ((2 * theta) - 1);
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

