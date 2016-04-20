package org.apache.commons.math.stat.descriptive.moment;


public class SemiVariance extends org.apache.commons.math.stat.descriptive.AbstractUnivariateStatistic implements java.io.Serializable {
	public static final Direction UPSIDE_VARIANCE = Direction.UPSIDE;

	public static final Direction DOWNSIDE_VARIANCE = Direction.DOWNSIDE;

	private static final long serialVersionUID = -2653430366886024994L;

	private boolean biasCorrected = true;

	private Direction varianceDirection = Direction.DOWNSIDE;

	public SemiVariance() {
	}

	public SemiVariance(final boolean biasCorrected) {
		this.biasCorrected = biasCorrected;
	}

	public SemiVariance(final Direction direction) {
		this.varianceDirection = direction;
	}

	public SemiVariance(final boolean corrected ,final Direction direction) {
		this.biasCorrected = corrected;
		this.varianceDirection = direction;
	}

	public SemiVariance(final org.apache.commons.math.stat.descriptive.moment.SemiVariance original) {
		org.apache.commons.math.stat.descriptive.moment.SemiVariance.copy(original, this);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.moment.SemiVariance copy() {
		org.apache.commons.math.stat.descriptive.moment.SemiVariance result = new org.apache.commons.math.stat.descriptive.moment.SemiVariance();
		org.apache.commons.math.stat.descriptive.moment.SemiVariance.copy(this, result);
		return result;
	}

	public static void copy(final org.apache.commons.math.stat.descriptive.moment.SemiVariance source, org.apache.commons.math.stat.descriptive.moment.SemiVariance dest) {
		dest.biasCorrected = source.biasCorrected;
		dest.varianceDirection = source.varianceDirection;
	}

	@java.lang.Override
	public double evaluate(final double[] values) {
		if (values == null) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("input values array is null");
		} 
		return evaluate(values, 0, values.length);
	}

	@java.lang.Override
	public double evaluate(final double[] values, final int start, final int length) {
		double m = new org.apache.commons.math.stat.descriptive.moment.Mean().evaluate(values, start, length);
		return evaluate(values, m, varianceDirection, biasCorrected, 0, values.length);
	}

	public double evaluate(final double[] values, Direction direction) {
		double m = new org.apache.commons.math.stat.descriptive.moment.Mean().evaluate(values);
		return evaluate(values, m, direction, biasCorrected, 0, values.length);
	}

	public double evaluate(final double[] values, final double cutoff) {
		return evaluate(values, cutoff, varianceDirection, biasCorrected, 0, values.length);
	}

	public double evaluate(final double[] values, final double cutoff, final Direction direction) {
		return evaluate(values, cutoff, direction, biasCorrected, 0, values.length);
	}

	public double evaluate(final double[] values, final double cutoff, final Direction direction, final boolean corrected, final int start, final int length) {
		test(values, start, length);
		if ((values.length) == 0) {
			return java.lang.Double.NaN;
		} else {
			if ((values.length) == 1) {
				return 0.0;
			} else {
				final boolean booleanDirection = direction.getDirection();
				double dev = 0.0;
				double sumsq = 0.0;
				for (int i = start ; i < length ; i++) {
					if (((values[i]) > cutoff) == booleanDirection) {
						dev = (values[i]) - cutoff;
						sumsq += dev * dev;
					} 
				}
				if (corrected) {
					return sumsq / (length - 1.0);
				} else {
					return sumsq / length;
				}
			}
		}
	}

	public boolean isBiasCorrected() {
		return biasCorrected;
	}

	public void setBiasCorrected(boolean biasCorrected) {
		this.biasCorrected = biasCorrected;
	}

	public Direction getVarianceDirection() {
		return varianceDirection;
	}

	public void setVarianceDirection(Direction varianceDirection) {
		this.varianceDirection = varianceDirection;
	}

	public enum Direction {
UPSIDE(true), DOWNSIDE(false);
		private boolean direction;
		Direction(boolean b) {
			direction = b;
		}
		boolean getDirection() {
			return direction;
		}
	}
}

