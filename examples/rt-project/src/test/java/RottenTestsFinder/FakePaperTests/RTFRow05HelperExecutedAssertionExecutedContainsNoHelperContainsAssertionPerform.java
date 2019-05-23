package RottenTestsFinder.FakePaperTests;

import org.junit.Test;

public class RTFRow05HelperExecutedAssertionExecutedContainsNoHelperContainsAssertionPerform
		extends AbstractRtTestCase {
//self assert: true.

	// self perform: ('good','Helper') asSymbol

	@Test
	public void test0() {
		this.assertTrue(5 > 1);
		this.perform(this, "goodHelper");
	}
}
