package org.apache.commons.math.util;


public class ResizableDoubleArray implements java.io.Serializable , org.apache.commons.math.util.DoubleArray {
	public static final int ADDITIVE_MODE = 1;

	public static final int MULTIPLICATIVE_MODE = 0;

	private static final long serialVersionUID = -3485529955529426875L;

	protected float contractionCriteria = 2.5F;

	protected float expansionFactor = 2.0F;

	protected int expansionMode = MULTIPLICATIVE_MODE;

	protected int initialCapacity = 16;

	protected double[] internalArray;

	protected int numElements = 0;

	protected int startIndex = 0;

	public ResizableDoubleArray() {
		internalArray = new double[initialCapacity];
	}

	public ResizableDoubleArray(int initialCapacity) {
		setInitialCapacity(initialCapacity);
		internalArray = new double[this.initialCapacity];
	}

	public ResizableDoubleArray(int initialCapacity ,float expansionFactor) {
		this.expansionFactor = expansionFactor;
		setInitialCapacity(initialCapacity);
		internalArray = new double[initialCapacity];
		setContractionCriteria((expansionFactor + 0.5F));
	}

	public ResizableDoubleArray(int initialCapacity ,float expansionFactor ,float contractionCriteria) {
		this.expansionFactor = expansionFactor;
		setContractionCriteria(contractionCriteria);
		setInitialCapacity(initialCapacity);
		internalArray = new double[initialCapacity];
	}

	public ResizableDoubleArray(int initialCapacity ,float expansionFactor ,float contractionCriteria ,int expansionMode) {
		this.expansionFactor = expansionFactor;
		setContractionCriteria(contractionCriteria);
		setInitialCapacity(initialCapacity);
		setExpansionMode(expansionMode);
		internalArray = new double[initialCapacity];
	}

	public ResizableDoubleArray(org.apache.commons.math.util.ResizableDoubleArray original) {
		org.apache.commons.math.util.ResizableDoubleArray.copy(original, this);
	}

	public synchronized void addElement(double value) {
		(numElements)++;
		if (((startIndex) + (numElements)) > (internalArray.length)) {
			expand();
		} 
		internalArray[((startIndex) + ((numElements) - 1))] = value;
		if (shouldContract()) {
			contract();
		} 
	}

	public synchronized double addElementRolling(double value) {
		double discarded = internalArray[startIndex];
		if (((startIndex) + ((numElements) + 1)) > (internalArray.length)) {
			expand();
		} 
		startIndex += 1;
		internalArray[((startIndex) + ((numElements) - 1))] = value;
		if (shouldContract()) {
			contract();
		} 
		return discarded;
	}

	public synchronized double substituteMostRecentElement(double value) {
		if ((numElements) < 1) {
			throw org.apache.commons.math.MathRuntimeException.createArrayIndexOutOfBoundsException("cannot substitute an element from an empty array");
		} 
		double discarded = internalArray[((startIndex) + ((numElements) - 1))];
		internalArray[((startIndex) + ((numElements) - 1))] = value;
		return discarded;
	}

