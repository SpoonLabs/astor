package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RTFRow03HelperExecutedAssertionNotExecutedContainsHelperContainsAssertion extends AbstractRtTestCase {

	/**
	 * { #category : #tests }
	 * RTFRow03HelperExecutedAssertionNotExecutedContainsHelperContainsAssertion >>
	 * test [ false ifTrue: [ self assert: true ].
	 * 
	 * self rottenHelper ]
	 *
	 */
	@Test
	public void test0() {
		if (false) {
			assertTrue(3 > 1);
		}
		this.rottenHelper();
	}

}
