package org.apache.commons.math.stat.inference;


public class ChiSquareFactoryTest extends org.apache.commons.math.stat.inference.ChiSquareTestTest {
	public ChiSquareFactoryTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public void setUp() throws java.lang.Exception {
		super.setUp();
		testStatistic = org.apache.commons.math.stat.inference.TestUtils.getUnknownDistributionChiSquareTest();
	}
}

