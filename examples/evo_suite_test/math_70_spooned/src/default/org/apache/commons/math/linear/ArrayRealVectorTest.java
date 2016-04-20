package org.apache.commons.math.linear;


public class ArrayRealVectorTest extends junit.framework.TestCase {
	protected double[][] ma1 = new double[][]{ new double[]{ 1.0 , 2.0 , 3.0 } , new double[]{ 4.0 , 5.0 , 6.0 } , new double[]{ 7.0 , 8.0 , 9.0 } };

	protected double[] vec1 = new double[]{ 1.0 , 2.0 , 3.0 };

	protected double[] vec2 = new double[]{ 4.0 , 5.0 , 6.0 };

	protected double[] vec3 = new double[]{ 7.0 , 8.0 , 9.0 };

	protected double[] vec4 = new double[]{ 1.0 , 2.0 , 3.0 , 4.0 , 5.0 , 6.0 , 7.0 , 8.0 , 9.0 };

	protected double[] vec5 = new double[]{ -4.0 , 0.0 , 3.0 , 1.0 , -6.0 , 3.0 };

	protected double[] vec_null = new double[]{ 0.0 , 0.0 , 0.0 };

	protected java.lang.Double[] dvec1 = new java.lang.Double[]{ 1.0 , 2.0 , 3.0 , 4.0 , 5.0 , 6.0 , 7.0 , 8.0 , 9.0 };

	protected double[][] mat1 = new double[][]{ new double[]{ 1.0 , 2.0 , 3.0 } , new double[]{ 4.0 , 5.0 , 6.0 } , new double[]{ 7.0 , 8.0 , 9.0 } };

	protected double entryTolerance = 1.0E-15;

	protected double normTolerance = 1.0E-13;

	public static class RealVectorTestImpl implements java.io.Serializable , org.apache.commons.math.linear.RealVector {
		private static final long serialVersionUID = 4715341047369582908L;

		protected double[] data;

		public RealVectorTestImpl(double[] d) {
			data = d.clone();
		}

		private java.lang.UnsupportedOperationException unsupported() {
			return new java.lang.UnsupportedOperationException("Not supported, unneeded for test purposes");
		}

		public org.apache.commons.math.linear.RealVector map(org.apache.commons.math.analysis.UnivariateRealFunction function) throws org.apache.commons.math.FunctionEvaluationException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapToSelf(org.apache.commons.math.analysis.UnivariateRealFunction function) throws org.apache.commons.math.FunctionEvaluationException {
			throw unsupported();
		}

		public java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> iterator() {
			return new java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry>() {
				int i = 0;

				public boolean hasNext() {
					return (i) < (data.length);
				}

				public org.apache.commons.math.linear.RealVector.Entry next() {
					final int j = (i)++;
					org.apache.commons.math.linear.RealVector.Entry e = new org.apache.commons.math.linear.RealVector.Entry() {
						@java.lang.Override
						public double getValue() {
							return data[j];
						}

						@java.lang.Override
						public void setValue(double newValue) {
							data[j] = newValue;
						}
					};
					e.setIndex(j);
					return e;
				}

				public void remove() {
				}
			};
		}

		public java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> sparseIterator() {
			return iterator();
		}

