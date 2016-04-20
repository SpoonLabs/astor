package org.apache.commons.math.linear;


public class DefaultFieldMatrixChangingVisitor<T extends org.apache.commons.math.FieldElement<T>> implements org.apache.commons.math.linear.FieldMatrixChangingVisitor<T> {
	private final T zero;

	public DefaultFieldMatrixChangingVisitor(final T zero) {
		this.zero = zero;
	}

	public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {
	}

	public T visit(int row, int column, T value) throws org.apache.commons.math.linear.MatrixVisitorException {
		return value;
	}

	public T end() {
		return zero;
	}
}

