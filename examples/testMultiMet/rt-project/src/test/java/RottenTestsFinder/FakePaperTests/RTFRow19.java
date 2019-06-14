package RottenTestsFinder.FakePaperTests;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RTFRow19 {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void test0() {
		thrown.expect(NullPointerException.class);
		throw new NullPointerException();
	}
}
