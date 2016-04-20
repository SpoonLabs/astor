package org.apache.commons.math.ode.nonstiff;


class EulerStepInterpolator extends org.apache.commons.math.ode.nonstiff.RungeKuttaStepInterpolator {
	private static final long serialVersionUID = -7179861704951334960L;

	public EulerStepInterpolator() {
	}

	public EulerStepInterpolator(final org.apache.commons.math.ode.nonstiff.EulerStepInterpolator interpolator) {
		super(interpolator);
	}

	@java.lang.Override
	protected org.apache.commons.math.ode.sampling.StepInterpolator doCopy() {
		return new org.apache.commons.math.ode.nonstiff.EulerStepInterpolator(this);
	}

	@java.lang.Override
	protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) throws org.apache.commons.math.ode.DerivativeException {
		for (int i = 0 ; i < (interpolatedState.length) ; ++i) {
			interpolatedState[i] = (currentState[i]) - (oneMinusThetaH * (yDotK[0][i]));
		}
		java.lang.System.arraycopy(yDotK[0], 0, interpolatedDerivatives, 0, interpolatedDerivatives.length);
	}
}

