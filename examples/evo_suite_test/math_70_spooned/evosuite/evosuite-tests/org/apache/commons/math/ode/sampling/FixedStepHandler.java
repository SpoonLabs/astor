package org.apache.commons.math.ode.sampling;


public interface FixedStepHandler {
	void handleStep(double t, double[] y, double[] yDot, boolean isLast) throws org.apache.commons.math.ode.DerivativeException;
}

