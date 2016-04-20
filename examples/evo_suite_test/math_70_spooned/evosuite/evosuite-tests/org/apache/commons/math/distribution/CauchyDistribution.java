package org.apache.commons.math.distribution;


public interface CauchyDistribution extends org.apache.commons.math.distribution.ContinuousDistribution {
	double getMedian();

	double getScale();

	@java.lang.Deprecated
	void setMedian(double median);

	@java.lang.Deprecated
	void setScale(double s);
}

