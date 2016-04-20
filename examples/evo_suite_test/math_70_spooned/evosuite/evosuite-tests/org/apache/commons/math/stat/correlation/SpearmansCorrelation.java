package org.apache.commons.math.stat.correlation;


public class SpearmansCorrelation {
	private final org.apache.commons.math.linear.RealMatrix data;

	private final org.apache.commons.math.stat.ranking.RankingAlgorithm rankingAlgorithm;

	private final org.apache.commons.math.stat.correlation.PearsonsCorrelation rankCorrelation;

	public SpearmansCorrelation(final org.apache.commons.math.linear.RealMatrix dataMatrix ,final org.apache.commons.math.stat.ranking.RankingAlgorithm rankingAlgorithm) {
		this.data = dataMatrix.copy();
		this.rankingAlgorithm = rankingAlgorithm;
		rankTransform(data);
		rankCorrelation = new org.apache.commons.math.stat.correlation.PearsonsCorrelation(data);
	}

	public SpearmansCorrelation(final org.apache.commons.math.linear.RealMatrix dataMatrix) {
		this(dataMatrix, new org.apache.commons.math.stat.ranking.NaturalRanking());
	}

	public SpearmansCorrelation() {
		data = null;
		this.rankingAlgorithm = new org.apache.commons.math.stat.ranking.NaturalRanking();
		rankCorrelation = null;
	}

	public org.apache.commons.math.linear.RealMatrix getCorrelationMatrix() {
		return rankCorrelation.getCorrelationMatrix();
	}

	public org.apache.commons.math.stat.correlation.PearsonsCorrelation getRankCorrelation() {
		return rankCorrelation;
	}

	public org.apache.commons.math.linear.RealMatrix computeCorrelationMatrix(org.apache.commons.math.linear.RealMatrix matrix) {
		org.apache.commons.math.linear.RealMatrix matrixCopy = matrix.copy();
		rankTransform(matrixCopy);
		return new org.apache.commons.math.stat.correlation.PearsonsCorrelation().computeCorrelationMatrix(matrixCopy);
	}

	public org.apache.commons.math.linear.RealMatrix computeCorrelationMatrix(double[][] matrix) {
		return computeCorrelationMatrix(new org.apache.commons.math.linear.BlockRealMatrix(matrix));
	}

	public double correlation(final double[] xArray, final double[] yArray) throws java.lang.IllegalArgumentException {
		if (((xArray.length) == (yArray.length)) && ((xArray.length) > 1)) {
			return new org.apache.commons.math.stat.correlation.PearsonsCorrelation().correlation(rankingAlgorithm.rank(xArray), rankingAlgorithm.rank(yArray));
		} else {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("invalid array dimensions. xArray has size {0}; yArray has {1} elements", xArray.length, yArray.length);
		}
	}

	private void rankTransform(org.apache.commons.math.linear.RealMatrix matrix) {
		for (int i = 0 ; i < (matrix.getColumnDimension()) ; i++) {
			matrix.setColumn(i, rankingAlgorithm.rank(matrix.getColumn(i)));
		}
	}
}

