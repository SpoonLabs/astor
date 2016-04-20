package org.apache.commons.math.ode.jacobians;


public interface ParameterizedODE extends org.apache.commons.math.ode.FirstOrderDifferentialEquations {
	int getParametersDimension();

	void setParameter(int i, double value);
}

