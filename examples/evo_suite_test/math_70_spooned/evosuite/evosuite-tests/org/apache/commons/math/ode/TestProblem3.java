package org.apache.commons.math.ode;


public class TestProblem3 extends org.apache.commons.math.ode.TestProblemAbstract {
	private static final long serialVersionUID = 8567328542728919999L;

	double e;

	private double[] y;

	public TestProblem3(double e) {
		super();
		this.e = e;
		double[] y0 = new double[]{ 1 - e , 0 , 0 , java.lang.Math.sqrt(((1 + e) / (1 - e))) };
		setInitialConditions(0.0, y0);
		setFinalConditions(20.0);
		double[] errorScale = new double[]{ 1.0 , 1.0 , 1.0 , 1.0 };
		setErrorScale(errorScale);
		y = new double[y0.length];
	}

	public TestProblem3() {
		this(0.1);
	}

	public TestProblem3(org.apache.commons.math.ode.TestProblem3 problem) {
		super(problem);
		e = problem.e;
		y = problem.y.clone();
	}

	@java.lang.Override
	public org.apache.commons.math.ode.TestProblem3 copy() {
		return new org.apache.commons.math.ode.TestProblem3(this);
	}

	@java.lang.Override
	public void doComputeDerivatives(double t, double[] y, double[] yDot) {
		double r2 = ((y[0]) * (y[0])) + ((y[1]) * (y[1]));
		double invR3 = 1 / (r2 * (java.lang.Math.sqrt(r2)));
		yDot[0] = y[2];
		yDot[1] = y[3];
		yDot[2] = (-invR3) * (y[0]);
		yDot[3] = (-invR3) * (y[1]);
	}

	@java.lang.Override
	public double[] computeTheoreticalState(double t) {
		double E = t;
		double d = 0;
		double corr = 999.0;
		for (int i = 0 ; (i < 50) && ((java.lang.Math.abs(corr)) > 1.0E-12) ; ++i) {
			double f2 = (e) * (java.lang.Math.sin(E));
			double f0 = d - f2;
			double f1 = 1 - ((e) * (java.lang.Math.cos(E)));
			double f12 = f1 + f1;
			corr = (f0 * f12) / ((f1 * f12) - (f0 * f2));
			d -= corr;
			E = t + d;
		}
		double cosE = java.lang.Math.cos(E);
		double sinE = java.lang.Math.sin(E);
		y[0] = cosE - (e);
		y[1] = (java.lang.Math.sqrt((1 - ((e) * (e))))) * sinE;
		y[2] = (-sinE) / (1 - ((e) * cosE));
		y[3] = ((java.lang.Math.sqrt((1 - ((e) * (e))))) * cosE) / (1 - ((e) * cosE));
		return y;
	}
}

