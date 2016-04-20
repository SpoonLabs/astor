package org.apache.commons.math;


public class FunctionEvaluationException extends org.apache.commons.math.MathException {
	private static final long serialVersionUID = -4305020489115478365L;

	private static final java.lang.String FAILED_EVALUATION_MESSAGE = "evaluation failed for argument = {0}";

	private double[] argument;

	public FunctionEvaluationException(double argument) {
		super(FAILED_EVALUATION_MESSAGE, argument);
		this.argument = new double[]{ argument };
	}

	public FunctionEvaluationException(double[] argument) {
		super(FAILED_EVALUATION_MESSAGE, new org.apache.commons.math.linear.ArrayRealVector(argument));
		this.argument = argument.clone();
	}

	public FunctionEvaluationException(double argument ,java.lang.String pattern ,java.lang.Object... arguments) {
		super(pattern, arguments);
		this.argument = new double[]{ argument };
	}

	public FunctionEvaluationException(double[] argument ,java.lang.String pattern ,java.lang.Object... arguments) {
		super(pattern, arguments);
		this.argument = argument.clone();
	}

	public FunctionEvaluationException(java.lang.Throwable cause ,double argument) {
		super(cause);
		this.argument = new double[]{ argument };
	}

	public FunctionEvaluationException(java.lang.Throwable cause ,double[] argument) {
		super(cause);
		this.argument = argument.clone();
	}

	public FunctionEvaluationException(java.lang.Throwable cause ,double argument ,java.lang.String pattern ,java.lang.Object... arguments) {
		super(cause, pattern, arguments);
		this.argument = new double[]{ argument };
	}

	public FunctionEvaluationException(java.lang.Throwable cause ,double[] argument ,java.lang.String pattern ,java.lang.Object... arguments) {
		super(cause, pattern, arguments);
		this.argument = argument.clone();
	}

	public double[] getArgument() {
		return argument.clone();
	}
}

