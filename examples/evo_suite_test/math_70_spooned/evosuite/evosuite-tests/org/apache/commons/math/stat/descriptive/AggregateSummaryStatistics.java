package org.apache.commons.math.stat.descriptive;


public class AggregateSummaryStatistics implements java.io.Serializable , org.apache.commons.math.stat.descriptive.StatisticalSummary {
	private static final long serialVersionUID = -8207112444016386906L;

	private final org.apache.commons.math.stat.descriptive.SummaryStatistics statisticsPrototype;

	private final org.apache.commons.math.stat.descriptive.SummaryStatistics statistics;

	public AggregateSummaryStatistics() {
		this(new org.apache.commons.math.stat.descriptive.SummaryStatistics());
	}

	public AggregateSummaryStatistics(org.apache.commons.math.stat.descriptive.SummaryStatistics prototypeStatistics) {
		this(prototypeStatistics, (prototypeStatistics == null ? null : new org.apache.commons.math.stat.descriptive.SummaryStatistics(prototypeStatistics)));
	}

	public AggregateSummaryStatistics(org.apache.commons.math.stat.descriptive.SummaryStatistics prototypeStatistics ,org.apache.commons.math.stat.descriptive.SummaryStatistics initialStatistics) {
		this.statisticsPrototype = prototypeStatistics == null ? new org.apache.commons.math.stat.descriptive.SummaryStatistics() : prototypeStatistics;
		this.statistics = initialStatistics == null ? new org.apache.commons.math.stat.descriptive.SummaryStatistics() : initialStatistics;
	}

	public double getMax() {
		synchronized(statistics) {
			return statistics.getMax();
		}
	}

	public double getMean() {
		synchronized(statistics) {
			return statistics.getMean();
		}
	}

	public double getMin() {
		synchronized(statistics) {
			return statistics.getMin();
		}
	}

	public long getN() {
		synchronized(statistics) {
			return statistics.getN();
		}
	}

	public double getStandardDeviation() {
		synchronized(statistics) {
			return statistics.getStandardDeviation();
		}
	}

	public double getSum() {
		synchronized(statistics) {
			return statistics.getSum();
		}
	}

	public double getVariance() {
		synchronized(statistics) {
			return statistics.getVariance();
		}
	}

	public double getSumOfLogs() {
		synchronized(statistics) {
			return statistics.getSumOfLogs();
		}
	}

	public double getGeometricMean() {
		synchronized(statistics) {
			return statistics.getGeometricMean();
		}
	}

	public double getSumsq() {
		synchronized(statistics) {
			return statistics.getSumsq();
		}
	}

	public double getSecondMoment() {
		synchronized(statistics) {
			return statistics.getSecondMoment();
		}
	}

	public org.apache.commons.math.stat.descriptive.StatisticalSummary getSummary() {
		synchronized(statistics) {
			return new org.apache.commons.math.stat.descriptive.StatisticalSummaryValues(getMean() , getVariance() , getN() , getMax() , getMin() , getSum());
		}
	}

	public org.apache.commons.math.stat.descriptive.SummaryStatistics createContributingStatistics() {
		org.apache.commons.math.stat.descriptive.SummaryStatistics contributingStatistics = new org.apache.commons.math.stat.descriptive.AggregateSummaryStatistics.AggregatingSummaryStatistics(statistics);
		org.apache.commons.math.stat.descriptive.SummaryStatistics.copy(statisticsPrototype, contributingStatistics);
		return contributingStatistics;
	}

	public static org.apache.commons.math.stat.descriptive.StatisticalSummaryValues aggregate(java.util.Collection<org.apache.commons.math.stat.descriptive.SummaryStatistics> statistics) {
		if (statistics == null) {
			return null;
		} 
		java.util.Iterator<org.apache.commons.math.stat.descriptive.SummaryStatistics> iterator = statistics.iterator();
		if (!(iterator.hasNext())) {
			return null;
		} 
		org.apache.commons.math.stat.descriptive.SummaryStatistics current = iterator.next();
		long n = current.getN();
		double min = current.getMin();
		double sum = current.getSum();
		double max = current.getMax();
		double m2 = current.getSecondMoment();
		double mean = current.getMean();
		while (iterator.hasNext()) {
			current = iterator.next();
			if (((current.getMin()) < min) || (java.lang.Double.isNaN(min))) {
				min = current.getMin();
			} 
			if (((current.getMax()) > max) || (java.lang.Double.isNaN(max))) {
				max = current.getMax();
			} 
			sum += current.getSum();
			final double oldN = n;
			final double curN = current.getN();
			n += curN;
			final double meanDiff = (current.getMean()) - mean;
			mean = sum / n;
			m2 = (m2 + (current.getSecondMoment())) + ((((meanDiff * meanDiff) * oldN) * curN) / n);
		}
		final double variance;
		if (n == 0) {
			variance = java.lang.Double.NaN;
		} else {
			if (n == 1) {
				variance = 0.0;
			} else {
				variance = m2 / (n - 1);
			}
		}
		return new org.apache.commons.math.stat.descriptive.StatisticalSummaryValues(mean , variance , n , max , min , sum);
	}

	private static class AggregatingSummaryStatistics extends org.apache.commons.math.stat.descriptive.SummaryStatistics {
		private static final long serialVersionUID = 1L;

		private final org.apache.commons.math.stat.descriptive.SummaryStatistics aggregateStatistics;

		public AggregatingSummaryStatistics(org.apache.commons.math.stat.descriptive.SummaryStatistics aggregateStatistics) {
			this.aggregateStatistics = aggregateStatistics;
		}

		@java.lang.Override
		public void addValue(double value) {
			super.addValue(value);
			synchronized(aggregateStatistics) {
				aggregateStatistics.addValue(value);
			}
		}

		@java.lang.Override
		public boolean equals(java.lang.Object object) {
			if (object == (this)) {
				return true;
			} 
			if ((object instanceof org.apache.commons.math.stat.descriptive.AggregateSummaryStatistics.AggregatingSummaryStatistics) == false) {
				return false;
			} 
			org.apache.commons.math.stat.descriptive.AggregateSummaryStatistics.AggregatingSummaryStatistics stat = ((org.apache.commons.math.stat.descriptive.AggregateSummaryStatistics.AggregatingSummaryStatistics)(object));
			return (super.equals(stat)) && (aggregateStatistics.equals(stat.aggregateStatistics));
		}

		@java.lang.Override
		public int hashCode() {
			return (123 + (super.hashCode())) + (aggregateStatistics.hashCode());
		}
	}
}

