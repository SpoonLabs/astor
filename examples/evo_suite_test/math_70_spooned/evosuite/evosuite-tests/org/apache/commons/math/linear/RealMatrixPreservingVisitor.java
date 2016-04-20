package org.apache.commons.math.linear;


public interface RealMatrixPreservingVisitor {
	void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn);

	void visit(int row, int column, double value) throws org.apache.commons.math.linear.MatrixVisitorException;

	double end();
}

