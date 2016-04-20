package org.apache.commons.math.linear;


public class SparseFieldVector<T extends org.apache.commons.math.FieldElement<T>> implements java.io.Serializable , org.apache.commons.math.linear.FieldVector<T> {
	private static final long serialVersionUID = 7841233292190413362L;

	private final org.apache.commons.math.Field<T> field;

	private final org.apache.commons.math.util.OpenIntToFieldHashMap<T> entries;

	private final int virtualSize;

	public SparseFieldVector(org.apache.commons.math.Field<T> field) {
		this(field, 0);
	}

	public SparseFieldVector(org.apache.commons.math.Field<T> field ,int dimension) {
		this.field = field;
		virtualSize = dimension;
		entries = new org.apache.commons.math.util.OpenIntToFieldHashMap<T>(field);
	}

	protected SparseFieldVector(org.apache.commons.math.linear.SparseFieldVector<T> v ,int resize) {
		field = v.field;
		virtualSize = (v.getDimension()) + resize;
		entries = new org.apache.commons.math.util.OpenIntToFieldHashMap<T>(v.entries);
	}

	public SparseFieldVector(org.apache.commons.math.Field<T> field ,int dimension ,int expectedSize) {
		this.field = field;
		virtualSize = dimension;
		entries = new org.apache.commons.math.util.OpenIntToFieldHashMap<T>(field , expectedSize);
	}

	public SparseFieldVector(org.apache.commons.math.Field<T> field ,T[] values) {
		this.field = field;
		virtualSize = values.length;
		entries = new org.apache.commons.math.util.OpenIntToFieldHashMap<T>(field);
		for (int key = 0 ; key < (values.length) ; key++) {
			T value = values[key];
			entries.put(key, value);
		}
	}

	public SparseFieldVector(org.apache.commons.math.linear.SparseFieldVector<T> v) {
		field = v.field;
		virtualSize = v.getDimension();
		entries = new org.apache.commons.math.util.OpenIntToFieldHashMap<T>(v.getEntries());
	}

	private org.apache.commons.math.util.OpenIntToFieldHashMap<T> getEntries() {
		return entries;
	}

	public org.apache.commons.math.linear.FieldVector<T> add(org.apache.commons.math.linear.SparseFieldVector<T> v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
		org.apache.commons.math.linear.SparseFieldVector<T> res = ((org.apache.commons.math.linear.SparseFieldVector<T>)(copy()));
		org.apache.commons.math.util.OpenIntToFieldHashMap<T>.Iterator iter = v.getEntries().iterator();
		while (iter.hasNext()) {
			iter.advance();
			int key = iter.key();
			T value = iter.value();
			if (entries.containsKey(key)) {
				res.setEntry(key, entries.get(key).add(value));
			} else {
				res.setEntry(key, value);
			}
		}
		return res;
	}

	public org.apache.commons.math.linear.FieldVector<T> add(T[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		org.apache.commons.math.linear.SparseFieldVector<T> res = new org.apache.commons.math.linear.SparseFieldVector<T>(field , getDimension());
		for (int i = 0 ; i < (v.length) ; i++) {
			res.setEntry(i, v[i].add(getEntry(i)));
		}
		return res;
	}

	public org.apache.commons.math.linear.FieldVector<T> append(org.apache.commons.math.linear.SparseFieldVector<T> v) {
		org.apache.commons.math.linear.SparseFieldVector<T> res = new org.apache.commons.math.linear.SparseFieldVector<T>(this , v.getDimension());
		org.apache.commons.math.util.OpenIntToFieldHashMap<T>.Iterator iter = v.entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			res.setEntry(((iter.key()) + (virtualSize)), iter.value());
		}
		return res;
	}

	public org.apache.commons.math.linear.FieldVector<T> append(org.apache.commons.math.linear.FieldVector<T> v) {
		if (v instanceof org.apache.commons.math.linear.SparseFieldVector<?>) {
			return append(((org.apache.commons.math.linear.SparseFieldVector<T>)(v)));
		} else {
			return append(v.toArray());
		}
	}

