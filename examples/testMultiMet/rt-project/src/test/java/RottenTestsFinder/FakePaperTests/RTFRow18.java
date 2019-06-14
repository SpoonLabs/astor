package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RTFRow18 {

	@Test(expected = NullPointerException.class)
	public void test0() {
		int a = 0;
		if (true) {
			assertTrue(a == 0);
			throw new NullPointerException("test");
		}
		assertTrue(10 > 0);
	}
}
