package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RTFRow05HelperExecutedAssertionExecutedContainsNoHelperContainsAssertionPerform
		extends AbstractRTestCase {
//self assert: true.

	// self perform: ('good','Helper') asSymbol

	@Test
	public void test0() {
		assertTrue(5 > 1);
		this.perform(this, "goodHelper");
	}
}
