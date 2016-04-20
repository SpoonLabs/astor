package org.apache.commons.math.ode.sampling;


public interface StepInterpolator extends java.io.Externalizable {
	double getPreviousTime();

	double getCurrentTime();

	double getInterpolatedTime();

	void setInterpolatedTime(double time);

	double[] getInterpolatedState() throws org.apache.commons.math.ode.DerivativeException;

	double[] getInterpolatedDerivatives() throws org.apache.commons.math.ode.DerivativeException;

	boolean isForward();

	org.apache.commons.math.ode.sampling.StepInterpolator copy() throws org.apache.commons.math.ode.DerivativeException;
}

