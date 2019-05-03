
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import test.MyBuggy;

public class MyBuggyTest {

	@Test
	public void testOperation1() {

		MyBuggy b = new MyBuggy();

		assertEquals((Integer) 3, (Integer) b.operation(1, 2, "+"));

		assertEquals((Integer) 3, (Integer) b.operation(2, 1, "+"));

	}

	@Test
	public void testOperation3() {

		MyBuggy b = new MyBuggy();

		assertEquals((Integer) 2, (Integer) b.operation(2, 1, "gr"));

		assertEquals((Integer) 12, (Integer) b.operation(2, 12, "gr"));

		assertEquals((Integer) 12, (Integer) b.operation(12, 12, "gr"));
	}

	@Test
	public void testOperation4() {

		MyBuggy b = new MyBuggy();

		assertEquals((Integer) 2, (Integer) b.operation(1, 2, "*"));

		assertEquals((Integer) 10, (Integer) b.operation(5, 2, "*"));

	}

	@Test
	public void testOperation5() {

		MyBuggy b = new MyBuggy();

		assertEquals((Integer) 7, (Integer) b.operation(5, 2, null));
		assertEquals((Integer) 7, (Integer) b.operation(3, 4, null));
		assertEquals((Integer) 7, (Integer) b.operation(5, 2, "other"));
		assertEquals((Integer) 7, (Integer) b.operation(3, 4, "other"));
	}

}
