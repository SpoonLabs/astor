package org.apache.commons.math.estimation;


@java.lang.Deprecated
public interface EstimationProblem {
	org.apache.commons.math.estimation.WeightedMeasurement[] getMeasurements();

	org.apache.commons.math.estimation.EstimatedParameter[] getUnboundParameters();

	org.apache.commons.math.estimation.EstimatedParameter[] getAllParameters();
}

