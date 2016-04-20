package org.apache.commons.math.linear;


public class InvalidMatrixException extends org.apache.commons.math.MathRuntimeException {
	private static final long serialVersionUID = 1135533765052675495L;

	public InvalidMatrixException(final java.lang.String pattern ,final java.lang.Object... arguments) {
		super(pattern, arguments);
	}

	public InvalidMatrixException(final java.lang.Throwable cause) {
		super(cause);
	}
}

