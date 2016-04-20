package org.apache.commons.math.ode.nonstiff;


class MidpointStepInterpolator extends org.apache.commons.math.ode.nonstiff.RungeKuttaStepInterpolator {
	private static final long serialVersionUID = -865524111506042509L;

	public MidpointStepInterpolator() {
	}

	public MidpointStepInterpolator(final org.apache.commons.math.ode.nonstiff.MidpointStepInterpolator interpolator) {
		super(interpolator);
	}

	@java.lang.Override
	protected org.apache.commons.math.ode.sampling.StepInterpolator doCopy() {
		return new org.apache.commons.math.ode.nonstiff.MidpointStepInterpolator(this);
	}

	@java.lang.Override
	protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) throws org.apache.commons.math.ode.DerivativeException {
		final double coeff1 = oneMinusThetaH * theta;
		final double coeff2 = oneMinusThetaH * (1.0 + theta);
		final double coeffDot2 = 2 * theta;
		final double coeffDot1 = 1 - coeffDot2;
		for (int i = 0 ; i < (interpolatedState.length) ; ++i) {
			final double yDot1 = yDotK[0][i];
			final double yDot2 = yDotK[1][i];
			interpolatedState[i] = ((currentState[i]) + (coeff1 * yDot1)) - (coeff2 * yDot2);
			interpolatedDerivatives[i] = (coeffDot1 * yDot1) + (coeffDot2 * yDot2);
		}
	}
}

