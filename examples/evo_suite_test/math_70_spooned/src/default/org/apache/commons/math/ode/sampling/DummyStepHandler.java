package org.apache.commons.math.ode.sampling;


public class DummyStepHandler implements org.apache.commons.math.ode.sampling.StepHandler {
	private DummyStepHandler() {
	}

	public static org.apache.commons.math.ode.sampling.DummyStepHandler getInstance() {
		return org.apache.commons.math.ode.sampling.DummyStepHandler.LazyHolder.INSTANCE;
	}

	public boolean requiresDenseOutput() {
		return false;
	}

	public void reset() {
	}

	public void handleStep(final org.apache.commons.math.ode.sampling.StepInterpolator interpolator, final boolean isLast) {
	}

	private static class LazyHolder {
		private static final org.apache.commons.math.ode.sampling.DummyStepHandler INSTANCE = new org.apache.commons.math.ode.sampling.DummyStepHandler();
	}

	private java.lang.Object readResolve() {
		return org.apache.commons.math.ode.sampling.DummyStepHandler.LazyHolder.INSTANCE;
	}
}

