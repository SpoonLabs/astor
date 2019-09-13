package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class RTFRow25Exception extends AbstractRTestCase {

	@Test(expected = NullPointerException.class)
	public void test0() {
		Object a = null;
		int i = 0;
		if (i >= 0) {
			a.toString();
		}

	}

	@Test
	public void test1() {
		Object a = null;
		int i = 0;
		try {
			if (i >= 0) {
				a.toString();
				fail("Should fail");
			}
		} catch (Exception e) {
			System.out.println("Captured");
		}
	}

	@Test
	public void test2() {
		System.out.println("Nothing");
		int i = 1;
		assertEquals(1, i);
	}

}
