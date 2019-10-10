package LeapYear.bug1;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class JUnitLeapYear{

	@Test
	public void test(){
		LeapYear a = new LeapYear();
		assertEquals(false, a.LeapChecking(1416128883));
	}

	@Test
	public void test1(){
		LeapYear a = new LeapYear();
		assertEquals(true, a.LeapChecking(1694498816));
	}

	@Test
	public void test2(){
		LeapYear a = new LeapYear();
		assertEquals(true, a.LeapChecking(100659200));
	}

	@Test
	public void test3(){
		LeapYear a = new LeapYear();
		assertEquals(false, a.LeapChecking(2106384300));
	}

	@Test
	public void test4(){
		LeapYear a = new LeapYear();
		assertEquals(false, a.LeapChecking(1579559530));
	}

	@Test
	public void test5(){
		LeapYear a = new LeapYear();
		assertEquals(true, a.LeapChecking(1056964608));
	}

	@Test
	public void test6(){
		LeapYear a = new LeapYear();
		assertEquals(true, a.LeapChecking(1677721600));
	}

	@Test
	public void test7(){
		LeapYear a = new LeapYear();
		assertEquals(false, a.LeapChecking(739513900));
	}

	@Test
	public void test8(){
		LeapYear a = new LeapYear();
		assertEquals(false, a.LeapChecking(724249387));
	}

	@Test
	public void test9(){
		LeapYear a = new LeapYear();
		assertEquals(true, a.LeapChecking(654311424));
	}


	@Test
	public void test10(){
		LeapYear a = new LeapYear();
		assertEquals(true, a.LeapChecking(165670400));
	}

	@Test
	public void test11(){
		LeapYear a = new LeapYear();
		assertEquals(false, a.LeapChecking(2147420100));
	}

	@Test
	public void test12(){
		LeapYear a = new LeapYear();
		assertEquals(true, a.LeapChecking(742145596));
	}

	@Test
	public void test13(){
		LeapYear a = new LeapYear();
		assertEquals(false, a.LeapChecking(742145605));
	}


	@Test
	public void test14(){
		LeapYear a = new LeapYear();
		assertEquals(true, a.LeapChecking(100659200));
	}

	@Test
	public void test15(){
		LeapYear a = new LeapYear();
		assertEquals(false, a.LeapChecking(572654100));
	}

	@Test
	public void test16(){
		LeapYear a = new LeapYear();
		assertEquals(false, a.LeapChecking(1515733846));
	}

	@Test
	public void test17(){
		LeapYear a = new LeapYear();
		assertEquals(true, a.LeapChecking(1514423364));
	}


	@Test
	public void test18(){
		LeapYear a = new LeapYear();
		assertEquals(true, a.LeapChecking(4198400));
	}

	@Test
	public void test19(){
		LeapYear a = new LeapYear();
		assertEquals(false, a.LeapChecking(1514423300));
	}
}
