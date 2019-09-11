package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RTFRow24Redundant extends AbstractRTestCase {

	@Test
	public void test0() {
		System.out.println("Nothing");
		assertTrue(true);
	}

	@Test
	public void test1() {
		System.out.println("Nothing");
		assertFalse(false);
	}

	@Test
	public void test2() {
		System.out.println("Nothing");
		assertEquals(true, true);
	}

	@Test
	public void test3() {
		System.out.println("Nothing");
		boolean f = false;
		assertFalse(f);
	}
}