		public org.apache.commons.math.linear.RealVector copy() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector add(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector add(double[] v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector subtract(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector subtract(double[] v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapAdd(double d) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapAddToSelf(double d) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapSubtract(double d) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapSubtractToSelf(double d) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapMultiply(double d) {
			double[] out = new double[data.length];
			for (int i = 0 ; i < (data.length) ; i++) {
				out[i] = (data[i]) * d;
			}
			return new org.apache.commons.math.linear.ArrayRealVector(out);
		}

		public org.apache.commons.math.linear.RealVector mapMultiplyToSelf(double d) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapDivide(double d) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapDivideToSelf(double d) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapPow(double d) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapPowToSelf(double d) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapExp() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapExpToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapExpm1() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapExpm1ToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapLog() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapLogToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapLog10() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapLog10ToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapLog1p() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapLog1pToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapCosh() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapCoshToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapSinh() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapSinhToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapTanh() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapTanhToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapCos() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapCosToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapSin() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapSinToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapTan() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapTanToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapAcos() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapAcosToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapAsin() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapAsinToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapAtan() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapAtanToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapInv() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapInvToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapAbs() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapAbsToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapSqrt() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapSqrtToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapCbrt() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapCbrtToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapCeil() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapCeilToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapFloor() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapFloorToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapRint() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapRintToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapSignum() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapSignumToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapUlp() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector mapUlpToSelf() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector ebeMultiply(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector ebeMultiply(double[] v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector ebeDivide(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector ebeDivide(double[] v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public double[] getData() {
			return data.clone();
		}

		public double dotProduct(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
			double dot = 0;
			for (int i = 0 ; i < (data.length) ; i++) {
				dot += (data[i]) * (v.getEntry(i));
			}
			return dot;
		}

		public double dotProduct(double[] v) throws java.lang.IllegalArgumentException {
			double dot = 0;
			for (int i = 0 ; i < (data.length) ; i++) {
				dot += (data[i]) * (v[i]);
			}
			return dot;
		}

		public double getNorm() {
			throw unsupported();
		}

		public double getL1Norm() {
			throw unsupported();
		}

		public double getLInfNorm() {
			throw unsupported();
		}

		public double getDistance(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public double getDistance(double[] v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public double getL1Distance(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public double getL1Distance(double[] v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public double getLInfDistance(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public double getLInfDistance(double[] v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector unitVector() {
			throw unsupported();
		}

		public void unitize() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector projection(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector projection(double[] v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealMatrix outerProduct(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealMatrix outerProduct(double[] v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public double getEntry(int index) throws org.apache.commons.math.linear.MatrixIndexException {
			return data[index];
		}

		public int getDimension() {
			return data.length;
		}

		public org.apache.commons.math.linear.RealVector append(org.apache.commons.math.linear.RealVector v) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector append(double d) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector append(double[] a) {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector getSubVector(int index, int n) throws org.apache.commons.math.linear.MatrixIndexException {
			throw unsupported();
		}

		public void setEntry(int index, double value) throws org.apache.commons.math.linear.MatrixIndexException {
			throw unsupported();
		}

		public void setSubVector(int index, org.apache.commons.math.linear.RealVector v) throws org.apache.commons.math.linear.MatrixIndexException {
			throw unsupported();
		}

		public void setSubVector(int index, double[] v) throws org.apache.commons.math.linear.MatrixIndexException {
			throw unsupported();
		}

		public void set(double value) {
			throw unsupported();
		}

		public double[] toArray() {
			throw unsupported();
		}

		public boolean isNaN() {
			throw unsupported();
		}

		public boolean isInfinite() {
			throw unsupported();
		}
	}

	public void testConstructors() {
		org.apache.commons.math.linear.ArrayRealVector v0 = new org.apache.commons.math.linear.ArrayRealVector();
		junit.framework.Assert.assertEquals("testData len", 0, v0.getDimension());
		org.apache.commons.math.linear.ArrayRealVector v1 = new org.apache.commons.math.linear.ArrayRealVector(7);
		junit.framework.Assert.assertEquals("testData len", 7, v1.getDimension());
		junit.framework.Assert.assertEquals("testData is 0.0 ", 0.0, v1.getEntry(6));
		org.apache.commons.math.linear.ArrayRealVector v2 = new org.apache.commons.math.linear.ArrayRealVector(5 , 1.23);
		junit.framework.Assert.assertEquals("testData len", 5, v2.getDimension());
		junit.framework.Assert.assertEquals("testData is 1.23 ", 1.23, v2.getEntry(4));
		org.apache.commons.math.linear.ArrayRealVector v3 = new org.apache.commons.math.linear.ArrayRealVector(vec1);
		junit.framework.Assert.assertEquals("testData len", 3, v3.getDimension());
		junit.framework.Assert.assertEquals("testData is 2.0 ", 2.0, v3.getEntry(1));
		org.apache.commons.math.linear.ArrayRealVector v3_bis = new org.apache.commons.math.linear.ArrayRealVector(vec1 , true);
		junit.framework.Assert.assertEquals("testData len", 3, v3_bis.getDimension());
		junit.framework.Assert.assertEquals("testData is 2.0 ", 2.0, v3_bis.getEntry(1));
		junit.framework.Assert.assertNotSame(v3_bis.getDataRef(), vec1);
		junit.framework.Assert.assertNotSame(v3_bis.getData(), vec1);
		org.apache.commons.math.linear.ArrayRealVector v3_ter = new org.apache.commons.math.linear.ArrayRealVector(vec1 , false);
		junit.framework.Assert.assertEquals("testData len", 3, v3_ter.getDimension());
		junit.framework.Assert.assertEquals("testData is 2.0 ", 2.0, v3_ter.getEntry(1));
		junit.framework.Assert.assertSame(v3_ter.getDataRef(), vec1);
		junit.framework.Assert.assertNotSame(v3_ter.getData(), vec1);
		org.apache.commons.math.linear.ArrayRealVector v4 = new org.apache.commons.math.linear.ArrayRealVector(vec4 , 3 , 2);
		junit.framework.Assert.assertEquals("testData len", 2, v4.getDimension());
		junit.framework.Assert.assertEquals("testData is 4.0 ", 4.0, v4.getEntry(0));
		try {
			new org.apache.commons.math.linear.ArrayRealVector(vec4 , 8 , 3);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		org.apache.commons.math.linear.RealVector v5_i = new org.apache.commons.math.linear.ArrayRealVector(dvec1);
		junit.framework.Assert.assertEquals("testData len", 9, v5_i.getDimension());
		junit.framework.Assert.assertEquals("testData is 9.0 ", 9.0, v5_i.getEntry(8));
		org.apache.commons.math.linear.ArrayRealVector v5 = new org.apache.commons.math.linear.ArrayRealVector(dvec1);
		junit.framework.Assert.assertEquals("testData len", 9, v5.getDimension());
		junit.framework.Assert.assertEquals("testData is 9.0 ", 9.0, v5.getEntry(8));
		org.apache.commons.math.linear.ArrayRealVector v6 = new org.apache.commons.math.linear.ArrayRealVector(dvec1 , 3 , 2);
		junit.framework.Assert.assertEquals("testData len", 2, v6.getDimension());
		junit.framework.Assert.assertEquals("testData is 4.0 ", 4.0, v6.getEntry(0));
		try {
			new org.apache.commons.math.linear.ArrayRealVector(dvec1 , 8 , 3);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		org.apache.commons.math.linear.ArrayRealVector v7 = new org.apache.commons.math.linear.ArrayRealVector(v1);
		junit.framework.Assert.assertEquals("testData len", 7, v7.getDimension());
		junit.framework.Assert.assertEquals("testData is 0.0 ", 0.0, v7.getEntry(6));
		org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl v7_i = new org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl(vec1);
		org.apache.commons.math.linear.ArrayRealVector v7_2 = new org.apache.commons.math.linear.ArrayRealVector(v7_i);
		junit.framework.Assert.assertEquals("testData len", 3, v7_2.getDimension());
		junit.framework.Assert.assertEquals("testData is 0.0 ", 2.0, v7_2.getEntry(1));
		org.apache.commons.math.linear.ArrayRealVector v8 = new org.apache.commons.math.linear.ArrayRealVector(v1 , true);
		junit.framework.Assert.assertEquals("testData len", 7, v8.getDimension());
		junit.framework.Assert.assertEquals("testData is 0.0 ", 0.0, v8.getEntry(6));
		junit.framework.Assert.assertNotSame("testData not same object ", v1.data, v8.data);
		org.apache.commons.math.linear.ArrayRealVector v8_2 = new org.apache.commons.math.linear.ArrayRealVector(v1 , false);
		junit.framework.Assert.assertEquals("testData len", 7, v8_2.getDimension());
		junit.framework.Assert.assertEquals("testData is 0.0 ", 0.0, v8_2.getEntry(6));
		junit.framework.Assert.assertEquals("testData same object ", v1.data, v8_2.data);
		org.apache.commons.math.linear.ArrayRealVector v9 = new org.apache.commons.math.linear.ArrayRealVector(v1 , v3);
		junit.framework.Assert.assertEquals("testData len", 10, v9.getDimension());
		junit.framework.Assert.assertEquals("testData is 1.0 ", 1.0, v9.getEntry(7));
		org.apache.commons.math.linear.ArrayRealVector v10 = new org.apache.commons.math.linear.ArrayRealVector(v2 , new org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl(vec3));
		junit.framework.Assert.assertEquals("testData len", 8, v10.getDimension());
		junit.framework.Assert.assertEquals("testData is 1.23 ", 1.23, v10.getEntry(4));
		junit.framework.Assert.assertEquals("testData is 7.0 ", 7.0, v10.getEntry(5));
		org.apache.commons.math.linear.ArrayRealVector v11 = new org.apache.commons.math.linear.ArrayRealVector(new org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl(vec3) , v2);
		junit.framework.Assert.assertEquals("testData len", 8, v11.getDimension());
		junit.framework.Assert.assertEquals("testData is 9.0 ", 9.0, v11.getEntry(2));
		junit.framework.Assert.assertEquals("testData is 1.23 ", 1.23, v11.getEntry(3));
		org.apache.commons.math.linear.ArrayRealVector v12 = new org.apache.commons.math.linear.ArrayRealVector(v2 , vec3);
		junit.framework.Assert.assertEquals("testData len", 8, v12.getDimension());
		junit.framework.Assert.assertEquals("testData is 1.23 ", 1.23, v12.getEntry(4));
		junit.framework.Assert.assertEquals("testData is 7.0 ", 7.0, v12.getEntry(5));
		org.apache.commons.math.linear.ArrayRealVector v13 = new org.apache.commons.math.linear.ArrayRealVector(vec3 , v2);
		junit.framework.Assert.assertEquals("testData len", 8, v13.getDimension());
		junit.framework.Assert.assertEquals("testData is 9.0 ", 9.0, v13.getEntry(2));
		junit.framework.Assert.assertEquals("testData is 1.23 ", 1.23, v13.getEntry(3));
		org.apache.commons.math.linear.ArrayRealVector v14 = new org.apache.commons.math.linear.ArrayRealVector(vec3 , vec4);
		junit.framework.Assert.assertEquals("testData len", 12, v14.getDimension());
		junit.framework.Assert.assertEquals("testData is 9.0 ", 9.0, v14.getEntry(2));
		junit.framework.Assert.assertEquals("testData is 1.0 ", 1.0, v14.getEntry(3));
		try {
			new org.apache.commons.math.linear.ArrayRealVector(((double[])(null)) , false);
			junit.framework.Assert.fail("expected exception");
		} catch (java.lang.NullPointerException npe) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		try {
			new org.apache.commons.math.linear.ArrayRealVector(new double[0] , false);
			junit.framework.Assert.fail("expected exception");
		} catch (java.lang.IllegalArgumentException iae) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testDataInOut() {
		org.apache.commons.math.linear.ArrayRealVector v1 = new org.apache.commons.math.linear.ArrayRealVector(vec1);
		org.apache.commons.math.linear.ArrayRealVector v2 = new org.apache.commons.math.linear.ArrayRealVector(vec2);
		org.apache.commons.math.linear.ArrayRealVector v4 = new org.apache.commons.math.linear.ArrayRealVector(vec4);
		org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl v2_t = new org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl(vec2);
		org.apache.commons.math.linear.RealVector v_append_1 = v1.append(v2);
		junit.framework.Assert.assertEquals("testData len", 6, v_append_1.getDimension());
		junit.framework.Assert.assertEquals("testData is 4.0 ", 4.0, v_append_1.getEntry(3));
		org.apache.commons.math.linear.RealVector v_append_2 = v1.append(2.0);
		junit.framework.Assert.assertEquals("testData len", 4, v_append_2.getDimension());
		junit.framework.Assert.assertEquals("testData is 2.0 ", 2.0, v_append_2.getEntry(3));
		org.apache.commons.math.linear.RealVector v_append_3 = v1.append(vec2);
		junit.framework.Assert.assertEquals("testData len", 6, v_append_3.getDimension());
		junit.framework.Assert.assertEquals("testData is  ", 4.0, v_append_3.getEntry(3));
		org.apache.commons.math.linear.RealVector v_append_4 = v1.append(v2_t);
		junit.framework.Assert.assertEquals("testData len", 6, v_append_4.getDimension());
		junit.framework.Assert.assertEquals("testData is 4.0 ", 4.0, v_append_4.getEntry(3));
		org.apache.commons.math.linear.RealVector v_append_5 = v1.append(((org.apache.commons.math.linear.RealVector)(v2)));
		junit.framework.Assert.assertEquals("testData len", 6, v_append_5.getDimension());
		junit.framework.Assert.assertEquals("testData is 4.0 ", 4.0, v_append_5.getEntry(3));
		org.apache.commons.math.linear.RealVector v_copy = v1.copy();
		junit.framework.Assert.assertEquals("testData len", 3, v_copy.getDimension());
		junit.framework.Assert.assertNotSame("testData not same object ", v1.data, v_copy.getData());
		double[] a_double = v1.toArray();
		junit.framework.Assert.assertEquals("testData len", 3, a_double.length);
		junit.framework.Assert.assertNotSame("testData not same object ", v1.data, a_double);
		org.apache.commons.math.linear.RealVector vout5 = v4.getSubVector(3, 3);
		junit.framework.Assert.assertEquals("testData len", 3, vout5.getDimension());
		junit.framework.Assert.assertEquals("testData is 4.0 ", 5.0, vout5.getEntry(1));
		try {
			v4.getSubVector(3, 7);
			junit.framework.Assert.fail("MatrixIndexException expected");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		org.apache.commons.math.linear.ArrayRealVector v_set1 = ((org.apache.commons.math.linear.ArrayRealVector)(v1.copy()));
		v_set1.setEntry(1, 11.0);
		junit.framework.Assert.assertEquals("testData is 11.0 ", 11.0, v_set1.getEntry(1));
		try {
			v_set1.setEntry(3, 11.0);
			junit.framework.Assert.fail("MatrixIndexException expected");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		org.apache.commons.math.linear.ArrayRealVector v_set2 = ((org.apache.commons.math.linear.ArrayRealVector)(v4.copy()));
		v_set2.set(3, v1);
		junit.framework.Assert.assertEquals("testData is 1.0 ", 1.0, v_set2.getEntry(3));
		junit.framework.Assert.assertEquals("testData is 7.0 ", 7.0, v_set2.getEntry(6));
		try {
			v_set2.set(7, v1);
			junit.framework.Assert.fail("MatrixIndexException expected");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		org.apache.commons.math.linear.ArrayRealVector v_set3 = ((org.apache.commons.math.linear.ArrayRealVector)(v1.copy()));
		v_set3.set(13.0);
		junit.framework.Assert.assertEquals("testData is 13.0 ", 13.0, v_set3.getEntry(2));
		try {
			v_set3.getEntry(23);
			junit.framework.Assert.fail("ArrayIndexOutOfBoundsException expected");
		} catch (java.lang.ArrayIndexOutOfBoundsException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		org.apache.commons.math.linear.ArrayRealVector v_set4 = ((org.apache.commons.math.linear.ArrayRealVector)(v4.copy()));
		v_set4.setSubVector(3, v2_t);
		junit.framework.Assert.assertEquals("testData is 1.0 ", 4.0, v_set4.getEntry(3));
		junit.framework.Assert.assertEquals("testData is 7.0 ", 7.0, v_set4.getEntry(6));
		try {
			v_set4.setSubVector(7, v2_t);
			junit.framework.Assert.fail("MatrixIndexException expected");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		org.apache.commons.math.linear.ArrayRealVector vout10 = ((org.apache.commons.math.linear.ArrayRealVector)(v1.copy()));
		org.apache.commons.math.linear.ArrayRealVector vout10_2 = ((org.apache.commons.math.linear.ArrayRealVector)(v1.copy()));
		junit.framework.Assert.assertEquals(vout10, vout10_2);
		vout10_2.setEntry(0, 1.1);
		junit.framework.Assert.assertNotSame(vout10, vout10_2);
	}

	public void testMapFunctions() {
		org.apache.commons.math.linear.ArrayRealVector v1 = new org.apache.commons.math.linear.ArrayRealVector(vec1);
		org.apache.commons.math.linear.RealVector v_mapAdd = v1.mapAdd(2.0);
		double[] result_mapAdd = new double[]{ 3.0 , 4.0 , 5.0 };
		assertClose("compare vectors", result_mapAdd, v_mapAdd.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapAddToSelf = v1.copy();
		v_mapAddToSelf.mapAddToSelf(2.0);
		double[] result_mapAddToSelf = new double[]{ 3.0 , 4.0 , 5.0 };
		assertClose("compare vectors", result_mapAddToSelf, v_mapAddToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapSubtract = v1.mapSubtract(2.0);
		double[] result_mapSubtract = new double[]{ -1.0 , 0.0 , 1.0 };
		assertClose("compare vectors", result_mapSubtract, v_mapSubtract.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapSubtractToSelf = v1.copy();
		v_mapSubtractToSelf.mapSubtractToSelf(2.0);
		double[] result_mapSubtractToSelf = new double[]{ -1.0 , 0.0 , 1.0 };
		assertClose("compare vectors", result_mapSubtractToSelf, v_mapSubtractToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapMultiply = v1.mapMultiply(2.0);
		double[] result_mapMultiply = new double[]{ 2.0 , 4.0 , 6.0 };
		assertClose("compare vectors", result_mapMultiply, v_mapMultiply.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapMultiplyToSelf = v1.copy();
		v_mapMultiplyToSelf.mapMultiplyToSelf(2.0);
		double[] result_mapMultiplyToSelf = new double[]{ 2.0 , 4.0 , 6.0 };
		assertClose("compare vectors", result_mapMultiplyToSelf, v_mapMultiplyToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapDivide = v1.mapDivide(2.0);
		double[] result_mapDivide = new double[]{ 0.5 , 1.0 , 1.5 };
		assertClose("compare vectors", result_mapDivide, v_mapDivide.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapDivideToSelf = v1.copy();
		v_mapDivideToSelf.mapDivideToSelf(2.0);
		double[] result_mapDivideToSelf = new double[]{ 0.5 , 1.0 , 1.5 };
		assertClose("compare vectors", result_mapDivideToSelf, v_mapDivideToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapPow = v1.mapPow(2.0);
		double[] result_mapPow = new double[]{ 1.0 , 4.0 , 9.0 };
		assertClose("compare vectors", result_mapPow, v_mapPow.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapPowToSelf = v1.copy();
		v_mapPowToSelf.mapPowToSelf(2.0);
		double[] result_mapPowToSelf = new double[]{ 1.0 , 4.0 , 9.0 };
		assertClose("compare vectors", result_mapPowToSelf, v_mapPowToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapExp = v1.mapExp();
		double[] result_mapExp = new double[]{ 2.718281828459045 , 7.38905609893065 , 20.08553692318767 };
		assertClose("compare vectors", result_mapExp, v_mapExp.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapExpToSelf = v1.copy();
		v_mapExpToSelf.mapExpToSelf();
		double[] result_mapExpToSelf = new double[]{ 2.718281828459045 , 7.38905609893065 , 20.08553692318767 };
		assertClose("compare vectors", result_mapExpToSelf, v_mapExpToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapExpm1 = v1.mapExpm1();
		double[] result_mapExpm1 = new double[]{ 1.718281828459045 , 6.38905609893065 , 19.085536923187668 };
		assertClose("compare vectors", result_mapExpm1, v_mapExpm1.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapExpm1ToSelf = v1.copy();
		v_mapExpm1ToSelf.mapExpm1ToSelf();
		double[] result_mapExpm1ToSelf = new double[]{ 1.718281828459045 , 6.38905609893065 , 19.085536923187668 };
		assertClose("compare vectors", result_mapExpm1ToSelf, v_mapExpm1ToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapLog = v1.mapLog();
		double[] result_mapLog = new double[]{ 0.0 , 0.6931471805599453 , 1.09861228866811 };
		assertClose("compare vectors", result_mapLog, v_mapLog.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapLogToSelf = v1.copy();
		v_mapLogToSelf.mapLogToSelf();
		double[] result_mapLogToSelf = new double[]{ 0.0 , 0.6931471805599453 , 1.09861228866811 };
		assertClose("compare vectors", result_mapLogToSelf, v_mapLogToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapLog10 = v1.mapLog10();
		double[] result_mapLog10 = new double[]{ 0.0 , 0.3010299956639812 , 0.4771212547196624 };
		assertClose("compare vectors", result_mapLog10, v_mapLog10.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapLog10ToSelf = v1.copy();
		v_mapLog10ToSelf.mapLog10ToSelf();
		double[] result_mapLog10ToSelf = new double[]{ 0.0 , 0.3010299956639812 , 0.4771212547196624 };
		assertClose("compare vectors", result_mapLog10ToSelf, v_mapLog10ToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapLog1p = v1.mapLog1p();
		double[] result_mapLog1p = new double[]{ 0.6931471805599453 , 1.0986122886681096 , 1.3862943611198906 };
		assertClose("compare vectors", result_mapLog1p, v_mapLog1p.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapLog1pToSelf = v1.copy();
		v_mapLog1pToSelf.mapLog1pToSelf();
		double[] result_mapLog1pToSelf = new double[]{ 0.6931471805599453 , 1.0986122886681096 , 1.3862943611198906 };
		assertClose("compare vectors", result_mapLog1pToSelf, v_mapLog1pToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapCosh = v1.mapCosh();
		double[] result_mapCosh = new double[]{ 1.543080634815244 , 3.762195691083631 , 10.06766199577777 };
		assertClose("compare vectors", result_mapCosh, v_mapCosh.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapCoshToSelf = v1.copy();
		v_mapCoshToSelf.mapCoshToSelf();
		double[] result_mapCoshToSelf = new double[]{ 1.543080634815244 , 3.762195691083631 , 10.06766199577777 };
		assertClose("compare vectors", result_mapCoshToSelf, v_mapCoshToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapSinh = v1.mapSinh();
		double[] result_mapSinh = new double[]{ 1.175201193643801 , 3.626860407847019 , 10.0178749274099 };
		assertClose("compare vectors", result_mapSinh, v_mapSinh.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapSinhToSelf = v1.copy();
		v_mapSinhToSelf.mapSinhToSelf();
		double[] result_mapSinhToSelf = new double[]{ 1.175201193643801 , 3.626860407847019 , 10.0178749274099 };
		assertClose("compare vectors", result_mapSinhToSelf, v_mapSinhToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapTanh = v1.mapTanh();
		double[] result_mapTanh = new double[]{ 0.7615941559557649 , 0.9640275800758169 , 0.9950547536867305 };
		assertClose("compare vectors", result_mapTanh, v_mapTanh.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapTanhToSelf = v1.copy();
		v_mapTanhToSelf.mapTanhToSelf();
		double[] result_mapTanhToSelf = new double[]{ 0.7615941559557649 , 0.9640275800758169 , 0.9950547536867305 };
		assertClose("compare vectors", result_mapTanhToSelf, v_mapTanhToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapCos = v1.mapCos();
		double[] result_mapCos = new double[]{ 0.5403023058681398 , -0.4161468365471424 , -0.9899924966004454 };
		assertClose("compare vectors", result_mapCos, v_mapCos.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapCosToSelf = v1.copy();
		v_mapCosToSelf.mapCosToSelf();
		double[] result_mapCosToSelf = new double[]{ 0.5403023058681398 , -0.4161468365471424 , -0.9899924966004454 };
		assertClose("compare vectors", result_mapCosToSelf, v_mapCosToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapSin = v1.mapSin();
		double[] result_mapSin = new double[]{ 0.8414709848078965 , 0.9092974268256817 , 0.1411200080598672 };
		assertClose("compare vectors", result_mapSin, v_mapSin.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapSinToSelf = v1.copy();
		v_mapSinToSelf.mapSinToSelf();
		double[] result_mapSinToSelf = new double[]{ 0.8414709848078965 , 0.9092974268256817 , 0.1411200080598672 };
		assertClose("compare vectors", result_mapSinToSelf, v_mapSinToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapTan = v1.mapTan();
		double[] result_mapTan = new double[]{ 1.557407724654902 , -2.185039863261519 , -0.1425465430742778 };
		assertClose("compare vectors", result_mapTan, v_mapTan.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapTanToSelf = v1.copy();
		v_mapTanToSelf.mapTanToSelf();
		double[] result_mapTanToSelf = new double[]{ 1.557407724654902 , -2.185039863261519 , -0.1425465430742778 };
		assertClose("compare vectors", result_mapTanToSelf, v_mapTanToSelf.getData(), normTolerance);
		double[] vat_a = new double[]{ 0.0 , 0.5 , 1.0 };
		org.apache.commons.math.linear.ArrayRealVector vat = new org.apache.commons.math.linear.ArrayRealVector(vat_a);
		org.apache.commons.math.linear.RealVector v_mapAcos = vat.mapAcos();
		double[] result_mapAcos = new double[]{ 1.570796326794897 , 1.047197551196598 , 0.0 };
		assertClose("compare vectors", result_mapAcos, v_mapAcos.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapAcosToSelf = vat.copy();
		v_mapAcosToSelf.mapAcosToSelf();
		double[] result_mapAcosToSelf = new double[]{ 1.570796326794897 , 1.047197551196598 , 0.0 };
		assertClose("compare vectors", result_mapAcosToSelf, v_mapAcosToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapAsin = vat.mapAsin();
		double[] result_mapAsin = new double[]{ 0.0 , 0.5235987755982989 , 1.570796326794897 };
		assertClose("compare vectors", result_mapAsin, v_mapAsin.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapAsinToSelf = vat.copy();
		v_mapAsinToSelf.mapAsinToSelf();
		double[] result_mapAsinToSelf = new double[]{ 0.0 , 0.5235987755982989 , 1.570796326794897 };
		assertClose("compare vectors", result_mapAsinToSelf, v_mapAsinToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapAtan = vat.mapAtan();
		double[] result_mapAtan = new double[]{ 0.0 , 0.4636476090008061 , 0.7853981633974483 };
		assertClose("compare vectors", result_mapAtan, v_mapAtan.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapAtanToSelf = vat.copy();
		v_mapAtanToSelf.mapAtanToSelf();
		double[] result_mapAtanToSelf = new double[]{ 0.0 , 0.4636476090008061 , 0.7853981633974483 };
		assertClose("compare vectors", result_mapAtanToSelf, v_mapAtanToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapInv = v1.mapInv();
		double[] result_mapInv = new double[]{ 1.0 , 0.5 , 0.3333333333333333 };
		assertClose("compare vectors", result_mapInv, v_mapInv.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapInvToSelf = v1.copy();
		v_mapInvToSelf.mapInvToSelf();
		double[] result_mapInvToSelf = new double[]{ 1.0 , 0.5 , 0.3333333333333333 };
		assertClose("compare vectors", result_mapInvToSelf, v_mapInvToSelf.getData(), normTolerance);
		double[] abs_a = new double[]{ -1.0 , 0.0 , 1.0 };
		org.apache.commons.math.linear.ArrayRealVector abs_v = new org.apache.commons.math.linear.ArrayRealVector(abs_a);
		org.apache.commons.math.linear.RealVector v_mapAbs = abs_v.mapAbs();
		double[] result_mapAbs = new double[]{ 1.0 , 0.0 , 1.0 };
		assertClose("compare vectors", result_mapAbs, v_mapAbs.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapAbsToSelf = abs_v.copy();
		v_mapAbsToSelf.mapAbsToSelf();
		double[] result_mapAbsToSelf = new double[]{ 1.0 , 0.0 , 1.0 };
		assertClose("compare vectors", result_mapAbsToSelf, v_mapAbsToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapSqrt = v1.mapSqrt();
		double[] result_mapSqrt = new double[]{ 1.0 , 1.414213562373095 , 1.732050807568877 };
		assertClose("compare vectors", result_mapSqrt, v_mapSqrt.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapSqrtToSelf = v1.copy();
		v_mapSqrtToSelf.mapSqrtToSelf();
		double[] result_mapSqrtToSelf = new double[]{ 1.0 , 1.414213562373095 , 1.732050807568877 };
		assertClose("compare vectors", result_mapSqrtToSelf, v_mapSqrtToSelf.getData(), normTolerance);
		double[] cbrt_a = new double[]{ -2.0 , 0.0 , 2.0 };
		org.apache.commons.math.linear.ArrayRealVector cbrt_v = new org.apache.commons.math.linear.ArrayRealVector(cbrt_a);
		org.apache.commons.math.linear.RealVector v_mapCbrt = cbrt_v.mapCbrt();
		double[] result_mapCbrt = new double[]{ -1.2599210498948732 , 0.0 , 1.2599210498948732 };
		assertClose("compare vectors", result_mapCbrt, v_mapCbrt.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapCbrtToSelf = cbrt_v.copy();
		v_mapCbrtToSelf.mapCbrtToSelf();
		double[] result_mapCbrtToSelf = new double[]{ -1.2599210498948732 , 0.0 , 1.2599210498948732 };
		assertClose("compare vectors", result_mapCbrtToSelf, v_mapCbrtToSelf.getData(), normTolerance);
		double[] ceil_a = new double[]{ -1.1 , 0.9 , 1.1 };
		org.apache.commons.math.linear.ArrayRealVector ceil_v = new org.apache.commons.math.linear.ArrayRealVector(ceil_a);
		org.apache.commons.math.linear.RealVector v_mapCeil = ceil_v.mapCeil();
		double[] result_mapCeil = new double[]{ -1.0 , 1.0 , 2.0 };
		assertClose("compare vectors", result_mapCeil, v_mapCeil.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapCeilToSelf = ceil_v.copy();
		v_mapCeilToSelf.mapCeilToSelf();
		double[] result_mapCeilToSelf = new double[]{ -1.0 , 1.0 , 2.0 };
		assertClose("compare vectors", result_mapCeilToSelf, v_mapCeilToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapFloor = ceil_v.mapFloor();
		double[] result_mapFloor = new double[]{ -2.0 , 0.0 , 1.0 };
		assertClose("compare vectors", result_mapFloor, v_mapFloor.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapFloorToSelf = ceil_v.copy();
		v_mapFloorToSelf.mapFloorToSelf();
		double[] result_mapFloorToSelf = new double[]{ -2.0 , 0.0 , 1.0 };
		assertClose("compare vectors", result_mapFloorToSelf, v_mapFloorToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapRint = ceil_v.mapRint();
		double[] result_mapRint = new double[]{ -1.0 , 1.0 , 1.0 };
		assertClose("compare vectors", result_mapRint, v_mapRint.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapRintToSelf = ceil_v.copy();
		v_mapRintToSelf.mapRintToSelf();
		double[] result_mapRintToSelf = new double[]{ -1.0 , 1.0 , 1.0 };
		assertClose("compare vectors", result_mapRintToSelf, v_mapRintToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapSignum = ceil_v.mapSignum();
		double[] result_mapSignum = new double[]{ -1.0 , 1.0 , 1.0 };
		assertClose("compare vectors", result_mapSignum, v_mapSignum.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapSignumToSelf = ceil_v.copy();
		v_mapSignumToSelf.mapSignumToSelf();
		double[] result_mapSignumToSelf = new double[]{ -1.0 , 1.0 , 1.0 };
		assertClose("compare vectors", result_mapSignumToSelf, v_mapSignumToSelf.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapUlp = ceil_v.mapUlp();
		double[] result_mapUlp = new double[]{ 2.220446049250313E-16 , 1.1102230246251565E-16 , 2.220446049250313E-16 };
		assertClose("compare vectors", result_mapUlp, v_mapUlp.getData(), normTolerance);
		org.apache.commons.math.linear.RealVector v_mapUlpToSelf = ceil_v.copy();
		v_mapUlpToSelf.mapUlpToSelf();
		double[] result_mapUlpToSelf = new double[]{ 2.220446049250313E-16 , 1.1102230246251565E-16 , 2.220446049250313E-16 };
		assertClose("compare vectors", result_mapUlpToSelf, v_mapUlpToSelf.getData(), normTolerance);
	}

	public void testBasicFunctions() {
		org.apache.commons.math.linear.ArrayRealVector v1 = new org.apache.commons.math.linear.ArrayRealVector(vec1);
		org.apache.commons.math.linear.ArrayRealVector v2 = new org.apache.commons.math.linear.ArrayRealVector(vec2);
		org.apache.commons.math.linear.ArrayRealVector v5 = new org.apache.commons.math.linear.ArrayRealVector(vec5);
		org.apache.commons.math.linear.ArrayRealVector v_null = new org.apache.commons.math.linear.ArrayRealVector(vec_null);
		org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl v2_t = new org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl(vec2);
		double d_getNorm = v5.getNorm();
		junit.framework.Assert.assertEquals("compare values  ", 8.426149773176359, d_getNorm);
		double d_getL1Norm = v5.getL1Norm();
		junit.framework.Assert.assertEquals("compare values  ", 17.0, d_getL1Norm);
		double d_getLInfNorm = v5.getLInfNorm();
		junit.framework.Assert.assertEquals("compare values  ", 6.0, d_getLInfNorm);
		double dist = v1.getDistance(v2);
		junit.framework.Assert.assertEquals("compare values  ", v1.subtract(v2).getNorm(), dist);
		double dist_2 = v1.getDistance(v2_t);
		junit.framework.Assert.assertEquals("compare values  ", v1.subtract(v2).getNorm(), dist_2);
		double dist_3 = v1.getDistance(((org.apache.commons.math.linear.RealVector)(v2)));
		junit.framework.Assert.assertEquals("compare values  ", v1.subtract(v2).getNorm(), dist_3);
		double d_getL1Distance = v1.getL1Distance(v2);
		junit.framework.Assert.assertEquals("compare values  ", 9.0, d_getL1Distance);
		double d_getL1Distance_2 = v1.getL1Distance(v2_t);
		junit.framework.Assert.assertEquals("compare values  ", 9.0, d_getL1Distance_2);
		double d_getL1Distance_3 = v1.getL1Distance(((org.apache.commons.math.linear.RealVector)(v2)));
		junit.framework.Assert.assertEquals("compare values  ", 9.0, d_getL1Distance_3);
		double d_getLInfDistance = v1.getLInfDistance(v2);
		junit.framework.Assert.assertEquals("compare values  ", 3.0, d_getLInfDistance);
		double d_getLInfDistance_2 = v1.getLInfDistance(v2_t);
		junit.framework.Assert.assertEquals("compare values  ", 3.0, d_getLInfDistance_2);
		double d_getLInfDistance_3 = v1.getLInfDistance(((org.apache.commons.math.linear.RealVector)(v2)));
		junit.framework.Assert.assertEquals("compare values  ", 3.0, d_getLInfDistance_3);
		org.apache.commons.math.linear.ArrayRealVector v_add = v1.add(v2);
		double[] result_add = new double[]{ 5.0 , 7.0 , 9.0 };
		assertClose("compare vect", v_add.getData(), result_add, normTolerance);
		org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl vt2 = new org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl(vec2);
		org.apache.commons.math.linear.RealVector v_add_i = v1.add(vt2);
		double[] result_add_i = new double[]{ 5.0 , 7.0 , 9.0 };
		assertClose("compare vect", v_add_i.getData(), result_add_i, normTolerance);
		org.apache.commons.math.linear.ArrayRealVector v_subtract = v1.subtract(v2);
		double[] result_subtract = new double[]{ -3.0 , -3.0 , -3.0 };
		assertClose("compare vect", v_subtract.getData(), result_subtract, normTolerance);
		org.apache.commons.math.linear.RealVector v_subtract_i = v1.subtract(vt2);
		double[] result_subtract_i = new double[]{ -3.0 , -3.0 , -3.0 };
		assertClose("compare vect", v_subtract_i.getData(), result_subtract_i, normTolerance);
		org.apache.commons.math.linear.ArrayRealVector v_ebeMultiply = v1.ebeMultiply(v2);
		double[] result_ebeMultiply = new double[]{ 4.0 , 10.0 , 18.0 };
		assertClose("compare vect", v_ebeMultiply.getData(), result_ebeMultiply, normTolerance);
		org.apache.commons.math.linear.RealVector v_ebeMultiply_2 = v1.ebeMultiply(v2_t);
		double[] result_ebeMultiply_2 = new double[]{ 4.0 , 10.0 , 18.0 };
		assertClose("compare vect", v_ebeMultiply_2.getData(), result_ebeMultiply_2, normTolerance);
		org.apache.commons.math.linear.RealVector v_ebeMultiply_3 = v1.ebeMultiply(((org.apache.commons.math.linear.RealVector)(v2)));
		double[] result_ebeMultiply_3 = new double[]{ 4.0 , 10.0 , 18.0 };
		assertClose("compare vect", v_ebeMultiply_3.getData(), result_ebeMultiply_3, normTolerance);
		org.apache.commons.math.linear.ArrayRealVector v_ebeDivide = v1.ebeDivide(v2);
		double[] result_ebeDivide = new double[]{ 0.25 , 0.4 , 0.5 };
		assertClose("compare vect", v_ebeDivide.getData(), result_ebeDivide, normTolerance);
		org.apache.commons.math.linear.RealVector v_ebeDivide_2 = v1.ebeDivide(v2_t);
		double[] result_ebeDivide_2 = new double[]{ 0.25 , 0.4 , 0.5 };
		assertClose("compare vect", v_ebeDivide_2.getData(), result_ebeDivide_2, normTolerance);
		org.apache.commons.math.linear.RealVector v_ebeDivide_3 = v1.ebeDivide(((org.apache.commons.math.linear.RealVector)(v2)));
		double[] result_ebeDivide_3 = new double[]{ 0.25 , 0.4 , 0.5 };
		assertClose("compare vect", v_ebeDivide_3.getData(), result_ebeDivide_3, normTolerance);
		double dot = v1.dotProduct(v2);
		junit.framework.Assert.assertEquals("compare val ", 32.0, dot);
		double dot_2 = v1.dotProduct(v2_t);
		junit.framework.Assert.assertEquals("compare val ", 32.0, dot_2);
		org.apache.commons.math.linear.RealMatrix m_outerProduct = v1.outerProduct(v2);
		junit.framework.Assert.assertEquals("compare val ", 4.0, m_outerProduct.getEntry(0, 0));
		org.apache.commons.math.linear.RealMatrix m_outerProduct_2 = v1.outerProduct(v2_t);
		junit.framework.Assert.assertEquals("compare val ", 4.0, m_outerProduct_2.getEntry(0, 0));
		org.apache.commons.math.linear.RealMatrix m_outerProduct_3 = v1.outerProduct(((org.apache.commons.math.linear.RealVector)(v2)));
		junit.framework.Assert.assertEquals("compare val ", 4.0, m_outerProduct_3.getEntry(0, 0));
		org.apache.commons.math.linear.RealVector v_unitVector = v1.unitVector();
		org.apache.commons.math.linear.RealVector v_unitVector_2 = v1.mapDivide(v1.getNorm());
		assertClose("compare vect", v_unitVector.getData(), v_unitVector_2.getData(), normTolerance);
		try {
			v_null.unitVector();
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		org.apache.commons.math.linear.ArrayRealVector v_unitize = ((org.apache.commons.math.linear.ArrayRealVector)(v1.copy()));
		v_unitize.unitize();
		assertClose("compare vect", v_unitVector_2.getData(), v_unitize.getData(), normTolerance);
		try {
			v_null.unitize();
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		org.apache.commons.math.linear.ArrayRealVector v_projection = v1.projection(v2);
		double[] result_projection = new double[]{ 1.662337662337662 , 2.0779220779220777 , 2.493506493506493 };
		assertClose("compare vect", v_projection.getData(), result_projection, normTolerance);
		org.apache.commons.math.linear.RealVector v_projection_2 = v1.projection(v2_t);
		double[] result_projection_2 = new double[]{ 1.662337662337662 , 2.0779220779220777 , 2.493506493506493 };
		assertClose("compare vect", v_projection_2.getData(), result_projection_2, normTolerance);
		org.apache.commons.math.linear.RealVector v_projection_3 = v1.projection(v2.getData());
		double[] result_projection_3 = new double[]{ 1.662337662337662 , 2.0779220779220777 , 2.493506493506493 };
		assertClose("compare vect", v_projection_3.getData(), result_projection_3, normTolerance);
	}

	public void testMisc() {
		org.apache.commons.math.linear.ArrayRealVector v1 = new org.apache.commons.math.linear.ArrayRealVector(vec1);
		org.apache.commons.math.linear.ArrayRealVector v4 = new org.apache.commons.math.linear.ArrayRealVector(vec4);
		org.apache.commons.math.linear.RealVector v4_2 = new org.apache.commons.math.linear.ArrayRealVector(vec4);
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

	public void testPredicates() {
		org.apache.commons.math.linear.ArrayRealVector v = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 0 , 1 , 2 });
		junit.framework.Assert.assertFalse(v.isNaN());
		v.setEntry(1, java.lang.Double.NaN);
		junit.framework.Assert.assertTrue(v.isNaN());
		junit.framework.Assert.assertFalse(v.isInfinite());
		v.setEntry(0, java.lang.Double.POSITIVE_INFINITY);
		junit.framework.Assert.assertFalse(v.isInfinite());
		v.setEntry(1, 1);
		junit.framework.Assert.assertTrue(v.isInfinite());
		v.setEntry(0, 1);
		junit.framework.Assert.assertFalse(v.isInfinite());
		v.setEntry(0, 0);
		junit.framework.Assert.assertEquals(v, new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 0 , 1 , 2 }));
		junit.framework.Assert.assertNotSame(v, new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 0 , 1 , 2 + (java.lang.Math.ulp(2)) }));
		junit.framework.Assert.assertNotSame(v, new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 0 , 1 , 2 , 3 }));
		junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.ArrayRealVector(new double[]{ java.lang.Double.NaN , 1 , 2 }).hashCode(), new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 0 , java.lang.Double.NaN , 2 }).hashCode());
		junit.framework.Assert.assertTrue(((new org.apache.commons.math.linear.ArrayRealVector(new double[]{ java.lang.Double.NaN , 1 , 2 }).hashCode()) != (new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 0 , 1 , 2 }).hashCode())));
		junit.framework.Assert.assertTrue(v.equals(v));
		junit.framework.Assert.assertTrue(v.equals(v.copy()));
		junit.framework.Assert.assertFalse(v.equals(null));
		junit.framework.Assert.assertFalse(v.equals(v.getDataRef()));
		junit.framework.Assert.assertFalse(v.equals(v.getSubVector(0, ((v.getDimension()) - 1))));
		junit.framework.Assert.assertTrue(v.equals(v.getSubVector(0, v.getDimension())));
	}

	public void testSerial() {
		org.apache.commons.math.linear.ArrayRealVector v = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 0 , 1 , 2 });
		junit.framework.Assert.assertEquals(v, org.apache.commons.math.TestUtils.serializeAndRecover(v));
	}

	public void testMinMax() {
		org.apache.commons.math.linear.ArrayRealVector v1 = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 0 , -6 , 4 , 12 , 7 });
		junit.framework.Assert.assertEquals(1, v1.getMinIndex());
		junit.framework.Assert.assertEquals(-6, v1.getMinValue(), 1.0E-12);
		junit.framework.Assert.assertEquals(3, v1.getMaxIndex());
		junit.framework.Assert.assertEquals(12, v1.getMaxValue(), 1.0E-12);
		org.apache.commons.math.linear.ArrayRealVector v2 = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ java.lang.Double.NaN , 3 , java.lang.Double.NaN , -2 });
		junit.framework.Assert.assertEquals(3, v2.getMinIndex());
		junit.framework.Assert.assertEquals(-2, v2.getMinValue(), 1.0E-12);
		junit.framework.Assert.assertEquals(1, v2.getMaxIndex());
		junit.framework.Assert.assertEquals(3, v2.getMaxValue(), 1.0E-12);
		org.apache.commons.math.linear.ArrayRealVector v3 = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ java.lang.Double.NaN , java.lang.Double.NaN });
		junit.framework.Assert.assertEquals(-1, v3.getMinIndex());
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(v3.getMinValue()));
		junit.framework.Assert.assertEquals(-1, v3.getMaxIndex());
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(v3.getMaxValue()));
		org.apache.commons.math.linear.ArrayRealVector v4 = new org.apache.commons.math.linear.ArrayRealVector(new double[0]);
		junit.framework.Assert.assertEquals(-1, v4.getMinIndex());
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(v4.getMinValue()));
		junit.framework.Assert.assertEquals(-1, v4.getMaxIndex());
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(v4.getMaxValue()));
	}

	protected void assertClose(java.lang.String msg, double[] m, double[] n, double tolerance) {
		if ((m.length) != (n.length)) {
			junit.framework.Assert.fail("vectors have different lengths");
		} 
		for (int i = 0 ; i < (m.length) ; i++) {
			junit.framework.Assert.assertEquals((((msg + " ") + i) + " elements differ"), m[i], n[i], tolerance);
		}
	}
}

