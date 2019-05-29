package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RTFRow017 extends AbstractRTestCase {

	@Test
	public void test0() {
		int a = 0;
		if (false) {
			System.out.println("then branch");
			assertTrue(a < 0);
		} else {
			System.out.println("else branch");
			assertTrue(a >= 0);
		}
	}
}
