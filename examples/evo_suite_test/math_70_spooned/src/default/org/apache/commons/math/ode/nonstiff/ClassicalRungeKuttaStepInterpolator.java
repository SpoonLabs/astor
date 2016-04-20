package org.apache.commons.math.ode.nonstiff;


class ClassicalRungeKuttaStepInterpolator extends org.apache.commons.math.ode.nonstiff.RungeKuttaStepInterpolator {
	private static final long serialVersionUID = -6576285612589783992L;

	public ClassicalRungeKuttaStepInterpolator() {
	}

	public ClassicalRungeKuttaStepInterpolator(final org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaStepInterpolator interpolator) {
		super(interpolator);
	}

	@java.lang.Override
	protected org.apache.commons.math.ode.sampling.StepInterpolator doCopy() {
		return new org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaStepInterpolator(this);
	}

	@java.lang.Override
	protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) throws org.apache.commons.math.ode.DerivativeException {
		final double fourTheta = 4 * theta;
		final double oneMinusTheta = 1 - theta;
		final double oneMinus2Theta = 1 - (2 * theta);
		final double s = oneMinusThetaH / 6.0;
		final double coeff1 = s * ((((-fourTheta) + 5) * theta) - 1);
		final double coeff23 = s * (((fourTheta - 2) * theta) - 2);
		final double coeff4 = s * ((((-fourTheta) - 1) * theta) - 1);
		final double coeffDot1 = oneMinusTheta * oneMinus2Theta;
		final double coeffDot23 = (2 * theta) * oneMinusTheta;
		final double coeffDot4 = (-theta) * oneMinus2Theta;
		for (int i = 0 ; i < (interpolatedState.length) ; ++i) {
			final double yDot1 = yDotK[0][i];
			final double yDot23 = (yDotK[1][i]) + (yDotK[2][i]);
			final double yDot4 = yDotK[3][i];
			interpolatedState[i] = (((currentState[i]) + (coeff1 * yDot1)) + (coeff23 * yDot23)) + (coeff4 * yDot4);
			interpolatedDerivatives[i] = ((coeffDot1 * yDot1) + (coeffDot23 * yDot23)) + (coeffDot4 * yDot4);
		}
	}
}

