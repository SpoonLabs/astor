package org.apache.commons.math.linear;


public interface RealVector {
	org.apache.commons.math.linear.RealVector mapToSelf(org.apache.commons.math.analysis.UnivariateRealFunction function) throws org.apache.commons.math.FunctionEvaluationException;

	org.apache.commons.math.linear.RealVector map(org.apache.commons.math.analysis.UnivariateRealFunction function) throws org.apache.commons.math.FunctionEvaluationException;

	public abstract class Entry {
		private int index;

		public abstract double getValue();

		public abstract void setValue(double value);

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}

	java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> iterator();

	java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> sparseIterator();

	org.apache.commons.math.linear.RealVector copy();

	org.apache.commons.math.linear.RealVector add(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.RealVector add(double[] v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.RealVector subtract(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.RealVector subtract(double[] v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.RealVector mapAdd(double d);

	org.apache.commons.math.linear.RealVector mapAddToSelf(double d);

	org.apache.commons.math.linear.RealVector mapSubtract(double d);

	org.apache.commons.math.linear.RealVector mapSubtractToSelf(double d);

	org.apache.commons.math.linear.RealVector mapMultiply(double d);

	org.apache.commons.math.linear.RealVector mapMultiplyToSelf(double d);

	org.apache.commons.math.linear.RealVector mapDivide(double d);

	org.apache.commons.math.linear.RealVector mapDivideToSelf(double d);

	org.apache.commons.math.linear.RealVector mapPow(double d);

	org.apache.commons.math.linear.RealVector mapPowToSelf(double d);

	org.apache.commons.math.linear.RealVector mapExp();

	org.apache.commons.math.linear.RealVector mapExpToSelf();

	org.apache.commons.math.linear.RealVector mapExpm1();

	org.apache.commons.math.linear.RealVector mapExpm1ToSelf();

	org.apache.commons.math.linear.RealVector mapLog();

	org.apache.commons.math.linear.RealVector mapLogToSelf();

	org.apache.commons.math.linear.RealVector mapLog10();

	org.apache.commons.math.linear.RealVector mapLog10ToSelf();

	org.apache.commons.math.linear.RealVector mapLog1p();

	org.apache.commons.math.linear.RealVector mapLog1pToSelf();

	org.apache.commons.math.linear.RealVector mapCosh();

	org.apache.commons.math.linear.RealVector mapCoshToSelf();

	org.apache.commons.math.linear.RealVector mapSinh();

	org.apache.commons.math.linear.RealVector mapSinhToSelf();

	org.apache.commons.math.linear.RealVector mapTanh();

	org.apache.commons.math.linear.RealVector mapTanhToSelf();

	org.apache.commons.math.linear.RealVector mapCos();

	org.apache.commons.math.linear.RealVector mapCosToSelf();

	org.apache.commons.math.linear.RealVector mapSin();

	org.apache.commons.math.linear.RealVector mapSinToSelf();

	org.apache.commons.math.linear.RealVector mapTan();

	org.apache.commons.math.linear.RealVector mapTanToSelf();

	org.apache.commons.math.linear.RealVector mapAcos();

	org.apache.commons.math.linear.RealVector mapAcosToSelf();

	org.apache.commons.math.linear.RealVector mapAsin();

	org.apache.commons.math.linear.RealVector mapAsinToSelf();

	org.apache.commons.math.linear.RealVector mapAtan();

	org.apache.commons.math.linear.RealVector mapAtanToSelf();

	org.apache.commons.math.linear.RealVector mapInv();

	org.apache.commons.math.linear.RealVector mapInvToSelf();

	org.apache.commons.math.linear.RealVector mapAbs();

	org.apache.commons.math.linear.RealVector mapAbsToSelf();

	org.apache.commons.math.linear.RealVector mapSqrt();

	org.apache.commons.math.linear.RealVector mapSqrtToSelf();

	org.apache.commons.math.linear.RealVector mapCbrt();

	org.apache.commons.math.linear.RealVector mapCbrtToSelf();

	org.apache.commons.math.linear.RealVector mapCeil();

	org.apache.commons.math.linear.RealVector mapCeilToSelf();

	org.apache.commons.math.linear.RealVector mapFloor();

	org.apache.commons.math.linear.RealVector mapFloorToSelf();

	org.apache.commons.math.linear.RealVector mapRint();

	org.apache.commons.math.linear.RealVector mapRintToSelf();

	org.apache.commons.math.linear.RealVector mapSignum();

	org.apache.commons.math.linear.RealVector mapSignumToSelf();

	org.apache.commons.math.linear.RealVector mapUlp();

	org.apache.commons.math.linear.RealVector mapUlpToSelf();

	org.apache.commons.math.linear.RealVector ebeMultiply(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.RealVector ebeMultiply(double[] v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.RealVector ebeDivide(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.RealVector ebeDivide(double[] v) throws java.lang.IllegalArgumentException;

	double[] getData();

	double dotProduct(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException;

	double dotProduct(double[] v) throws java.lang.IllegalArgumentException;

	double getNorm();

	double getL1Norm();

	double getLInfNorm();

	double getDistance(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException;

	double getDistance(double[] v) throws java.lang.IllegalArgumentException;

	double getL1Distance(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException;

	double getL1Distance(double[] v) throws java.lang.IllegalArgumentException;

	double getLInfDistance(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException;

	double getLInfDistance(double[] v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.RealVector unitVector();

	void unitize();

	org.apache.commons.math.linear.RealVector projection(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.RealVector projection(double[] v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.RealMatrix outerProduct(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.RealMatrix outerProduct(double[] v) throws java.lang.IllegalArgumentException;

	double getEntry(int index) throws org.apache.commons.math.linear.MatrixIndexException;

	void setEntry(int index, double value) throws org.apache.commons.math.linear.MatrixIndexException;

	int getDimension();

	org.apache.commons.math.linear.RealVector append(org.apache.commons.math.linear.RealVector v);

	org.apache.commons.math.linear.RealVector append(double d);

	org.apache.commons.math.linear.RealVector append(double[] a);

	org.apache.commons.math.linear.RealVector getSubVector(int index, int n) throws org.apache.commons.math.linear.MatrixIndexException;

	void setSubVector(int index, org.apache.commons.math.linear.RealVector v) throws org.apache.commons.math.linear.MatrixIndexException;

	void setSubVector(int index, double[] v) throws org.apache.commons.math.linear.MatrixIndexException;

	void set(double value);

	double[] toArray();

	boolean isNaN();

	boolean isInfinite();
}

