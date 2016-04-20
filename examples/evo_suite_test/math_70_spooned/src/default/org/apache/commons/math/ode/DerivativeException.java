package org.apache.commons.math.ode;


public class DerivativeException extends org.apache.commons.math.MathException {
	private static final long serialVersionUID = 5666710788967425123L;

	public DerivativeException(final java.lang.String specifier ,final java.lang.Object... parts) {
		super(specifier, parts);
	}

	public DerivativeException(final java.lang.Throwable cause) {
		super(cause);
	}
}

