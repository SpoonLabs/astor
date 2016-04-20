package org.apache.commons.math.ode;


public interface ODEIntegrator {
	java.lang.String getName();

	void addStepHandler(org.apache.commons.math.ode.sampling.StepHandler handler);

	java.util.Collection<org.apache.commons.math.ode.sampling.StepHandler> getStepHandlers();

	void clearStepHandlers();

	void addEventHandler(org.apache.commons.math.ode.events.EventHandler handler, double maxCheckInterval, double convergence, int maxIterationCount);

	java.util.Collection<org.apache.commons.math.ode.events.EventHandler> getEventHandlers();

	void clearEventHandlers();

	double getCurrentStepStart();

	double getCurrentSignedStepsize();

	void setMaxEvaluations(int maxEvaluations);

	int getMaxEvaluations();

	int getEvaluations();
}