	public org.apache.commons.math.linear.FieldVector<T> append(T d) {
		org.apache.commons.math.linear.FieldVector<T> res = new org.apache.commons.math.linear.SparseFieldVector<T>(this , 1);
		res.setEntry(virtualSize, d);
		return res;
	}

	public org.apache.commons.math.linear.FieldVector<T> append(T[] a) {
		org.apache.commons.math.linear.FieldVector<T> res = new org.apache.commons.math.linear.SparseFieldVector<T>(this , a.length);
		for (int i = 0 ; i < (a.length) ; i++) {
			res.setEntry((i + (virtualSize)), a[i]);
		}
		return res;
	}

	public org.apache.commons.math.linear.FieldVector<T> copy() {
		return new org.apache.commons.math.linear.SparseFieldVector<T>(this);
	}

	public T dotProduct(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
		T res = field.getZero();
		org.apache.commons.math.util.OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			res = res.add(v.getEntry(iter.key()).multiply(iter.value()));
		}
		return res;
	}

	public T dotProduct(T[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		T res = field.getZero();
		org.apache.commons.math.util.OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			int idx = iter.key();
			T value = field.getZero();
			if (idx < (v.length)) {
				value = v[idx];
			} 
			res = res.add(value.multiply(iter.value()));
		}
		return res;
	}

	public org.apache.commons.math.linear.FieldVector<T> ebeDivide(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
		org.apache.commons.math.linear.SparseFieldVector<T> res = new org.apache.commons.math.linear.SparseFieldVector<T>(this);
		org.apache.commons.math.util.OpenIntToFieldHashMap<T>.Iterator iter = res.entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			res.setEntry(iter.key(), iter.value().divide(v.getEntry(iter.key())));
		}
		return res;
	}

	public org.apache.commons.math.linear.FieldVector<T> ebeDivide(T[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		org.apache.commons.math.linear.SparseFieldVector<T> res = new org.apache.commons.math.linear.SparseFieldVector<T>(this);
		org.apache.commons.math.util.OpenIntToFieldHashMap<T>.Iterator iter = res.entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			res.setEntry(iter.key(), iter.value().divide(v[iter.key()]));
		}
		return res;
	}

	public org.apache.commons.math.linear.FieldVector<T> ebeMultiply(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
		org.apache.commons.math.linear.SparseFieldVector<T> res = new org.apache.commons.math.linear.SparseFieldVector<T>(this);
		org.apache.commons.math.util.OpenIntToFieldHashMap<T>.Iterator iter = res.entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			res.setEntry(iter.key(), iter.value().multiply(v.getEntry(iter.key())));
		}
		return res;
	}

	public org.apache.commons.math.linear.FieldVector<T> ebeMultiply(T[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		org.apache.commons.math.linear.SparseFieldVector<T> res = new org.apache.commons.math.linear.SparseFieldVector<T>(this);
		org.apache.commons.math.util.OpenIntToFieldHashMap<T>.Iterator iter = res.entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			res.setEntry(iter.key(), iter.value().multiply(v[iter.key()]));
		}
		return res;
	}

	public T[] getData() {
		T[] res = buildArray(virtualSize);
		org.apache.commons.math.util.OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			res[iter.key()] = iter.value();
		}
		return res;
	}

	public int getDimension() {
		return virtualSize;
	}

	public T getEntry(int index) throws org.apache.commons.math.linear.MatrixIndexException {
		checkIndex(index);
		return entries.get(index);
	}

	public org.apache.commons.math.Field<T> getField() {
		return field;
	}

	public org.apache.commons.math.linear.FieldVector<T> getSubVector(int index, int n) throws org.apache.commons.math.linear.MatrixIndexException {
		checkIndex(index);
		checkIndex(((index + n) - 1));
		org.apache.commons.math.linear.SparseFieldVector<T> res = new org.apache.commons.math.linear.SparseFieldVector<T>(field , n);
		int end = index + n;
		org.apache.commons.math.util.OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			int key = iter.key();
			if ((key >= index) && (key < end)) {
				res.setEntry((key - index), iter.value());
			} 
		}
		return res;
	}

	public org.apache.commons.math.linear.FieldVector<T> mapAdd(T d) {
		return copy().mapAddToSelf(d);
	}

	public org.apache.commons.math.linear.FieldVector<T> mapAddToSelf(T d) {
		for (int i = 0 ; i < (virtualSize) ; i++) {
			setEntry(i, getEntry(i).add(d));
		}
		return this;
	}

	public org.apache.commons.math.linear.FieldVector<T> mapDivide(T d) {
		return copy().mapDivideToSelf(d);
	}

	public org.apache.commons.math.linear.FieldVector<T> mapDivideToSelf(T d) {
		org.apache.commons.math.util.OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			entries.put(iter.key(), iter.value().divide(d));
		}
		return this;
	}

	public org.apache.commons.math.linear.FieldVector<T> mapInv() {
		return copy().mapInvToSelf();
	}

	public org.apache.commons.math.linear.FieldVector<T> mapInvToSelf() {
		for (int i = 0 ; i < (virtualSize) ; i++) {
			setEntry(i, field.getOne().divide(getEntry(i)));
		}
		return this;
	}

	public org.apache.commons.math.linear.FieldVector<T> mapMultiply(T d) {
		return copy().mapMultiplyToSelf(d);
	}

	public org.apache.commons.math.linear.FieldVector<T> mapMultiplyToSelf(T d) {
		org.apache.commons.math.util.OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			entries.put(iter.key(), iter.value().multiply(d));
		}
		return this;
	}

	public org.apache.commons.math.linear.FieldVector<T> mapSubtract(T d) {
		return copy().mapSubtractToSelf(d);
	}

	public org.apache.commons.math.linear.FieldVector<T> mapSubtractToSelf(T d) {
		return mapAddToSelf(field.getZero().subtract(d));
	}

	public org.apache.commons.math.linear.FieldMatrix<T> outerProduct(org.apache.commons.math.linear.SparseFieldVector<T> v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
		org.apache.commons.math.linear.SparseFieldMatrix<T> res = new org.apache.commons.math.linear.SparseFieldMatrix<T>(field , virtualSize , virtualSize);
		org.apache.commons.math.util.OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			org.apache.commons.math.util.OpenIntToFieldHashMap<T>.Iterator iter2 = v.entries.iterator();
			while (iter2.hasNext()) {
				iter2.advance();
				res.setEntry(iter.key(), iter2.key(), iter.value().multiply(iter2.value()));
			}
		}
		return res;
	}

	public org.apache.commons.math.linear.FieldMatrix<T> outerProduct(T[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		org.apache.commons.math.linear.FieldMatrix<T> res = new org.apache.commons.math.linear.SparseFieldMatrix<T>(field , virtualSize , virtualSize);
		org.apache.commons.math.util.OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			int row = iter.key();
			org.apache.commons.math.FieldElement<T> value = iter.value();
			for (int col = 0 ; col < (virtualSize) ; col++) {
				res.setEntry(row, col, value.multiply(v[col]));
			}
		}
		return res;
	}

	public org.apache.commons.math.linear.FieldMatrix<T> outerProduct(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
		if (v instanceof org.apache.commons.math.linear.SparseFieldVector<?>) {
			return outerProduct(((org.apache.commons.math.linear.SparseFieldVector<T>)(v)));
		} else {
			return outerProduct(v.toArray());
		}
	}

	public org.apache.commons.math.linear.FieldVector<T> projection(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
		return v.mapMultiply(dotProduct(v).divide(v.dotProduct(v)));
	}

	public org.apache.commons.math.linear.FieldVector<T> projection(T[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		return projection(new org.apache.commons.math.linear.SparseFieldVector<T>(field , v));
	}

	public void set(T value) {
		for (int i = 0 ; i < (virtualSize) ; i++) {
			setEntry(i, value);
		}
	}

	public void setEntry(int index, T value) throws org.apache.commons.math.linear.MatrixIndexException {
		checkIndex(index);
		entries.put(index, value);
	}

	public void setSubVector(int index, org.apache.commons.math.linear.FieldVector<T> v) throws org.apache.commons.math.linear.MatrixIndexException {
		checkIndex(index);
		checkIndex(((index + (v.getDimension())) - 1));
		setSubVector(index, v.getData());
	}

	public void setSubVector(int index, T[] v) throws org.apache.commons.math.linear.MatrixIndexException {
		checkIndex(index);
		checkIndex(((index + (v.length)) - 1));
		for (int i = 0 ; i < (v.length) ; i++) {
			setEntry((i + index), v[i]);
		}
	}

	public org.apache.commons.math.linear.SparseFieldVector<T> subtract(org.apache.commons.math.linear.SparseFieldVector<T> v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
		org.apache.commons.math.linear.SparseFieldVector<T> res = ((org.apache.commons.math.linear.SparseFieldVector<T>)(copy()));
		org.apache.commons.math.util.OpenIntToFieldHashMap<T>.Iterator iter = v.getEntries().iterator();
		while (iter.hasNext()) {
			iter.advance();
			int key = iter.key();
			if (entries.containsKey(key)) {
				res.setEntry(key, entries.get(key).subtract(iter.value()));
			} else {
				res.setEntry(key, field.getZero().subtract(iter.value()));
			}
		}
		return res;
	}

	public org.apache.commons.math.linear.FieldVector<T> subtract(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
		if (v instanceof org.apache.commons.math.linear.SparseFieldVector<?>) {
			return subtract(((org.apache.commons.math.linear.SparseFieldVector<T>)(v)));
		} else {
			return subtract(v.toArray());
		}
	}

	public org.apache.commons.math.linear.FieldVector<T> subtract(T[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		org.apache.commons.math.linear.SparseFieldVector<T> res = new org.apache.commons.math.linear.SparseFieldVector<T>(this);
		for (int i = 0 ; i < (v.length) ; i++) {
			if (entries.containsKey(i)) {
				res.setEntry(i, entries.get(i).subtract(v[i]));
			} else {
				res.setEntry(i, field.getZero().subtract(v[i]));
			}
		}
		return res;
	}

	public T[] toArray() {
		return getData();
	}

	private void checkIndex(final int index) throws org.apache.commons.math.linear.MatrixIndexException {
		if ((index < 0) || (index >= (getDimension()))) {
			throw new org.apache.commons.math.linear.MatrixIndexException("index {0} out of allowed range [{1}, {2}]" , index , 0 , ((getDimension()) - 1));
		} 
	}

	protected void checkVectorDimensions(int n) throws java.lang.IllegalArgumentException {
		if ((getDimension()) != n) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("vector length mismatch: got {0} but expected {1}", getDimension(), n);
		} 
	}

	public org.apache.commons.math.linear.FieldVector<T> add(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
		if (v instanceof org.apache.commons.math.linear.SparseFieldVector<?>) {
			return add(((org.apache.commons.math.linear.SparseFieldVector<T>)(v)));
		} else {
			return add(v.toArray());
		}
	}

	@java.lang.SuppressWarnings(value = "unchecked")
	private T[] buildArray(final int length) {
		return ((T[])(java.lang.reflect.Array.newInstance(field.getZero().getClass(), length)));
	}

	@java.lang.Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((field) == null ? 0 : field.hashCode());
		result = (prime * result) + (virtualSize);
		org.apache.commons.math.util.OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			int temp = iter.value().hashCode();
			result = (prime * result) + temp;
		}
		return result;
	}

	@java.lang.Override
	public boolean equals(java.lang.Object obj) {
		if ((this) == obj) {
			return true;
		} 
		if (!(obj instanceof org.apache.commons.math.linear.SparseFieldVector<?>)) {
			return false;
		} 
		@java.lang.SuppressWarnings(value = "unchecked")
		org.apache.commons.math.linear.SparseFieldVector<T> other = ((org.apache.commons.math.linear.SparseFieldVector<T>)(obj));
		if ((field) == null) {
			if ((other.field) != null) {
				return false;
			} 
		} else {
			if (!(field.equals(other.field))) {
				return false;
			} 
		}
		if ((virtualSize) != (other.virtualSize)) {
			return false;
		} 
		org.apache.commons.math.util.OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			T test = other.getEntry(iter.key());
			if (!(test.equals(iter.value()))) {
				return false;
			} 
		}
		iter = other.getEntries().iterator();
		while (iter.hasNext()) {
			iter.advance();
			T test = iter.value();
			if (!(test.equals(getEntry(iter.key())))) {
				return false;
			} 
		}
		return true;
	}
}

