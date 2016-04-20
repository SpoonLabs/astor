package org.apache.commons.math;


public abstract class RetryTestCase extends junit.framework.TestCase {
	public RetryTestCase() {
		super();
	}

	public RetryTestCase(java.lang.String arg0) {
		super(arg0);
	}

	@java.lang.Override
	protected void runTest() throws java.lang.Throwable {
		try {
			super.runTest();
		} catch (junit.framework.AssertionFailedError err) {
			super.runTest();
		}
	}
}

