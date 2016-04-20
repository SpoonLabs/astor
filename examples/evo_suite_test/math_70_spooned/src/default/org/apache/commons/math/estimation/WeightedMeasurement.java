package org.apache.commons.math.estimation;


@java.lang.Deprecated
public abstract class WeightedMeasurement implements java.io.Serializable {
	private static final long serialVersionUID = 4360046376796901941L;

	private final double weight;

	private final double measuredValue;

	private boolean ignored;

	public WeightedMeasurement(double weight ,double measuredValue) {
		this.weight = weight;
		this.measuredValue = measuredValue;
		ignored = false;
	}

	public WeightedMeasurement(double weight ,double measuredValue ,boolean ignored) {
		this.weight = weight;
		this.measuredValue = measuredValue;
		this.ignored = ignored;
	}

	public double getWeight() {
		return weight;
	}

	public double getMeasuredValue() {
		return measuredValue;
	}

	public double getResidual() {
		return (measuredValue) - (getTheoreticalValue());
	}

	public abstract double getTheoreticalValue();

	public abstract double getPartial(org.apache.commons.math.estimation.EstimatedParameter parameter);

	public void setIgnored(boolean ignored) {
		this.ignored = ignored;
	}

	public boolean isIgnored() {
		return ignored;
	}
}

