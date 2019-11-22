package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RTFRow31IfContextFP extends AbstractRTestCase {
	@Test
	public void test0() {
		int i = 11;
		if (i < 0) {
			assertTrue(i < 0);
		} else {
			System.out.println("executed");
			// will execute the assert and pass
			assertWithIfPositive(i);
		}

	}

	@Test
	public void test1() {
		int i = 10;
		if (i < 0) {
			// not executed
			assertTrue(i < 0);
		} else {
			System.out.println("executed");
			// do not execute the assert
			assertWithIfPositive(-1);
		}

	}

	public void assertWithIfPositive(int i) {
		// helper not always assert....
		if (i > 0) {
			assertTrue(i > 10);
		}
	}

}
