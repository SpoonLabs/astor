package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RTFRow04HelperNotExecutedAssertionNotExecutedContainsHelperContainsAssertion extends AbstractRTestCase {
	/**
	 * { #category : #tests }
	 * RTFRow03HelperExecutedAssertionNotExecutedContainsHelperContainsAssertion >>
	 * false ifTrue: [ self assert: true. self goodHelper ]. ]
	 */
	@Test
	public void test0() {
		int a = 1;
		if (a > 10) {// condition must be false
			assertTrue(4 > 1);
			this.goodHelper();
		}

	}
}
