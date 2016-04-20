package org.apache.commons.math.estimation;


@java.lang.Deprecated
public class EstimatedParameter implements java.io.Serializable {
	private static final long serialVersionUID = -555440800213416949L;

	protected double estimate;

	private final java.lang.String name;

	private boolean bound;

	public EstimatedParameter(java.lang.String name ,double firstEstimate) {
		this.name = name;
		estimate = firstEstimate;
		bound = false;
	}

	public EstimatedParameter(java.lang.String name ,double firstEstimate ,boolean bound) {
		this.name = name;
		estimate = firstEstimate;
		this.bound = bound;
	}

	public EstimatedParameter(org.apache.commons.math.estimation.EstimatedParameter parameter) {
		name = parameter.name;
		estimate = parameter.estimate;
		bound = parameter.bound;
	}

	public void setEstimate(double estimate) {
		this.estimate = estimate;
	}

	public double getEstimate() {
		return estimate;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setBound(boolean bound) {
		this.bound = bound;
	}

	public boolean isBound() {
		return bound;
	}
}

