package org.apache.commons.math.linear;


public interface FieldMatrixChangingVisitor<T extends org.apache.commons.math.FieldElement<?>> {
	void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn);

	T visit(int row, int column, T value) throws org.apache.commons.math.linear.MatrixVisitorException;

	T end();
}

