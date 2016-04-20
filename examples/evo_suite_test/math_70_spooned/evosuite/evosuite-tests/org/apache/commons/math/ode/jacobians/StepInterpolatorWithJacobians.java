package org.apache.commons.math.ode.jacobians;


public interface StepInterpolatorWithJacobians extends java.io.Externalizable {
	double getPreviousTime();

	double getCurrentTime();

	double getInterpolatedTime();

	void setInterpolatedTime(double time);

	double[] getInterpolatedY() throws org.apache.commons.math.ode.DerivativeException;

	double[][] getInterpolatedDyDy0() throws org.apache.commons.math.ode.DerivativeException;

	double[][] getInterpolatedDyDp() throws org.apache.commons.math.ode.DerivativeException;

	double[] getInterpolatedYDot() throws org.apache.commons.math.ode.DerivativeException;

	double[][] getInterpolatedDyDy0Dot() throws org.apache.commons.math.ode.DerivativeException;

	double[][] getInterpolatedDyDpDot() throws org.apache.commons.math.ode.DerivativeException;

	boolean isForward();

	org.apache.commons.math.ode.jacobians.StepInterpolatorWithJacobians copy() throws org.apache.commons.math.ode.DerivativeException;
}

