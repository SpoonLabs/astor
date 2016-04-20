package org.apache.commons.math.ode.sampling;


public class DummyStepInterpolator extends org.apache.commons.math.ode.sampling.AbstractStepInterpolator {
	private static final long serialVersionUID = 1708010296707839488L;

	private double[] currentDerivative;

	public DummyStepInterpolator() {
		super();
		currentDerivative = null;
	}

	public DummyStepInterpolator(final double[] y ,final double[] yDot ,final boolean forward) {
		super(y, forward);
		currentDerivative = yDot;
	}

	public DummyStepInterpolator(final org.apache.commons.math.ode.sampling.DummyStepInterpolator interpolator) {
		super(interpolator);
		currentDerivative = interpolator.currentDerivative.clone();
	}

	@java.lang.Override
	protected org.apache.commons.math.ode.sampling.StepInterpolator doCopy() {
		return new org.apache.commons.math.ode.sampling.DummyStepInterpolator(this);
	}

	@java.lang.Override
	protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) throws org.apache.commons.math.ode.DerivativeException {
		java.lang.System.arraycopy(currentState, 0, interpolatedState, 0, currentState.length);
		java.lang.System.arraycopy(currentDerivative, 0, interpolatedDerivatives, 0, currentDerivative.length);
	}

	@java.lang.Override
	public void writeExternal(final java.io.ObjectOutput out) throws java.io.IOException {
		writeBaseExternal(out);
		if ((currentDerivative) != null) {
			for (int i = 0 ; i < (currentDerivative.length) ; ++i) {
				out.writeDouble(currentDerivative[i]);
			}
		} 
	}

	@java.lang.Override
	public void readExternal(final java.io.ObjectInput in) throws java.io.IOException {
		final double t = readBaseExternal(in);
		if ((currentState) == null) {
			currentDerivative = null;
		} else {
			currentDerivative = new double[currentState.length];
			for (int i = 0 ; i < (currentDerivative.length) ; ++i) {
				currentDerivative[i] = in.readDouble();
			}
		}
		setInterpolatedTime(t);
	}
}

