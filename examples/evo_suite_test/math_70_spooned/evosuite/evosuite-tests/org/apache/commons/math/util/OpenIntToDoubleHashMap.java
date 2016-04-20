package org.apache.commons.math.util;


public class OpenIntToDoubleHashMap implements java.io.Serializable {
	protected static final byte FREE = 0;

	protected static final byte FULL = 1;

	protected static final byte REMOVED = 2;

	private static final long serialVersionUID = -3646337053166149105L;

	private static final java.lang.String CONCURRENT_MODIFICATION_MESSAGE = "map has been modified while iterating";

	private static final java.lang.String EXHAUSTED_ITERATOR_MESSAGE = "iterator exhausted";

	private static final float LOAD_FACTOR = 0.5F;

	private static final int DEFAULT_EXPECTED_SIZE = 16;

	private static final int RESIZE_MULTIPLIER = 2;

	private static final int PERTURB_SHIFT = 5;

	private int[] keys;

	private double[] values;

	private byte[] states;

	private final double missingEntries;

	private int size;

	private int mask;

	private transient int count;

	public OpenIntToDoubleHashMap() {
		this(DEFAULT_EXPECTED_SIZE, java.lang.Double.NaN);
	}

	public OpenIntToDoubleHashMap(final double missingEntries) {
		this(DEFAULT_EXPECTED_SIZE, missingEntries);
	}

	public OpenIntToDoubleHashMap(final int expectedSize) {
		this(expectedSize, java.lang.Double.NaN);
	}

	public OpenIntToDoubleHashMap(final int expectedSize ,final double missingEntries) {
		final int capacity = org.apache.commons.math.util.OpenIntToDoubleHashMap.computeCapacity(expectedSize);
		keys = new int[capacity];
		values = new double[capacity];
		states = new byte[capacity];
		this.missingEntries = missingEntries;
		mask = capacity - 1;
	}

	public OpenIntToDoubleHashMap(final org.apache.commons.math.util.OpenIntToDoubleHashMap source) {
		final int length = source.keys.length;
		keys = new int[length];
		java.lang.System.arraycopy(source.keys, 0, keys, 0, length);
		values = new double[length];
		java.lang.System.arraycopy(source.values, 0, values, 0, length);
		states = new byte[length];
		java.lang.System.arraycopy(source.states, 0, states, 0, length);
		missingEntries = source.missingEntries;
		size = source.size;
		mask = source.mask;
		count = source.count;
	}

	private static int computeCapacity(final int expectedSize) {
		if (expectedSize == 0) {
			return 1;
		} 
		final int capacity = ((int)(java.lang.Math.ceil((expectedSize / (LOAD_FACTOR)))));
		final int powerOfTwo = java.lang.Integer.highestOneBit(capacity);
		if (powerOfTwo == capacity) {
			return capacity;
		} 
		return org.apache.commons.math.util.OpenIntToDoubleHashMap.nextPowerOfTwo(capacity);
	}

	private static int nextPowerOfTwo(final int i) {
		return (java.lang.Integer.highestOneBit(i)) << 1;
	}

	public double get(final int key) {
		final int hash = org.apache.commons.math.util.OpenIntToDoubleHashMap.hashOf(key);
		int index = hash & (mask);
		if (containsKey(key, index)) {
			return values[index];
		} 
		if ((states[index]) == (FREE)) {
			return missingEntries;
		} 
		int j = index;
		for (int perturb = org.apache.commons.math.util.OpenIntToDoubleHashMap.perturb(hash) ; (states[index]) != (FREE) ; perturb >>= PERTURB_SHIFT) {
			j = org.apache.commons.math.util.OpenIntToDoubleHashMap.probe(perturb, j);
			index = j & (mask);
			if (containsKey(key, index)) {
				return values[index];
			} 
		}
		return missingEntries;
	}

