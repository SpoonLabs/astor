package org.apache.commons.math.linear;


public class ArrayFieldVectorTest extends junit.framework.TestCase {
	protected org.apache.commons.math.fraction.Fraction[][] ma1 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(8) , new org.apache.commons.math.fraction.Fraction(9) } };

	protected org.apache.commons.math.fraction.Fraction[] vec1 = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) };

	protected org.apache.commons.math.fraction.Fraction[] vec2 = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) };

	protected org.apache.commons.math.fraction.Fraction[] vec3 = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(8) , new org.apache.commons.math.fraction.Fraction(9) };

	protected org.apache.commons.math.fraction.Fraction[] vec4 = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(8) , new org.apache.commons.math.fraction.Fraction(9) };

	protected org.apache.commons.math.fraction.Fraction[] vec_null = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(0) };

	protected org.apache.commons.math.fraction.Fraction[] dvec1 = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(8) , new org.apache.commons.math.fraction.Fraction(9) };

	protected org.apache.commons.math.fraction.Fraction[][] mat1 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(8) , new org.apache.commons.math.fraction.Fraction(9) } };

	public static class FieldVectorTestImpl<T extends org.apache.commons.math.FieldElement<T>> implements java.io.Serializable , org.apache.commons.math.linear.FieldVector<T> {
		private static final long serialVersionUID = 3970959016014158539L;

		private final org.apache.commons.math.Field<T> field;

		protected T[] data;

		@java.lang.SuppressWarnings(value = "unchecked")
		private T[] buildArray(final int length) {
			return ((T[])(java.lang.reflect.Array.newInstance(field.getZero().getClass(), length)));
		}

		public FieldVectorTestImpl(T[] d) {
			field = d[0].getField();
			data = d.clone();
		}

		public org.apache.commons.math.Field<T> getField() {
			return field;
		}

		private java.lang.UnsupportedOperationException unsupported() {
			return new java.lang.UnsupportedOperationException("Not supported, unneeded for test purposes");
		}

		public org.apache.commons.math.linear.FieldVector<T> copy() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> add(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> add(T[] v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> subtract(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> subtract(T[] v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> mapAdd(T d) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> mapAddToSelf(T d) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> mapSubtract(T d) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> mapSubtractToSelf(T d) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> mapMultiply(T d) {
			T[] out = buildArray(data.length);
			for (int i = 0 ; i < (data.length) ; i++) {
				out[i] = data[i].multiply(d);
			}
			return new org.apache.commons.math.linear.ArrayFieldVectorTest.FieldVectorTestImpl<T>(out);
		}

		public org.apache.commons.math.linear.FieldVector<T> mapMultiplyToSelf(T d) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> mapDivide(T d) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> mapDivideToSelf(T d) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> mapInv() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> mapInvToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> ebeMultiply(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> ebeMultiply(T[] v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> ebeDivide(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> ebeDivide(T[] v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public T[] getData() {
			return data.clone();
		}

		public T dotProduct(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
			T dot = field.getZero();
			for (int i = 0 ; i < (data.length) ; i++) {
				dot = dot.add(data[i].multiply(v.getEntry(i)));
			}
			return dot;
		}

		public T dotProduct(T[] v) throws java.lang.IllegalArgumentException {
			T dot = field.getZero();
			for (int i = 0 ; i < (data.length) ; i++) {
				dot = dot.add(data[i].multiply(v[i]));
			}
			return dot;
		}

		public org.apache.commons.math.linear.FieldVector<T> projection(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> projection(T[] v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldMatrix<T> outerProduct(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldMatrix<T> outerProduct(T[] v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public T getEntry(int index) throws org.apache.commons.math.linear.MatrixIndexException {
			return data[index];
		}

		public int getDimension() {
			return data.length;
		}

		public org.apache.commons.math.linear.FieldVector<T> append(org.apache.commons.math.linear.FieldVector<T> v) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> append(T d) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> append(T[] a) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.FieldVector<T> getSubVector(int index, int n) throws org.apache.commons.math.linear.MatrixIndexException {
			throw unsupported();
		}

		public void setEntry(int index, T value) throws org.apache.commons.math.linear.MatrixIndexException {
			throw unsupported();
		}

		public void setSubVector(int index, org.apache.commons.math.linear.FieldVector<T> v) throws org.apache.commons.math.linear.MatrixIndexException {
			throw unsupported();
		}

		public void setSubVector(int index, T[] v) throws org.apache.commons.math.linear.MatrixIndexException {
			throw unsupported();
		}

		public void set(T value) {
			throw unsupported();
		}

		public T[] toArray() {
			throw unsupported();
		}
	}

	public void testConstructors() {
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v0 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(org.apache.commons.math.fraction.FractionField.getInstance());
		junit.framework.Assert.assertEquals(0, v0.getDimension());
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v1 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(org.apache.commons.math.fraction.FractionField.getInstance() , 7);
		junit.framework.Assert.assertEquals(7, v1.getDimension());
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(0), v1.getEntry(6));
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v2 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(5 , new org.apache.commons.math.fraction.Fraction(123 , 100));
		junit.framework.Assert.assertEquals(5, v2.getDimension());
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(123 , 100), v2.getEntry(4));
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v3 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(vec1);
		junit.framework.Assert.assertEquals(3, v3.getDimension());
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(2), v3.getEntry(1));
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v4 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(vec4 , 3 , 2);
		junit.framework.Assert.assertEquals(2, v4.getDimension());
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(4), v4.getEntry(0));
		try {
			new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(vec4 , 8 , 3);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v5_i = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(dvec1);
		junit.framework.Assert.assertEquals(9, v5_i.getDimension());
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(9), v5_i.getEntry(8));
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v5 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(dvec1);
		junit.framework.Assert.assertEquals(9, v5.getDimension());
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(9), v5.getEntry(8));
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v6 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(dvec1 , 3 , 2);
		junit.framework.Assert.assertEquals(2, v6.getDimension());
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(4), v6.getEntry(0));
		try {
			new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(dvec1 , 8 , 3);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v7 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(v1);
		junit.framework.Assert.assertEquals(7, v7.getDimension());
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(0), v7.getEntry(6));
		org.apache.commons.math.linear.ArrayFieldVectorTest.FieldVectorTestImpl<org.apache.commons.math.fraction.Fraction> v7_i = new org.apache.commons.math.linear.ArrayFieldVectorTest.FieldVectorTestImpl<org.apache.commons.math.fraction.Fraction>(vec1);
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v7_2 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(v7_i);
		junit.framework.Assert.assertEquals(3, v7_2.getDimension());
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(2), v7_2.getEntry(1));
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v8 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(v1 , true);
		junit.framework.Assert.assertEquals(7, v8.getDimension());
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(0), v8.getEntry(6));
		junit.framework.Assert.assertNotSame("testData not same object ", v1.data, v8.data);
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v8_2 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(v1 , false);
		junit.framework.Assert.assertEquals(7, v8_2.getDimension());
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(0), v8_2.getEntry(6));
		junit.framework.Assert.assertEquals(v1.data, v8_2.data);
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v9 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(v1 , v3);
		junit.framework.Assert.assertEquals(10, v9.getDimension());
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(1), v9.getEntry(7));
	}

	public void testDataInOut() {
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v1 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(vec1);
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v2 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(vec2);
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v4 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(vec4);
		org.apache.commons.math.linear.ArrayFieldVectorTest.FieldVectorTestImpl<org.apache.commons.math.fraction.Fraction> v2_t = new org.apache.commons.math.linear.ArrayFieldVectorTest.FieldVectorTestImpl<org.apache.commons.math.fraction.Fraction>(vec2);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_append_1 = v1.append(v2);
		junit.framework.Assert.assertEquals(6, v_append_1.getDimension());
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(4), v_append_1.getEntry(3));
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_append_2 = v1.append(new org.apache.commons.math.fraction.Fraction(2));
		junit.framework.Assert.assertEquals(4, v_append_2.getDimension());
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(2), v_append_2.getEntry(3));
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_append_3 = v1.append(vec2);
		junit.framework.Assert.assertEquals(6, v_append_3.getDimension());
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(4), v_append_3.getEntry(3));
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_append_4 = v1.append(v2_t);
		junit.framework.Assert.assertEquals(6, v_append_4.getDimension());
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(4), v_append_4.getEntry(3));
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_copy = v1.copy();
		junit.framework.Assert.assertEquals(3, v_copy.getDimension());
		junit.framework.Assert.assertNotSame("testData not same object ", v1.data, v_copy.getData());
		org.apache.commons.math.fraction.Fraction[] a_frac = v1.toArray();
		junit.framework.Assert.assertEquals(3, a_frac.length);
		junit.framework.Assert.assertNotSame("testData not same object ", v1.data, a_frac);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> vout5 = v4.getSubVector(3, 3);
		junit.framework.Assert.assertEquals(3, vout5.getDimension());
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(5), vout5.getEntry(1));
		try {
			v4.getSubVector(3, 7);
			junit.framework.Assert.fail("MatrixIndexException expected");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v_set1 = ((org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>)(v1.copy()));
		v_set1.setEntry(1, new org.apache.commons.math.fraction.Fraction(11));
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(11), v_set1.getEntry(1));
		try {
			v_set1.setEntry(3, new org.apache.commons.math.fraction.Fraction(11));
			junit.framework.Assert.fail("MatrixIndexException expected");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v_set2 = ((org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>)(v4.copy()));
		v_set2.set(3, v1);
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(1), v_set2.getEntry(3));
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(7), v_set2.getEntry(6));
		try {
			v_set2.set(7, v1);
			junit.framework.Assert.fail("MatrixIndexException expected");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v_set3 = ((org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>)(v1.copy()));
		v_set3.set(new org.apache.commons.math.fraction.Fraction(13));
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(13), v_set3.getEntry(2));
		try {
			v_set3.getEntry(23);
			junit.framework.Assert.fail("ArrayIndexOutOfBoundsException expected");
		} catch (java.lang.ArrayIndexOutOfBoundsException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v_set4 = ((org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>)(v4.copy()));
		v_set4.setSubVector(3, v2_t);
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(4), v_set4.getEntry(3));
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(7), v_set4.getEntry(6));
		try {
			v_set4.setSubVector(7, v2_t);
			junit.framework.Assert.fail("MatrixIndexException expected");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> vout10 = ((org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>)(v1.copy()));
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> vout10_2 = ((org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>)(v1.copy()));
		junit.framework.Assert.assertEquals(vout10, vout10_2);
		vout10_2.setEntry(0, new org.apache.commons.math.fraction.Fraction(11 , 10));
		junit.framework.Assert.assertNotSame(vout10, vout10_2);
	}

	public void testMapFunctions() {
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v1 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(vec1);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapAdd = v1.mapAdd(new org.apache.commons.math.fraction.Fraction(2));
		org.apache.commons.math.fraction.Fraction[] result_mapAdd = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) };
		checkArray("compare vectors", result_mapAdd, v_mapAdd.getData());
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapAddToSelf = v1.copy();
		v_mapAddToSelf.mapAddToSelf(new org.apache.commons.math.fraction.Fraction(2));
		org.apache.commons.math.fraction.Fraction[] result_mapAddToSelf = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) };
		checkArray("compare vectors", result_mapAddToSelf, v_mapAddToSelf.getData());
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapSubtract = v1.mapSubtract(new org.apache.commons.math.fraction.Fraction(2));
		org.apache.commons.math.fraction.Fraction[] result_mapSubtract = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(-1) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(1) };
		checkArray("compare vectors", result_mapSubtract, v_mapSubtract.getData());
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapSubtractToSelf = v1.copy();
		v_mapSubtractToSelf.mapSubtractToSelf(new org.apache.commons.math.fraction.Fraction(2));
		org.apache.commons.math.fraction.Fraction[] result_mapSubtractToSelf = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(-1) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(1) };
		checkArray("compare vectors", result_mapSubtractToSelf, v_mapSubtractToSelf.getData());
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapMultiply = v1.mapMultiply(new org.apache.commons.math.fraction.Fraction(2));
		org.apache.commons.math.fraction.Fraction[] result_mapMultiply = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(6) };
		checkArray("compare vectors", result_mapMultiply, v_mapMultiply.getData());
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapMultiplyToSelf = v1.copy();
		v_mapMultiplyToSelf.mapMultiplyToSelf(new org.apache.commons.math.fraction.Fraction(2));
		org.apache.commons.math.fraction.Fraction[] result_mapMultiplyToSelf = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(6) };
		checkArray("compare vectors", result_mapMultiplyToSelf, v_mapMultiplyToSelf.getData());
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapDivide = v1.mapDivide(new org.apache.commons.math.fraction.Fraction(2));
		org.apache.commons.math.fraction.Fraction[] result_mapDivide = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1 , 2) , new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(3 , 2) };
		checkArray("compare vectors", result_mapDivide, v_mapDivide.getData());
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapDivideToSelf = v1.copy();
		v_mapDivideToSelf.mapDivideToSelf(new org.apache.commons.math.fraction.Fraction(2));
		org.apache.commons.math.fraction.Fraction[] result_mapDivideToSelf = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1 , 2) , new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(3 , 2) };
		checkArray("compare vectors", result_mapDivideToSelf, v_mapDivideToSelf.getData());
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapInv = v1.mapInv();
		org.apache.commons.math.fraction.Fraction[] result_mapInv = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(1 , 2) , new org.apache.commons.math.fraction.Fraction(1 , 3) };
		checkArray("compare vectors", result_mapInv, v_mapInv.getData());
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapInvToSelf = v1.copy();
		v_mapInvToSelf.mapInvToSelf();
		org.apache.commons.math.fraction.Fraction[] result_mapInvToSelf = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(1 , 2) , new org.apache.commons.math.fraction.Fraction(1 , 3) };
		checkArray("compare vectors", result_mapInvToSelf, v_mapInvToSelf.getData());
	}

	public void testBasicFunctions() {
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v1 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(vec1);
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v2 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(vec2);
		new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(vec_null);
		org.apache.commons.math.linear.ArrayFieldVectorTest.FieldVectorTestImpl<org.apache.commons.math.fraction.Fraction> v2_t = new org.apache.commons.math.linear.ArrayFieldVectorTest.FieldVectorTestImpl<org.apache.commons.math.fraction.Fraction>(vec2);
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v_add = v1.add(v2);
		org.apache.commons.math.fraction.Fraction[] result_add = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(9) };
		checkArray("compare vect", v_add.getData(), result_add);
		org.apache.commons.math.linear.ArrayFieldVectorTest.FieldVectorTestImpl<org.apache.commons.math.fraction.Fraction> vt2 = new org.apache.commons.math.linear.ArrayFieldVectorTest.FieldVectorTestImpl<org.apache.commons.math.fraction.Fraction>(vec2);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_add_i = v1.add(vt2);
		org.apache.commons.math.fraction.Fraction[] result_add_i = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(9) };
		checkArray("compare vect", v_add_i.getData(), result_add_i);
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v_subtract = v1.subtract(v2);
		org.apache.commons.math.fraction.Fraction[] result_subtract = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(-3) , new org.apache.commons.math.fraction.Fraction(-3) , new org.apache.commons.math.fraction.Fraction(-3) };
		checkArray("compare vect", v_subtract.getData(), result_subtract);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_subtract_i = v1.subtract(vt2);
		org.apache.commons.math.fraction.Fraction[] result_subtract_i = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(-3) , new org.apache.commons.math.fraction.Fraction(-3) , new org.apache.commons.math.fraction.Fraction(-3) };
		checkArray("compare vect", v_subtract_i.getData(), result_subtract_i);
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v_ebeMultiply = v1.ebeMultiply(v2);
		org.apache.commons.math.fraction.Fraction[] result_ebeMultiply = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(10) , new org.apache.commons.math.fraction.Fraction(18) };
		checkArray("compare vect", v_ebeMultiply.getData(), result_ebeMultiply);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_ebeMultiply_2 = v1.ebeMultiply(v2_t);
		org.apache.commons.math.fraction.Fraction[] result_ebeMultiply_2 = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(10) , new org.apache.commons.math.fraction.Fraction(18) };
		checkArray("compare vect", v_ebeMultiply_2.getData(), result_ebeMultiply_2);
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v_ebeDivide = v1.ebeDivide(v2);
		org.apache.commons.math.fraction.Fraction[] result_ebeDivide = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1 , 4) , new org.apache.commons.math.fraction.Fraction(2 , 5) , new org.apache.commons.math.fraction.Fraction(1 , 2) };
		checkArray("compare vect", v_ebeDivide.getData(), result_ebeDivide);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_ebeDivide_2 = v1.ebeDivide(v2_t);
		org.apache.commons.math.fraction.Fraction[] result_ebeDivide_2 = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1 , 4) , new org.apache.commons.math.fraction.Fraction(2 , 5) , new org.apache.commons.math.fraction.Fraction(1 , 2) };
		checkArray("compare vect", v_ebeDivide_2.getData(), result_ebeDivide_2);
		org.apache.commons.math.fraction.Fraction dot = v1.dotProduct(v2);
		junit.framework.Assert.assertEquals("compare val ", new org.apache.commons.math.fraction.Fraction(32), dot);
		org.apache.commons.math.fraction.Fraction dot_2 = v1.dotProduct(v2_t);
		junit.framework.Assert.assertEquals("compare val ", new org.apache.commons.math.fraction.Fraction(32), dot_2);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m_outerProduct = v1.outerProduct(v2);
		junit.framework.Assert.assertEquals("compare val ", new org.apache.commons.math.fraction.Fraction(4), m_outerProduct.getEntry(0, 0));
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m_outerProduct_2 = v1.outerProduct(v2_t);
		junit.framework.Assert.assertEquals("compare val ", new org.apache.commons.math.fraction.Fraction(4), m_outerProduct_2.getEntry(0, 0));
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v_projection = v1.projection(v2);
		org.apache.commons.math.fraction.Fraction[] result_projection = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(128 , 77) , new org.apache.commons.math.fraction.Fraction(160 , 77) , new org.apache.commons.math.fraction.Fraction(192 , 77) };
		checkArray("compare vect", v_projection.getData(), result_projection);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_projection_2 = v1.projection(v2_t);
		org.apache.commons.math.fraction.Fraction[] result_projection_2 = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(128 , 77) , new org.apache.commons.math.fraction.Fraction(160 , 77) , new org.apache.commons.math.fraction.Fraction(192 , 77) };
		checkArray("compare vect", v_projection_2.getData(), result_projection_2);
	}

	public void testMisc() {
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v1 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(vec1);
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v4 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(vec4);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v4_2 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(vec4);
		java.lang.String out1 = v1.toString();
		junit.framework.Assert.assertTrue("some output ", ((out1.length()) != 0));
		try {
			v1.checkVectorDimensions(2);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		try {
			v1.checkVectorDimensions(v4);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		try {
			v1.checkVectorDimensions(v4_2);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testSerial() {
		org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction> v = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(vec1);
		junit.framework.Assert.assertEquals(v, org.apache.commons.math.TestUtils.serializeAndRecover(v));
	}

	protected void checkArray(java.lang.String msg, org.apache.commons.math.fraction.Fraction[] m, org.apache.commons.math.fraction.Fraction[] n) {
		if ((m.length) != (n.length)) {
			junit.framework.Assert.fail("vectors have different lengths");
		} 
		for (int i = 0 ; i < (m.length) ; i++) {
			junit.framework.Assert.assertEquals((((msg + " ") + i) + " elements differ"), m[i], n[i]);
		}
	}
}

