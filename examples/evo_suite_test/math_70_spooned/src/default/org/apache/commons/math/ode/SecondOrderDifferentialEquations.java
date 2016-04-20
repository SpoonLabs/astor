package org.apache.commons.math.ode;


public interface SecondOrderDifferentialEquations {
	int getDimension();

	void computeSecondDerivatives(double t, double[] y, double[] yDot, double[] yDDot) throws org.apache.commons.math.ode.DerivativeException;
}

