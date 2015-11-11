package fr.inria.astor.core.util;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
/**
 * 
 * @author matias
 *
 */
public class TimeUtilTest {

	@Test
	public void test() throws InterruptedException {
		Date d = new Date();
		Thread.currentThread().sleep(61000);
		long min = TimeUtil.deltaInMinutes(d);
		Assert.assertTrue(min>0);
	}

	@Test
	public void test2() throws InterruptedException {
		Date d = new Date();
		Thread.currentThread().sleep(181000);
		long min = TimeUtil.deltaInMinutes(d);
		Assert.assertTrue(min>=2);
	}
	@Test
	public void test3() throws InterruptedException {
		Date d = new Date();
		Thread.currentThread().sleep(61000);
		long min = TimeUtil.deltaInMinutes(d);
		Assert.assertTrue(min<2);
	}
}
