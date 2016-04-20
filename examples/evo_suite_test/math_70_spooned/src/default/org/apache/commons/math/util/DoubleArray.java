package org.apache.commons.math.util;


public interface DoubleArray {
	int getNumElements();

	double getElement(int index);

	void setElement(int index, double value);

	void addElement(double value);

	double addElementRolling(double value);

	double[] getElements();

	void clear();
}

