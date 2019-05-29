package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RTFRow02HelperNotExecutedAssertionExecutedContainsHelperContainsAssertion extends AbstractRTestCase {

	/**
	 * { #category : #tests }
	 * RTFRow02HelperNotExecutedAssertionExecutedContainsHelperContainsAssertion >>
	 * test [ self assert: true.
	 * 
	 * false ifTrue: [ self goodHelper ] ]
	 */
	@Test
	public void test0() {
		
		assertTrue(true);
		if (false) {
			this.goodHelper();
		}
	}

}
