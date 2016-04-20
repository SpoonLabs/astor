package org.apache.commons.math.ode.events;


public class CombinedEventsManager {
	private final java.util.List<org.apache.commons.math.ode.events.EventState> states;

	private org.apache.commons.math.ode.events.EventState first;

	private boolean initialized;

	public CombinedEventsManager() {
		states = new java.util.ArrayList<org.apache.commons.math.ode.events.EventState>();
		first = null;
		initialized = false;
	}

	public void addEventHandler(final org.apache.commons.math.ode.events.EventHandler handler, final double maxCheckInterval, final double convergence, final int maxIterationCount) {
		states.add(new org.apache.commons.math.ode.events.EventState(handler , maxCheckInterval , convergence , maxIterationCount));
	}

	public java.util.Collection<org.apache.commons.math.ode.events.EventHandler> getEventsHandlers() {
		final java.util.List<org.apache.commons.math.ode.events.EventHandler> list = new java.util.ArrayList<org.apache.commons.math.ode.events.EventHandler>();
		for (org.apache.commons.math.ode.events.EventState state : states) {
			list.add(state.getEventHandler());
		}
		return java.util.Collections.unmodifiableCollection(list);
	}

	public void clearEventsHandlers() {
		states.clear();
	}

	public java.util.Collection<org.apache.commons.math.ode.events.EventState> getEventsStates() {
		return states;
	}

	public boolean isEmpty() {
		return states.isEmpty();
	}

	public boolean evaluateStep(final org.apache.commons.math.ode.sampling.StepInterpolator interpolator) throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		try {
			first = null;
			if (states.isEmpty()) {
				return false;
			} 
			if (!(initialized)) {
				final double t0 = interpolator.getPreviousTime();
				interpolator.setInterpolatedTime(t0);
				final double[] y = interpolator.getInterpolatedState();
				for (org.apache.commons.math.ode.events.EventState state : states) {
					state.reinitializeBegin(t0, y);
				}
				initialized = true;
			} 
			for (org.apache.commons.math.ode.events.EventState state : states) {
				if (state.evaluateStep(interpolator)) {
					if ((first) == null) {
						first = state;
					} else {
						if (interpolator.isForward()) {
							if ((state.getEventTime()) < (first.getEventTime())) {
								first = state;
							} 
						} else {
							if ((state.getEventTime()) > (first.getEventTime())) {
								first = state;
							} 
						}
					}
				} 
			}
			return (first) != null;
		} catch (org.apache.commons.math.ode.events.EventException se) {
			throw new org.apache.commons.math.ode.IntegratorException(se);
		} catch (org.apache.commons.math.ConvergenceException ce) {
			throw new org.apache.commons.math.ode.IntegratorException(ce);
		}
	}

	public double getEventTime() {
		return (first) == null ? java.lang.Double.NaN : first.getEventTime();
	}

	public void stepAccepted(final double t, final double[] y) throws org.apache.commons.math.ode.IntegratorException {
		try {
			for (org.apache.commons.math.ode.events.EventState state : states) {
				state.stepAccepted(t, y);
			}
		} catch (org.apache.commons.math.ode.events.EventException se) {
			throw new org.apache.commons.math.ode.IntegratorException(se);
		}
	}

	public boolean stop() {
		for (org.apache.commons.math.ode.events.EventState state : states) {
			if (state.stop()) {
				return true;
			} 
		}
		return false;
	}

	public boolean reset(final double t, final double[] y) throws org.apache.commons.math.ode.IntegratorException {
		try {
			boolean resetDerivatives = false;
			for (org.apache.commons.math.ode.events.EventState state : states) {
				if (state.reset(t, y)) {
					resetDerivatives = true;
				} 
			}
			return resetDerivatives;
		} catch (org.apache.commons.math.ode.events.EventException se) {
			throw new org.apache.commons.math.ode.IntegratorException(se);
		}
	}
}

