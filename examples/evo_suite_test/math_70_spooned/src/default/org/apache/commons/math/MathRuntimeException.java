package org.apache.commons.math;


public class MathRuntimeException extends java.lang.RuntimeException {
	private static final long serialVersionUID = -5128983364075381060L;

	private final java.lang.String pattern;

	private final java.lang.Object[] arguments;

	public MathRuntimeException(final java.lang.String pattern ,final java.lang.Object... arguments) {
		this.pattern = pattern;
		this.arguments = arguments == null ? new java.lang.Object[0] : arguments.clone();
	}

	public MathRuntimeException(final java.lang.Throwable rootCause) {
		super(rootCause);
		this.pattern = getMessage();
		this.arguments = new java.lang.Object[0];
	}

	public MathRuntimeException(final java.lang.Throwable rootCause ,final java.lang.String pattern ,final java.lang.Object... arguments) {
		super(rootCause);
		this.pattern = pattern;
		this.arguments = arguments == null ? new java.lang.Object[0] : arguments.clone();
	}

	private static java.lang.String translate(final java.lang.String s, final java.util.Locale locale) {
		try {
			java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org.apache.commons.math.MessagesResources", locale);
			if (bundle.getLocale().getLanguage().equals(locale.getLanguage())) {
				return bundle.getString(s);
			} 
		} catch (java.util.MissingResourceException mre) {
		}
		return s;
	}

	private static java.lang.String buildMessage(final java.util.Locale locale, final java.lang.String pattern, final java.lang.Object... arguments) {
		return pattern == null ? "" : new java.text.MessageFormat(org.apache.commons.math.MathRuntimeException.translate(pattern, locale) , locale).format(arguments);
	}

	public java.lang.String getPattern() {
		return pattern;
	}

	public java.lang.Object[] getArguments() {
		return arguments.clone();
	}

	public java.lang.String getMessage(final java.util.Locale locale) {
		return org.apache.commons.math.MathRuntimeException.buildMessage(locale, pattern, arguments);
	}

	@java.lang.Override
	public java.lang.String getMessage() {
		return getMessage(java.util.Locale.US);
	}

	@java.lang.Override
	public java.lang.String getLocalizedMessage() {
		return getMessage(java.util.Locale.getDefault());
	}

	@java.lang.Override
	public void printStackTrace() {
		printStackTrace(java.lang.System.err);
	}

	@java.lang.Override
	public void printStackTrace(final java.io.PrintStream out) {
		synchronized(out) {
			java.io.PrintWriter pw = new java.io.PrintWriter(out , false);
			printStackTrace(pw);
			pw.flush();
		}
	}

	public static java.lang.ArithmeticException createArithmeticException(final java.lang.String pattern, final java.lang.Object... arguments) {
		return new java.lang.ArithmeticException() {
			private static final long serialVersionUID = 7705628723242533939L;

			@java.lang.Override
			public java.lang.String getMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.US, pattern, arguments);
			}

