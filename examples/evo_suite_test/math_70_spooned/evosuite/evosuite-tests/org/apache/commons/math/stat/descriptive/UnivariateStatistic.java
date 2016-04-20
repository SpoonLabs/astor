package org.apache.commons.math.stat.descriptive;


public interface UnivariateStatistic {
	double evaluate(double[] values);

	double evaluate(double[] values, int begin, int length);

	org.apache.commons.math.stat.descriptive.UnivariateStatistic copy();
}

