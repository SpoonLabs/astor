package org.apache.commons.math.linear;


public class NonSquareMatrixException extends org.apache.commons.math.linear.InvalidMatrixException {
	private static final long serialVersionUID = 8996207526636673730L;

	public NonSquareMatrixException(final int rows ,final int columns) {
		super("a {0}x{1} matrix was provided instead of a square matrix", rows, columns);
	}
}

