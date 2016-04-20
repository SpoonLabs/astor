package org.apache.commons.math.ode;


public class TestProblem4 extends org.apache.commons.math.ode.TestProblemAbstract {
	private static final long serialVersionUID = -5910438521889015745L;

	private double a;

	private double[] y;

	public TestProblem4() {
		super();
		a = 1.2;
		double[] y0 = new double[]{ java.lang.Math.sin(a) , java.lang.Math.cos(a) };
		setInitialConditions(0.0, y0);
		setFinalConditions(15);
		double[] errorScale = new double[]{ 1.0 , 0.0 };
		setErrorScale(errorScale);
		y = new double[y0.length];
	}

	public TestProblem4(org.apache.commons.math.ode.TestProblem4 problem) {
		super(problem);
		a = problem.a;
		y = problem.y.clone();
	}

	@java.lang.Override
	public org.apache.commons.math.ode.TestProblem4 copy() {
		return new org.apache.commons.math.ode.TestProblem4(this);
	}

	@java.lang.Override
	public org.apache.commons.math.ode.events.EventHandler[] getEventsHandlers() {
		return new org.apache.commons.math.ode.events.EventHandler[]{ new org.apache.commons.math.ode.TestProblem4.Bounce() , new org.apache.commons.math.ode.TestProblem4.Stop() };
	}

	@java.lang.Override
	public void doComputeDerivatives(double t, double[] y, double[] yDot) {
		yDot[0] = y[1];
		yDot[1] = -(y[0]);
	}

	@java.lang.Override
	public double[] computeTheoreticalState(double t) {
		double sin = java.lang.Math.sin((t + (a)));
		double cos = java.lang.Math.cos((t + (a)));
		y[0] = java.lang.Math.abs(sin);
		y[1] = sin >= 0 ? cos : -cos;
		return y;
	}

	private static class Bounce implements org.apache.commons.math.ode.events.EventHandler {
		private static final long serialVersionUID = 1356097180027801200L;

		private int sign;

		public Bounce() {
			sign = +1;
		}

		public double g(double t, double[] y) {
			return (sign) * (y[0]);
		}

		public int eventOccurred(double t, double[] y, boolean increasing) {
			sign = -(sign);
			return org.apache.commons.math.ode.events.EventHandler.RESET_STATE;
		}

		public void resetState(double t, double[] y) {
			y[0] = -(y[0]);
			y[1] = -(y[1]);
		}
	}

	private static class Stop implements org.apache.commons.math.ode.events.EventHandler {
		private static final long serialVersionUID = 6975050568227951931L;

		public Stop() {
		}

		public double g(double t, double[] y) {
			return t - 12.0;
		}

		public int eventOccurred(double t, double[] y, boolean increasing) {
			return org.apache.commons.math.ode.events.EventHandler.STOP;
		}

		public void resetState(double t, double[] y) {
		}
	}
}

