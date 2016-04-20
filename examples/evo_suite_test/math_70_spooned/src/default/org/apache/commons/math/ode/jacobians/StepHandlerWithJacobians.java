package org.apache.commons.math.ode.jacobians;


public interface StepHandlerWithJacobians {
	boolean requiresDenseOutput();

	void reset();

	void handleStep(org.apache.commons.math.ode.jacobians.StepInterpolatorWithJacobians interpolator, boolean isLast) throws org.apache.commons.math.ode.DerivativeException;
}

