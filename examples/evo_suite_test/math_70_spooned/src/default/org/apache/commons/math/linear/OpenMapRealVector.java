package org.apache.commons.math.linear;


public class OpenMapRealVector extends org.apache.commons.math.linear.AbstractRealVector implements java.io.Serializable , org.apache.commons.math.linear.SparseRealVector {
	public static final double DEFAULT_ZERO_TOLERANCE = 1.0E-12;

	private static final long serialVersionUID = 8772222695580707260L;

	private final org.apache.commons.math.util.OpenIntToDoubleHashMap entries;

	private final int virtualSize;

	private double epsilon;

	public OpenMapRealVector() {
		this(0, DEFAULT_ZERO_TOLERANCE);
	}

	public OpenMapRealVector(int dimension) {
		this(dimension, DEFAULT_ZERO_TOLERANCE);
	}

	public OpenMapRealVector(int dimension ,double epsilon) {
		virtualSize = dimension;
		entries = new org.apache.commons.math.util.OpenIntToDoubleHashMap(0.0);
		this.epsilon = epsilon;
	}

	protected OpenMapRealVector(org.apache.commons.math.linear.OpenMapRealVector v ,int resize) {
		virtualSize = (v.getDimension()) + resize;
		entries = new org.apache.commons.math.util.OpenIntToDoubleHashMap(v.entries);
		epsilon = v.epsilon;
	}

	public OpenMapRealVector(int dimension ,int expectedSize) {
		this(dimension, expectedSize, DEFAULT_ZERO_TOLERANCE);
	}

	public OpenMapRealVector(int dimension ,int expectedSize ,double epsilon) {
		virtualSize = dimension;
		entries = new org.apache.commons.math.util.OpenIntToDoubleHashMap(expectedSize , 0.0);
		this.epsilon = epsilon;
	}

	public OpenMapRealVector(double[] values) {
		this(values, DEFAULT_ZERO_TOLERANCE);
	}

	public OpenMapRealVector(double[] values ,double epsilon) {
		virtualSize = values.length;
		entries = new org.apache.commons.math.util.OpenIntToDoubleHashMap(0.0);
		this.epsilon = epsilon;
		for (int key = 0 ; key < (values.length) ; key++) {
			double value = values[key];
			if (!(isDefaultValue(value))) {
				entries.put(key, value);
			} 
		}
	}

	public OpenMapRealVector(java.lang.Double[] values) {
		this(values, DEFAULT_ZERO_TOLERANCE);
	}

	public OpenMapRealVector(java.lang.Double[] values ,double epsilon) {
		virtualSize = values.length;
		entries = new org.apache.commons.math.util.OpenIntToDoubleHashMap(0.0);
		this.epsilon = epsilon;
		for (int key = 0 ; key < (values.length) ; key++) {
			double value = values[key].doubleValue();
			if (!(isDefaultValue(value))) {
				entries.put(key, value);
			} 
		}
	}

	public OpenMapRealVector(org.apache.commons.math.linear.OpenMapRealVector v) {
		virtualSize = v.getDimension();
		entries = new org.apache.commons.math.util.OpenIntToDoubleHashMap(v.getEntries());
		epsilon = v.epsilon;
	}

	public OpenMapRealVector(org.apache.commons.math.linear.RealVector v) {
		virtualSize = v.getDimension();
		entries = new org.apache.commons.math.util.OpenIntToDoubleHashMap(0.0);
		epsilon = DEFAULT_ZERO_TOLERANCE;
		for (int key = 0 ; key < (virtualSize) ; key++) {
			double value = v.getEntry(key);
			if (!(isDefaultValue(value))) {
				entries.put(key, value);
			} 
		}
	}

	private org.apache.commons.math.util.OpenIntToDoubleHashMap getEntries() {
		return entries;
	}

	protected boolean isDefaultValue(double value) {
		return (java.lang.Math.abs(value)) < (epsilon);
	}

	@java.lang.Override
	public org.apache.commons.math.linear.RealVector add(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
		if (v instanceof org.apache.commons.math.linear.OpenMapRealVector) {
			return add(((org.apache.commons.math.linear.OpenMapRealVector)(v)));
		} else {
			return super.add(v);
		}
	}

