package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

//import junit.framework.TestCase;

//import junit.framework.TestCase;

@SuppressWarnings("static-access")
public class AbstractRtTestCase /* extends TestCase */ {
	/**
	 * { #category : #helper } RTFAbstractTestCaseForPaper >> goodHelper [
	 * 
	 * ]
	 */
	@Test
	@Ignore
	public void test1() {
		assertTrue(1 > 0);
	}

	public void goodHelper() {
		assertTrue(1 > 0);
	}

	/**
	 * { #category : #helper } RTFAbstractTestCaseForPaper >> goodHelperWrapper [
	 * self goodHelper ]
	 */

	public void goodHelperWrapper() {
		this.goodHelper();
	}

	/**
	 * { #category : #'doing nothing' } RTFAbstractTestCaseForPaper >>
	 * methodDoingNothing [ "Do nothing on purpose." ]
	 * 
	 */
	public void methodDoingNothing() {
		System.out.println("Nothing");
	}

	/**
	 * { #category : #helper } RTFAbstractTestCaseForPaper >> rottenHelper [ false
	 * ifTrue: [ self assert: true ] ]
	 */

	public void rottenHelper() {
		if (false) {
			assertTrue(1 > 0);
		}
	}

	protected void perform(Object receiver, String methodName) {
		java.lang.reflect.Method method;
		try {
			method = receiver.getClass().getMethod(methodName);
			method.invoke(receiver);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void performAssert(Object receiver, String methodName) {
		java.lang.reflect.Method method;
		try {
			method = receiver.getClass().getMethod(methodName, boolean.class);
			// for simplicity, we pass value 'true'
			method.invoke(receiver, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
