package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class RTFRow30Skip extends AbstractRTestCase {
	@Test
	public void test0() {
		int i = 10;
		if (i > 0) {
			return;
		}
		System.out.println("test");
	}

	@Test
	public void test1() {
		int i = 10;
		if (i > 0) {
			return;
		}
		fail("test");
	}

	@Test
	public void test2() {
		int i = 10;
		if (i > 0) {
			return;
		}
		assertTrue(i > 0);
	}

}