	public org.apache.commons.math.linear.OpenMapRealVector add(org.apache.commons.math.linear.OpenMapRealVector v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
		boolean copyThis = (entries.size()) > (v.entries.size());
		org.apache.commons.math.linear.OpenMapRealVector res = copyThis ? copy() : v.copy();
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = copyThis ? v.entries.iterator() : entries.iterator();
		org.apache.commons.math.util.OpenIntToDoubleHashMap randomAccess = copyThis ? entries : v.entries;
		while (iter.hasNext()) {
			iter.advance();
			int key = iter.key();
			if (randomAccess.containsKey(key)) {
				res.setEntry(key, ((randomAccess.get(key)) + (iter.value())));
			} else {
				res.setEntry(key, iter.value());
			}
		}
		return res;
	}

	public org.apache.commons.math.linear.OpenMapRealVector append(org.apache.commons.math.linear.OpenMapRealVector v) {
		org.apache.commons.math.linear.OpenMapRealVector res = new org.apache.commons.math.linear.OpenMapRealVector(this , v.getDimension());
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = v.entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			res.setEntry(((iter.key()) + (virtualSize)), iter.value());
		}
		return res;
	}

	public org.apache.commons.math.linear.OpenMapRealVector append(org.apache.commons.math.linear.RealVector v) {
		if (v instanceof org.apache.commons.math.linear.OpenMapRealVector) {
			return append(((org.apache.commons.math.linear.OpenMapRealVector)(v)));
		} 
		return append(v.getData());
	}

	public org.apache.commons.math.linear.OpenMapRealVector append(double d) {
		org.apache.commons.math.linear.OpenMapRealVector res = new org.apache.commons.math.linear.OpenMapRealVector(this , 1);
		res.setEntry(virtualSize, d);
		return res;
	}

	public org.apache.commons.math.linear.OpenMapRealVector append(double[] a) {
		org.apache.commons.math.linear.OpenMapRealVector res = new org.apache.commons.math.linear.OpenMapRealVector(this , a.length);
		for (int i = 0 ; i < (a.length) ; i++) {
			res.setEntry((i + (virtualSize)), a[i]);
		}
		return res;
	}

	@java.lang.Override
	public org.apache.commons.math.linear.OpenMapRealVector copy() {
		return new org.apache.commons.math.linear.OpenMapRealVector(this);
	}

	public double dotProduct(org.apache.commons.math.linear.OpenMapRealVector v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
		boolean thisIsSmaller = (entries.size()) < (v.entries.size());
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = thisIsSmaller ? entries.iterator() : v.entries.iterator();
		org.apache.commons.math.util.OpenIntToDoubleHashMap larger = thisIsSmaller ? v.entries : entries;
		double d = 0;
		while (iter.hasNext()) {
			iter.advance();
			d += (iter.value()) * (larger.get(iter.key()));
		}
		return d;
	}

	@java.lang.Override
	public double dotProduct(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
		if (v instanceof org.apache.commons.math.linear.OpenMapRealVector) {
			return dotProduct(((org.apache.commons.math.linear.OpenMapRealVector)(v)));
		} else {
			return super.dotProduct(v);
		}
	}

	public org.apache.commons.math.linear.OpenMapRealVector ebeDivide(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
		org.apache.commons.math.linear.OpenMapRealVector res = new org.apache.commons.math.linear.OpenMapRealVector(this);
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = res.entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			res.setEntry(iter.key(), ((iter.value()) / (v.getEntry(iter.key()))));
		}
		return res;
	}

	@java.lang.Override
	public org.apache.commons.math.linear.OpenMapRealVector ebeDivide(double[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		org.apache.commons.math.linear.OpenMapRealVector res = new org.apache.commons.math.linear.OpenMapRealVector(this);
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = res.entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			res.setEntry(iter.key(), ((iter.value()) / (v[iter.key()])));
		}
		return res;
	}

	public org.apache.commons.math.linear.OpenMapRealVector ebeMultiply(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
		org.apache.commons.math.linear.OpenMapRealVector res = new org.apache.commons.math.linear.OpenMapRealVector(this);
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = res.entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			res.setEntry(iter.key(), ((iter.value()) * (v.getEntry(iter.key()))));
		}
		return res;
	}

	@java.lang.Override
	public org.apache.commons.math.linear.OpenMapRealVector ebeMultiply(double[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		org.apache.commons.math.linear.OpenMapRealVector res = new org.apache.commons.math.linear.OpenMapRealVector(this);
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = res.entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			res.setEntry(iter.key(), ((iter.value()) * (v[iter.key()])));
		}
		return res;
	}

	public org.apache.commons.math.linear.OpenMapRealVector getSubVector(int index, int n) throws org.apache.commons.math.linear.MatrixIndexException {
		checkIndex(index);
		checkIndex(((index + n) - 1));
		org.apache.commons.math.linear.OpenMapRealVector res = new org.apache.commons.math.linear.OpenMapRealVector(n);
		int end = index + n;
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			int key = iter.key();
			if ((key >= index) && (key < end)) {
				res.setEntry((key - index), iter.value());
			} 
		}
		return res;
	}

	@java.lang.Override
	public double[] getData() {
		double[] res = new double[virtualSize];
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			res[iter.key()] = iter.value();
		}
		return res;
	}

	public int getDimension() {
		return virtualSize;
	}

	public double getDistance(org.apache.commons.math.linear.OpenMapRealVector v) throws java.lang.IllegalArgumentException {
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
		double res = 0;
		while (iter.hasNext()) {
			iter.advance();
			int key = iter.key();
			double delta;
			delta = (iter.value()) - (v.getEntry(key));
			res += delta * delta;
		}
		iter = v.getEntries().iterator();
		while (iter.hasNext()) {
			iter.advance();
			int key = iter.key();
			if (!(entries.containsKey(key))) {
				final double value = iter.value();
				res += value * value;
			} 
		}
		return java.lang.Math.sqrt(res);
	}

	@java.lang.Override
	public double getDistance(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
		if (v instanceof org.apache.commons.math.linear.OpenMapRealVector) {
			return getDistance(((org.apache.commons.math.linear.OpenMapRealVector)(v)));
		} 
		return getDistance(v.getData());
	}

	@java.lang.Override
	public double getDistance(double[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		double res = 0;
		for (int i = 0 ; i < (v.length) ; i++) {
			double delta = (entries.get(i)) - (v[i]);
			res += delta * delta;
		}
		return java.lang.Math.sqrt(res);
	}

	public double getEntry(int index) throws org.apache.commons.math.linear.MatrixIndexException {
		checkIndex(index);
		return entries.get(index);
	}

	public double getL1Distance(org.apache.commons.math.linear.OpenMapRealVector v) {
		double max = 0;
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			double delta = java.lang.Math.abs(((iter.value()) - (v.getEntry(iter.key()))));
			max += delta;
		}
		iter = v.getEntries().iterator();
		while (iter.hasNext()) {
			iter.advance();
			int key = iter.key();
			if (!(entries.containsKey(key))) {
				double delta = java.lang.Math.abs(iter.value());
				max += java.lang.Math.abs(delta);
			} 
		}
		return max;
	}

	@java.lang.Override
	public double getL1Distance(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
		if (v instanceof org.apache.commons.math.linear.OpenMapRealVector) {
			return getL1Distance(((org.apache.commons.math.linear.OpenMapRealVector)(v)));
		} 
		return getL1Distance(v.getData());
	}

	@java.lang.Override
	public double getL1Distance(double[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		double max = 0;
		for (int i = 0 ; i < (v.length) ; i++) {
			double delta = java.lang.Math.abs(((getEntry(i)) - (v[i])));
			max += delta;
		}
		return max;
	}

	private double getLInfDistance(org.apache.commons.math.linear.OpenMapRealVector v) {
		double max = 0;
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			double delta = java.lang.Math.abs(((iter.value()) - (v.getEntry(iter.key()))));
			if (delta > max) {
				max = delta;
			} 
		}
		iter = v.getEntries().iterator();
		while (iter.hasNext()) {
			iter.advance();
			int key = iter.key();
			if (!(entries.containsKey(key))) {
				if ((iter.value()) > max) {
					max = iter.value();
				} 
			} 
		}
		return max;
	}

	@java.lang.Override
	public double getLInfDistance(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
		if (v instanceof org.apache.commons.math.linear.OpenMapRealVector) {
			return getLInfDistance(((org.apache.commons.math.linear.OpenMapRealVector)(v)));
		} 
		return getLInfDistance(v.getData());
	}

	@java.lang.Override
	public double getLInfDistance(double[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		double max = 0;
		for (int i = 0 ; i < (v.length) ; i++) {
			double delta = java.lang.Math.abs(((getEntry(i)) - (v[i])));
			if (delta > max) {
				max = delta;
			} 
		}
		return max;
	}

	public boolean isInfinite() {
		boolean infiniteFound = false;
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			final double value = iter.value();
			if (java.lang.Double.isNaN(value)) {
				return false;
			} 
			if (java.lang.Double.isInfinite(value)) {
				infiniteFound = true;
			} 
		}
		return infiniteFound;
	}

	public boolean isNaN() {
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			if (java.lang.Double.isNaN(iter.value())) {
				return true;
			} 
		}
		return false;
	}

	@java.lang.Override
	public org.apache.commons.math.linear.OpenMapRealVector mapAdd(double d) {
		return copy().mapAddToSelf(d);
	}

	@java.lang.Override
	public org.apache.commons.math.linear.OpenMapRealVector mapAddToSelf(double d) {
		for (int i = 0 ; i < (virtualSize) ; i++) {
			setEntry(i, ((getEntry(i)) + d));
		}
		return this;
	}

	@java.lang.Override
	public org.apache.commons.math.linear.RealMatrix outerProduct(double[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		org.apache.commons.math.linear.RealMatrix res = new org.apache.commons.math.linear.OpenMapRealMatrix(virtualSize , virtualSize);
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			int row = iter.key();
			double value = iter.value();
			for (int col = 0 ; col < (virtualSize) ; col++) {
				res.setEntry(row, col, (value * (v[col])));
			}
		}
		return res;
	}

	public org.apache.commons.math.linear.RealVector projection(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
		return v.mapMultiply(((dotProduct(v)) / (v.dotProduct(v))));
	}

	@java.lang.Override
	public org.apache.commons.math.linear.OpenMapRealVector projection(double[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		return ((org.apache.commons.math.linear.OpenMapRealVector)(projection(new org.apache.commons.math.linear.OpenMapRealVector(v))));
	}

	public void setEntry(int index, double value) throws org.apache.commons.math.linear.MatrixIndexException {
		checkIndex(index);
		if (!(isDefaultValue(value))) {
			entries.put(index, value);
		} else if (entries.containsKey(index)) {
			entries.remove(index);
		} 
	}

	@java.lang.Override
	public void setSubVector(int index, org.apache.commons.math.linear.RealVector v) throws org.apache.commons.math.linear.MatrixIndexException {
		checkIndex(index);
		checkIndex(((index + (v.getDimension())) - 1));
		setSubVector(index, v.getData());
	}

	@java.lang.Override
	public void setSubVector(int index, double[] v) throws org.apache.commons.math.linear.MatrixIndexException {
		checkIndex(index);
		checkIndex(((index + (v.length)) - 1));
		for (int i = 0 ; i < (v.length) ; i++) {
			setEntry((i + index), v[i]);
		}
	}

	@java.lang.Override
	public void set(double value) {
		for (int i = 0 ; i < (virtualSize) ; i++) {
			setEntry(i, value);
		}
	}

	public org.apache.commons.math.linear.OpenMapRealVector subtract(org.apache.commons.math.linear.OpenMapRealVector v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
		org.apache.commons.math.linear.OpenMapRealVector res = copy();
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = v.getEntries().iterator();
		while (iter.hasNext()) {
			iter.advance();
			int key = iter.key();
			if (entries.containsKey(key)) {
				res.setEntry(key, ((entries.get(key)) - (iter.value())));
			} else {
				res.setEntry(key, -(iter.value()));
			}
		}
		return res;
	}

	@java.lang.Override
	public org.apache.commons.math.linear.OpenMapRealVector subtract(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
		if (v instanceof org.apache.commons.math.linear.OpenMapRealVector) {
			return subtract(((org.apache.commons.math.linear.OpenMapRealVector)(v)));
		} 
		return subtract(v.getData());
	}

	@java.lang.Override
	public org.apache.commons.math.linear.OpenMapRealVector subtract(double[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		org.apache.commons.math.linear.OpenMapRealVector res = new org.apache.commons.math.linear.OpenMapRealVector(this);
		for (int i = 0 ; i < (v.length) ; i++) {
			if (entries.containsKey(i)) {
				res.setEntry(i, ((entries.get(i)) - (v[i])));
			} else {
				res.setEntry(i, -(v[i]));
			}
		}
		return res;
	}

	@java.lang.Override
	public org.apache.commons.math.linear.OpenMapRealVector unitVector() {
		org.apache.commons.math.linear.OpenMapRealVector res = copy();
		res.unitize();
		return res;
	}

	@java.lang.Override
	public void unitize() {
		double norm = getNorm();
		if (isDefaultValue(norm)) {
			throw org.apache.commons.math.MathRuntimeException.createArithmeticException("cannot normalize a zero norm vector");
		} 
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			entries.put(iter.key(), ((iter.value()) / norm));
		}
	}

	@java.lang.Override
	public double[] toArray() {
		return getData();
	}

	@java.lang.Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = java.lang.Double.doubleToLongBits(epsilon);
		result = (prime * result) + ((int)((temp ^ (temp >>> 32))));
		result = (prime * result) + (virtualSize);
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			temp = java.lang.Double.doubleToLongBits(iter.value());
			result = (prime * result) + ((int)((temp ^ (temp >> 32))));
		}
		return result;
	}

	@java.lang.Override
	public boolean equals(java.lang.Object obj) {
		if ((this) == obj) {
			return true;
		} 
		if (!(obj instanceof org.apache.commons.math.linear.OpenMapRealVector)) {
			return false;
		} 
		org.apache.commons.math.linear.OpenMapRealVector other = ((org.apache.commons.math.linear.OpenMapRealVector)(obj));
		if ((virtualSize) != (other.virtualSize)) {
			return false;
		} 
		if ((java.lang.Double.doubleToLongBits(epsilon)) != (java.lang.Double.doubleToLongBits(other.epsilon))) {
			return false;
		} 
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			double test = other.getEntry(iter.key());
			if ((java.lang.Double.doubleToLongBits(test)) != (java.lang.Double.doubleToLongBits(iter.value()))) {
				return false;
			} 
		}
		iter = other.getEntries().iterator();
		while (iter.hasNext()) {
			iter.advance();
			double test = iter.value();
			if ((java.lang.Double.doubleToLongBits(test)) != (java.lang.Double.doubleToLongBits(getEntry(iter.key())))) {
				return false;
			} 
		}
		return true;
	}

	public double getSparcity() {
		return ((double)(entries.size())) / ((double)(getDimension()));
	}

	@java.lang.Override
	public java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> sparseIterator() {
		return new OpenMapSparseIterator();
	}

	protected class OpenMapEntry extends org.apache.commons.math.linear.RealVector.Entry {
		private final org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter;

		protected OpenMapEntry(org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter) {
			this.iter = iter;
		}

		@java.lang.Override
		public double getValue() {
			return iter.value();
		}

		@java.lang.Override
		public void setValue(double value) {
			entries.put(iter.key(), value);
		}

		@java.lang.Override
		public int getIndex() {
			return iter.key();
		}
	}

	protected class OpenMapSparseIterator implements java.util.Iterator<org.apache.commons.math.linear.RealVector.Entry> {
		private final org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iter;

		private final org.apache.commons.math.linear.RealVector.Entry current;

		protected OpenMapSparseIterator() {
			iter = entries.iterator();
			current = new OpenMapEntry(iter);
		}

		public boolean hasNext() {
			return iter.hasNext();
		}

		public org.apache.commons.math.linear.RealVector.Entry next() {
			iter.advance();
			return current;
		}

		public void remove() {
			throw new java.lang.UnsupportedOperationException("Not supported");
		}
	}
}

