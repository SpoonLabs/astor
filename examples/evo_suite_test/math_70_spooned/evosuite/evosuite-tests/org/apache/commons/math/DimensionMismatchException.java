package org.apache.commons.math;


public class DimensionMismatchException extends org.apache.commons.math.MathException {
	private static final long serialVersionUID = -1316089546353786411L;

	private final int dimension1;

	private final int dimension2;

	public DimensionMismatchException(final int dimension1 ,final int dimension2) {
		super("dimension mismatch {0} != {1}", dimension1, dimension2);
		this.dimension1 = dimension1;
		this.dimension2 = dimension2;
	}

	public int getDimension1() {
		return dimension1;
	}

	public int getDimension2() {
		return dimension2;
	}
}