			@java.lang.Override
			public java.lang.String getLocalizedMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.getDefault(), pattern, arguments);
			}
		};
	}

	public static java.lang.ArrayIndexOutOfBoundsException createArrayIndexOutOfBoundsException(final java.lang.String pattern, final java.lang.Object... arguments) {
		return new java.lang.ArrayIndexOutOfBoundsException() {
			private static final long serialVersionUID = -3394748305449283486L;

			@java.lang.Override
			public java.lang.String getMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.US, pattern, arguments);
			}

			@java.lang.Override
			public java.lang.String getLocalizedMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.getDefault(), pattern, arguments);
			}
		};
	}

	public static java.io.EOFException createEOFException(final java.lang.String pattern, final java.lang.Object... arguments) {
		return new java.io.EOFException() {
			private static final long serialVersionUID = 279461544586092584L;

			@java.lang.Override
			public java.lang.String getMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.US, pattern, arguments);
			}

			@java.lang.Override
			public java.lang.String getLocalizedMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.getDefault(), pattern, arguments);
			}
		};
	}

	public static java.io.IOException createIOException(final java.lang.Throwable rootCause) {
		java.io.IOException ioe = new java.io.IOException(rootCause.getLocalizedMessage());
		ioe.initCause(rootCause);
		return ioe;
	}

	public static java.lang.IllegalArgumentException createIllegalArgumentException(final java.lang.String pattern, final java.lang.Object... arguments) {
		return new java.lang.IllegalArgumentException() {
			private static final long serialVersionUID = -6555453980658317913L;

			@java.lang.Override
			public java.lang.String getMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.US, pattern, arguments);
			}

			@java.lang.Override
			public java.lang.String getLocalizedMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.getDefault(), pattern, arguments);
			}
		};
	}

	public static java.lang.IllegalArgumentException createIllegalArgumentException(final java.lang.Throwable rootCause) {
		java.lang.IllegalArgumentException iae = new java.lang.IllegalArgumentException(rootCause.getLocalizedMessage());
		iae.initCause(rootCause);
		return iae;
	}

	public static java.lang.IllegalStateException createIllegalStateException(final java.lang.String pattern, final java.lang.Object... arguments) {
		return new java.lang.IllegalStateException() {
			private static final long serialVersionUID = -95247648156277208L;

			@java.lang.Override
			public java.lang.String getMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.US, pattern, arguments);
			}

			@java.lang.Override
			public java.lang.String getLocalizedMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.getDefault(), pattern, arguments);
			}
		};
	}

	public static java.util.ConcurrentModificationException createConcurrentModificationException(final java.lang.String pattern, final java.lang.Object... arguments) {
		return new java.util.ConcurrentModificationException() {
			private static final long serialVersionUID = 6134247282754009421L;

			@java.lang.Override
			public java.lang.String getMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.US, pattern, arguments);
			}

			@java.lang.Override
			public java.lang.String getLocalizedMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.getDefault(), pattern, arguments);
			}
		};
	}

	public static java.util.NoSuchElementException createNoSuchElementException(final java.lang.String pattern, final java.lang.Object... arguments) {
		return new java.util.NoSuchElementException() {
			private static final long serialVersionUID = 7304273322489425799L;

			@java.lang.Override
			public java.lang.String getMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.US, pattern, arguments);
			}

			@java.lang.Override
			public java.lang.String getLocalizedMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.getDefault(), pattern, arguments);
			}
		};
	}

	public static java.lang.NullPointerException createNullPointerException(final java.lang.String pattern, final java.lang.Object... arguments) {
		return new java.lang.NullPointerException() {
			private static final long serialVersionUID = -3075660477939965216L;

			@java.lang.Override
			public java.lang.String getMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.US, pattern, arguments);
			}

			@java.lang.Override
			public java.lang.String getLocalizedMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.getDefault(), pattern, arguments);
			}
		};
	}

	public static java.text.ParseException createParseException(final int offset, final java.lang.String pattern, final java.lang.Object... arguments) {
		return new java.text.ParseException(null, offset) {
			private static final long serialVersionUID = -1103502177342465975L;

			@java.lang.Override
			public java.lang.String getMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.US, pattern, arguments);
			}

			@java.lang.Override
			public java.lang.String getLocalizedMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.getDefault(), pattern, arguments);
			}
		};
	}

	public static java.lang.RuntimeException createInternalError(final java.lang.Throwable cause) {
		final java.lang.String pattern = "internal error, please fill a bug report at {0}";
		final java.lang.String argument = "https://issues.apache.org/jira/browse/MATH";
		return new java.lang.RuntimeException() {
			private static final long serialVersionUID = -201865440834027016L;

			@java.lang.Override
			public java.lang.String getMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.US, pattern, argument);
			}

			@java.lang.Override
			public java.lang.String getLocalizedMessage() {
				return org.apache.commons.math.MathRuntimeException.buildMessage(java.util.Locale.getDefault(), pattern, argument);
			}
		};
	}
}

