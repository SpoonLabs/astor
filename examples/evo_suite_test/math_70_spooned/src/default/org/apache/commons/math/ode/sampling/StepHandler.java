package org.apache.commons.math.ode.sampling;


public interface StepHandler {
	boolean requiresDenseOutput();

	void reset();

	void handleStep(org.apache.commons.math.ode.sampling.StepInterpolator interpolator, boolean isLast) throws org.apache.commons.math.ode.DerivativeException;
}

