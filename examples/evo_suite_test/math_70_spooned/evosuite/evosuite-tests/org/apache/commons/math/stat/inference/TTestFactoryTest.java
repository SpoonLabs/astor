package org.apache.commons.math.stat.inference;


public class TTestFactoryTest extends org.apache.commons.math.stat.inference.TTestTest {
	public TTestFactoryTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public void setUp() {
		super.setUp();
		testStatistic = org.apache.commons.math.stat.inference.TestUtils.getTTest();
	}
}

