package org.apache.commons.math;


public class ArgumentOutsideDomainException extends org.apache.commons.math.FunctionEvaluationException {
	private static final long serialVersionUID = -4965972841162580234L;

	public ArgumentOutsideDomainException(double argument ,double lower ,double upper) {
		super(argument, "Argument {0} outside domain [{1} ; {2}]", argument, lower, upper);
	}
}

