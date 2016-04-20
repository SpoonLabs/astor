package org.apache.commons.math.distribution;


public interface ZipfDistribution extends org.apache.commons.math.distribution.IntegerDistribution {
	int getNumberOfElements();

	@java.lang.Deprecated
	void setNumberOfElements(int n);

	double getExponent();

	@java.lang.Deprecated
	void setExponent(double s);
}

