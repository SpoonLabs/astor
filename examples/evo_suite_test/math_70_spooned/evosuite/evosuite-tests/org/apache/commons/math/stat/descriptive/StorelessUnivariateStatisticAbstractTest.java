package org.apache.commons.math.stat.descriptive;


public abstract class StorelessUnivariateStatisticAbstractTest extends org.apache.commons.math.stat.descriptive.UnivariateStatisticAbstractTest {
	public StorelessUnivariateStatisticAbstractTest(java.lang.String name) {
		super(name);
	}

	protected double[][] smallSamples = new double[][]{ new double[]{  } , new double[]{ 1 } , new double[]{ 1 , 2 } , new double[]{ 1 , 2 , 3 } , new double[]{ 1 , 2 , 3 , 4 } };

	@java.lang.Override
	public abstract org.apache.commons.math.stat.descriptive.UnivariateStatistic getUnivariateStatistic();

	@java.lang.Override
	public abstract double expectedValue();

	public void testIncrementation() throws java.lang.Exception {
		org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic statistic = ((org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic)(getUnivariateStatistic()));
		for (int i = 0 ; i < (testArray.length) ; i++) {
			statistic.increment(testArray[i]);
		}
		junit.framework.Assert.assertEquals(expectedValue(), statistic.getResult(), getTolerance());
		junit.framework.Assert.assertEquals(testArray.length, statistic.getN());
		statistic.clear();
		statistic.incrementAll(testArray);
		junit.framework.Assert.assertEquals(expectedValue(), statistic.getResult(), getTolerance());
		junit.framework.Assert.assertEquals(testArray.length, statistic.getN());
		statistic.clear();
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(statistic.getResult()));
		junit.framework.Assert.assertEquals(0, statistic.getN());
	}

	public void testSerialization() throws java.lang.Exception {
		org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic statistic = ((org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic)(getUnivariateStatistic()));
		org.apache.commons.math.TestUtils.checkSerializedEquality(statistic);
		statistic.clear();
		for (int i = 0 ; i < (testArray.length) ; i++) {
			statistic.increment(testArray[i]);
			if ((i % 5) == 0) {
				statistic = ((org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic)(org.apache.commons.math.TestUtils.serializeAndRecover(statistic)));
			} 
		}
		org.apache.commons.math.TestUtils.checkSerializedEquality(statistic);
		junit.framework.Assert.assertEquals(expectedValue(), statistic.getResult(), getTolerance());
		statistic.clear();
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(statistic.getResult()));
	}

	public void testEqualsAndHashCode() {
		org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic statistic = ((org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic)(getUnivariateStatistic()));
		org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic statistic2 = null;
		junit.framework.Assert.assertTrue("non-null, compared to null", !(statistic.equals(statistic2)));
		junit.framework.Assert.assertTrue("reflexive, non-null", statistic.equals(statistic));
		int emptyHash = statistic.hashCode();
		statistic2 = ((org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic)(getUnivariateStatistic()));
		junit.framework.Assert.assertTrue("empty stats should be equal", statistic.equals(statistic2));
		junit.framework.Assert.assertEquals("empty stats should have the same hashcode", emptyHash, statistic2.hashCode());
		statistic.increment(1.0);
		junit.framework.Assert.assertTrue("reflexive, non-empty", statistic.equals(statistic));
		junit.framework.Assert.assertTrue("non-empty, compared to empty", !(statistic.equals(statistic2)));
		junit.framework.Assert.assertTrue("non-empty, compared to empty", !(statistic2.equals(statistic)));
		junit.framework.Assert.assertTrue("non-empty stat should have different hashcode from empty stat", ((statistic.hashCode()) != emptyHash));
		statistic2.increment(1.0);
		junit.framework.Assert.assertTrue("stats with same data should be equal", statistic.equals(statistic2));
		junit.framework.Assert.assertEquals("stats with same data should have the same hashcode", statistic.hashCode(), statistic2.hashCode());
		statistic.increment(java.lang.Double.POSITIVE_INFINITY);
		junit.framework.Assert.assertTrue("stats with different n's should not be equal", !(statistic2.equals(statistic)));
		junit.framework.Assert.assertTrue("stats with different n's should have different hashcodes", ((statistic.hashCode()) != (statistic2.hashCode())));
		statistic2.increment(java.lang.Double.POSITIVE_INFINITY);
		junit.framework.Assert.assertTrue("stats with same data should be equal", statistic.equals(statistic2));
		junit.framework.Assert.assertEquals("stats with same data should have the same hashcode", statistic.hashCode(), statistic2.hashCode());
		statistic.clear();
		statistic2.clear();
		junit.framework.Assert.assertTrue("cleared stats should be equal", statistic.equals(statistic2));
		junit.framework.Assert.assertEquals("cleared stats should have thashcode of empty stat", emptyHash, statistic2.hashCode());
		junit.framework.Assert.assertEquals("cleared stats should have thashcode of empty stat", emptyHash, statistic.hashCode());
	}

	public void testMomentSmallSamples() {
		org.apache.commons.math.stat.descriptive.UnivariateStatistic stat = getUnivariateStatistic();
		if (stat instanceof org.apache.commons.math.stat.descriptive.moment.SecondMoment) {
			org.apache.commons.math.stat.descriptive.moment.SecondMoment moment = ((org.apache.commons.math.stat.descriptive.moment.SecondMoment)(getUnivariateStatistic()));
			junit.framework.Assert.assertTrue(java.lang.Double.isNaN(moment.getResult()));
			moment.increment(1.0);
			junit.framework.Assert.assertEquals(0.0, moment.getResult(), 0);
		} 
	}

	public void testConsistency() {
		org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic stat = ((org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic)(getUnivariateStatistic()));
		stat.incrementAll(testArray);
		junit.framework.Assert.assertEquals(stat.getResult(), stat.evaluate(testArray), getTolerance());
		for (int i = 0 ; i < (smallSamples.length) ; i++) {
			stat.clear();
			for (int j = 0 ; j < (smallSamples[i].length) ; j++) {
				stat.increment(smallSamples[i][j]);
			}
			org.apache.commons.math.TestUtils.assertEquals(stat.getResult(), stat.evaluate(smallSamples[i]), getTolerance());
		}
	}

	public void testCopyConsistency() {
		org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic master = ((org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic)(getUnivariateStatistic()));
		org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic replica = null;
		long index = java.lang.Math.round(((java.lang.Math.random()) * (testArray.length)));
		master.incrementAll(testArray, 0, ((int)(index)));
		replica = master.copy();
		junit.framework.Assert.assertTrue(replica.equals(master));
		junit.framework.Assert.assertTrue(master.equals(replica));
		master.incrementAll(testArray, ((int)(index)), ((int)((testArray.length) - index)));
		replica.incrementAll(testArray, ((int)(index)), ((int)((testArray.length) - index)));
		junit.framework.Assert.assertTrue(replica.equals(master));
		junit.framework.Assert.assertTrue(master.equals(replica));
	}

	public void testSerial() {
		org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic s = ((org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic)(getUnivariateStatistic()));
		junit.framework.Assert.assertEquals(s, org.apache.commons.math.TestUtils.serializeAndRecover(s));
	}
}

