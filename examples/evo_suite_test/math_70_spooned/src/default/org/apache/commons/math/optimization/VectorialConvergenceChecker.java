package org.apache.commons.math.optimization;


public interface VectorialConvergenceChecker {
	boolean converged(int iteration, org.apache.commons.math.optimization.VectorialPointValuePair previous, org.apache.commons.math.optimization.VectorialPointValuePair current);
}

