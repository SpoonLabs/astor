package RottenTestsFinder.FakePaperTests;

import org.junit.Test;

public class RTFRow10HelperNotExecutedAssertionExecutedContainsHelperContainsNoAssertion extends AbstractRtTestCase {

	// false
//	ifTrue: [ self goodHelper ].	
//self perform: ('as' , 'sert:') asSymbol with: true
	@Test
	public void test0() {
		if (false) {
			this.goodHelper();

		}
		this.performAssert(this, "assertTrue");
	}
}
