package org.apache.commons.math.estimation;


@java.lang.Deprecated
public class SimpleEstimationProblem implements org.apache.commons.math.estimation.EstimationProblem {
	private final java.util.List<org.apache.commons.math.estimation.EstimatedParameter> parameters;

	private final java.util.List<org.apache.commons.math.estimation.WeightedMeasurement> measurements;

	public SimpleEstimationProblem() {
		parameters = new java.util.ArrayList<org.apache.commons.math.estimation.EstimatedParameter>();
		measurements = new java.util.ArrayList<org.apache.commons.math.estimation.WeightedMeasurement>();
	}

	public org.apache.commons.math.estimation.EstimatedParameter[] getAllParameters() {
		return parameters.toArray(new org.apache.commons.math.estimation.EstimatedParameter[parameters.size()]);
	}

	public org.apache.commons.math.estimation.EstimatedParameter[] getUnboundParameters() {
		java.util.List<org.apache.commons.math.estimation.EstimatedParameter> unbound = new java.util.ArrayList<org.apache.commons.math.estimation.EstimatedParameter>(parameters.size());
		for (org.apache.commons.math.estimation.EstimatedParameter p : parameters) {
			if (!(p.isBound())) {
				unbound.add(p);
			} 
		}
		return unbound.toArray(new org.apache.commons.math.estimation.EstimatedParameter[unbound.size()]);
	}

	public org.apache.commons.math.estimation.WeightedMeasurement[] getMeasurements() {
		return measurements.toArray(new org.apache.commons.math.estimation.WeightedMeasurement[measurements.size()]);
	}

	protected void addParameter(org.apache.commons.math.estimation.EstimatedParameter p) {
		parameters.add(p);
	}

	protected void addMeasurement(org.apache.commons.math.estimation.WeightedMeasurement m) {
		measurements.add(m);
	}
}

