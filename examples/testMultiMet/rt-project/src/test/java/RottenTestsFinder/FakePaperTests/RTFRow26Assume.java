package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertTrue;

import org.junit.Assume;
import org.junit.Test;

public class RTFRow26Assume extends AbstractRTestCase {

	@Test
	public void test0() {
		int i = 10;
		Assume.assumeTrue(i > 11);
		i++;
		assertTrue(i > 0);
	}

	@Test
	public void test1() {
		int i = 10;
		Assume.assumeTrue(i == 10);
		i++;
		assertTrue(i > 0);
	}

}
