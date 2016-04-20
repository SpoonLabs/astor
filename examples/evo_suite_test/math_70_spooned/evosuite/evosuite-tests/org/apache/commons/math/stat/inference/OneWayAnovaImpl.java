package org.apache.commons.math.stat.inference;


public class OneWayAnovaImpl implements org.apache.commons.math.stat.inference.OneWayAnova {
	public OneWayAnovaImpl() {
	}

	public double anovaFValue(java.util.Collection<double[]> categoryData) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		org.apache.commons.math.stat.inference.OneWayAnovaImpl.AnovaStats a = anovaStats(categoryData);
		return a.F;
	}

	public double anovaPValue(java.util.Collection<double[]> categoryData) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		org.apache.commons.math.stat.inference.OneWayAnovaImpl.AnovaStats a = anovaStats(categoryData);
		org.apache.commons.math.distribution.FDistribution fdist = new org.apache.commons.math.distribution.FDistributionImpl(a.dfbg , a.dfwg);
		return 1.0 - (fdist.cumulativeProbability(a.F));
	}

	public boolean anovaTest(java.util.Collection<double[]> categoryData, double alpha) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		if ((alpha <= 0) || (alpha > 0.5)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("out of bounds significance level {0}, must be between {1} and {2}", alpha, 0, 0.5);
		} 
		return (anovaPValue(categoryData)) < alpha;
	}

	private org.apache.commons.math.stat.inference.OneWayAnovaImpl.AnovaStats anovaStats(java.util.Collection<double[]> categoryData) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		if ((categoryData.size()) < 2) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("two or more categories required, got {0}", categoryData.size());
		} 
		for (double[] array : categoryData) {
			if ((array.length) <= 1) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("two or more values required in each category, one has {0}", array.length);
			} 
		}
		int dfwg = 0;
		double sswg = 0;
		org.apache.commons.math.stat.descriptive.summary.Sum totsum = new org.apache.commons.math.stat.descriptive.summary.Sum();
		org.apache.commons.math.stat.descriptive.summary.SumOfSquares totsumsq = new org.apache.commons.math.stat.descriptive.summary.SumOfSquares();
		int totnum = 0;
		for (double[] data : categoryData) {
			org.apache.commons.math.stat.descriptive.summary.Sum sum = new org.apache.commons.math.stat.descriptive.summary.Sum();
			org.apache.commons.math.stat.descriptive.summary.SumOfSquares sumsq = new org.apache.commons.math.stat.descriptive.summary.SumOfSquares();
			int num = 0;
			for (int i = 0 ; i < (data.length) ; i++) {
				double val = data[i];
				num++;
				sum.increment(val);
				sumsq.increment(val);
				totnum++;
				totsum.increment(val);
				totsumsq.increment(val);
			}
			dfwg += num - 1;
			double ss = (sumsq.getResult()) - (((sum.getResult()) * (sum.getResult())) / num);
			sswg += ss;
		}
		double sst = (totsumsq.getResult()) - (((totsum.getResult()) * (totsum.getResult())) / totnum);
		double ssbg = sst - sswg;
		int dfbg = (categoryData.size()) - 1;
		double msbg = ssbg / dfbg;
		double mswg = sswg / dfwg;
		double F = msbg / mswg;
		return new org.apache.commons.math.stat.inference.OneWayAnovaImpl.AnovaStats(dfbg , dfwg , F);
	}

	private static class AnovaStats {
		private int dfbg;

		private int dfwg;

		private double F;

		private AnovaStats(int dfbg ,int dfwg ,double F) {
			this.dfbg = dfbg;
			this.dfwg = dfwg;
			this.F = F;
		}
	}
}

