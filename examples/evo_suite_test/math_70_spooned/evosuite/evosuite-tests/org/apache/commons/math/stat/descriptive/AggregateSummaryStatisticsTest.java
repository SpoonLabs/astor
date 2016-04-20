package org.apache.commons.math.stat.descriptive;


public class AggregateSummaryStatisticsTest extends junit.framework.TestCase {
	public void testAggregation() {
		org.apache.commons.math.stat.descriptive.AggregateSummaryStatistics aggregate = new org.apache.commons.math.stat.descriptive.AggregateSummaryStatistics();
		org.apache.commons.math.stat.descriptive.SummaryStatistics setOneStats = aggregate.createContributingStatistics();
		org.apache.commons.math.stat.descriptive.SummaryStatistics setTwoStats = aggregate.createContributingStatistics();
		junit.framework.Assert.assertNotNull("The set one contributing stats are null", setOneStats);
		junit.framework.Assert.assertNotNull("The set two contributing stats are null", setTwoStats);
		junit.framework.Assert.assertNotSame("Contributing stats objects are the same", setOneStats, setTwoStats);
		setOneStats.addValue(2);
		setOneStats.addValue(3);
		setOneStats.addValue(5);
		setOneStats.addValue(7);
		setOneStats.addValue(11);
		junit.framework.Assert.assertEquals("Wrong number of set one values", 5, setOneStats.getN());
		junit.framework.Assert.assertEquals("Wrong sum of set one values", 28.0, setOneStats.getSum());
		setTwoStats.addValue(2);
		setTwoStats.addValue(4);
		setTwoStats.addValue(8);
		junit.framework.Assert.assertEquals("Wrong number of set two values", 3, setTwoStats.getN());
		junit.framework.Assert.assertEquals("Wrong sum of set two values", 14.0, setTwoStats.getSum());
		junit.framework.Assert.assertEquals("Wrong number of aggregate values", 8, aggregate.getN());
		junit.framework.Assert.assertEquals("Wrong aggregate sum", 42.0, aggregate.getSum());
	}

	public void testAggregationConsistency() throws java.lang.Exception {
		double[] totalSample = generateSample();
		double[][] subSamples = generatePartition(totalSample);
		int nSamples = subSamples.length;
		org.apache.commons.math.stat.descriptive.AggregateSummaryStatistics aggregate = new org.apache.commons.math.stat.descriptive.AggregateSummaryStatistics();
		org.apache.commons.math.stat.descriptive.SummaryStatistics totalStats = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		org.apache.commons.math.stat.descriptive.SummaryStatistics[] componentStats = new org.apache.commons.math.stat.descriptive.SummaryStatistics[nSamples];
		for (int i = 0 ; i < nSamples ; i++) {
			componentStats[i] = aggregate.createContributingStatistics();
			for (int j = 0 ; j < (subSamples[i].length) ; j++) {
				componentStats[i].addValue(subSamples[i][j]);
			}
		}
		for (int i = 0 ; i < (totalSample.length) ; i++) {
			totalStats.addValue(totalSample[i]);
		}
		junit.framework.Assert.assertEquals(totalStats.getSummary(), aggregate.getSummary());
	}

	public void testAggregate() throws java.lang.Exception {
		double[] totalSample = generateSample();
		double[][] subSamples = generatePartition(totalSample);
		int nSamples = subSamples.length;
		org.apache.commons.math.stat.descriptive.SummaryStatistics totalStats = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		for (int i = 0 ; i < (totalSample.length) ; i++) {
			totalStats.addValue(totalSample[i]);
		}
		org.apache.commons.math.stat.descriptive.SummaryStatistics[] subSampleStats = new org.apache.commons.math.stat.descriptive.SummaryStatistics[nSamples];
		for (int i = 0 ; i < nSamples ; i++) {
			subSampleStats[i] = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		}
		java.util.Collection<org.apache.commons.math.stat.descriptive.SummaryStatistics> aggregate = new java.util.ArrayList<org.apache.commons.math.stat.descriptive.SummaryStatistics>();
		for (int i = 0 ; i < nSamples ; i++) {
			for (int j = 0 ; j < (subSamples[i].length) ; j++) {
				subSampleStats[i].addValue(subSamples[i][j]);
			}
			aggregate.add(subSampleStats[i]);
		}
		org.apache.commons.math.stat.descriptive.StatisticalSummary aggregatedStats = org.apache.commons.math.stat.descriptive.AggregateSummaryStatistics.aggregate(aggregate);
		org.apache.commons.math.stat.descriptive.AggregateSummaryStatisticsTest.assertEquals(totalStats.getSummary(), aggregatedStats, 1.0E-11);
	}

