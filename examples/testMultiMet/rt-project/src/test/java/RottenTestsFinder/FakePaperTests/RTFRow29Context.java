package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RTFRow29Context extends AbstractRTestCase {

	@Test
	public void test0() {
		helper(true);
	}

	@Test
	public void test1() {
		helper(false);
	}

	public void helper(boolean t) {
		int i = 10;
		if (t) {

			assertTrue(i >= 10);
		}

	}

	@Test
	public void test2() {

		int i = 0;
		if (i > 0) {
			helper(true);
		}
	}

}
