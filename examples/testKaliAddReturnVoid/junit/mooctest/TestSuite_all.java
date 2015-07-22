package mooctest;

import mooctest.IntMax;
import junit.framework.TestCase;

/**
 * 
 * @author ShuoSong
 * 
 *         This test suite contains all three categories of test cases.
 * 
 */

public class TestSuite_all extends TestCase {

	IntMax fm = new IntMax();

	// 1. Covers the fault and manifests : y > x && y > z
	public void test1_1() {
		assertEquals(fm.max(1, 3, 1), 3);
	}

	public void test1_2() {
		assertEquals(fm.max(2, 3, 1), 3);
	}

	public void test1_3() {
		assertEquals(fm.max(1, 3, 2), 3);
	}

	// 2. Covers the fault but does not manifest : x >= y && y > z
	public void test2_1() {
		assertEquals(fm.max(3, 3, 2), 3);
	}

	public void test2_2() {
		assertEquals(fm.max(3, 2, 1), 3);
	}

	// 3. Does not cover the fault
	public void test3_1() {
		assertEquals(fm.max(1, 1, 1), 1);
	}

	public void test3_2() {
		//assertEquals(fm.max(1, 1, 2), 2);
	}

	public void test3_3() {
		assertEquals(fm.max(1, 2, 2), 2);
	}
}
