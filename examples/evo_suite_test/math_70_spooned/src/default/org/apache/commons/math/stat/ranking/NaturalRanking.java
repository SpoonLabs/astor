package org.apache.commons.math.stat.ranking;


public class NaturalRanking implements org.apache.commons.math.stat.ranking.RankingAlgorithm {
	public static final org.apache.commons.math.stat.ranking.NaNStrategy DEFAULT_NAN_STRATEGY = org.apache.commons.math.stat.ranking.NaNStrategy.MAXIMAL;

	public static final org.apache.commons.math.stat.ranking.TiesStrategy DEFAULT_TIES_STRATEGY = org.apache.commons.math.stat.ranking.TiesStrategy.AVERAGE;

	private final org.apache.commons.math.stat.ranking.NaNStrategy nanStrategy;

	private final org.apache.commons.math.stat.ranking.TiesStrategy tiesStrategy;

	private final org.apache.commons.math.random.RandomData randomData;

	public NaturalRanking() {
		super();
		tiesStrategy = DEFAULT_TIES_STRATEGY;
		nanStrategy = DEFAULT_NAN_STRATEGY;
		randomData = null;
	}

	public NaturalRanking(org.apache.commons.math.stat.ranking.TiesStrategy tiesStrategy) {
		super();
		this.tiesStrategy = tiesStrategy;
		nanStrategy = DEFAULT_NAN_STRATEGY;
		randomData = new org.apache.commons.math.random.RandomDataImpl();
	}

	public NaturalRanking(org.apache.commons.math.stat.ranking.NaNStrategy nanStrategy) {
		super();
		this.nanStrategy = nanStrategy;
		tiesStrategy = DEFAULT_TIES_STRATEGY;
		randomData = null;
	}

	public NaturalRanking(org.apache.commons.math.stat.ranking.NaNStrategy nanStrategy ,org.apache.commons.math.stat.ranking.TiesStrategy tiesStrategy) {
		super();
		this.nanStrategy = nanStrategy;
		this.tiesStrategy = tiesStrategy;
		randomData = new org.apache.commons.math.random.RandomDataImpl();
	}

	public NaturalRanking(org.apache.commons.math.random.RandomGenerator randomGenerator) {
		super();
		this.tiesStrategy = org.apache.commons.math.stat.ranking.TiesStrategy.RANDOM;
		nanStrategy = DEFAULT_NAN_STRATEGY;
		randomData = new org.apache.commons.math.random.RandomDataImpl(randomGenerator);
	}

	public NaturalRanking(org.apache.commons.math.stat.ranking.NaNStrategy nanStrategy ,org.apache.commons.math.random.RandomGenerator randomGenerator) {
		super();
		this.nanStrategy = nanStrategy;
		this.tiesStrategy = org.apache.commons.math.stat.ranking.TiesStrategy.RANDOM;
		randomData = new org.apache.commons.math.random.RandomDataImpl(randomGenerator);
	}

	public org.apache.commons.math.stat.ranking.NaNStrategy getNanStrategy() {
		return nanStrategy;
	}

	public org.apache.commons.math.stat.ranking.TiesStrategy getTiesStrategy() {
		return tiesStrategy;
	}

	public double[] rank(double[] data) {
		org.apache.commons.math.stat.ranking.NaturalRanking.IntDoublePair[] ranks = new org.apache.commons.math.stat.ranking.NaturalRanking.IntDoublePair[data.length];
		for (int i = 0 ; i < (data.length) ; i++) {
			ranks[i] = new org.apache.commons.math.stat.ranking.NaturalRanking.IntDoublePair(data[i] , i);
		}
		java.util.List<java.lang.Integer> nanPositions = null;
		switch (nanStrategy) {
			case MAXIMAL :
				recodeNaNs(ranks, java.lang.Double.POSITIVE_INFINITY);
				break;
			case MINIMAL :
				recodeNaNs(ranks, java.lang.Double.NEGATIVE_INFINITY);
				break;
			case REMOVED :
				ranks = removeNaNs(ranks);
				break;
			case FIXED :
				nanPositions = getNanPositions(ranks);
				break;
			default :
				throw org.apache.commons.math.MathRuntimeException.createInternalError(null);
		}
		java.util.Arrays.sort(ranks);
		double[] out = new double[ranks.length];
		int pos = 1;
		out[ranks[0].getPosition()] = pos;
		java.util.List<java.lang.Integer> tiesTrace = new java.util.ArrayList<java.lang.Integer>();
		tiesTrace.add(ranks[0].getPosition());
		for (int i = 1 ; i < (ranks.length) ; i++) {
			if ((java.lang.Double.compare(ranks[i].getValue(), ranks[(i - 1)].getValue())) > 0) {
				pos = i + 1;
				if ((tiesTrace.size()) > 1) {
					resolveTie(out, tiesTrace);
				} 
				tiesTrace = new java.util.ArrayList<java.lang.Integer>();
				tiesTrace.add(ranks[i].getPosition());
			} else {
				tiesTrace.add(ranks[i].getPosition());
			}
			out[ranks[i].getPosition()] = pos;
		}
		if ((tiesTrace.size()) > 1) {
			resolveTie(out, tiesTrace);
		} 
		if ((nanStrategy) == (org.apache.commons.math.stat.ranking.NaNStrategy.FIXED)) {
			restoreNaNs(out, nanPositions);
		} 
		return out;
	}

