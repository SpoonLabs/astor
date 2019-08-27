package RottenTestsFinder.FakePaperTests;

import org.junit.Test;

public class RTFRow12HelperNotExecutedAssertionNotExecutedContainsHelperContainsNoAssertion extends AbstractRTestCase {
//	false
//	ifTrue: [ self goodHelper ]
//]

	@Test
	public void test0() {
		int a = 1;
		if (a > 10) {// condition must be false.
			this.goodHelper();
		}

	}

}