	public boolean containsKey(final int key) {
		final int hash = org.apache.commons.math.util.OpenIntToDoubleHashMap.hashOf(key);
		int index = hash & (mask);
		if (containsKey(key, index)) {
			return true;
		} 
		if ((states[index]) == (FREE)) {
			return false;
		} 
		int j = index;
		for (int perturb = org.apache.commons.math.util.OpenIntToDoubleHashMap.perturb(hash) ; (states[index]) != (FREE) ; perturb >>= PERTURB_SHIFT) {
			j = org.apache.commons.math.util.OpenIntToDoubleHashMap.probe(perturb, j);
			index = j & (mask);
			if (containsKey(key, index)) {
				return true;
			} 
		}
		return false;
	}

	public Iterator iterator() {
		return new Iterator();
	}

	private static int perturb(final int hash) {
		return hash & 2147483647;
	}

	private int findInsertionIndex(final int key) {
		return org.apache.commons.math.util.OpenIntToDoubleHashMap.findInsertionIndex(keys, states, key, mask);
	}

	private static int findInsertionIndex(final int[] keys, final byte[] states, final int key, final int mask) {
		final int hash = org.apache.commons.math.util.OpenIntToDoubleHashMap.hashOf(key);
		int index = hash & mask;
		if ((states[index]) == (FREE)) {
			return index;
		} else {
			if (((states[index]) == (FULL)) && ((keys[index]) == key)) {
				return org.apache.commons.math.util.OpenIntToDoubleHashMap.changeIndexSign(index);
			} 
		}
		int perturb = org.apache.commons.math.util.OpenIntToDoubleHashMap.perturb(hash);
		int j = index;
		if ((states[index]) == (FULL)) {
			while (true) {
				j = org.apache.commons.math.util.OpenIntToDoubleHashMap.probe(perturb, j);
				index = j & mask;
				perturb >>= PERTURB_SHIFT;
				if (((states[index]) != (FULL)) || ((keys[index]) == key)) {
					break;
				} 
			}
		} 
		if ((states[index]) == (FREE)) {
			return index;
		} else {
			if ((states[index]) == (FULL)) {
				return org.apache.commons.math.util.OpenIntToDoubleHashMap.changeIndexSign(index);
			} 
		}
		final int firstRemoved = index;
		while (true) {
			j = org.apache.commons.math.util.OpenIntToDoubleHashMap.probe(perturb, j);
			index = j & mask;
			if ((states[index]) == (FREE)) {
				return firstRemoved;
			} else {
				if (((states[index]) == (FULL)) && ((keys[index]) == key)) {
					return org.apache.commons.math.util.OpenIntToDoubleHashMap.changeIndexSign(index);
				} 
			}
			perturb >>= PERTURB_SHIFT;
		}
	}

	private static int probe(final int perturb, final int j) {
		return (((j << 2) + j) + perturb) + 1;
	}

	private static int changeIndexSign(final int index) {
		return (-index) - 1;
	}

	public int size() {
		return size;
	}

	public double remove(final int key) {
		final int hash = org.apache.commons.math.util.OpenIntToDoubleHashMap.hashOf(key);
		int index = hash & (mask);
		if (containsKey(key, index)) {
			return doRemove(index);
		} 
		if ((states[index]) == (FREE)) {
			return missingEntries;
		} 
		int j = index;
		for (int perturb = org.apache.commons.math.util.OpenIntToDoubleHashMap.perturb(hash) ; (states[index]) != (FREE) ; perturb >>= PERTURB_SHIFT) {
			j = org.apache.commons.math.util.OpenIntToDoubleHashMap.probe(perturb, j);
			index = j & (mask);
			if (containsKey(key, index)) {
				return doRemove(index);
			} 
		}
		return missingEntries;
	}

	private boolean containsKey(final int key, final int index) {
		return ((key != 0) || ((states[index]) == (FULL))) && ((keys[index]) == key);
	}

	private double doRemove(int index) {
		keys[index] = 0;
		states[index] = REMOVED;
		final double previous = values[index];
		values[index] = missingEntries;
		--(size);
		++(count);
		return previous;
	}

