package org.apache.commons.math.ode.jacobians;


public interface EventHandlerWithJacobians {
	int STOP = 0;

	int RESET_STATE = 1;

	int RESET_DERIVATIVES = 2;

	int CONTINUE = 3;

	double g(double t, double[] y, double[][] dydy0, double[][] dydp) throws org.apache.commons.math.ode.events.EventException;

	int eventOccurred(double t, double[] y, double[][] dydy0, double[][] dydp, boolean increasing) throws org.apache.commons.math.ode.events.EventException;

	void resetState(double t, double[] y, double[][] dydy0, double[][] dydp) throws org.apache.commons.math.ode.events.EventException;
}

