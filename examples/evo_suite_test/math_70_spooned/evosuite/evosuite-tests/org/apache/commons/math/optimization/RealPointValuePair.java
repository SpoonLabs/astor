package org.apache.commons.math.optimization;


public class RealPointValuePair implements java.io.Serializable {
	private static final long serialVersionUID = 1003888396256744753L;

	private final double[] point;

	private final double value;

	public RealPointValuePair(final double[] point ,final double value) {
		this.point = point.clone();
		this.value = value;
	}

	public RealPointValuePair(final double[] point ,final double value ,final boolean copyArray) {
		this.point = copyArray ? point.clone() : point;
		this.value = value;
	}

	public double[] getPoint() {
		return point.clone();
	}

	public double[] getPointRef() {
		return point;
	}

	public double getValue() {
		return value;
	}
}

