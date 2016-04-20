package org.apache.commons.math.ode;


public class TestProblem6 extends org.apache.commons.math.ode.TestProblemAbstract {
	private static final long serialVersionUID = 1353409119804352378L;

	private double[] y;

	public TestProblem6() {
		super();
		double[] y0 = new double[]{ -360.0 };
		setInitialConditions(0.0, y0);
		setFinalConditions(1.0);
		double[] errorScale = new double[]{ 1.0 };
		setErrorScale(errorScale);
		y = new double[y0.length];
	}

	public TestProblem6(org.apache.commons.math.ode.TestProblem6 problem) {
		super(problem);
		y = problem.y.clone();
	}

	@java.lang.Override
	public org.apache.commons.math.ode.TestProblem6 copy() {
		return new org.apache.commons.math.ode.TestProblem6(this);
	}

	@java.lang.Override
	public void doComputeDerivatives(double t, double[] y, double[] yDot) {
		double t2 = t * t;
		double t4 = t2 * t2;
		double t5 = t4 * t;
		for (int i = 0 ; i < (n) ; ++i) {
			yDot[i] = (3 * t5) - (y[i]);
		}
	}

	@java.lang.Override
	public double[] computeTheoreticalState(double t) {
		for (int i = 0 ; i < (n) ; ++i) {
			y[i] = (((((((((3 * t) - 15) * t) + 60) * t) - 180) * t) + 360) * t) - 360;
		}
		return y;
	}
}

