package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RTFRow23MissingFail extends AbstractRTestCase {
//{ #category : #tests }
//RTFRow16HelperNotExecutedAssertionNotExecutedContainsNoHelperContainsNoAssertion >> test [
//"Does nothing."
//]
//
	@Test
	public void test0() {
		int i = 10;
		if (i > 11) {
			System.out.println("Nothing");
			assertTrue(false);
		}
	}

}
