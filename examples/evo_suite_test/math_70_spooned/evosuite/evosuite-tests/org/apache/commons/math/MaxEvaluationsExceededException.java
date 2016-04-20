package org.apache.commons.math;


public class MaxEvaluationsExceededException extends org.apache.commons.math.ConvergenceException {
	private static final long serialVersionUID = -5921271447220129118L;

	private final int maxEvaluations;

	public MaxEvaluationsExceededException(final int maxEvaluations) {
		super("Maximal number of evaluations ({0}) exceeded", maxEvaluations);
		this.maxEvaluations = maxEvaluations;
	}

	public MaxEvaluationsExceededException(final int maxEvaluations ,final java.lang.String pattern ,final java.lang.Object... arguments) {
		super(pattern, arguments);
		this.maxEvaluations = maxEvaluations;
	}

	public int getMaxEvaluations() {
		return maxEvaluations;
	}
}

