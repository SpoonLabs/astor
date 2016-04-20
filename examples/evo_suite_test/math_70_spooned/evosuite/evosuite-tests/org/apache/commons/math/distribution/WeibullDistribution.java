package org.apache.commons.math.distribution;


public interface WeibullDistribution extends org.apache.commons.math.distribution.ContinuousDistribution {
	double getShape();

	double getScale();

	@java.lang.Deprecated
	void setShape(double alpha);

	@java.lang.Deprecated
	void setScale(double beta);
}

