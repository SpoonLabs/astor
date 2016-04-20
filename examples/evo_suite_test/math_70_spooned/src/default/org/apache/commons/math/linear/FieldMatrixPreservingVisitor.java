package org.apache.commons.math.linear;


public interface FieldMatrixPreservingVisitor<T extends org.apache.commons.math.FieldElement<?>> {
	void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn);

	void visit(int row, int column, T value) throws org.apache.commons.math.linear.MatrixVisitorException;

	T end();
}

