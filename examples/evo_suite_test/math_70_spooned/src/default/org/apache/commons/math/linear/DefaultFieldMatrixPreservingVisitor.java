package org.apache.commons.math.linear;


public class DefaultFieldMatrixPreservingVisitor<T extends org.apache.commons.math.FieldElement<T>> implements org.apache.commons.math.linear.FieldMatrixPreservingVisitor<T> {
	private final T zero;

	public DefaultFieldMatrixPreservingVisitor(final T zero) {
		this.zero = zero;
	}

	public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {
	}

	public void visit(int row, int column, T value) throws org.apache.commons.math.linear.MatrixVisitorException {
	}

	public T end() {
		return zero;
	}
}

