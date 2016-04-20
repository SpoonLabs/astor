package org.apache.commons.math.ode;


public class IntegratorException extends org.apache.commons.math.MathException {
	private static final long serialVersionUID = -1607588949778036796L;

	public IntegratorException(final java.lang.String specifier ,final java.lang.Object... parts) {
		super(specifier, parts);
	}

	public IntegratorException(final java.lang.Throwable cause) {
		super(cause);
	}
}

