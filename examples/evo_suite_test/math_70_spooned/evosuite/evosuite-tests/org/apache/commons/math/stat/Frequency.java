package org.apache.commons.math.stat;


public class Frequency implements java.io.Serializable {
	private static final long serialVersionUID = -3845586908418844111L;

	private final java.util.TreeMap<java.lang.Comparable<?>, java.lang.Long> freqTable;

	public Frequency() {
		freqTable = new java.util.TreeMap<java.lang.Comparable<?>, java.lang.Long>();
	}

	@java.lang.SuppressWarnings(value = "unchecked")
	public Frequency(java.util.Comparator<?> comparator) {
		freqTable = new java.util.TreeMap<java.lang.Comparable<?>, java.lang.Long>(((java.util.Comparator<? super java.lang.Comparable<?>>)(comparator)));
	}

	@java.lang.Override
	public java.lang.String toString() {
		java.text.NumberFormat nf = java.text.NumberFormat.getPercentInstance();
		java.lang.StringBuffer outBuffer = new java.lang.StringBuffer();
		outBuffer.append("Value \t Freq. \t Pct. \t Cum Pct. \n");
		java.util.Iterator<java.lang.Comparable<?>> iter = freqTable.keySet().iterator();
		while (iter.hasNext()) {
			java.lang.Comparable<?> value = iter.next();
			outBuffer.append(value);
			outBuffer.append('\t');
			outBuffer.append(getCount(value));
			outBuffer.append('\t');
			outBuffer.append(nf.format(getPct(value)));
			outBuffer.append('\t');
			outBuffer.append(nf.format(getCumPct(value)));
			outBuffer.append('\n');
		}
		return outBuffer.toString();
	}

	@java.lang.Deprecated
	public void addValue(java.lang.Object v) {
		if (v instanceof java.lang.Comparable<?>) {
			addValue(((java.lang.Comparable<?>)(v)));
		} else {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("class ({0}) does not implement Comparable", v.getClass().getName());
		}
	}

