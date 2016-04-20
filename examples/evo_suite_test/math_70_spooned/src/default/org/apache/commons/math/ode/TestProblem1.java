package org.apache.commons.math.ode;


public class TestProblem1 extends org.apache.commons.math.ode.TestProblemAbstract {
	private static final long serialVersionUID = 1977870815289373164L;

	private double[] y;

	public TestProblem1() {
		super();
		double[] y0 = new double[]{ 1.0 , 0.1 };
		setInitialConditions(0.0, y0);
		setFinalConditions(4.0);
		double[] errorScale = new double[]{ 1.0 , 1.0 };
		setErrorScale(errorScale);
		y = new double[y0.length];
	}

	public TestProblem1(org.apache.commons.math.ode.TestProblem1 problem) {
		super(problem);
		y = problem.y.clone();
	}

	@java.lang.Override
	public org.apache.commons.math.ode.TestProblem1 copy() {
		return new org.apache.commons.math.ode.TestProblem1(this);
	}

	@java.lang.Override
	public void doComputeDerivatives(double t, double[] y, double[] yDot) {
		for (int i = 0 ; i < (n) ; ++i)
			yDot[i] = -(y[i]);
	}

	@java.lang.Override
	public double[] computeTheoreticalState(double t) {
		double c = java.lang.Math.exp(((t0) - t));
		for (int i = 0 ; i < (n) ; ++i) {
			y[i] = c * (y0[i]);
		}
		return y;
	}
}

