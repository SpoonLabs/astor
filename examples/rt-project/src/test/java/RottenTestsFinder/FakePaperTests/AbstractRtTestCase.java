package RottenTestsFinder.FakePaperTests;

import junit.framework.TestCase;

@SuppressWarnings("static-access")
public class AbstractRtTestCase extends TestCase {
	/**
	 * { #category : #helper } RTFAbstractTestCaseForPaper >> goodHelper [
	 * 
	 * ]
	 */

	// @Test
	public void goodHelper() {
		this.assertTrue(true);
	}

	/**
	 * { #category : #helper } RTFAbstractTestCaseForPaper >> goodHelperWrapper [
	 * self goodHelper ]
	 */

	// @Test
	public void goodHelperWrapper() {
		this.goodHelper();
	}

	/**
	 * { #category : #'doing nothing' } RTFAbstractTestCaseForPaper >>
	 * methodDoingNothing [ "Do nothing on purpose." ]
	 * 
	 */
	// @Test
	public void methodDoingNothing() {
		System.out.println("Nothing");
	}

	/**
	 * { #category : #helper } RTFAbstractTestCaseForPaper >> rottenHelper [ false
	 * ifTrue: [ self assert: true ] ]
	 */

	public void rottenHelper() {
		if (false) {
			this.assertTrue(true);
		}
	}

}
