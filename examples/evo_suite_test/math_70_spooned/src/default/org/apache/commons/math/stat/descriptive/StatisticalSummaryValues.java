package org.apache.commons.math.stat.descriptive;


public class StatisticalSummaryValues implements java.io.Serializable , org.apache.commons.math.stat.descriptive.StatisticalSummary {
	private static final long serialVersionUID = -5108854841843722536L;

	private final double mean;

	private final double variance;

	private final long n;

	private final double max;

	private final double min;

	private final double sum;

	public StatisticalSummaryValues(double mean ,double variance ,long n ,double max ,double min ,double sum) {
		super();
		this.mean = mean;
		this.variance = variance;
		this.n = n;
		this.max = max;
		this.min = min;
		this.sum = sum;
	}

	public double getMax() {
		return max;
	}

	public double getMean() {
		return mean;
	}

	public double getMin() {
		return min;
	}

	public long getN() {
		return n;
	}

	public double getSum() {
		return sum;
	}

	public double getStandardDeviation() {
		return java.lang.Math.sqrt(variance);
	}

	public double getVariance() {
		return variance;
	}

	@java.lang.Override
	public boolean equals(java.lang.Object object) {
		if (object == (this)) {
			return true;
		} 
		if ((object instanceof org.apache.commons.math.stat.descriptive.StatisticalSummaryValues) == false) {
			return false;
		} 
		org.apache.commons.math.stat.descriptive.StatisticalSummaryValues stat = ((org.apache.commons.math.stat.descriptive.StatisticalSummaryValues)(object));
		return (((((org.apache.commons.math.util.MathUtils.equals(stat.getMax(), getMax())) && (org.apache.commons.math.util.MathUtils.equals(stat.getMean(), getMean()))) && (org.apache.commons.math.util.MathUtils.equals(stat.getMin(), getMin()))) && (org.apache.commons.math.util.MathUtils.equals(stat.getN(), getN()))) && (org.apache.commons.math.util.MathUtils.equals(stat.getSum(), getSum()))) && (org.apache.commons.math.util.MathUtils.equals(stat.getVariance(), getVariance()));
	}

	@java.lang.Override
	public int hashCode() {
		int result = 31 + (org.apache.commons.math.util.MathUtils.hash(getMax()));
		result = (result * 31) + (org.apache.commons.math.util.MathUtils.hash(getMean()));
		result = (result * 31) + (org.apache.commons.math.util.MathUtils.hash(getMin()));
		result = (result * 31) + (org.apache.commons.math.util.MathUtils.hash(getN()));
		result = (result * 31) + (org.apache.commons.math.util.MathUtils.hash(getSum()));
		result = (result * 31) + (org.apache.commons.math.util.MathUtils.hash(getVariance()));
		return result;
	}
}

