package org.apache.commons.math.ode;


public class TestProblem2 extends org.apache.commons.math.ode.TestProblemAbstract {
	private static final long serialVersionUID = 8330741783213512366L;

	private double[] y;

	public TestProblem2() {
		super();
		double[] y0 = new double[]{ 0.0 };
		setInitialConditions(0.0, y0);
		setFinalConditions(1.0);
		double[] errorScale = new double[]{ 1.0 };
		setErrorScale(errorScale);
		y = new double[y0.length];
	}

	public TestProblem2(org.apache.commons.math.ode.TestProblem2 problem) {
		super(problem);
		y = problem.y.clone();
	}

	@java.lang.Override
	public org.apache.commons.math.ode.TestProblem2 copy() {
		return new org.apache.commons.math.ode.TestProblem2(this);
	}

	@java.lang.Override
	public void doComputeDerivatives(double t, double[] y, double[] yDot) {
		for (int i = 0 ; i < (n) ; ++i)
			yDot[i] = t * ((t * t) - (y[i]));
	}

	@java.lang.Override
	public double[] computeTheoreticalState(double t) {
		double t2 = t * t;
		double c = t2 + (2 * ((java.lang.Math.exp(((-0.5) * t2))) - 1));
		for (int i = 0 ; i < (n) ; ++i) {
			y[i] = c;
		}
		return y;
	}
}

