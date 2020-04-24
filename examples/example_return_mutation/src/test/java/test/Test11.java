package test;

import org.junit.Test;

import junit.framework.TestCase;

public class Test11 extends TestCase {

	Test1 test1;

	@Test
	public void testGetSum() {
		test1 = new Test1();
		org.junit.Assert.assertTrue(4 == test1.getSum(3, 1));
	}

	@Test
	public void testGetSum2() {
		test1 = new Test1();
		org.junit.Assert.assertTrue(8 == test1.getSum(6, 2));
	}

	@Test
	public void testGetSum3() {
		test1 = new Test1();
		org.junit.Assert.assertTrue(4 == test1.getSum(4, 0));
	}

	@Test
	public void testGetSum4() {
		test1 = new Test1();
		org.junit.Assert.assertTrue(6 == test1.getSum(4, 2));
	}

	@Test
	public void testGetSum5() {
		test1 = new Test1();
		org.junit.Assert.assertTrue(5 == test1.getSum(4, 1));
	}

	@Test
	public void testGetEven() {
		test1 = new Test1();
		org.junit.Assert.assertFalse(test1.isEven(1));
		org.junit.Assert.assertTrue(test1.isEven(2));
		org.junit.Assert.assertFalse(test1.isEven(3));
		org.junit.Assert.assertTrue(test1.isEven(4));
	}

}
