package org.apache.commons.math.optimization;


public class SimpleScalarValueChecker implements org.apache.commons.math.optimization.RealConvergenceChecker {
	private static final double DEFAULT_RELATIVE_THRESHOLD = 100 * (org.apache.commons.math.util.MathUtils.EPSILON);

	private static final double DEFAULT_ABSOLUTE_THRESHOLD = 100 * (org.apache.commons.math.util.MathUtils.SAFE_MIN);

	private final double relativeThreshold;

	private final double absoluteThreshold;

	public SimpleScalarValueChecker() {
		this.relativeThreshold = DEFAULT_RELATIVE_THRESHOLD;
		this.absoluteThreshold = DEFAULT_ABSOLUTE_THRESHOLD;
	}

	public SimpleScalarValueChecker(final double relativeThreshold ,final double absoluteThreshold) {
		this.relativeThreshold = relativeThreshold;
		this.absoluteThreshold = absoluteThreshold;
	}

	public boolean converged(final int iteration, final org.apache.commons.math.optimization.RealPointValuePair previous, final org.apache.commons.math.optimization.RealPointValuePair current) {
		final double p = previous.getValue();
		final double c = current.getValue();
		final double difference = java.lang.Math.abs((p - c));
		final double size = java.lang.Math.max(java.lang.Math.abs(p), java.lang.Math.abs(c));
		return (difference <= (size * (relativeThreshold))) || (difference <= (absoluteThreshold));
	}
}

