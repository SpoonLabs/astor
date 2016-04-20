package org.apache.commons.math.linear;


public class NotPositiveDefiniteMatrixException extends org.apache.commons.math.MathException {
	private static final long serialVersionUID = 4122929125438624648L;

	public NotPositiveDefiniteMatrixException() {
		super("not positive definite matrix");
	}
}

