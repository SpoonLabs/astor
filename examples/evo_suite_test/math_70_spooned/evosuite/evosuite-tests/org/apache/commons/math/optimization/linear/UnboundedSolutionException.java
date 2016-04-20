package org.apache.commons.math.optimization.linear;


public class UnboundedSolutionException extends org.apache.commons.math.optimization.OptimizationException {
	private static final long serialVersionUID = 940539497277290619L;

	public UnboundedSolutionException() {
		super("unbounded solution");
	}
}