	private org.apache.commons.math.stat.ranking.NaturalRanking.IntDoublePair[] removeNaNs(org.apache.commons.math.stat.ranking.NaturalRanking.IntDoublePair[] ranks) {
		if (!(containsNaNs(ranks))) {
			return ranks;
		} 
		org.apache.commons.math.stat.ranking.NaturalRanking.IntDoublePair[] outRanks = new org.apache.commons.math.stat.ranking.NaturalRanking.IntDoublePair[ranks.length];
		int j = 0;
		for (int i = 0 ; i < (ranks.length) ; i++) {
			if (java.lang.Double.isNaN(ranks[i].getValue())) {
				for (int k = i + 1 ; k < (ranks.length) ; k++) {
					ranks[k] = new org.apache.commons.math.stat.ranking.NaturalRanking.IntDoublePair(ranks[k].getValue() , ((ranks[k].getPosition()) - 1));
				}
			} else {
				outRanks[j] = new org.apache.commons.math.stat.ranking.NaturalRanking.IntDoublePair(ranks[i].getValue() , ranks[i].getPosition());
				j++;
			}
		}
		org.apache.commons.math.stat.ranking.NaturalRanking.IntDoublePair[] returnRanks = new org.apache.commons.math.stat.ranking.NaturalRanking.IntDoublePair[j];
		java.lang.System.arraycopy(outRanks, 0, returnRanks, 0, j);
		return returnRanks;
	}

	private void recodeNaNs(org.apache.commons.math.stat.ranking.NaturalRanking.IntDoublePair[] ranks, double value) {
		for (int i = 0 ; i < (ranks.length) ; i++) {
			if (java.lang.Double.isNaN(ranks[i].getValue())) {
				ranks[i] = new org.apache.commons.math.stat.ranking.NaturalRanking.IntDoublePair(value , ranks[i].getPosition());
			} 
		}
	}

	private boolean containsNaNs(org.apache.commons.math.stat.ranking.NaturalRanking.IntDoublePair[] ranks) {
		for (int i = 0 ; i < (ranks.length) ; i++) {
			if (java.lang.Double.isNaN(ranks[i].getValue())) {
				return true;
			} 
		}
		return false;
	}

	private void resolveTie(double[] ranks, java.util.List<java.lang.Integer> tiesTrace) {
		final double c = ranks[tiesTrace.get(0)];
		final int length = tiesTrace.size();
		switch (tiesStrategy) {
			case AVERAGE :
				fill(ranks, tiesTrace, ((((2 * c) + length) - 1) / 2.0));
				break;
			case MAXIMUM :
				fill(ranks, tiesTrace, ((c + length) - 1));
				break;
			case MINIMUM :
				fill(ranks, tiesTrace, c);
				break;
			case RANDOM :
				java.util.Iterator<java.lang.Integer> iterator = tiesTrace.iterator();
				long f = java.lang.Math.round(c);
				while (iterator.hasNext()) {
					ranks[iterator.next()] = randomData.nextLong(f, ((f + length) - 1));
				}
				break;
			case SEQUENTIAL :
				iterator = tiesTrace.iterator();
				f = java.lang.Math.round(c);
				int i = 0;
				while (iterator.hasNext()) {
					ranks[iterator.next()] = f + (i++);
				}
				break;
			default :
				throw org.apache.commons.math.MathRuntimeException.createInternalError(null);
		}
	}

	private void fill(double[] data, java.util.List<java.lang.Integer> tiesTrace, double value) {
		java.util.Iterator<java.lang.Integer> iterator = tiesTrace.iterator();
		while (iterator.hasNext()) {
			data[iterator.next()] = value;
		}
	}

	private void restoreNaNs(double[] ranks, java.util.List<java.lang.Integer> nanPositions) {
		if ((nanPositions.size()) == 0) {
			return ;
		} 
		java.util.Iterator<java.lang.Integer> iterator = nanPositions.iterator();
		while (iterator.hasNext()) {
			ranks[iterator.next().intValue()] = java.lang.Double.NaN;
		}
	}

	private java.util.List<java.lang.Integer> getNanPositions(org.apache.commons.math.stat.ranking.NaturalRanking.IntDoublePair[] ranks) {
		java.util.ArrayList<java.lang.Integer> out = new java.util.ArrayList<java.lang.Integer>();
		for (int i = 0 ; i < (ranks.length) ; i++) {
			if (java.lang.Double.isNaN(ranks[i].getValue())) {
				out.add(java.lang.Integer.valueOf(i));
			} 
		}
		return out;
	}

	private static class IntDoublePair implements java.lang.Comparable<org.apache.commons.math.stat.ranking.NaturalRanking.IntDoublePair> {
		private final double value;

		private final int position;

		public IntDoublePair(double value ,int position) {
			this.value = value;
			this.position = position;
		}

		public int compareTo(org.apache.commons.math.stat.ranking.NaturalRanking.IntDoublePair other) {
			return java.lang.Double.compare(value, other.value);
		}

		public double getValue() {
			return value;
		}

		public int getPosition() {
			return position;
		}
	}
}

