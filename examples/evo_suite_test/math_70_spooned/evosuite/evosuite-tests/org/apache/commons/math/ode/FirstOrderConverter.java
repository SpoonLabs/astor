package org.apache.commons.math.ode;


public class FirstOrderConverter implements org.apache.commons.math.ode.FirstOrderDifferentialEquations {
	private final org.apache.commons.math.ode.SecondOrderDifferentialEquations equations;

	private final int dimension;

	private final double[] z;

	private final double[] zDot;

	private final double[] zDDot;

	public FirstOrderConverter(final org.apache.commons.math.ode.SecondOrderDifferentialEquations equations) {
		this.equations = equations;
		dimension = equations.getDimension();
		z = new double[dimension];
		zDot = new double[dimension];
		zDDot = new double[dimension];
	}

	public int getDimension() {
		return 2 * (dimension);
	}

	public void computeDerivatives(final double t, final double[] y, final double[] yDot) throws org.apache.commons.math.ode.DerivativeException {
		java.lang.System.arraycopy(y, 0, z, 0, dimension);
		java.lang.System.arraycopy(y, dimension, zDot, 0, dimension);
		equations.computeSecondDerivatives(t, z, zDot, zDDot);
		java.lang.System.arraycopy(zDot, 0, yDot, 0, dimension);
		java.lang.System.arraycopy(zDDot, 0, yDot, dimension, dimension);
	}
}

