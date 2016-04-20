package org.apache.commons.math.stat.descriptive;


public class ListUnivariateImpl extends org.apache.commons.math.stat.descriptive.DescriptiveStatistics implements java.io.Serializable {
	private static final long serialVersionUID = -8837442489133392138L;

	protected java.util.List<java.lang.Object> list;

	protected org.apache.commons.math.util.NumberTransformer transformer;

	public ListUnivariateImpl() {
		this(new java.util.ArrayList<java.lang.Object>());
	}

	public ListUnivariateImpl(java.util.List<java.lang.Object> list) {
		this(list, new org.apache.commons.math.util.DefaultTransformer());
	}

	public ListUnivariateImpl(java.util.List<java.lang.Object> list ,org.apache.commons.math.util.NumberTransformer transformer) {
		super();
		this.list = list;
		this.transformer = transformer;
	}

	@java.lang.Override
	public double[] getValues() {
		int length = list.size();
		if (((windowSize) != (org.apache.commons.math.stat.descriptive.DescriptiveStatistics.INFINITE_WINDOW)) && ((windowSize) < (list.size()))) {
			length = (list.size()) - (java.lang.Math.max(0, ((list.size()) - (windowSize))));
		} 
		double[] copiedArray = new double[length];
		for (int i = 0 ; i < (copiedArray.length) ; i++) {
			copiedArray[i] = getElement(i);
		}
		return copiedArray;
	}

	@java.lang.Override
	public double getElement(int index) {
		double value = java.lang.Double.NaN;
		int calcIndex = index;
		if (((windowSize) != (org.apache.commons.math.stat.descriptive.DescriptiveStatistics.INFINITE_WINDOW)) && ((windowSize) < (list.size()))) {
			calcIndex = ((list.size()) - (windowSize)) + index;
		} 
		try {
			value = transformer.transform(list.get(calcIndex));
		} catch (org.apache.commons.math.MathException e) {
			e.printStackTrace();
		}
		return value;
	}

	@java.lang.Override
	public long getN() {
		int n = 0;
		if ((windowSize) != (org.apache.commons.math.stat.descriptive.DescriptiveStatistics.INFINITE_WINDOW)) {
			if ((list.size()) > (windowSize)) {
				n = windowSize;
			} else {
				n = list.size();
			}
		} else {
			n = list.size();
		}
		return n;
	}

	@java.lang.Override
	public void addValue(double v) {
		list.add(java.lang.Double.valueOf(v));
	}

	public void addObject(java.lang.Object o) {
		list.add(o);
	}

	@java.lang.Override
	public void clear() {
		list.clear();
	}

	@java.lang.Override
	public double apply(org.apache.commons.math.stat.descriptive.UnivariateStatistic stat) {
		double[] v = getValues();
		if (v != null) {
			return stat.evaluate(v, 0, v.length);
		} 
		return java.lang.Double.NaN;
	}

	public org.apache.commons.math.util.NumberTransformer getTransformer() {
		return transformer;
	}

	public void setTransformer(org.apache.commons.math.util.NumberTransformer transformer) {
		this.transformer = transformer;
	}

	@java.lang.Override
	public synchronized void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
		int extra = (list.size()) - windowSize;
		for (int i = 0 ; i < extra ; i++) {
			list.remove(0);
		}
	}

	@java.lang.Override
	public synchronized int getWindowSize() {
		return windowSize;
	}
}

