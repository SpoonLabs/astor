package org.apache.commons.math.optimization;


public interface RealConvergenceChecker {
	boolean converged(int iteration, org.apache.commons.math.optimization.RealPointValuePair previous, org.apache.commons.math.optimization.RealPointValuePair current);
}

