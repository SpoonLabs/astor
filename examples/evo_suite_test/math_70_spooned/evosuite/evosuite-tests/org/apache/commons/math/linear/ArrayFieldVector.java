package org.apache.commons.math.linear;


public class ArrayFieldVector<T extends org.apache.commons.math.FieldElement<T>> implements java.io.Serializable , org.apache.commons.math.linear.FieldVector<T> {
	private static final long serialVersionUID = 7648186910365927050L;

	protected T[] data;

	private final org.apache.commons.math.Field<T> field;

	public ArrayFieldVector(final org.apache.commons.math.Field<T> field) {
		this(field, 0);
	}

	public ArrayFieldVector(org.apache.commons.math.Field<T> field ,int size) {
		this.field = field;
		data = buildArray(size);
		java.util.Arrays.fill(data, field.getZero());
	}

	public ArrayFieldVector(int size ,T preset) {
		this(preset.getField(), size);
		java.util.Arrays.fill(data, preset);
	}

	public ArrayFieldVector(T[] d) throws java.lang.IllegalArgumentException {
		try {
			field = d[0].getField();
			data = d.clone();
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("vector must have at least one element");
		}
	}

	public ArrayFieldVector(T[] d ,boolean copyArray) throws java.lang.IllegalArgumentException , java.lang.NullPointerException {
		try {
			field = d[0].getField();
			data = copyArray ? d.clone() : d;
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("vector must have at least one element");
		}
	}

	public ArrayFieldVector(T[] d ,int pos ,int size) {
		if ((d.length) < (pos + size)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("position {0} and size {1} don't fit to the size of the input array {2}", pos, size, d.length);
		} 
		field = d[0].getField();
		data = buildArray(size);
		java.lang.System.arraycopy(d, pos, data, 0, size);
	}

	public ArrayFieldVector(org.apache.commons.math.linear.FieldVector<T> v) {
		field = v.getField();
		data = buildArray(v.getDimension());
		for (int i = 0 ; i < (data.length) ; ++i) {
			data[i] = v.getEntry(i);
		}
	}

	public ArrayFieldVector(org.apache.commons.math.linear.ArrayFieldVector<T> v) {
		field = v.getField();
		data = v.data.clone();
	}

	public ArrayFieldVector(org.apache.commons.math.linear.ArrayFieldVector<T> v ,boolean deep) {
		field = v.getField();
		data = deep ? v.data.clone() : v.data;
	}

	public ArrayFieldVector(org.apache.commons.math.linear.ArrayFieldVector<T> v1 ,org.apache.commons.math.linear.ArrayFieldVector<T> v2) {
		field = v1.getField();
		data = buildArray(((v1.data.length) + (v2.data.length)));
		java.lang.System.arraycopy(v1.data, 0, data, 0, v1.data.length);
		java.lang.System.arraycopy(v2.data, 0, data, v1.data.length, v2.data.length);
	}

	public ArrayFieldVector(org.apache.commons.math.linear.ArrayFieldVector<T> v1 ,T[] v2) {
		field = v1.getField();
		data = buildArray(((v1.data.length) + (v2.length)));
		java.lang.System.arraycopy(v1.data, 0, data, 0, v1.data.length);
		java.lang.System.arraycopy(v2, 0, data, v1.data.length, v2.length);
	}

	public ArrayFieldVector(T[] v1 ,org.apache.commons.math.linear.ArrayFieldVector<T> v2) {
		field = v2.getField();
		data = buildArray(((v1.length) + (v2.data.length)));
		java.lang.System.arraycopy(v1, 0, data, 0, v1.length);
		java.lang.System.arraycopy(v2.data, 0, data, v1.length, v2.data.length);
	}

	public ArrayFieldVector(T[] v1 ,T[] v2) {
		try {
			data = buildArray(((v1.length) + (v2.length)));
			java.lang.System.arraycopy(v1, 0, data, 0, v1.length);
			java.lang.System.arraycopy(v2, 0, data, v1.length, v2.length);
			field = data[0].getField();
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("vector must have at least one element");
		}
	}

