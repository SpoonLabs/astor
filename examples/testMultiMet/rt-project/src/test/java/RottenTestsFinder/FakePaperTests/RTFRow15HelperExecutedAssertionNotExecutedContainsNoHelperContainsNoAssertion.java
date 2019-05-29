package RottenTestsFinder.FakePaperTests;

import org.junit.Test;

public class RTFRow15HelperExecutedAssertionNotExecutedContainsNoHelperContainsNoAssertion extends AbstractRTestCase {

//	{ #category : #tests }
//	RTFRow15HelperExecutedAssertionNotExecutedContainsNoHelperContainsNoAssertion >> test [
//		self perform: ('goodHelper' , 'Wrapper') asSymbol
//	]
//

	@Test
	public void test0() {
		this.perform(this, "goodHelperWrapper");
	}

//	{ #category : #tests }
//	RTFRow15HelperExecutedAssertionNotExecutedContainsNoHelperContainsNoAssertion >> test2 [
//		self perform: ('good','Helper') asSymbol
//	]

	@Test
	public void test2() {

		this.perform(this, "rottenHelper");
	}
}
