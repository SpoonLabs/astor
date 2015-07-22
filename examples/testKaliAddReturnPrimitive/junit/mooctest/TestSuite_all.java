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
		assertEquals(fm.max(-1, -3, 0), /*3*/0); //I modify the test for validate Kali operator
	}

	public void test1_2() {
		assertEquals(fm.max(0, -3, -2), /*3*/0);
	}

	public void test1_3() {
		assertEquals(fm.max(-11, 0, -2), /*3*/0);
	}


}