	public void testAggregateDegenerate() throws java.lang.Exception {
		double[] totalSample = new double[]{ 1 , 2 , 3 , 4 , 5 };
		double[][] subSamples = new double[][]{ new double[]{ 1 } , new double[]{ 2 } , new double[]{ 3 } , new double[]{ 4 } , new double[]{ 5 } };
		org.apache.commons.math.stat.descriptive.SummaryStatistics totalStats = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		for (int i = 0 ; i < (totalSample.length) ; i++) {
			totalStats.addValue(totalSample[i]);
		}
		org.apache.commons.math.stat.descriptive.SummaryStatistics[] subSampleStats = new org.apache.commons.math.stat.descriptive.SummaryStatistics[5];
		for (int i = 0 ; i < 5 ; i++) {
			subSampleStats[i] = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		}
		java.util.Collection<org.apache.commons.math.stat.descriptive.SummaryStatistics> aggregate = new java.util.ArrayList<org.apache.commons.math.stat.descriptive.SummaryStatistics>();
		for (int i = 0 ; i < 5 ; i++) {
			for (int j = 0 ; j < (subSamples[i].length) ; j++) {
				subSampleStats[i].addValue(subSamples[i][j]);
			}
			aggregate.add(subSampleStats[i]);
		}
		org.apache.commons.math.stat.descriptive.StatisticalSummaryValues aggregatedStats = org.apache.commons.math.stat.descriptive.AggregateSummaryStatistics.aggregate(aggregate);
		org.apache.commons.math.stat.descriptive.AggregateSummaryStatisticsTest.assertEquals(totalStats.getSummary(), aggregatedStats, 1.0E-11);
	}

	public void testAggregateSpecialValues() throws java.lang.Exception {
		double[] totalSample = new double[]{ java.lang.Double.POSITIVE_INFINITY , 2 , 3 , java.lang.Double.NaN , 5 };
		double[][] subSamples = new double[][]{ new double[]{ java.lang.Double.POSITIVE_INFINITY , 2 } , new double[]{ 3 } , new double[]{ java.lang.Double.NaN } , new double[]{ 5 } };
		org.apache.commons.math.stat.descriptive.SummaryStatistics totalStats = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		for (int i = 0 ; i < (totalSample.length) ; i++) {
			totalStats.addValue(totalSample[i]);
		}
		org.apache.commons.math.stat.descriptive.SummaryStatistics[] subSampleStats = new org.apache.commons.math.stat.descriptive.SummaryStatistics[5];
		for (int i = 0 ; i < 4 ; i++) {
			subSampleStats[i] = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		}
		java.util.Collection<org.apache.commons.math.stat.descriptive.SummaryStatistics> aggregate = new java.util.ArrayList<org.apache.commons.math.stat.descriptive.SummaryStatistics>();
		for (int i = 0 ; i < 4 ; i++) {
			for (int j = 0 ; j < (subSamples[i].length) ; j++) {
				subSampleStats[i].addValue(subSamples[i][j]);
			}
			aggregate.add(subSampleStats[i]);
		}
		org.apache.commons.math.stat.descriptive.StatisticalSummaryValues aggregatedStats = org.apache.commons.math.stat.descriptive.AggregateSummaryStatistics.aggregate(aggregate);
		org.apache.commons.math.stat.descriptive.AggregateSummaryStatisticsTest.assertEquals(totalStats.getSummary(), aggregatedStats, 1.0E-11);
	}

	protected static void assertEquals(org.apache.commons.math.stat.descriptive.StatisticalSummary expected, org.apache.commons.math.stat.descriptive.StatisticalSummary observed, double delta) {
		org.apache.commons.math.TestUtils.assertEquals(expected.getMax(), observed.getMax(), 0);
		org.apache.commons.math.TestUtils.assertEquals(expected.getMin(), observed.getMin(), 0);
		junit.framework.Assert.assertEquals(expected.getN(), observed.getN());
		org.apache.commons.math.TestUtils.assertEquals(expected.getSum(), observed.getSum(), delta);
		org.apache.commons.math.TestUtils.assertEquals(expected.getMean(), observed.getMean(), delta);
		org.apache.commons.math.TestUtils.assertEquals(expected.getStandardDeviation(), observed.getStandardDeviation(), delta);
		org.apache.commons.math.TestUtils.assertEquals(expected.getVariance(), observed.getVariance(), delta);
	}

	private double[] generateSample() {
		final org.apache.commons.math.random.RandomData randomData = new org.apache.commons.math.random.RandomDataImpl();
		final int sampleSize = randomData.nextInt(10, 100);
		double[] out = new double[sampleSize];
		for (int i = 0 ; i < (out.length) ; i++) {
			out[i] = randomData.nextUniform(-100, 100);
		}
		return out;
	}

	private double[][] generatePartition(double[] sample) {
		final int length = sample.length;
		final double[][] out = new double[5][];
		final org.apache.commons.math.random.RandomData randomData = new org.apache.commons.math.random.RandomDataImpl();
		int cur = 0;
		int offset = 0;
		int sampleCount = 0;
		for (int i = 0 ; i < 5 ; i++) {
			if ((cur == length) || (offset == length)) {
				break;
			} 
			final int next = (i == 4) || (cur == (length - 1)) ? length - 1 : randomData.nextInt(cur, (length - 1));
			final int subLength = (next - cur) + 1;
			out[i] = new double[subLength];
			java.lang.System.arraycopy(sample, offset, out[i], 0, subLength);
			cur = next + 1;
			sampleCount++;
			offset += subLength;
		}
		if (sampleCount < 5) {
			double[][] out2 = new double[sampleCount][];
			for (int j = 0 ; j < sampleCount ; j++) {
				final int curSize = out[j].length;
				out2[j] = new double[curSize];
				java.lang.System.arraycopy(out[j], 0, out2[j], 0, curSize);
			}
			return out2;
		} else {
			return out;
		}
	}
}

