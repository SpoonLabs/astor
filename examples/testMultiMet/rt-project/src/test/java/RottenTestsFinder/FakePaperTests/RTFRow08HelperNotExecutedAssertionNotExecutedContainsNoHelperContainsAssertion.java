package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RTFRow08HelperNotExecutedAssertionNotExecutedContainsNoHelperContainsAssertion extends AbstractRTestCase {
//false ifTrue: [ self assert: true ]
	@Test
	public void test0() {
		if (0 > 10) {
			assertTrue(8 > 1);
		}
	}
}
