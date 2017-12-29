package fr.inria.astor.core.faultlocalization.cocospoon.testrunner;


/**
 * Created by bdanglot on 10/3/16.
 */
public class TestResultImpl implements TestResult {

	private TestCase testCase;
	private boolean successful;

	public TestResultImpl(TestCase testCase, boolean successful) {
		this.testCase = testCase;
		this.successful = successful;
	}

	@Override
	public TestCase getTestCase() {
		return this.testCase;
	}

	@Override
	public boolean isSuccessful() {
		return this.successful;
	}
}
