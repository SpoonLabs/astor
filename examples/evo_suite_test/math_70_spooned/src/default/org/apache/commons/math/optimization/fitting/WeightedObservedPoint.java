package org.apache.commons.math.optimization.fitting;


public class WeightedObservedPoint implements java.io.Serializable {
	private static final long serialVersionUID = 5306874947404636157L;

	private final double weight;

	private final double x;

	private final double y;

	public WeightedObservedPoint(final double weight ,final double x ,final double y) {
		this.weight = weight;
		this.x = x;
		this.y = y;
	}

	public double getWeight() {
		return weight;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
}

