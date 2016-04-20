package org.apache.commons.math;


public interface FieldElement<T> {
	T add(T a);

	T subtract(T a);

	T multiply(T a);

	T divide(T a) throws java.lang.ArithmeticException;

	org.apache.commons.math.Field<T> getField();
}

