package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class RTFRow25Exception2 extends AbstractRTestCase {

	@Test(expected = NullPointerException.class)
	public void test0() {
		Object a = null;
		int i = 0;
		if (i >= 0) {
			a.toString();
		}
		assertTrue(i >= 0);
	}

	@Test(expected = NullPointerException.class)
	public void test1() {
		Object a = null;
		int i = 0;
		if (i >= 0) {
			a.toString();
		}
		assertTrue(false);
	}

	@Test(expected = NullPointerException.class)
	public void test2() {
		Object a = null;
		int i = 0;
		if (i >= 0) {
			a.toString();
		}
		fail();
	}

}
