package org.apache.commons.math.ode.nonstiff;


class HighamHall54StepInterpolator extends org.apache.commons.math.ode.nonstiff.RungeKuttaStepInterpolator {
	private static final long serialVersionUID = -3583240427587318654L;

	public HighamHall54StepInterpolator() {
		super();
	}

	public HighamHall54StepInterpolator(final org.apache.commons.math.ode.nonstiff.HighamHall54StepInterpolator interpolator) {
		super(interpolator);
	}

	@java.lang.Override
	protected org.apache.commons.math.ode.sampling.StepInterpolator doCopy() {
		return new org.apache.commons.math.ode.nonstiff.HighamHall54StepInterpolator(this);
	}

	@java.lang.Override
	protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) throws org.apache.commons.math.ode.DerivativeException {
		final double theta2 = theta * theta;
		final double b0 = (h) * (((-1.0) / 12.0) + (theta * (1.0 + (theta * (((-15.0) / 4.0) + (theta * ((16.0 / 3.0) + ((theta * (-5.0)) / 2.0))))))));
		final double b2 = (h) * (((-27.0) / 32.0) + (theta2 * ((459.0 / 32.0) + (theta * (((-243.0) / 8.0) + ((theta * 135.0) / 8.0))))));
		final double b3 = (h) * ((4.0 / 3.0) + (theta2 * ((-22.0) + (theta * ((152.0 / 3.0) + (theta * (-30.0)))))));
		final double b4 = (h) * (((-125.0) / 96.0) + (theta2 * ((375.0 / 32.0) + (theta * (((-625.0) / 24.0) + ((theta * 125.0) / 8.0))))));
		final double b5 = (h) * (((-5.0) / 48.0) + (theta2 * (((-5.0) / 16.0) + ((theta * 5.0) / 12.0))));
		final double bDot0 = 1 + (theta * (((-15.0) / 2.0) + (theta * (16.0 - (10.0 * theta)))));
		final double bDot2 = theta * ((459.0 / 16.0) + (theta * (((-729.0) / 8.0) + ((135.0 / 2.0) * theta))));
		final double bDot3 = theta * ((-44.0) + (theta * (152.0 - (120.0 * theta))));
		final double bDot4 = theta * ((375.0 / 16.0) + (theta * (((-625.0) / 8.0) + ((125.0 / 2.0) * theta))));
		final double bDot5 = ((theta * 5.0) / 8.0) * ((2 * theta) - 1);
		for (int i = 0 ; i < (interpolatedState.length) ; ++i) {
			final double yDot0 = yDotK[0][i];
			final double yDot2 = yDotK[2][i];
			final double yDot3 = yDotK[3][i];
			final double yDot4 = yDotK[4][i];
			final double yDot5 = yDotK[5][i];
			interpolatedState[i] = (((((currentState[i]) + (b0 * yDot0)) + (b2 * yDot2)) + (b3 * yDot3)) + (b4 * yDot4)) + (b5 * yDot5);
			interpolatedDerivatives[i] = ((((bDot0 * yDot0) + (bDot2 * yDot2)) + (bDot3 * yDot3)) + (bDot4 * yDot4)) + (bDot5 * yDot5);
		}
	}
}

