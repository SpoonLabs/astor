package org.apache.commons.math.optimization;


public class SimpleRealPointChecker implements org.apache.commons.math.optimization.RealConvergenceChecker {
	private static final double DEFAULT_RELATIVE_THRESHOLD = 100 * (org.apache.commons.math.util.MathUtils.EPSILON);

	private static final double DEFAULT_ABSOLUTE_THRESHOLD = 100 * (org.apache.commons.math.util.MathUtils.SAFE_MIN);

	private final double relativeThreshold;

	private final double absoluteThreshold;

	public SimpleRealPointChecker() {
		this.relativeThreshold = DEFAULT_RELATIVE_THRESHOLD;
		this.absoluteThreshold = DEFAULT_ABSOLUTE_THRESHOLD;
	}

	public SimpleRealPointChecker(final double relativeThreshold ,final double absoluteThreshold) {
		this.relativeThreshold = relativeThreshold;
		this.absoluteThreshold = absoluteThreshold;
	}

	public boolean converged(final int iteration, final org.apache.commons.math.optimization.RealPointValuePair previous, final org.apache.commons.math.optimization.RealPointValuePair current) {
		final double[] p = previous.getPoint();
		final double[] c = current.getPoint();
		for (int i = 0 ; i < (p.length) ; ++i) {
			final double difference = java.lang.Math.abs(((p[i]) - (c[i])));
			final double size = java.lang.Math.max(java.lang.Math.abs(p[i]), java.lang.Math.abs(c[i]));
			if ((difference > (size * (relativeThreshold))) && (difference > (absoluteThreshold))) {
				return false;
			} 
		}
		return true;
	}
}

