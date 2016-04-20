package org.apache.commons.math.linear;


public abstract class AbstractRealVector implements org.apache.commons.math.linear.RealVector {
	protected void checkVectorDimensions(org.apache.commons.math.linear.RealVector v) {
		checkVectorDimensions(v.getDimension());
	}

	protected void checkVectorDimensions(int n) throws java.lang.IllegalArgumentException {
		double d = getDimension();
		if (d != n) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("vector length mismatch: got {0} but expected {1}", d, n);
		} 
	}

	protected void checkIndex(final int index) throws org.apache.commons.math.linear.MatrixIndexException {
		if ((index < 0) || (index >= (getDimension()))) {
			throw new org.apache.commons.math.linear.MatrixIndexException("index {0} out of allowed range [{1}, {2}]" , index , 0 , ((getDimension()) - 1));
		} 
	}

	public void setSubVector(int index, org.apache.commons.math.linear.RealVector v) throws org.apache.commons.math.linear.MatrixIndexException {
		checkIndex(index);
		checkIndex(((index + (v.getDimension())) - 1));
		setSubVector(index, v.getData());
	}

	public void setSubVector(int index, double[] v) throws org.apache.commons.math.linear.MatrixIndexException {
		checkIndex(index);
		checkIndex(((index + (v.length)) - 1));
		for (int i = 0 ; i < (v.length) ; i++) {
			setEntry((i + index), v[i]);
		}
	}

	public org.apache.commons.math.linear.RealVector add(double[] v) throws java.lang.IllegalArgumentException {
		double[] result = v.clone();
		java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> it = sparseIterator();
		org.apache.commons.math.linear.RealVector.Entry e;
		while ((it.hasNext()) && ((e = it.next()) != null)) {
			result[e.getIndex()] += e.getValue();
		}
		return new org.apache.commons.math.linear.ArrayRealVector(result , false);
	}

	public org.apache.commons.math.linear.RealVector add(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
		if (v instanceof org.apache.commons.math.linear.ArrayRealVector) {
			double[] values = ((org.apache.commons.math.linear.ArrayRealVector)(v)).getDataRef();
			return add(values);
		} 
		org.apache.commons.math.linear.RealVector result = v.copy();
		java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> it = sparseIterator();
		org.apache.commons.math.linear.RealVector.Entry e;
		while ((it.hasNext()) && ((e = it.next()) != null)) {
			final int index = e.getIndex();
			result.setEntry(index, ((e.getValue()) + (result.getEntry(index))));
		}
		return result;
	}

	public org.apache.commons.math.linear.RealVector subtract(double[] v) throws java.lang.IllegalArgumentException {
		double[] result = v.clone();
		java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> it = sparseIterator();
		org.apache.commons.math.linear.RealVector.Entry e;
		while ((it.hasNext()) && ((e = it.next()) != null)) {
			final int index = e.getIndex();
			result[index] = (e.getValue()) - (result[index]);
		}
		return new org.apache.commons.math.linear.ArrayRealVector(result , false);
	}

	public org.apache.commons.math.linear.RealVector subtract(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
		if (v instanceof org.apache.commons.math.linear.ArrayRealVector) {
			double[] values = ((org.apache.commons.math.linear.ArrayRealVector)(v)).getDataRef();
			return add(values);
		} 
		org.apache.commons.math.linear.RealVector result = v.copy();
		java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> it = sparseIterator();
		org.apache.commons.math.linear.RealVector.Entry e;
		while ((it.hasNext()) && ((e = it.next()) != null)) {
			final int index = e.getIndex();
			v.setEntry(index, ((e.getValue()) - (result.getEntry(index))));
		}
		return result;
	}

	public org.apache.commons.math.linear.RealVector mapAdd(double d) {
		return copy().mapAddToSelf(d);
	}

	public org.apache.commons.math.linear.RealVector mapAddToSelf(double d) {
		if (d != 0) {
			try {
				return mapToSelf(org.apache.commons.math.analysis.BinaryFunction.ADD.fix1stArgument(d));
			} catch (org.apache.commons.math.FunctionEvaluationException e) {
				throw new java.lang.IllegalArgumentException(e);
			}
		} 
		return this;
	}

	public abstract org.apache.commons.math.linear.AbstractRealVector copy();

	public double dotProduct(double[] v) throws java.lang.IllegalArgumentException {
		return dotProduct(new org.apache.commons.math.linear.ArrayRealVector(v , false));
	}

	public double dotProduct(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v);
		double d = 0;
		java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> it = sparseIterator();
		org.apache.commons.math.linear.RealVector.Entry e;
		while ((it.hasNext()) && ((e = it.next()) != null)) {
			d += (e.getValue()) * (v.getEntry(e.getIndex()));
		}
		return d;
	}

	public org.apache.commons.math.linear.RealVector ebeDivide(double[] v) throws java.lang.IllegalArgumentException {
		return ebeDivide(new org.apache.commons.math.linear.ArrayRealVector(v , false));
	}

	public org.apache.commons.math.linear.RealVector ebeMultiply(double[] v) throws java.lang.IllegalArgumentException {
		return ebeMultiply(new org.apache.commons.math.linear.ArrayRealVector(v , false));
	}

	public double getDistance(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v);
		double d = 0;
		java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> it = iterator();
		org.apache.commons.math.linear.RealVector.Entry e;
		while ((it.hasNext()) && ((e = it.next()) != null)) {
			final double diff = (e.getValue()) - (v.getEntry(e.getIndex()));
			d += diff * diff;
		}
		return java.lang.Math.sqrt(d);
	}

	public double getNorm() {
		double sum = 0;
		java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> it = sparseIterator();
		org.apache.commons.math.linear.RealVector.Entry e;
		while ((it.hasNext()) && ((e = it.next()) != null)) {
			final double value = e.getValue();
			sum += value * value;
		}
		return java.lang.Math.sqrt(sum);
	}

	public double getL1Norm() {
		double norm = 0;
		java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> it = sparseIterator();
		org.apache.commons.math.linear.RealVector.Entry e;
		while ((it.hasNext()) && ((e = it.next()) != null)) {
			norm += java.lang.Math.abs(e.getValue());
		}
		return norm;
	}

	public double getLInfNorm() {
		double norm = 0;
		java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> it = sparseIterator();
		org.apache.commons.math.linear.RealVector.Entry e;
		while ((it.hasNext()) && ((e = it.next()) != null)) {
			norm = java.lang.Math.max(norm, java.lang.Math.abs(e.getValue()));
		}
		return norm;
	}

	public double getDistance(double[] v) throws java.lang.IllegalArgumentException {
		return getDistance(new org.apache.commons.math.linear.ArrayRealVector(v , false));
	}

	public double getL1Distance(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v);
		double d = 0;
		java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> it = iterator();
		org.apache.commons.math.linear.RealVector.Entry e;
		while ((it.hasNext()) && ((e = it.next()) != null)) {
			d += java.lang.Math.abs(((e.getValue()) - (v.getEntry(e.getIndex()))));
		}
		return d;
	}

	public double getL1Distance(double[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		double d = 0;
		java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> it = iterator();
		org.apache.commons.math.linear.RealVector.Entry e;
		while ((it.hasNext()) && ((e = it.next()) != null)) {
			d += java.lang.Math.abs(((e.getValue()) - (v[e.getIndex()])));
		}
		return d;
	}

	public double getLInfDistance(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v);
		double d = 0;
		java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> it = iterator();
		org.apache.commons.math.linear.RealVector.Entry e;
		while ((it.hasNext()) && ((e = it.next()) != null)) {
			d = java.lang.Math.max(java.lang.Math.abs(((e.getValue()) - (v.getEntry(e.getIndex())))), d);
		}
		return d;
	}

	public double getLInfDistance(double[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		double d = 0;
		java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> it = iterator();
		org.apache.commons.math.linear.RealVector.Entry e;
		while ((it.hasNext()) && ((e = it.next()) != null)) {
			d = java.lang.Math.max(java.lang.Math.abs(((e.getValue()) - (v[e.getIndex()]))), d);
		}
		return d;
	}

	public int getMinIndex() {
		int minIndex = -1;
		double minValue = java.lang.Double.POSITIVE_INFINITY;
		java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> iterator = iterator();
		while (iterator.hasNext()) {
			final org.apache.commons.math.linear.RealVector.Entry entry = iterator.next();
			if ((entry.getValue()) <= minValue) {
				minIndex = entry.getIndex();
				minValue = entry.getValue();
			} 
		}
		return minIndex;
	}

	public double getMinValue() {
		final int minIndex = getMinIndex();
		return minIndex < 0 ? java.lang.Double.NaN : getEntry(minIndex);
	}

	public int getMaxIndex() {
		int maxIndex = -1;
		double maxValue = java.lang.Double.NEGATIVE_INFINITY;
		java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> iterator = iterator();
		while (iterator.hasNext()) {
			final org.apache.commons.math.linear.RealVector.Entry entry = iterator.next();
			if ((entry.getValue()) >= maxValue) {
				maxIndex = entry.getIndex();
				maxValue = entry.getValue();
			} 
		}
		return maxIndex;
	}

	public double getMaxValue() {
		final int maxIndex = getMaxIndex();
		return maxIndex < 0 ? java.lang.Double.NaN : getEntry(maxIndex);
	}

	public org.apache.commons.math.linear.RealVector mapAbs() {
		return copy().mapAbsToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapAbsToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.ABS);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapAcos() {
		return copy().mapAcosToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapAcosToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.ACOS);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapAsin() {
		return copy().mapAsinToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapAsinToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.ASIN);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapAtan() {
		return copy().mapAtanToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapAtanToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.ATAN);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapCbrt() {
		return copy().mapCbrtToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapCbrtToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.CBRT);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapCeil() {
		return copy().mapCeilToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapCeilToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.CEIL);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapCos() {
		return copy().mapCosToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapCosToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.COS);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapCosh() {
		return copy().mapCoshToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapCoshToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.COSH);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapDivide(double d) {
		return copy().mapDivideToSelf(d);
	}

	public org.apache.commons.math.linear.RealVector mapDivideToSelf(double d) {
		try {
			return mapToSelf(org.apache.commons.math.analysis.BinaryFunction.DIVIDE.fix2ndArgument(d));
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapExp() {
		return copy().mapExpToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapExpToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.EXP);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapExpm1() {
		return copy().mapExpm1ToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapExpm1ToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.EXPM1);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapFloor() {
		return copy().mapFloorToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapFloorToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.FLOOR);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapInv() {
		return copy().mapInvToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapInvToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.INVERT);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapLog() {
		return copy().mapLogToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapLogToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.LOG);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapLog10() {
		return copy().mapLog10ToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapLog10ToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.LOG10);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapLog1p() {
		return copy().mapLog1pToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapLog1pToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.LOG1P);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapMultiply(double d) {
		return copy().mapMultiplyToSelf(d);
	}

	public org.apache.commons.math.linear.RealVector mapMultiplyToSelf(double d) {
		try {
			return mapToSelf(org.apache.commons.math.analysis.BinaryFunction.MULTIPLY.fix1stArgument(d));
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapPow(double d) {
		return copy().mapPowToSelf(d);
	}

	public org.apache.commons.math.linear.RealVector mapPowToSelf(double d) {
		try {
			return mapToSelf(org.apache.commons.math.analysis.BinaryFunction.POW.fix2ndArgument(d));
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapRint() {
		return copy().mapRintToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapRintToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.RINT);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapSignum() {
		return copy().mapSignumToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapSignumToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.SIGNUM);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapSin() {
		return copy().mapSinToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapSinToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.SIN);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapSinh() {
		return copy().mapSinhToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapSinhToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.SINH);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapSqrt() {
		return copy().mapSqrtToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapSqrtToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.SQRT);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapSubtract(double d) {
		return copy().mapSubtractToSelf(d);
	}

	public org.apache.commons.math.linear.RealVector mapSubtractToSelf(double d) {
		return mapAddToSelf(-d);
	}

	public org.apache.commons.math.linear.RealVector mapTan() {
		return copy().mapTanToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapTanToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.TAN);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapTanh() {
		return copy().mapTanhToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapTanhToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.TANH);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealVector mapUlp() {
		return copy().mapUlpToSelf();
	}

	public org.apache.commons.math.linear.RealVector mapUlpToSelf() {
		try {
			return mapToSelf(org.apache.commons.math.analysis.ComposableFunction.ULP);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new java.lang.IllegalArgumentException(e);
		}
	}

	public org.apache.commons.math.linear.RealMatrix outerProduct(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
		org.apache.commons.math.linear.RealMatrix product;
		if ((v instanceof org.apache.commons.math.linear.SparseRealVector) || ((this) instanceof org.apache.commons.math.linear.SparseRealVector)) {
			product = new org.apache.commons.math.linear.OpenMapRealMatrix(getDimension() , v.getDimension());
		} else {
			product = new org.apache.commons.math.linear.Array2DRowRealMatrix(getDimension() , v.getDimension());
		}
		java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> thisIt = sparseIterator();
		org.apache.commons.math.linear.RealVector.Entry thisE = null;
		while ((thisIt.hasNext()) && ((thisE = thisIt.next()) != null)) {
			java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> otherIt = v.sparseIterator();
			org.apache.commons.math.linear.RealVector.Entry otherE = null;
			while ((otherIt.hasNext()) && ((otherE = otherIt.next()) != null)) {
				product.setEntry(thisE.getIndex(), otherE.getIndex(), ((thisE.getValue()) * (otherE.getValue())));
			}
		}
		return product;
	}

	public org.apache.commons.math.linear.RealMatrix outerProduct(double[] v) throws java.lang.IllegalArgumentException {
		return outerProduct(new org.apache.commons.math.linear.ArrayRealVector(v , false));
	}

	public org.apache.commons.math.linear.RealVector projection(double[] v) throws java.lang.IllegalArgumentException {
		return projection(new org.apache.commons.math.linear.ArrayRealVector(v , false));
	}

	public void set(double value) {
		java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> it = iterator();
		org.apache.commons.math.linear.RealVector.Entry e = null;
		while ((it.hasNext()) && ((e = it.next()) != null)) {
			e.setValue(value);
		}
	}

	public double[] toArray() {
		int dim = getDimension();
		double[] values = new double[dim];
		for (int i = 0 ; i < dim ; i++) {
			values[i] = getEntry(i);
		}
		return values;
	}

	public double[] getData() {
		return toArray();
	}

	public org.apache.commons.math.linear.RealVector unitVector() {
		org.apache.commons.math.linear.RealVector copy = copy();
		copy.unitize();
		return copy;
	}

	public void unitize() {
		mapDivideToSelf(getNorm());
	}

	public java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> sparseIterator() {
		return new SparseEntryIterator();
	}

	public java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> iterator() {
		final int dim = getDimension();
		return new java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry>() {
			private int i = 0;

			private EntryImpl e = new EntryImpl();

			public boolean hasNext() {
				return (i) < dim;
			}

			public org.apache.commons.math.linear.RealVector.Entry next() {
				e.setIndex((i)++);
				return e;
			}

			public void remove() {
				throw new java.lang.UnsupportedOperationException("Not supported");
			}
		};
	}

	public org.apache.commons.math.linear.RealVector map(org.apache.commons.math.analysis.UnivariateRealFunction function) throws org.apache.commons.math.FunctionEvaluationException {
		return copy().mapToSelf(function);
	}

	public org.apache.commons.math.linear.RealVector mapToSelf(org.apache.commons.math.analysis.UnivariateRealFunction function) throws org.apache.commons.math.FunctionEvaluationException {
		java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> it = (function.value(0)) == 0 ? sparseIterator() : iterator();
		org.apache.commons.math.linear.RealVector.Entry e;
		while ((it.hasNext()) && ((e = it.next()) != null)) {
			e.setValue(function.value(e.getValue()));
		}
		return this;
	}

	protected class EntryImpl extends org.apache.commons.math.linear.RealVector.Entry {
		public EntryImpl() {
			setIndex(0);
		}

		@java.lang.Override
		public double getValue() {
			return getEntry(getIndex());
		}

		@java.lang.Override
		public void setValue(double newValue) {
			setEntry(getIndex(), newValue);
		}
	}

	protected class SparseEntryIterator implements java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> {
		private final int dim;

		private EntryImpl tmp = new EntryImpl();

		private EntryImpl current;

		private EntryImpl next;

		protected SparseEntryIterator() {
			dim = getDimension();
			current = new EntryImpl();
			if ((current.getValue()) == 0) {
				advance(current);
			} 
			if ((current.getIndex()) >= 0) {
				next = new EntryImpl();
				next.setIndex(current.getIndex());
				advance(next);
			} else {
				current = null;
			}
		}

		protected void advance(EntryImpl e) {
			if (e == null) {
				return ;
			} 
			do {
				e.setIndex(((e.getIndex()) + 1));
			} while (((e.getIndex()) < (dim)) && ((e.getValue()) == 0) );
			if ((e.getIndex()) >= (dim)) {
				e.setIndex(-1);
			} 
		}

		public boolean hasNext() {
			return (current) != null;
		}

		public org.apache.commons.math.linear.RealVector.Entry next() {
			tmp.setIndex(current.getIndex());
			if ((next) != null) {
				current.setIndex(next.getIndex());
				advance(next);
				if ((next.getIndex()) < 0) {
					next = null;
				} 
			} else {
				current = null;
			}
			return tmp;
		}

		public void remove() {
			throw new java.lang.UnsupportedOperationException("Not supported");
		}
	}
}

