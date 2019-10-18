package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RTFRow27HelperDistance extends AbstractRTestCase2 {

	@Test
	public void test0() {
		int i = 10;
		if (i > 11) {
			this.goodHelper();
		}

	}

	@Test
	public void test1() {
		int i = 10;
		if (i > 11) {
			this.localHelper();
		}

	}

	@Test
	public void test2() {
		int i = 10;
		if (i > 11) {
			this.goodHelperWrapper();
		}

	}

	public void localHelper() {
		assertTrue(1 > 0);
	}

}
