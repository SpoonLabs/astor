package org.apache.commons.math.ode;


public interface FirstOrderIntegrator extends org.apache.commons.math.ode.ODEIntegrator {
	double integrate(org.apache.commons.math.ode.FirstOrderDifferentialEquations equations, double t0, double[] y0, double t, double[] y) throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException;
}