	protected void checkContractExpand(float contraction, float expansion) {
		if (contraction < expansion) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(("contraction criteria ({0}) smaller than the expansion factor ({1}).  This would " + ("lead to a never ending loop of expansion and contraction as a newly expanded " + "internal storage array would immediately satisfy the criteria for contraction")), contraction, expansion);
		} 
		if (contraction <= 1.0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(("contraction criteria smaller than one ({0}).  This would lead to a never ending " + ("loop of expansion and contraction as an internal storage array length equal " + "to the number of elements would satisfy the contraction criteria.")), contraction);
		} 
		if (expansion <= 1.0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("expansion factor smaller than one ({0})", expansion);
		} 
	}

	public synchronized void clear() {
		numElements = 0;
		startIndex = 0;
		internalArray = new double[initialCapacity];
	}

	public synchronized void contract() {
		double[] tempArray = new double[(numElements) + 1];
		java.lang.System.arraycopy(internalArray, startIndex, tempArray, 0, numElements);
		internalArray = tempArray;
		startIndex = 0;
	}

	public synchronized void discardFrontElements(int i) {
		discardExtremeElements(i, true);
	}

	public synchronized void discardMostRecentElements(int i) {
		discardExtremeElements(i, false);
	}

	private synchronized void discardExtremeElements(int i, boolean front) {
		if (i > (numElements)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("cannot discard {0} elements from a {1} elements array", i, numElements);
		} else if (i < 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("cannot discard a negative number of elements ({0})", i);
		} else {
			numElements -= i;
			if (front)
				startIndex += i;
			
		}
		if (shouldContract()) {
			contract();
		} 
	}

	protected synchronized void expand() {
		int newSize = 0;
		if ((expansionMode) == (MULTIPLICATIVE_MODE)) {
			newSize = ((int)(java.lang.Math.ceil(((internalArray.length) * (expansionFactor)))));
		} else {
			newSize = (internalArray.length) + (java.lang.Math.round(expansionFactor));
		}
		double[] tempArray = new double[newSize];
		java.lang.System.arraycopy(internalArray, 0, tempArray, 0, internalArray.length);
		internalArray = tempArray;
	}

	private synchronized void expandTo(int size) {
		double[] tempArray = new double[size];
		java.lang.System.arraycopy(internalArray, 0, tempArray, 0, internalArray.length);
		internalArray = tempArray;
	}

	public float getContractionCriteria() {
		return contractionCriteria;
	}

	public synchronized double getElement(int index) {
		if (index >= (numElements)) {
			throw org.apache.commons.math.MathRuntimeException.createArrayIndexOutOfBoundsException("the index specified: {0} is larger than the current maximal index {1}", index, ((numElements) - 1));
		} else if (index >= 0) {
			return internalArray[((startIndex) + index)];
		} else {
			throw org.apache.commons.math.MathRuntimeException.createArrayIndexOutOfBoundsException("elements cannot be retrieved from a negative array index {0}", index);
		}
	}

	public synchronized double[] getElements() {
		double[] elementArray = new double[numElements];
		java.lang.System.arraycopy(internalArray, startIndex, elementArray, 0, numElements);
		return elementArray;
	}

	public float getExpansionFactor() {
		return expansionFactor;
	}

	public int getExpansionMode() {
		return expansionMode;
	}

	synchronized int getInternalLength() {
		return internalArray.length;
	}

	public synchronized int getNumElements() {
		return numElements;
	}

	@java.lang.Deprecated
	public synchronized double[] getValues() {
		return internalArray;
	}

	public synchronized double[] getInternalValues() {
		return internalArray;
	}

	public void setContractionCriteria(float contractionCriteria) {
		checkContractExpand(contractionCriteria, getExpansionFactor());
		synchronized(this) {
			this.contractionCriteria = contractionCriteria;
		}
	}

	public synchronized void setElement(int index, double value) {
		if (index < 0) {
			throw org.apache.commons.math.MathRuntimeException.createArrayIndexOutOfBoundsException("cannot set an element at a negative index {0}", index);
		} 
		if ((index + 1) > (numElements)) {
			numElements = index + 1;
		} 
		if (((startIndex) + index) >= (internalArray.length)) {
			expandTo(((startIndex) + (index + 1)));
		} 
		internalArray[((startIndex) + index)] = value;
	}

	public void setExpansionFactor(float expansionFactor) {
		checkContractExpand(getContractionCriteria(), expansionFactor);
		synchronized(this) {
			this.expansionFactor = expansionFactor;
		}
	}

	public void setExpansionMode(int expansionMode) {
		if ((expansionMode != (MULTIPLICATIVE_MODE)) && (expansionMode != (ADDITIVE_MODE))) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("unsupported expansion mode {0}, supported modes are {1} ({2}) and {3} ({4})", expansionMode, MULTIPLICATIVE_MODE, "MULTIPLICATIVE_MODE", ADDITIVE_MODE, "ADDITIVE_MODE");
		} 
		synchronized(this) {
			this.expansionMode = expansionMode;
		}
	}

	protected void setInitialCapacity(int initialCapacity) {
		if (initialCapacity > 0) {
			synchronized(this) {
				this.initialCapacity = initialCapacity;
			}
		} else {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("initial capacity ({0}) is not positive", initialCapacity);
		}
	}

	public synchronized void setNumElements(int i) {
		if (i < 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("index ({0}) is not positive", i);
		} 
		if (((startIndex) + i) > (internalArray.length)) {
			expandTo(((startIndex) + i));
		} 
		numElements = i;
	}

	private synchronized boolean shouldContract() {
		if ((expansionMode) == (MULTIPLICATIVE_MODE)) {
			return ((internalArray.length) / ((float)(numElements))) > (contractionCriteria);
		} else {
			return ((internalArray.length) - (numElements)) > (contractionCriteria);
		}
	}

	public synchronized int start() {
		return startIndex;
	}

	public static void copy(org.apache.commons.math.util.ResizableDoubleArray source, org.apache.commons.math.util.ResizableDoubleArray dest) {
		synchronized(source) {
			synchronized(dest) {
				dest.initialCapacity = source.initialCapacity;
				dest.contractionCriteria = source.contractionCriteria;
				dest.expansionFactor = source.expansionFactor;
				dest.expansionMode = source.expansionMode;
				dest.internalArray = new double[source.internalArray.length];
				java.lang.System.arraycopy(source.internalArray, 0, dest.internalArray, 0, dest.internalArray.length);
				dest.numElements = source.numElements;
				dest.startIndex = source.startIndex;
			}
		}
	}

	public synchronized org.apache.commons.math.util.ResizableDoubleArray copy() {
		org.apache.commons.math.util.ResizableDoubleArray result = new org.apache.commons.math.util.ResizableDoubleArray();
		org.apache.commons.math.util.ResizableDoubleArray.copy(this, result);
		return result;
	}

	@java.lang.Override
	public boolean equals(java.lang.Object object) {
		if (object == (this)) {
			return true;
		} 
		if ((object instanceof org.apache.commons.math.util.ResizableDoubleArray) == false) {
			return false;
		} 
		synchronized(this) {
			synchronized(object) {
				boolean result = true;
				org.apache.commons.math.util.ResizableDoubleArray other = ((org.apache.commons.math.util.ResizableDoubleArray)(object));
				result = result && ((other.initialCapacity) == (initialCapacity));
				result = result && ((other.contractionCriteria) == (contractionCriteria));
				result = result && ((other.expansionFactor) == (expansionFactor));
				result = result && ((other.expansionMode) == (expansionMode));
				result = result && ((other.numElements) == (numElements));
				result = result && ((other.startIndex) == (startIndex));
				if (!result) {
					return false;
				} else {
					return java.util.Arrays.equals(internalArray, other.internalArray);
				}
			}
		}
	}

	@java.lang.Override
	public synchronized int hashCode() {
		int[] hashData = new int[7];
		hashData[0] = new java.lang.Float(expansionFactor).hashCode();
		hashData[1] = new java.lang.Float(contractionCriteria).hashCode();
		hashData[2] = expansionMode;
		hashData[3] = java.util.Arrays.hashCode(internalArray);
		hashData[4] = initialCapacity;
		hashData[5] = numElements;
		hashData[6] = startIndex;
		return java.util.Arrays.hashCode(hashData);
	}
}

