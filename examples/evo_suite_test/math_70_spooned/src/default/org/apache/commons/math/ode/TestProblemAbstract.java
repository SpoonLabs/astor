package org.apache.commons.math.ode;


public abstract class TestProblemAbstract implements org.apache.commons.math.ode.FirstOrderDifferentialEquations {
	private static final long serialVersionUID = -8521928974502839379L;

	protected int n;

	protected int calls;

	protected double t0;

	protected double[] y0;

	protected double t1;

	protected double[] errorScale;

	protected TestProblemAbstract() {
		n = 0;
		calls = 0;
		t0 = 0;
		y0 = null;
		t1 = 0;
		errorScale = null;
	}

	protected TestProblemAbstract(org.apache.commons.math.ode.TestProblemAbstract problem) {
		n = problem.n;
		calls = problem.calls;
		t0 = problem.t0;
		if ((problem.y0) == null) {
			y0 = null;
		} else {
			y0 = problem.y0.clone();
		}
		if ((problem.errorScale) == null) {
			errorScale = null;
		} else {
			errorScale = problem.errorScale.clone();
		}
		t1 = problem.t1;
	}

	public abstract org.apache.commons.math.ode.TestProblemAbstract copy();

	protected void setInitialConditions(double t0, double[] y0) {
		calls = 0;
		n = y0.length;
		this.t0 = t0;
		this.y0 = y0.clone();
	}

	protected void setFinalConditions(double t1) {
		this.t1 = t1;
	}

	protected void setErrorScale(double[] errorScale) {
		this.errorScale = errorScale.clone();
	}

	public int getDimension() {
		return n;
	}

	public double getInitialTime() {
		return t0;
	}

	public double[] getInitialState() {
		return y0;
	}

	public double getFinalTime() {
		return t1;
	}

	public double[] getErrorScale() {
		return errorScale;
	}

	public org.apache.commons.math.ode.events.EventHandler[] getEventsHandlers() {
		return new org.apache.commons.math.ode.events.EventHandler[0];
	}

	public int getCalls() {
		return calls;
	}

	public void computeDerivatives(double t, double[] y, double[] yDot) {
		++(calls);
		doComputeDerivatives(t, y, yDot);
	}

	public abstract void doComputeDerivatives(double t, double[] y, double[] yDot);

	public abstract double[] computeTheoreticalState(double t);
}

