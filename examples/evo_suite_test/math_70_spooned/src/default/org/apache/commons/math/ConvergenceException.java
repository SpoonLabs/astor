package org.apache.commons.math;


public class ConvergenceException extends org.apache.commons.math.MathException {
	private static final long serialVersionUID = 4883703247677159141L;

	public ConvergenceException() {
		super("Convergence failed");
	}

	public ConvergenceException(java.lang.String pattern ,java.lang.Object... arguments) {
		super(pattern, arguments);
	}

	public ConvergenceException(java.lang.Throwable cause) {
		super(cause);
	}

	public ConvergenceException(java.lang.Throwable cause ,java.lang.String pattern ,java.lang.Object... arguments) {
		super(cause, pattern, arguments);
	}
}

