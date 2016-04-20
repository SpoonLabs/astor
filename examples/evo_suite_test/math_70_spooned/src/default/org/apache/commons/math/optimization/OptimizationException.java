package org.apache.commons.math.optimization;


public class OptimizationException extends org.apache.commons.math.ConvergenceException {
	private static final long serialVersionUID = -357696069587075016L;

	public OptimizationException(java.lang.String specifier ,java.lang.Object... parts) {
		super(specifier, parts);
	}

	public OptimizationException(java.lang.Throwable cause) {
		super(cause);
	}
}

