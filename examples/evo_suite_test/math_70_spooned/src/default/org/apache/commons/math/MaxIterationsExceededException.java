package org.apache.commons.math;


public class MaxIterationsExceededException extends org.apache.commons.math.ConvergenceException {
	private static final long serialVersionUID = -7821226672760574694L;

	private final int maxIterations;

	public MaxIterationsExceededException(final int maxIterations) {
		super("Maximal number of iterations ({0}) exceeded", maxIterations);
		this.maxIterations = maxIterations;
	}

	public MaxIterationsExceededException(final int maxIterations ,final java.lang.String pattern ,final java.lang.Object... arguments) {
		super(pattern, arguments);
		this.maxIterations = maxIterations;
	}

	public int getMaxIterations() {
		return maxIterations;
	}
}

