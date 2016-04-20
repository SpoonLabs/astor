package org.apache.commons.math.linear;


public class AbstractRealVectorTest extends junit.framework.TestCase {
	private double[] vec1 = new double[]{ 1.0 , 2.0 , 3.0 , 4.0 , 5.0 };

	private double[] vec2 = new double[]{ -3.0 , 0.0 , 0.0 , 2.0 , 1.0 };

	private static class TestVectorImpl extends org.apache.commons.math.linear.AbstractRealVector {
		private double[] values;

		TestVectorImpl(double[] values) {
			this.values = values;
		}

		@java.lang.Override
		public double[] getData() {
			return values;
		}

		@java.lang.Override
		public org.apache.commons.math.linear.AbstractRealVector copy() {
			return new org.apache.commons.math.linear.AbstractRealVectorTest.TestVectorImpl(values.clone());
		}

		java.lang.UnsupportedOperationException unsupported() {
			return new java.lang.UnsupportedOperationException("Test implementation only supports methods necessary for testing");
		}

		@java.lang.Override
		public org.apache.commons.math.linear.RealVector add(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
			org.apache.commons.math.linear.RealVector result = new org.apache.commons.math.linear.ArrayRealVector(v);
			return result.add(this);
		}

		@java.lang.Override
		public org.apache.commons.math.linear.RealVector subtract(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
			org.apache.commons.math.linear.RealVector result = new org.apache.commons.math.linear.ArrayRealVector(v);
			return result.subtract(this).mapMultiplyToSelf(-1);
		}

		@java.lang.Override
		public org.apache.commons.math.linear.RealVector mapAddToSelf(double d) {
			for (int i = 0 ; i < (values.length) ; i++) {
				values[i] += d;
			}
			return this;
		}

		@java.lang.Override
		public org.apache.commons.math.linear.RealVector mapSubtractToSelf(double d) {
			for (int i = 0 ; i < (values.length) ; i++) {
				values[i] -= d;
			}
			return this;
		}

		@java.lang.Override
		public org.apache.commons.math.linear.RealVector mapMultiplyToSelf(double d) {
			for (int i = 0 ; i < (values.length) ; i++) {
				values[i] *= d;
			}
			return this;
		}

		@java.lang.Override
		public org.apache.commons.math.linear.RealVector mapDivideToSelf(double d) {
			for (int i = 0 ; i < (values.length) ; i++) {
				values[i] /= d;
			}
			return this;
		}

		@java.lang.Override
		public org.apache.commons.math.linear.RealVector mapPowToSelf(double d) {
			for (int i = 0 ; i < (values.length) ; i++) {
				values[i] = java.lang.Math.pow(values[i], d);
			}
			return this;
		}

		@java.lang.Override
		public org.apache.commons.math.linear.RealVector mapInvToSelf() {
			for (int i = 0 ; i < (values.length) ; i++) {
				values[i] = 1 / (values[i]);
			}
			return this;
		}

		public org.apache.commons.math.linear.RealVector ebeMultiply(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector ebeDivide(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		@java.lang.Override
		public double dotProduct(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		@java.lang.Override
		public double getNorm() {
			throw unsupported();
		}

		@java.lang.Override
		public double getL1Norm() {
			throw unsupported();
		}

		@java.lang.Override
		public double getLInfNorm() {
			throw unsupported();
		}

		public org.apache.commons.math.linear.RealVector projection(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
			throw unsupported();
		}

		public double getEntry(int index) throws org.apache.commons.math.linear.MatrixIndexException {
			return values[index];
		}

		public void setEntry(int index, double value) throws org.apache.commons.math.linear.MatrixIndexException {
			values[index] = value;
		}

		public int getDimension() {
			return values.length;
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

		public boolean isNaN() {
			throw unsupported();
		}

		public boolean isInfinite() {
			throw unsupported();
		}
	}

	private static void assertEquals(double[] d1, double[] d2) {
		junit.framework.Assert.assertEquals(d1.length, d2.length);
		for (int i = 0 ; i < (d1.length) ; i++)
			junit.framework.Assert.assertEquals(d1[i], d2[i]);
	}

	public void testMap() throws java.lang.Exception {
		double[] vec1Squared = new double[]{ 1.0 , 4.0 , 9.0 , 16.0 , 25.0 };
		org.apache.commons.math.linear.RealVector v = new org.apache.commons.math.linear.AbstractRealVectorTest.TestVectorImpl(vec1.clone());
		org.apache.commons.math.linear.RealVector w = v.map(new org.apache.commons.math.analysis.UnivariateRealFunction() {
			public double value(double x) {
				return x * x;
			}
		});
		org.apache.commons.math.linear.AbstractRealVectorTest.assertEquals(vec1Squared, w.getData());
	}

	public void testIterator() throws java.lang.Exception {
		org.apache.commons.math.linear.RealVector v = new org.apache.commons.math.linear.AbstractRealVectorTest.TestVectorImpl(vec2.clone());
		org.apache.commons.math.linear.RealVector.Entry e;
		int i = 0;
		for (java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> it = v.iterator() ; (it.hasNext()) && ((e = it.next()) != null) ; i++) {
			junit.framework.Assert.assertEquals(vec2[i], e.getValue());
		}
	}

	public void testSparseIterator() throws java.lang.Exception {
		org.apache.commons.math.linear.RealVector v = new org.apache.commons.math.linear.AbstractRealVectorTest.TestVectorImpl(vec2.clone());
		org.apache.commons.math.linear.RealVector.Entry e;
		int i = 0;
		double[] nonDefaultV2 = new double[]{ -3.0 , 2.0 , 1.0 };
		for (java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> it = v.sparseIterator() ; (it.hasNext()) && ((e = it.next()) != null) ; i++) {
			junit.framework.Assert.assertEquals(nonDefaultV2[i], e.getValue());
		}
	}

	public void testClone() throws java.lang.Exception {
		double[] d = new double[1000000];
		java.util.Random r = new java.util.Random(1234);
		for (int i = 0 ; i < (d.length) ; i++)
			d[i] = r.nextDouble();
		junit.framework.Assert.assertTrue(((new org.apache.commons.math.linear.ArrayRealVector(d).getNorm()) > 0));
		double[] c = d.clone();
		c[0] = 1;
		junit.framework.Assert.assertNotSame(c[0], d[0]);
		d[0] = 1;
		junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.ArrayRealVector(d).getNorm(), new org.apache.commons.math.linear.ArrayRealVector(c).getNorm());
		long cloneTime = 0;
		long setAndAddTime = 0;
		for (int i = 0 ; i < 10 ; i++) {
			long start = java.lang.System.nanoTime();
			double[] v = d.clone();
			for (int j = 0 ; j < (v.length) ; j++)
				v[j] += 1234.5678;
			if (i > 4) {
				cloneTime += (java.lang.System.nanoTime()) - start;
			} 
			start = java.lang.System.nanoTime();
			v = new double[d.length];
			for (int j = 0 ; j < (v.length) ; j++)
				v[j] = (d[j]) + 1234.5678;
			if (i > 4) {
				setAndAddTime += (java.lang.System.nanoTime()) - start;
			} 
		}
	}
}