	public double put(final int key, final double value) {
		int index = findInsertionIndex(key);
		double previous = missingEntries;
		boolean newMapping = true;
		if (index < 0) {
			index = org.apache.commons.math.util.OpenIntToDoubleHashMap.changeIndexSign(index);
			previous = values[index];
			newMapping = false;
		} 
		keys[index] = key;
		states[index] = FULL;
		values[index] = value;
		if (newMapping) {
			++(size);
			if (shouldGrowTable()) {
				growTable();
			} 
			++(count);
		} 
		return previous;
	}

	private void growTable() {
		final int oldLength = states.length;
		final int[] oldKeys = keys;
		final double[] oldValues = values;
		final byte[] oldStates = states;
		final int newLength = (RESIZE_MULTIPLIER) * oldLength;
		final int[] newKeys = new int[newLength];
		final double[] newValues = new double[newLength];
		final byte[] newStates = new byte[newLength];
		final int newMask = newLength - 1;
		for (int i = 0 ; i < oldLength ; ++i) {
			if ((oldStates[i]) == (FULL)) {
				final int key = oldKeys[i];
				final int index = org.apache.commons.math.util.OpenIntToDoubleHashMap.findInsertionIndex(newKeys, newStates, key, newMask);
				newKeys[index] = key;
				newValues[index] = oldValues[i];
				newStates[index] = FULL;
			} 
		}
		mask = newMask;
		keys = newKeys;
		values = newValues;
		states = newStates;
	}

	private boolean shouldGrowTable() {
		return (size) > (((mask) + 1) * (LOAD_FACTOR));
	}

	private static int hashOf(final int key) {
		final int h = key ^ ((key >>> 20) ^ (key >>> 12));
		return (h ^ (h >>> 7)) ^ (h >>> 4);
	}

	public class Iterator {
		private final int referenceCount;

		private int current;

		private int next;

		private Iterator() {
			referenceCount = count;
			next = -1;
			try {
				advance();
			} catch (java.util.NoSuchElementException nsee) {
			}
		}

		public boolean hasNext() {
			return (next) >= 0;
		}

		public int key() throws java.util.ConcurrentModificationException, java.util.NoSuchElementException {
			if ((referenceCount) != (count)) {
				throw org.apache.commons.math.MathRuntimeException.createConcurrentModificationException(org.apache.commons.math.util.OpenIntToDoubleHashMap.CONCURRENT_MODIFICATION_MESSAGE);
			} 
			if ((current) < 0) {
				throw org.apache.commons.math.MathRuntimeException.createNoSuchElementException(org.apache.commons.math.util.OpenIntToDoubleHashMap.EXHAUSTED_ITERATOR_MESSAGE);
			} 
			return keys[current];
		}

		public double value() throws java.util.ConcurrentModificationException, java.util.NoSuchElementException {
			if ((referenceCount) != (count)) {
				throw org.apache.commons.math.MathRuntimeException.createConcurrentModificationException(org.apache.commons.math.util.OpenIntToDoubleHashMap.CONCURRENT_MODIFICATION_MESSAGE);
			} 
			if ((current) < 0) {
				throw org.apache.commons.math.MathRuntimeException.createNoSuchElementException(org.apache.commons.math.util.OpenIntToDoubleHashMap.EXHAUSTED_ITERATOR_MESSAGE);
			} 
			return values[current];
		}

		public void advance() throws java.util.ConcurrentModificationException, java.util.NoSuchElementException {
			if ((referenceCount) != (count)) {
				throw org.apache.commons.math.MathRuntimeException.createConcurrentModificationException(org.apache.commons.math.util.OpenIntToDoubleHashMap.CONCURRENT_MODIFICATION_MESSAGE);
			} 
			current = next;
			try {
				while ((states[++(next)]) != (org.apache.commons.math.util.OpenIntToDoubleHashMap.FULL)) {
				}
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				next = -2;
				if ((current) < 0) {
					throw org.apache.commons.math.MathRuntimeException.createNoSuchElementException(org.apache.commons.math.util.OpenIntToDoubleHashMap.EXHAUSTED_ITERATOR_MESSAGE);
				} 
			}
		}
	}

	private void readObject(final java.io.ObjectInputStream stream) throws java.io.IOException, java.lang.ClassNotFoundException {
		stream.defaultReadObject();
		count = 0;
	}
}

