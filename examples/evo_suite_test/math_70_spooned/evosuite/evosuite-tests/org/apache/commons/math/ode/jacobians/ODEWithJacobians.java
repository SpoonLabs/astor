package org.apache.commons.math.ode.jacobians;


public interface ODEWithJacobians extends org.apache.commons.math.ode.FirstOrderDifferentialEquations {
	int getParametersDimension();

	void computeJacobians(double t, double[] y, double[] yDot, double[][] dFdY, double[][] dFdP) throws org.apache.commons.math.ode.DerivativeException;
}