	@java.lang.SuppressWarnings(value = "unchecked")
	private T[] buildArray(final int length) {
		return ((T[])(java.lang.reflect.Array.newInstance(field.getZero().getClass(), length)));
	}

	public org.apache.commons.math.Field<T> getField() {
		return field;
	}

	public org.apache.commons.math.linear.FieldVector<T> copy() {
		return new org.apache.commons.math.linear.ArrayFieldVector<T>(this , true);
	}

	public org.apache.commons.math.linear.FieldVector<T> add(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
		try {
			return add(((org.apache.commons.math.linear.ArrayFieldVector<T>)(v)));
		} catch (java.lang.ClassCastException cce) {
			checkVectorDimensions(v);
			T[] out = buildArray(data.length);
			for (int i = 0 ; i < (data.length) ; i++) {
				out[i] = data[i].add(v.getEntry(i));
			}
			return new org.apache.commons.math.linear.ArrayFieldVector<T>(out);
		}
	}

	public org.apache.commons.math.linear.FieldVector<T> add(T[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		T[] out = buildArray(data.length);
		for (int i = 0 ; i < (data.length) ; i++) {
			out[i] = data[i].add(v[i]);
		}
		return new org.apache.commons.math.linear.ArrayFieldVector<T>(out);
	}

	public org.apache.commons.math.linear.ArrayFieldVector<T> add(org.apache.commons.math.linear.ArrayFieldVector<T> v) throws java.lang.IllegalArgumentException {
		return ((org.apache.commons.math.linear.ArrayFieldVector<T>)(add(v.data)));
	}

	public org.apache.commons.math.linear.FieldVector<T> subtract(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
		try {
			return subtract(((org.apache.commons.math.linear.ArrayFieldVector<T>)(v)));
		} catch (java.lang.ClassCastException cce) {
			checkVectorDimensions(v);
			T[] out = buildArray(data.length);
			for (int i = 0 ; i < (data.length) ; i++) {
				out[i] = data[i].subtract(v.getEntry(i));
			}
			return new org.apache.commons.math.linear.ArrayFieldVector<T>(out);
		}
	}

	public org.apache.commons.math.linear.FieldVector<T> subtract(T[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		T[] out = buildArray(data.length);
		for (int i = 0 ; i < (data.length) ; i++) {
			out[i] = data[i].subtract(v[i]);
		}
		return new org.apache.commons.math.linear.ArrayFieldVector<T>(out);
	}

	public org.apache.commons.math.linear.ArrayFieldVector<T> subtract(org.apache.commons.math.linear.ArrayFieldVector<T> v) throws java.lang.IllegalArgumentException {
		return ((org.apache.commons.math.linear.ArrayFieldVector<T>)(subtract(v.data)));
	}

	public org.apache.commons.math.linear.FieldVector<T> mapAdd(T d) {
		T[] out = buildArray(data.length);
		for (int i = 0 ; i < (data.length) ; i++) {
			out[i] = data[i].add(d);
		}
		return new org.apache.commons.math.linear.ArrayFieldVector<T>(out);
	}

	public org.apache.commons.math.linear.FieldVector<T> mapAddToSelf(T d) {
		for (int i = 0 ; i < (data.length) ; i++) {
			data[i] = data[i].add(d);
		}
		return this;
	}

	public org.apache.commons.math.linear.FieldVector<T> mapSubtract(T d) {
		T[] out = buildArray(data.length);
		for (int i = 0 ; i < (data.length) ; i++) {
			out[i] = data[i].subtract(d);
		}
		return new org.apache.commons.math.linear.ArrayFieldVector<T>(out);
	}

	public org.apache.commons.math.linear.FieldVector<T> mapSubtractToSelf(T d) {
		for (int i = 0 ; i < (data.length) ; i++) {
			data[i] = data[i].subtract(d);
		}
		return this;
	}

	public org.apache.commons.math.linear.FieldVector<T> mapMultiply(T d) {
		T[] out = buildArray(data.length);
		for (int i = 0 ; i < (data.length) ; i++) {
			out[i] = data[i].multiply(d);
		}
		return new org.apache.commons.math.linear.ArrayFieldVector<T>(out);
	}

	public org.apache.commons.math.linear.FieldVector<T> mapMultiplyToSelf(T d) {
		for (int i = 0 ; i < (data.length) ; i++) {
			data[i] = data[i].multiply(d);
		}
		return this;
	}

	public org.apache.commons.math.linear.FieldVector<T> mapDivide(T d) {
		T[] out = buildArray(data.length);
		for (int i = 0 ; i < (data.length) ; i++) {
			out[i] = data[i].divide(d);
		}
		return new org.apache.commons.math.linear.ArrayFieldVector<T>(out);
	}

	public org.apache.commons.math.linear.FieldVector<T> mapDivideToSelf(T d) {
		for (int i = 0 ; i < (data.length) ; i++) {
			data[i] = data[i].divide(d);
		}
		return this;
	}

	public org.apache.commons.math.linear.FieldVector<T> mapInv() {
		T[] out = buildArray(data.length);
		final T one = field.getOne();
		for (int i = 0 ; i < (data.length) ; i++) {
			out[i] = one.divide(data[i]);
		}
		return new org.apache.commons.math.linear.ArrayFieldVector<T>(out);
	}

	public org.apache.commons.math.linear.FieldVector<T> mapInvToSelf() {
		final T one = field.getOne();
		for (int i = 0 ; i < (data.length) ; i++) {
			data[i] = one.divide(data[i]);
		}
		return this;
	}

	public org.apache.commons.math.linear.FieldVector<T> ebeMultiply(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
		try {
			return ebeMultiply(((org.apache.commons.math.linear.ArrayFieldVector<T>)(v)));
		} catch (java.lang.ClassCastException cce) {
			checkVectorDimensions(v);
			T[] out = buildArray(data.length);
			for (int i = 0 ; i < (data.length) ; i++) {
				out[i] = data[i].multiply(v.getEntry(i));
			}
			return new org.apache.commons.math.linear.ArrayFieldVector<T>(out);
		}
	}

	public org.apache.commons.math.linear.FieldVector<T> ebeMultiply(T[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		T[] out = buildArray(data.length);
		for (int i = 0 ; i < (data.length) ; i++) {
			out[i] = data[i].multiply(v[i]);
		}
		return new org.apache.commons.math.linear.ArrayFieldVector<T>(out);
	}

	public org.apache.commons.math.linear.ArrayFieldVector<T> ebeMultiply(org.apache.commons.math.linear.ArrayFieldVector<T> v) throws java.lang.IllegalArgumentException {
		return ((org.apache.commons.math.linear.ArrayFieldVector<T>)(ebeMultiply(v.data)));
	}

	public org.apache.commons.math.linear.FieldVector<T> ebeDivide(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
		try {
			return ebeDivide(((org.apache.commons.math.linear.ArrayFieldVector<T>)(v)));
		} catch (java.lang.ClassCastException cce) {
			checkVectorDimensions(v);
			T[] out = buildArray(data.length);
			for (int i = 0 ; i < (data.length) ; i++) {
				out[i] = data[i].divide(v.getEntry(i));
			}
			return new org.apache.commons.math.linear.ArrayFieldVector<T>(out);
		}
	}

	public org.apache.commons.math.linear.FieldVector<T> ebeDivide(T[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		T[] out = buildArray(data.length);
		for (int i = 0 ; i < (data.length) ; i++) {
			out[i] = data[i].divide(v[i]);
		}
		return new org.apache.commons.math.linear.ArrayFieldVector<T>(out);
	}

	public org.apache.commons.math.linear.ArrayFieldVector<T> ebeDivide(org.apache.commons.math.linear.ArrayFieldVector<T> v) throws java.lang.IllegalArgumentException {
		return ((org.apache.commons.math.linear.ArrayFieldVector<T>)(ebeDivide(v.data)));
	}

	public T[] getData() {
		return data.clone();
	}

	public T[] getDataRef() {
		return data;
	}

	public T dotProduct(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
		try {
			return dotProduct(((org.apache.commons.math.linear.ArrayFieldVector<T>)(v)));
		} catch (java.lang.ClassCastException cce) {
			checkVectorDimensions(v);
			T dot = field.getZero();
			for (int i = 0 ; i < (data.length) ; i++) {
				dot = dot.add(data[i].multiply(v.getEntry(i)));
			}
			return dot;
		}
	}

	public T dotProduct(T[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		T dot = field.getZero();
		for (int i = 0 ; i < (data.length) ; i++) {
			dot = dot.add(data[i].multiply(v[i]));
		}
		return dot;
	}

	public T dotProduct(org.apache.commons.math.linear.ArrayFieldVector<T> v) throws java.lang.IllegalArgumentException {
		return dotProduct(v.data);
	}

	public org.apache.commons.math.linear.FieldVector<T> projection(org.apache.commons.math.linear.FieldVector<T> v) {
		return v.mapMultiply(dotProduct(v).divide(v.dotProduct(v)));
	}

	public org.apache.commons.math.linear.FieldVector<T> projection(T[] v) {
		return projection(new org.apache.commons.math.linear.ArrayFieldVector<T>(v , false));
	}

	public org.apache.commons.math.linear.ArrayFieldVector<T> projection(org.apache.commons.math.linear.ArrayFieldVector<T> v) {
		return ((org.apache.commons.math.linear.ArrayFieldVector<T>)(v.mapMultiply(dotProduct(v).divide(v.dotProduct(v)))));
	}

	public org.apache.commons.math.linear.FieldMatrix<T> outerProduct(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
		try {
			return outerProduct(((org.apache.commons.math.linear.ArrayFieldVector<T>)(v)));
		} catch (java.lang.ClassCastException cce) {
			checkVectorDimensions(v);
			final int m = data.length;
			final org.apache.commons.math.linear.FieldMatrix<T> out = new org.apache.commons.math.linear.Array2DRowFieldMatrix<T>(field , m , m);
			for (int i = 0 ; i < (data.length) ; i++) {
				for (int j = 0 ; j < (data.length) ; j++) {
					out.setEntry(i, j, data[i].multiply(v.getEntry(j)));
				}
			}
			return out;
		}
	}

	public org.apache.commons.math.linear.FieldMatrix<T> outerProduct(org.apache.commons.math.linear.ArrayFieldVector<T> v) throws java.lang.IllegalArgumentException {
		return outerProduct(v.data);
	}

	public org.apache.commons.math.linear.FieldMatrix<T> outerProduct(T[] v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.length);
		final int m = data.length;
		final org.apache.commons.math.linear.FieldMatrix<T> out = new org.apache.commons.math.linear.Array2DRowFieldMatrix<T>(field , m , m);
		for (int i = 0 ; i < (data.length) ; i++) {
			for (int j = 0 ; j < (data.length) ; j++) {
				out.setEntry(i, j, data[i].multiply(v[j]));
			}
		}
		return out;
	}

	public T getEntry(int index) throws org.apache.commons.math.linear.MatrixIndexException {
		return data[index];
	}

	public int getDimension() {
		return data.length;
	}

	public org.apache.commons.math.linear.FieldVector<T> append(org.apache.commons.math.linear.FieldVector<T> v) {
		try {
			return append(((org.apache.commons.math.linear.ArrayFieldVector<T>)(v)));
		} catch (java.lang.ClassCastException cce) {
			return new org.apache.commons.math.linear.ArrayFieldVector<T>(this , new org.apache.commons.math.linear.ArrayFieldVector<T>(v));
		}
	}

	public org.apache.commons.math.linear.ArrayFieldVector<T> append(org.apache.commons.math.linear.ArrayFieldVector<T> v) {
		return new org.apache.commons.math.linear.ArrayFieldVector<T>(this , v);
	}

	public org.apache.commons.math.linear.FieldVector<T> append(T in) {
		final T[] out = buildArray(((data.length) + 1));
		java.lang.System.arraycopy(data, 0, out, 0, data.length);
		out[data.length] = in;
		return new org.apache.commons.math.linear.ArrayFieldVector<T>(out);
	}

	public org.apache.commons.math.linear.FieldVector<T> append(T[] in) {
		return new org.apache.commons.math.linear.ArrayFieldVector<T>(this , in);
	}

	public org.apache.commons.math.linear.FieldVector<T> getSubVector(int index, int n) {
		org.apache.commons.math.linear.ArrayFieldVector<T> out = new org.apache.commons.math.linear.ArrayFieldVector<T>(field , n);
		try {
			java.lang.System.arraycopy(data, index, out.data, 0, n);
		} catch (java.lang.IndexOutOfBoundsException e) {
			checkIndex(index);
			checkIndex(((index + n) - 1));
		}
		return out;
	}

	public void setEntry(int index, T value) {
		try {
			data[index] = value;
		} catch (java.lang.IndexOutOfBoundsException e) {
			checkIndex(index);
		}
	}

	public void setSubVector(int index, org.apache.commons.math.linear.FieldVector<T> v) {
		try {
			try {
				set(index, ((org.apache.commons.math.linear.ArrayFieldVector<T>)(v)));
			} catch (java.lang.ClassCastException cce) {
				for (int i = index ; i < (index + (v.getDimension())) ; ++i) {
					data[i] = v.getEntry((i - index));
				}
			}
		} catch (java.lang.IndexOutOfBoundsException e) {
			checkIndex(index);
			checkIndex(((index + (v.getDimension())) - 1));
		}
	}

	public void setSubVector(int index, T[] v) {
		try {
			java.lang.System.arraycopy(v, 0, data, index, v.length);
		} catch (java.lang.IndexOutOfBoundsException e) {
			checkIndex(index);
			checkIndex(((index + (v.length)) - 1));
		}
	}

	public void set(int index, org.apache.commons.math.linear.ArrayFieldVector<T> v) throws org.apache.commons.math.linear.MatrixIndexException {
		setSubVector(index, v.data);
	}

	public void set(T value) {
		java.util.Arrays.fill(data, value);
	}

	public T[] toArray() {
		return data.clone();
	}

	protected void checkVectorDimensions(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException {
		checkVectorDimensions(v.getDimension());
	}

	protected void checkVectorDimensions(int n) throws java.lang.IllegalArgumentException {
		if ((data.length) != n) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("vector length mismatch: got {0} but expected {1}", data.length, n);
		} 
	}

	@java.lang.Override
	public boolean equals(java.lang.Object other) {
		if ((this) == other) {
			return true;
		} 
		if (other == null) {
			return false;
		} 
		try {
			@java.lang.SuppressWarnings(value = "unchecked")
			org.apache.commons.math.linear.FieldVector<T> rhs = ((org.apache.commons.math.linear.FieldVector<T>)(other));
			if ((data.length) != (rhs.getDimension())) {
				return false;
			} 
			for (int i = 0 ; i < (data.length) ; ++i) {
				if (!(data[i].equals(rhs.getEntry(i)))) {
					return false;
				} 
			}
			return true;
		} catch (java.lang.ClassCastException ex) {
			return false;
		}
	}

	@java.lang.Override
	public int hashCode() {
		int h = 3542;
		for (final T a : data) {
			h = h ^ (a.hashCode());
		}
		return h;
	}

	private void checkIndex(final int index) throws org.apache.commons.math.linear.MatrixIndexException {
		if ((index < 0) || (index >= (getDimension()))) {
			throw new org.apache.commons.math.linear.MatrixIndexException("index {0} out of allowed range [{1}, {2}]" , index , 0 , ((getDimension()) - 1));
		} 
	}
}

