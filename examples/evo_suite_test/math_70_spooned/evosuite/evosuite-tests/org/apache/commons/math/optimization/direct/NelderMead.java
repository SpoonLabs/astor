package org.apache.commons.math.optimization.direct;


public class NelderMead extends org.apache.commons.math.optimization.direct.DirectSearchOptimizer {
	private final double rho;

	private final double khi;

	private final double gamma;

	private final double sigma;

	public NelderMead() {
		this.rho = 1.0;
		this.khi = 2.0;
		this.gamma = 0.5;
		this.sigma = 0.5;
	}

	public NelderMead(final double rho ,final double khi ,final double gamma ,final double sigma) {
		this.rho = rho;
		this.khi = khi;
		this.gamma = gamma;
		this.sigma = sigma;
	}

	@java.lang.Override
	protected void iterateSimplex(final java.util.Comparator<org.apache.commons.math.optimization.RealPointValuePair> comparator) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		incrementIterationsCounter();
		final int n = (simplex.length) - 1;
		final org.apache.commons.math.optimization.RealPointValuePair best = simplex[0];
		final org.apache.commons.math.optimization.RealPointValuePair secondBest = simplex[(n - 1)];
		final org.apache.commons.math.optimization.RealPointValuePair worst = simplex[n];
		final double[] xWorst = worst.getPointRef();
		final double[] centroid = new double[n];
		for (int i = 0 ; i < n ; ++i) {
			final double[] x = simplex[i].getPointRef();
			for (int j = 0 ; j < n ; ++j) {
				centroid[j] += x[j];
			}
		}
		final double scaling = 1.0 / n;
		for (int j = 0 ; j < n ; ++j) {
			centroid[j] *= scaling;
		}
		final double[] xR = new double[n];
		for (int j = 0 ; j < n ; ++j) {
			xR[j] = (centroid[j]) + ((rho) * ((centroid[j]) - (xWorst[j])));
		}
		final org.apache.commons.math.optimization.RealPointValuePair reflected = new org.apache.commons.math.optimization.RealPointValuePair(xR , evaluate(xR) , false);
		if (((comparator.compare(best, reflected)) <= 0) && ((comparator.compare(reflected, secondBest)) < 0)) {
			replaceWorstPoint(reflected, comparator);
		} else {
			if ((comparator.compare(reflected, best)) < 0) {
				final double[] xE = new double[n];
				for (int j = 0 ; j < n ; ++j) {
					xE[j] = (centroid[j]) + ((khi) * ((xR[j]) - (centroid[j])));
				}
				final org.apache.commons.math.optimization.RealPointValuePair expanded = new org.apache.commons.math.optimization.RealPointValuePair(xE , evaluate(xE) , false);
				if ((comparator.compare(expanded, reflected)) < 0) {
					replaceWorstPoint(expanded, comparator);
				} else {
					replaceWorstPoint(reflected, comparator);
				}
			} else {
				if ((comparator.compare(reflected, worst)) < 0) {
					final double[] xC = new double[n];
					for (int j = 0 ; j < n ; ++j) {
						xC[j] = (centroid[j]) + ((gamma) * ((xR[j]) - (centroid[j])));
					}
					final org.apache.commons.math.optimization.RealPointValuePair outContracted = new org.apache.commons.math.optimization.RealPointValuePair(xC , evaluate(xC) , false);
					if ((comparator.compare(outContracted, reflected)) <= 0) {
						replaceWorstPoint(outContracted, comparator);
						return ;
					} 
				} else {
					final double[] xC = new double[n];
					for (int j = 0 ; j < n ; ++j) {
						xC[j] = (centroid[j]) - ((gamma) * ((centroid[j]) - (xWorst[j])));
					}
					final org.apache.commons.math.optimization.RealPointValuePair inContracted = new org.apache.commons.math.optimization.RealPointValuePair(xC , evaluate(xC) , false);
					if ((comparator.compare(inContracted, worst)) < 0) {
						replaceWorstPoint(inContracted, comparator);
						return ;
					} 
				}
				final double[] xSmallest = simplex[0].getPointRef();
				for (int i = 1 ; i < (simplex.length) ; ++i) {
					final double[] x = simplex[i].getPoint();
					for (int j = 0 ; j < n ; ++j) {
						x[j] = (xSmallest[j]) + ((sigma) * ((x[j]) - (xSmallest[j])));
					}
					simplex[i] = new org.apache.commons.math.optimization.RealPointValuePair(x , java.lang.Double.NaN , false);
				}
				evaluateSimplex(comparator);
			}
		}
	}
}

