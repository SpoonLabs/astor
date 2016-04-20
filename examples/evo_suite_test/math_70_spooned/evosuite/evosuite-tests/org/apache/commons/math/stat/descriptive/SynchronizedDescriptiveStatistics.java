package org.apache.commons.math.stat.descriptive;


public class SynchronizedDescriptiveStatistics extends org.apache.commons.math.stat.descriptive.DescriptiveStatistics {
	private static final long serialVersionUID = 1L;

	public SynchronizedDescriptiveStatistics() {
		this(org.apache.commons.math.stat.descriptive.DescriptiveStatistics.INFINITE_WINDOW);
	}

	public SynchronizedDescriptiveStatistics(int window) {
		super(window);
	}

	public SynchronizedDescriptiveStatistics(org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics original) {
		org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics.copy(original, this);
	}

	@java.lang.Override
	public synchronized void addValue(double v) {
		super.addValue(v);
	}

	@java.lang.Override
	public synchronized double apply(org.apache.commons.math.stat.descriptive.UnivariateStatistic stat) {
		return super.apply(stat);
	}

	@java.lang.Override
	public synchronized void clear() {
		super.clear();
	}

	@java.lang.Override
	public synchronized double getElement(int index) {
		return super.getElement(index);
	}

	@java.lang.Override
	public synchronized long getN() {
		return super.getN();
	}

	@java.lang.Override
	public synchronized double getStandardDeviation() {
		return super.getStandardDeviation();
	}

	@java.lang.Override
	public synchronized double[] getValues() {
		return super.getValues();
	}

	@java.lang.Override
	public synchronized int getWindowSize() {
		return super.getWindowSize();
	}

	@java.lang.Override
	public synchronized void setWindowSize(int windowSize) {
		super.setWindowSize(windowSize);
	}

	@java.lang.Override
	public synchronized java.lang.String toString() {
		return super.toString();
	}

	@java.lang.Override
	public synchronized org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics copy() {
		org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics result = new org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics();
		org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics.copy(this, result);
		return result;
	}

	public static void copy(org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics source, org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics dest) {
		synchronized(source) {
			synchronized(dest) {
				org.apache.commons.math.stat.descriptive.DescriptiveStatistics.copy(source, dest);
			}
		}
	}
}

