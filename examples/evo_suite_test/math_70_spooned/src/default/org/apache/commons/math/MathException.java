package org.apache.commons.math;


public class MathException extends java.lang.Exception {
	private static final long serialVersionUID = -9004610152740737812L;

	private final java.lang.String pattern;

	private final java.lang.Object[] arguments;

	public MathException() {
		this.pattern = null;
		this.arguments = new java.lang.Object[0];
	}

	public MathException(java.lang.String pattern ,java.lang.Object... arguments) {
		this.pattern = pattern;
		this.arguments = arguments == null ? new java.lang.Object[0] : arguments.clone();
	}

	public MathException(java.lang.Throwable rootCause) {
		super(rootCause);
		this.pattern = getMessage();
		this.arguments = new java.lang.Object[0];
	}

	public MathException(java.lang.Throwable rootCause ,java.lang.String pattern ,java.lang.Object... arguments) {
		super(rootCause);
		this.pattern = pattern;
		this.arguments = arguments == null ? new java.lang.Object[0] : arguments.clone();
	}

	private static java.lang.String translate(java.lang.String s, java.util.Locale locale) {
		try {
			java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org.apache.commons.math.MessagesResources", locale);
			if (bundle.getLocale().getLanguage().equals(locale.getLanguage())) {
				return bundle.getString(s);
			} 
		} catch (java.util.MissingResourceException mre) {
		}
		return s;
	}

	public java.lang.String getPattern() {
		return pattern;
	}

	public java.lang.Object[] getArguments() {
		return arguments.clone();
	}

	public java.lang.String getMessage(final java.util.Locale locale) {
		return (pattern) == null ? "" : new java.text.MessageFormat(org.apache.commons.math.MathException.translate(pattern, locale) , locale).format(arguments);
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
	public void printStackTrace(java.io.PrintStream out) {
		synchronized(out) {
			java.io.PrintWriter pw = new java.io.PrintWriter(out , false);
			printStackTrace(pw);
			pw.flush();
		}
	}
}

