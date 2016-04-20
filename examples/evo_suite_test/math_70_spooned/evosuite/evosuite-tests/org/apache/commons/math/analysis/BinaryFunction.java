package org.apache.commons.math.analysis;


public abstract class BinaryFunction implements org.apache.commons.math.analysis.BivariateRealFunction {
	public static final org.apache.commons.math.analysis.BinaryFunction ADD = new org.apache.commons.math.analysis.BinaryFunction() {
		@java.lang.Override
		public double value(double x, double y) {
			return x + y;
		}
	};

	public static final org.apache.commons.math.analysis.BinaryFunction SUBTRACT = new org.apache.commons.math.analysis.BinaryFunction() {
		@java.lang.Override
		public double value(double x, double y) {
			return x - y;
		}
	};

	public static final org.apache.commons.math.analysis.BinaryFunction MULTIPLY = new org.apache.commons.math.analysis.BinaryFunction() {
		@java.lang.Override
		public double value(double x, double y) {
			return x * y;
		}
	};

	public static final org.apache.commons.math.analysis.BinaryFunction DIVIDE = new org.apache.commons.math.analysis.BinaryFunction() {
		@java.lang.Override
		public double value(double x, double y) {
			return x / y;
		}
	};

	public static final org.apache.commons.math.analysis.BinaryFunction POW = new org.apache.commons.math.analysis.BinaryFunction() {
		@java.lang.Override
		public double value(double x, double y) {
			return java.lang.Math.pow(x, y);
		}
	};

	public static final org.apache.commons.math.analysis.BinaryFunction ATAN2 = new org.apache.commons.math.analysis.BinaryFunction() {
		@java.lang.Override
		public double value(double x, double y) {
			return java.lang.Math.atan2(x, y);
		}
	};

	public abstract double value(double x, double y) throws org.apache.commons.math.FunctionEvaluationException;

	public org.apache.commons.math.analysis.ComposableFunction fix1stArgument(final double fixedX) {
		return new org.apache.commons.math.analysis.ComposableFunction() {
			@java.lang.Override
			public double value(double x) throws org.apache.commons.math.FunctionEvaluationException {
				return org.apache.commons.math.analysis.BinaryFunction.this.value(fixedX, x);
			}
		};
	}

	public org.apache.commons.math.analysis.ComposableFunction fix2ndArgument(final double fixedY) {
		return new org.apache.commons.math.analysis.ComposableFunction() {
			@java.lang.Override
			public double value(double x) throws org.apache.commons.math.FunctionEvaluationException {
				return org.apache.commons.math.analysis.BinaryFunction.this.value(x, fixedY);
			}
		};
	}
}

