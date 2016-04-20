package org.apache.commons.math.ode.nonstiff;


public class StepProblem implements org.apache.commons.math.ode.FirstOrderDifferentialEquations , org.apache.commons.math.ode.events.EventHandler {
	public StepProblem(double rateBefore ,double rateAfter ,double switchTime) {
		this.rateAfter = rateAfter;
		this.switchTime = switchTime;
		setRate(rateBefore);
	}

	public void computeDerivatives(double t, double[] y, double[] yDot) {
		yDot[0] = rate;
	}

	public int getDimension() {
		return 1;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public int eventOccurred(double t, double[] y, boolean increasing) {
		setRate(rateAfter);
		return org.apache.commons.math.ode.events.EventHandler.RESET_DERIVATIVES;
	}

	public double g(double t, double[] y) {
		return t - (switchTime);
	}

	public void resetState(double t, double[] y) {
	}

	private double rate;

	private double rateAfter;

	private double switchTime;

	private static final long serialVersionUID = 7590601995477504318L;
}

