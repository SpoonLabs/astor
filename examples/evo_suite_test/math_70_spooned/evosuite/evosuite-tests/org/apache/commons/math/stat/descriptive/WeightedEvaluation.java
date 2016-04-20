package org.apache.commons.math.stat.descriptive;


public interface WeightedEvaluation {
	double evaluate(double[] values, double[] weights);

	double evaluate(double[] values, double[] weights, int begin, int length);
}