	public void addValue(java.lang.Comparable<?> v) {
		java.lang.Comparable<?> obj = v;
		if (v instanceof java.lang.Integer) {
			obj = java.lang.Long.valueOf(((java.lang.Integer)(v)).longValue());
		} 
		try {
			java.lang.Long count = freqTable.get(obj);
			if (count == null) {
				freqTable.put(obj, java.lang.Long.valueOf(1));
			} else {
				freqTable.put(obj, java.lang.Long.valueOf(((count.longValue()) + 1)));
			}
		} catch (java.lang.ClassCastException ex) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("instance of class {0} not comparable to existing values", v.getClass().getName());
		}
	}

	public void addValue(int v) {
		addValue(java.lang.Long.valueOf(v));
	}

	@java.lang.Deprecated
	public void addValue(java.lang.Integer v) {
		addValue(java.lang.Long.valueOf(v.longValue()));
	}

	public void addValue(long v) {
		addValue(java.lang.Long.valueOf(v));
	}

	public void addValue(char v) {
		addValue(java.lang.Character.valueOf(v));
	}

	public void clear() {
		freqTable.clear();
	}

	public java.util.Iterator<java.lang.Comparable<?>> valuesIterator() {
		return freqTable.keySet().iterator();
	}

	public long getSumFreq() {
		long result = 0;
		java.util.Iterator<java.lang.Long> iterator = freqTable.values().iterator();
		while (iterator.hasNext()) {
			result += iterator.next().longValue();
		}
		return result;
	}

	@java.lang.Deprecated
	public long getCount(java.lang.Object v) {
		return getCount(((java.lang.Comparable<?>)(v)));
	}

	public long getCount(java.lang.Comparable<?> v) {
		if (v instanceof java.lang.Integer) {
			return getCount(((java.lang.Integer)(v)).longValue());
		} 
		long result = 0;
		try {
			java.lang.Long count = freqTable.get(v);
			if (count != null) {
				result = count.longValue();
			} 
		} catch (java.lang.ClassCastException ex) {
		}
		return result;
	}

	public long getCount(int v) {
		return getCount(java.lang.Long.valueOf(v));
	}

	public long getCount(long v) {
		return getCount(java.lang.Long.valueOf(v));
	}

	public long getCount(char v) {
		return getCount(java.lang.Character.valueOf(v));
	}

	@java.lang.Deprecated
	public double getPct(java.lang.Object v) {
		return getPct(((java.lang.Comparable<?>)(v)));
	}

	public double getPct(java.lang.Comparable<?> v) {
		final long sumFreq = getSumFreq();
		if (sumFreq == 0) {
			return java.lang.Double.NaN;
		} 
		return ((double)(getCount(v))) / ((double)(sumFreq));
	}

	public double getPct(int v) {
		return getPct(java.lang.Long.valueOf(v));
	}

	public double getPct(long v) {
		return getPct(java.lang.Long.valueOf(v));
	}

	public double getPct(char v) {
		return getPct(java.lang.Character.valueOf(v));
	}

	@java.lang.Deprecated
	public long getCumFreq(java.lang.Object v) {
		return getCumFreq(((java.lang.Comparable<?>)(v)));
	}

	public long getCumFreq(java.lang.Comparable<?> v) {
		if ((getSumFreq()) == 0) {
			return 0;
		} 
		if (v instanceof java.lang.Integer) {
			return getCumFreq(((java.lang.Integer)(v)).longValue());
		} 
		@java.lang.SuppressWarnings(value = "unchecked")
		java.util.Comparator<java.lang.Comparable<?>> c = ((java.util.Comparator<java.lang.Comparable<?>>)(freqTable.comparator()));
		if (c == null) {
			c = new org.apache.commons.math.stat.Frequency.NaturalComparator();
		} 
		long result = 0;
		try {
			java.lang.Long value = freqTable.get(v);
			if (value != null) {
				result = value.longValue();
			} 
		} catch (java.lang.ClassCastException ex) {
			return result;
		}
		if ((c.compare(v, freqTable.firstKey())) < 0) {
			return 0;
		} 
		if ((c.compare(v, freqTable.lastKey())) >= 0) {
			return getSumFreq();
		} 
		java.util.Iterator<java.lang.Comparable<?>> values = valuesIterator();
		while (values.hasNext()) {
			java.lang.Comparable<?> nextValue = values.next();
			if ((c.compare(v, nextValue)) > 0) {
				result += getCount(nextValue);
			} else {
				return result;
			}
		}
		return result;
	}

	public long getCumFreq(int v) {
		return getCumFreq(java.lang.Long.valueOf(v));
	}

	public long getCumFreq(long v) {
		return getCumFreq(java.lang.Long.valueOf(v));
	}

	public long getCumFreq(char v) {
		return getCumFreq(java.lang.Character.valueOf(v));
	}

	@java.lang.Deprecated
	public double getCumPct(java.lang.Object v) {
		return getCumPct(((java.lang.Comparable<?>)(v)));
	}

	public double getCumPct(java.lang.Comparable<?> v) {
		final long sumFreq = getSumFreq();
		if (sumFreq == 0) {
			return java.lang.Double.NaN;
		} 
		return ((double)(getCumFreq(v))) / ((double)(sumFreq));
	}

	public double getCumPct(int v) {
		return getCumPct(java.lang.Long.valueOf(v));
	}

	public double getCumPct(long v) {
		return getCumPct(java.lang.Long.valueOf(v));
	}

	public double getCumPct(char v) {
		return getCumPct(java.lang.Character.valueOf(v));
	}

	private static class NaturalComparator<T extends java.lang.Comparable<T>> implements java.io.Serializable , java.util.Comparator<java.lang.Comparable<T>> {
		private static final long serialVersionUID = -3852193713161395148L;

		@java.lang.SuppressWarnings(value = "unchecked")
		public int compare(java.lang.Comparable<T> o1, java.lang.Comparable<T> o2) {
			return o1.compareTo(((T)(o2)));
		}
	}

	@java.lang.Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((freqTable) == null ? 0 : freqTable.hashCode());
		return result;
	}

	@java.lang.Override
	public boolean equals(java.lang.Object obj) {
		if ((this) == obj) {
			return true;
		} 
		if (!(obj instanceof org.apache.commons.math.stat.Frequency)) {
			return false;
		} 
		org.apache.commons.math.stat.Frequency other = ((org.apache.commons.math.stat.Frequency)(obj));
		if ((freqTable) == null) {
			if ((other.freqTable) != null) {
				return false;
			} 
		} else {
			if (!(freqTable.equals(other.freqTable))) {
				return false;
			} 
		}
		return true;
	}
}

