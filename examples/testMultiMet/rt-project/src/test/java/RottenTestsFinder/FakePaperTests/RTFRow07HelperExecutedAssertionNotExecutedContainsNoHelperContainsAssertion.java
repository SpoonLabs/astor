package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RTFRow07HelperExecutedAssertionNotExecutedContainsNoHelperContainsAssertion extends AbstractRTestCase {
//false
//	ifTrue: [ self assert: true ].	
//self perform: ('rotten','Helper') asSymbol

	@Test
	public void test0() {
		if (false) {
			assertTrue(7 > 1);
		}
		this.perform(this, "rottenHelper");
	}
}
