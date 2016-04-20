package org.apache.commons.math.ode.events;


public class EventState {
	private final org.apache.commons.math.ode.events.EventHandler handler;

	private final double maxCheckInterval;

	private final double convergence;

	private final int maxIterationCount;

	private double t0;

	private double g0;

	private boolean g0Positive;

	private boolean pendingEvent;

	private double pendingEventTime;

	private double previousEventTime;

	private boolean forward;

	private boolean increasing;

	private int nextAction;

	public EventState(final org.apache.commons.math.ode.events.EventHandler handler ,final double maxCheckInterval ,final double convergence ,final int maxIterationCount) {
		this.handler = handler;
		this.maxCheckInterval = maxCheckInterval;
		this.convergence = java.lang.Math.abs(convergence);
		this.maxIterationCount = maxIterationCount;
		t0 = java.lang.Double.NaN;
		g0 = java.lang.Double.NaN;
		g0Positive = true;
		pendingEvent = false;
		pendingEventTime = java.lang.Double.NaN;
		previousEventTime = java.lang.Double.NaN;
		increasing = true;
		nextAction = org.apache.commons.math.ode.events.EventHandler.CONTINUE;
	}

	public org.apache.commons.math.ode.events.EventHandler getEventHandler() {
		return handler;
	}

	public double getMaxCheckInterval() {
		return maxCheckInterval;
	}

	public double getConvergence() {
		return convergence;
	}

	public int getMaxIterationCount() {
		return maxIterationCount;
	}

	public void reinitializeBegin(final double tStart, final double[] yStart) throws org.apache.commons.math.ode.events.EventException {
		t0 = tStart;
		g0 = handler.g(tStart, yStart);
		g0Positive = (g0) >= 0;
	}

	public boolean evaluateStep(final org.apache.commons.math.ode.sampling.StepInterpolator interpolator) throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.events.EventException {
		try {
			forward = interpolator.isForward();
			final double t1 = interpolator.getCurrentTime();
			final int n = java.lang.Math.max(1, ((int)(java.lang.Math.ceil(((java.lang.Math.abs((t1 - (t0)))) / (maxCheckInterval))))));
			final double h = (t1 - (t0)) / n;
			double ta = t0;
			double ga = g0;
			double tb = (t0) + (interpolator.isForward() ? convergence : -(convergence));
			for (int i = 0 ; i < n ; ++i) {
				tb += h;
				interpolator.setInterpolatedTime(tb);
				final double gb = handler.g(tb, interpolator.getInterpolatedState());
				if ((g0Positive) ^ (gb >= 0)) {
					if ((ga * gb) > 0) {
						final double epsilon = (forward ? 0.25 : -0.25) * (convergence);
						for (int k = 0 ; (k < 4) && ((ga * gb) > 0) ; ++k) {
							ta += epsilon;
							interpolator.setInterpolatedTime(ta);
							ga = handler.g(ta, interpolator.getInterpolatedState());
						}
						if ((ga * gb) > 0) {
							throw org.apache.commons.math.MathRuntimeException.createInternalError(null);
						} 
					} 
					increasing = gb >= ga;
					final org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.UnivariateRealFunction() {
						public double value(final double t) throws org.apache.commons.math.FunctionEvaluationException {
							try {
								interpolator.setInterpolatedTime(t);
								return handler.g(t, interpolator.getInterpolatedState());
							} catch (org.apache.commons.math.ode.DerivativeException e) {
								throw new org.apache.commons.math.FunctionEvaluationException(e , t);
							} catch (org.apache.commons.math.ode.events.EventException e) {
								throw new org.apache.commons.math.FunctionEvaluationException(e , t);
							}
						}
					};
					final org.apache.commons.math.analysis.solvers.BrentSolver solver = new org.apache.commons.math.analysis.solvers.BrentSolver();
					solver.setAbsoluteAccuracy(convergence);
					solver.setMaximalIterationCount(maxIterationCount);
					final double root = ta <= tb ? solver.solve(f, ta, tb) : solver.solve(f, tb, ta);
					if (((java.lang.Math.abs((root - ta))) <= (convergence)) && ((java.lang.Math.abs((root - (previousEventTime)))) <= (convergence))) {
						ta = tb;
						ga = gb;
					} else {
						if ((java.lang.Double.isNaN(previousEventTime)) || ((java.lang.Math.abs(((previousEventTime) - root))) > (convergence))) {
							pendingEventTime = root;
							if ((pendingEvent) && ((java.lang.Math.abs((t1 - (pendingEventTime)))) <= (convergence))) {
								return false;
							} 
							pendingEvent = true;
							return true;
						} 
					}
				} else {
					ta = tb;
					ga = gb;
				}
			}
			pendingEvent = false;
			pendingEventTime = java.lang.Double.NaN;
			return false;
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			final java.lang.Throwable cause = e.getCause();
			if ((cause != null) && (cause instanceof org.apache.commons.math.ode.DerivativeException)) {
				throw ((org.apache.commons.math.ode.DerivativeException)(cause));
			} else {
				if ((cause != null) && (cause instanceof org.apache.commons.math.ode.events.EventException)) {
					throw ((org.apache.commons.math.ode.events.EventException)(cause));
				} 
			}
			throw new org.apache.commons.math.ode.events.EventException(e);
		}
	}

	public double getEventTime() {
		return pendingEventTime;
	}

	public void stepAccepted(final double t, final double[] y) throws org.apache.commons.math.ode.events.EventException {
		t0 = t;
		g0 = handler.g(t, y);
		if (pendingEvent) {
			previousEventTime = t;
			g0Positive = increasing;
			nextAction = handler.eventOccurred(t, y, !((increasing) ^ (forward)));
		} else {
			g0Positive = (g0) >= 0;
			nextAction = org.apache.commons.math.ode.events.EventHandler.CONTINUE;
		}
	}

	public boolean stop() {
		return (nextAction) == (org.apache.commons.math.ode.events.EventHandler.STOP);
	}

	public boolean reset(final double t, final double[] y) throws org.apache.commons.math.ode.events.EventException {
		if (!(pendingEvent)) {
			return false;
		} 
		if ((nextAction) == (org.apache.commons.math.ode.events.EventHandler.RESET_STATE)) {
			handler.resetState(t, y);
		} 
		pendingEvent = false;
		pendingEventTime = java.lang.Double.NaN;
		return ((nextAction) == (org.apache.commons.math.ode.events.EventHandler.RESET_STATE)) || ((nextAction) == (org.apache.commons.math.ode.events.EventHandler.RESET_DERIVATIVES));
	}
}

