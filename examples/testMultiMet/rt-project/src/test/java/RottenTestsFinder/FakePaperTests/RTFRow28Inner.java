package RottenTestsFinder.FakePaperTests;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Comparator;

import org.junit.Test;

public class RTFRow28Inner extends AbstractRTestCase {

	@Test
	public void test0() {
		// This is NOT RT
		int i = 10;
		Integer[] is = { 2, 3, 4 };

		Arrays.asList(is).sort(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				localHelperPossitive(i);
				return 0;
			}
		});

	}

	@Test
	public void test1() {
		// This is NOT RT
		int i = 10;
		Comparator c = new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				localHelperPossitive(i);
				return 0;
			}
		};
		c.compare(1, 1);

	}

	@Test
	public void test2() {

		// This is RT
		int i = 11;
		Comparator c = new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				localHelperPossitive(i);
				return 0;
			}
		};

		assertTrue(i > 10);
	}

	@Test
	public void test3() {
		// This is Not RT
		int i = 10;
		Comparator c = new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				if (o1 % 2 == 0)
					localHelperPossitive(i);
				else {
					localHelperPossitive(i);
				}
				return 0;
			}
		};
		c.compare(1, 1);

	}

	@Test
	public void test4() {
		// This is RT
		int i = 10;
		Comparator c = new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				if (o1 > 100)
					// Never executed
					localHelperPossitive(i);
				else {
					localHelperPossitive(i);
				}
				return 0;
			}
		};
		c.compare(1, 1);

	}

	public void localHelperPossitive(int i) {
		assertTrue(i > 0);
	}

}
