package org.apache.commons.math.linear;


public interface FieldVector<T extends org.apache.commons.math.FieldElement<T>> {
	org.apache.commons.math.Field<T> getField();

	org.apache.commons.math.linear.FieldVector<T> copy();

	org.apache.commons.math.linear.FieldVector<T> add(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.FieldVector<T> add(T[] v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.FieldVector<T> subtract(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.FieldVector<T> subtract(T[] v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.FieldVector<T> mapAdd(T d);

	org.apache.commons.math.linear.FieldVector<T> mapAddToSelf(T d);

	org.apache.commons.math.linear.FieldVector<T> mapSubtract(T d);

	org.apache.commons.math.linear.FieldVector<T> mapSubtractToSelf(T d);

	org.apache.commons.math.linear.FieldVector<T> mapMultiply(T d);

	org.apache.commons.math.linear.FieldVector<T> mapMultiplyToSelf(T d);

	org.apache.commons.math.linear.FieldVector<T> mapDivide(T d);

	org.apache.commons.math.linear.FieldVector<T> mapDivideToSelf(T d);

	org.apache.commons.math.linear.FieldVector<T> mapInv();

	org.apache.commons.math.linear.FieldVector<T> mapInvToSelf();

	org.apache.commons.math.linear.FieldVector<T> ebeMultiply(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.FieldVector<T> ebeMultiply(T[] v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.FieldVector<T> ebeDivide(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.FieldVector<T> ebeDivide(T[] v) throws java.lang.IllegalArgumentException;

	T[] getData();

	T dotProduct(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException;

	T dotProduct(T[] v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.FieldVector<T> projection(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.FieldVector<T> projection(T[] v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.FieldMatrix<T> outerProduct(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.FieldMatrix<T> outerProduct(T[] v) throws java.lang.IllegalArgumentException;

	T getEntry(int index) throws org.apache.commons.math.linear.MatrixIndexException;

	void setEntry(int index, T value) throws org.apache.commons.math.linear.MatrixIndexException;

	int getDimension();

	org.apache.commons.math.linear.FieldVector<T> append(org.apache.commons.math.linear.FieldVector<T> v);

	org.apache.commons.math.linear.FieldVector<T> append(T d);

	org.apache.commons.math.linear.FieldVector<T> append(T[] a);

	org.apache.commons.math.linear.FieldVector<T> getSubVector(int index, int n) throws org.apache.commons.math.linear.MatrixIndexException;

	void setSubVector(int index, org.apache.commons.math.linear.FieldVector<T> v) throws org.apache.commons.math.linear.MatrixIndexException;

	void setSubVector(int index, T[] v) throws org.apache.commons.math.linear.MatrixIndexException;

	void set(T value);

	T[] toArray();
}

