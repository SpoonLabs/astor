package org.apache.commons.math.ode.nonstiff;


public class GraggBulirschStoerIntegrator extends org.apache.commons.math.ode.nonstiff.AdaptiveStepsizeIntegrator {
	private static final java.lang.String METHOD_NAME = "Gragg-Bulirsch-Stoer";

	private int maxOrder;

	private int[] sequence;

	private int[] costPerStep;

	private double[] costPerTimeUnit;

	private double[] optimalStep;

	private double[][] coeff;

	private boolean performTest;

	private int maxChecks;

	private int maxIter;

	private double stabilityReduction;

	private double stepControl1;

	private double stepControl2;

	private double stepControl3;

	private double stepControl4;

	private double orderControl1;

	private double orderControl2;

	private boolean denseOutput;

	private boolean useInterpolationError;

	private int mudif;

	public GraggBulirschStoerIntegrator(final double minStep ,final double maxStep ,final double scalAbsoluteTolerance ,final double scalRelativeTolerance) {
		super(METHOD_NAME, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
		denseOutput = (requiresDenseOutput()) || (!(eventsHandlersManager.isEmpty()));
		setStabilityCheck(true, -1, -1, -1);
		setStepsizeControl(-1, -1, -1, -1);
		setOrderControl(-1, -1, -1);
		setInterpolationControl(true, -1);
	}

	public GraggBulirschStoerIntegrator(final double minStep ,final double maxStep ,final double[] vecAbsoluteTolerance ,final double[] vecRelativeTolerance) {
		super(METHOD_NAME, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
		denseOutput = (requiresDenseOutput()) || (!(eventsHandlersManager.isEmpty()));
		setStabilityCheck(true, -1, -1, -1);
		setStepsizeControl(-1, -1, -1, -1);
		setOrderControl(-1, -1, -1);
		setInterpolationControl(true, -1);
	}

	public void setStabilityCheck(final boolean performStabilityCheck, final int maxNumIter, final int maxNumChecks, final double stepsizeReductionFactor) {
		this.performTest = performStabilityCheck;
		this.maxIter = maxNumIter <= 0 ? 2 : maxNumIter;
		this.maxChecks = maxNumChecks <= 0 ? 1 : maxNumChecks;
		if ((stepsizeReductionFactor < 1.0E-4) || (stepsizeReductionFactor > 0.9999)) {
			this.stabilityReduction = 0.5;
		} else {
			this.stabilityReduction = stepsizeReductionFactor;
		}
	}

	public void setStepsizeControl(final double control1, final double control2, final double control3, final double control4) {
		if ((control1 < 1.0E-4) || (control1 > 0.9999)) {
			this.stepControl1 = 0.65;
		} else {
			this.stepControl1 = control1;
		}
		if ((control2 < 1.0E-4) || (control2 > 0.9999)) {
			this.stepControl2 = 0.94;
		} else {
			this.stepControl2 = control2;
		}
		if ((control3 < 1.0E-4) || (control3 > 0.9999)) {
			this.stepControl3 = 0.02;
		} else {
			this.stepControl3 = control3;
		}
		if ((control4 < 1.0001) || (control4 > 999.9)) {
			this.stepControl4 = 4.0;
		} else {
			this.stepControl4 = control4;
		}
	}

	public void setOrderControl(final int maximalOrder, final double control1, final double control2) {
		if ((maximalOrder <= 6) || ((maximalOrder % 2) != 0)) {
			this.maxOrder = 18;
		} 
		if ((control1 < 1.0E-4) || (control1 > 0.9999)) {
			this.orderControl1 = 0.8;
		} else {
			this.orderControl1 = control1;
		}
		if ((control2 < 1.0E-4) || (control2 > 0.9999)) {
			this.orderControl2 = 0.9;
		} else {
			this.orderControl2 = control2;
		}
		initializeArrays();
	}

	@java.lang.Override
	public void addStepHandler(final org.apache.commons.math.ode.sampling.StepHandler handler) {
		super.addStepHandler(handler);
		denseOutput = (requiresDenseOutput()) || (!(eventsHandlersManager.isEmpty()));
		initializeArrays();
	}

	@java.lang.Override
	public void addEventHandler(final org.apache.commons.math.ode.events.EventHandler function, final double maxCheckInterval, final double convergence, final int maxIterationCount) {
		super.addEventHandler(function, maxCheckInterval, convergence, maxIterationCount);
		denseOutput = (requiresDenseOutput()) || (!(eventsHandlersManager.isEmpty()));
		initializeArrays();
	}

	private void initializeArrays() {
		final int size = (maxOrder) / 2;
		if (((sequence) == null) || ((sequence.length) != size)) {
			sequence = new int[size];
			costPerStep = new int[size];
			coeff = new double[size][];
			costPerTimeUnit = new double[size];
			optimalStep = new double[size];
		} 
		if (denseOutput) {
			for (int k = 0 ; k < size ; ++k) {
				sequence[k] = (4 * k) + 2;
			}
		} else {
			for (int k = 0 ; k < size ; ++k) {
				sequence[k] = 2 * (k + 1);
			}
		}
		costPerStep[0] = (sequence[0]) + 1;
		for (int k = 1 ; k < size ; ++k) {
			costPerStep[k] = (costPerStep[(k - 1)]) + (sequence[k]);
		}
		for (int k = 0 ; k < size ; ++k) {
			coeff[k] = k > 0 ? new double[k] : null;
			for (int l = 0 ; l < k ; ++l) {
				final double ratio = ((double)(sequence[k])) / (sequence[((k - l) - 1)]);
				coeff[k][l] = 1.0 / ((ratio * ratio) - 1.0);
			}
		}
	}

	public void setInterpolationControl(final boolean useInterpolationErrorForControl, final int mudifControlParameter) {
		this.useInterpolationError = useInterpolationErrorForControl;
		if ((mudifControlParameter <= 0) || (mudifControlParameter >= 7)) {
			this.mudif = 4;
		} else {
			this.mudif = mudifControlParameter;
		}
	}

	private void rescale(final double[] y1, final double[] y2, final double[] scale) {
		if ((vecAbsoluteTolerance) == null) {
			for (int i = 0 ; i < (scale.length) ; ++i) {
				final double yi = java.lang.Math.max(java.lang.Math.abs(y1[i]), java.lang.Math.abs(y2[i]));
				scale[i] = (scalAbsoluteTolerance) + ((scalRelativeTolerance) * yi);
			}
		} else {
			for (int i = 0 ; i < (scale.length) ; ++i) {
				final double yi = java.lang.Math.max(java.lang.Math.abs(y1[i]), java.lang.Math.abs(y2[i]));
				scale[i] = (vecAbsoluteTolerance[i]) + ((vecRelativeTolerance[i]) * yi);
			}
		}
	}

	private boolean tryStep(final double t0, final double[] y0, final double step, final int k, final double[] scale, final double[][] f, final double[] yMiddle, final double[] yEnd, final double[] yTmp) throws org.apache.commons.math.ode.DerivativeException {
		final int n = sequence[k];
		final double subStep = step / n;
		final double subStep2 = 2 * subStep;
		double t = t0 + subStep;
		for (int i = 0 ; i < (y0.length) ; ++i) {
			yTmp[i] = y0[i];
			yEnd[i] = (y0[i]) + (subStep * (f[0][i]));
		}
		computeDerivatives(t, yEnd, f[1]);
		for (int j = 1 ; j < n ; ++j) {
			if ((2 * j) == n) {
				java.lang.System.arraycopy(yEnd, 0, yMiddle, 0, y0.length);
			} 
			t += subStep;
			for (int i = 0 ; i < (y0.length) ; ++i) {
				final double middle = yEnd[i];
				yEnd[i] = (yTmp[i]) + (subStep2 * (f[j][i]));
				yTmp[i] = middle;
			}
			computeDerivatives(t, yEnd, f[(j + 1)]);
			if (((performTest) && (j <= (maxChecks))) && (k < (maxIter))) {
				double initialNorm = 0.0;
				for (int l = 0 ; l < (y0.length) ; ++l) {
					final double ratio = (f[0][l]) / (scale[l]);
					initialNorm += ratio * ratio;
				}
				double deltaNorm = 0.0;
				for (int l = 0 ; l < (y0.length) ; ++l) {
					final double ratio = ((f[(j + 1)][l]) - (f[0][l])) / (scale[l]);
					deltaNorm += ratio * ratio;
				}
				if (deltaNorm > (4 * (java.lang.Math.max(1.0E-15, initialNorm)))) {
					return false;
				} 
			} 
		}
		for (int i = 0 ; i < (y0.length) ; ++i) {
			yEnd[i] = 0.5 * (((yTmp[i]) + (yEnd[i])) + (subStep * (f[n][i])));
		}
		return true;
	}

	private void extrapolate(final int offset, final int k, final double[][] diag, final double[] last) {
		for (int j = 1 ; j < k ; ++j) {
			for (int i = 0 ; i < (last.length) ; ++i) {
				diag[((k - j) - 1)][i] = (diag[(k - j)][i]) + ((coeff[(k + offset)][(j - 1)]) * ((diag[(k - j)][i]) - (diag[((k - j) - 1)][i])));
			}
		}
		for (int i = 0 ; i < (last.length) ; ++i) {
			last[i] = (diag[0][i]) + ((coeff[(k + offset)][(k - 1)]) * ((diag[0][i]) - (last[i])));
		}
	}

	@java.lang.Override
	public double integrate(final org.apache.commons.math.ode.FirstOrderDifferentialEquations equations, final double t0, final double[] y0, final double t, final double[] y) throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		sanityChecks(equations, t0, y0, t, y);
		setEquations(equations);
		resetEvaluations();
		final boolean forward = t > t0;
		final double[] yDot0 = new double[y0.length];
		final double[] y1 = new double[y0.length];
		final double[] yTmp = new double[y0.length];
		final double[] yTmpDot = new double[y0.length];
		final double[][] diagonal = new double[(sequence.length) - 1][];
		final double[][] y1Diag = new double[(sequence.length) - 1][];
		for (int k = 0 ; k < ((sequence.length) - 1) ; ++k) {
			diagonal[k] = new double[y0.length];
			y1Diag[k] = new double[y0.length];
		}
		final double[][][] fk = new double[sequence.length][][];
		for (int k = 0 ; k < (sequence.length) ; ++k) {
			fk[k] = new double[(sequence[k]) + 1][];
			fk[k][0] = yDot0;
			for (int l = 0 ; l < (sequence[k]) ; ++l) {
				fk[k][(l + 1)] = new double[y0.length];
			}
		}
		if (y != y0) {
			java.lang.System.arraycopy(y0, 0, y, 0, y0.length);
		} 
		double[] yDot1 = null;
		double[][] yMidDots = null;
		if (denseOutput) {
			yDot1 = new double[y0.length];
			yMidDots = new double[1 + (2 * (sequence.length))][];
			for (int j = 0 ; j < (yMidDots.length) ; ++j) {
				yMidDots[j] = new double[y0.length];
			}
		} else {
			yMidDots = new double[1][];
			yMidDots[0] = new double[y0.length];
		}
		final double[] scale = new double[y0.length];
		rescale(y, y, scale);
		final double tol = (vecRelativeTolerance) == null ? scalRelativeTolerance : vecRelativeTolerance[0];
		final double log10R = (java.lang.Math.log(java.lang.Math.max(1.0E-10, tol))) / (java.lang.Math.log(10.0));
		int targetIter = java.lang.Math.max(1, java.lang.Math.min(((sequence.length) - 2), ((int)(java.lang.Math.floor((0.5 - (0.6 * log10R)))))));
		org.apache.commons.math.ode.sampling.AbstractStepInterpolator interpolator = null;
		if ((denseOutput) || (!(eventsHandlersManager.isEmpty()))) {
			interpolator = new org.apache.commons.math.ode.nonstiff.GraggBulirschStoerStepInterpolator(y , yDot0 , y1 , yDot1 , yMidDots , forward);
		} else {
			interpolator = new org.apache.commons.math.ode.sampling.DummyStepInterpolator(y , yDot1 , forward);
		}
		interpolator.storeTime(t0);
		stepStart = t0;
		double hNew = 0;
		double maxError = java.lang.Double.MAX_VALUE;
		boolean previousRejected = false;
		boolean firstTime = true;
		boolean newStep = true;
		boolean lastStep = false;
		boolean firstStepAlreadyComputed = false;
		for (org.apache.commons.math.ode.sampling.StepHandler handler : stepHandlers) {
			handler.reset();
		}
		costPerTimeUnit[0] = 0;
		while (!lastStep) {
			double error;
			boolean reject = false;
			if (newStep) {
				interpolator.shift();
				if (!firstStepAlreadyComputed) {
					computeDerivatives(stepStart, y, yDot0);
				} 
				if (firstTime) {
					hNew = initializeStep(equations, forward, ((2 * targetIter) + 1), scale, stepStart, y, yDot0, yTmp, yTmpDot);
					if (!forward) {
						hNew = -hNew;
					} 
				} 
				newStep = false;
			} 
			stepSize = hNew;
			if ((forward && (((stepStart) + (stepSize)) > t)) || ((!forward) && (((stepStart) + (stepSize)) < t))) {
				stepSize = t - (stepStart);
			} 
			final double nextT = (stepStart) + (stepSize);
			lastStep = forward ? nextT >= t : nextT <= t;
			int k = -1;
			for (boolean loop = true ; loop ; ) {
				++k;
				if (!(tryStep(stepStart, y, stepSize, k, scale, fk[k], (k == 0 ? yMidDots[0] : diagonal[(k - 1)]), (k == 0 ? y1 : y1Diag[(k - 1)]), yTmp))) {
					hNew = java.lang.Math.abs(filterStep(((stepSize) * (stabilityReduction)), forward, false));
					reject = true;
					loop = false;
				} else {
					if (k > 0) {
						extrapolate(0, k, y1Diag, y1);
						rescale(y, y1, scale);
						error = 0;
						for (int j = 0 ; j < (y0.length) ; ++j) {
							final double e = (java.lang.Math.abs(((y1[j]) - (y1Diag[0][j])))) / (scale[j]);
							error += e * e;
						}
						error = java.lang.Math.sqrt((error / (y0.length)));
						if ((error > 1.0E15) || ((k > 1) && (error > maxError))) {
							hNew = java.lang.Math.abs(filterStep(((stepSize) * (stabilityReduction)), forward, false));
							reject = true;
							loop = false;
						} else {
							maxError = java.lang.Math.max((4 * error), 1.0);
							final double exp = 1.0 / ((2 * k) + 1);
							double fac = (stepControl2) / (java.lang.Math.pow((error / (stepControl1)), exp));
							final double pow = java.lang.Math.pow(stepControl3, exp);
							fac = java.lang.Math.max((pow / (stepControl4)), java.lang.Math.min((1 / pow), fac));
							optimalStep[k] = java.lang.Math.abs(filterStep(((stepSize) * fac), forward, true));
							costPerTimeUnit[k] = (costPerStep[k]) / (optimalStep[k]);
							switch (k - targetIter) {
								case -1 :
									if ((targetIter > 1) && (!previousRejected)) {
										if (error <= 1.0) {
											loop = false;
										} else {
											final double ratio = (((double)(sequence[targetIter])) * (sequence[(targetIter + 1)])) / ((sequence[0]) * (sequence[0]));
											if (error > (ratio * ratio)) {
												reject = true;
												loop = false;
												targetIter = k;
												if ((targetIter > 1) && ((costPerTimeUnit[(targetIter - 1)]) < ((orderControl1) * (costPerTimeUnit[targetIter])))) {
													--targetIter;
												} 
												hNew = optimalStep[targetIter];
											} 
										}
									} 
									break;
								case 0 :
									if (error <= 1.0) {
										loop = false;
									} else {
										final double ratio = ((double)(sequence[(k + 1)])) / (sequence[0]);
										if (error > (ratio * ratio)) {
											reject = true;
											loop = false;
											if ((targetIter > 1) && ((costPerTimeUnit[(targetIter - 1)]) < ((orderControl1) * (costPerTimeUnit[targetIter])))) {
												--targetIter;
											} 
											hNew = optimalStep[targetIter];
										} 
									}
									break;
								case 1 :
									if (error > 1.0) {
										reject = true;
										if ((targetIter > 1) && ((costPerTimeUnit[(targetIter - 1)]) < ((orderControl1) * (costPerTimeUnit[targetIter])))) {
											--targetIter;
										} 
										hNew = optimalStep[targetIter];
									} 
									loop = false;
									break;
								default :
									if ((firstTime || lastStep) && (error <= 1.0)) {
										loop = false;
									} 
									break;
							}
						}
					} 
				}
			}
			double hInt = getMaxStep();
			if ((denseOutput) && (!reject)) {
				for (int j = 1 ; j <= k ; ++j) {
					extrapolate(0, j, diagonal, yMidDots[0]);
				}
				computeDerivatives(((stepStart) + (stepSize)), y1, yDot1);
				final int mu = ((2 * k) - (mudif)) + 3;
				for (int l = 0 ; l < mu ; ++l) {
					final int l2 = l / 2;
					double factor = java.lang.Math.pow((0.5 * (sequence[l2])), l);
					int middleIndex = (fk[l2].length) / 2;
					for (int i = 0 ; i < (y0.length) ; ++i) {
						yMidDots[(l + 1)][i] = factor * (fk[l2][(middleIndex + l)][i]);
					}
					for (int j = 1 ; j <= (k - l2) ; ++j) {
						factor = java.lang.Math.pow((0.5 * (sequence[(j + l2)])), l);
						middleIndex = (fk[(l2 + j)].length) / 2;
						for (int i = 0 ; i < (y0.length) ; ++i) {
							diagonal[(j - 1)][i] = factor * (fk[(l2 + j)][(middleIndex + l)][i]);
						}
						extrapolate(l2, j, diagonal, yMidDots[(l + 1)]);
					}
					for (int i = 0 ; i < (y0.length) ; ++i) {
						yMidDots[(l + 1)][i] *= stepSize;
					}
					for (int j = (l + 1) / 2 ; j <= k ; ++j) {
						for (int m = (fk[j].length) - 1 ; m >= (2 * (l + 1)) ; --m) {
							for (int i = 0 ; i < (y0.length) ; ++i) {
								fk[j][m][i] -= fk[j][(m - 2)][i];
							}
						}
					}
				}
				if (mu >= 0) {
					final org.apache.commons.math.ode.nonstiff.GraggBulirschStoerStepInterpolator gbsInterpolator = ((org.apache.commons.math.ode.nonstiff.GraggBulirschStoerStepInterpolator)(interpolator));
					gbsInterpolator.computeCoefficients(mu, stepSize);
					if (useInterpolationError) {
						final double interpError = gbsInterpolator.estimateError(scale);
						hInt = java.lang.Math.abs(((stepSize) / (java.lang.Math.max(java.lang.Math.pow(interpError, (1.0 / (mu + 4))), 0.01))));
						if (interpError > 10.0) {
							hNew = hInt;
							reject = true;
						} 
					} 
					if (!reject) {
						interpolator.storeTime(((stepStart) + (stepSize)));
						if (eventsHandlersManager.evaluateStep(interpolator)) {
							final double dt = (eventsHandlersManager.getEventTime()) - (stepStart);
							if ((java.lang.Math.abs(dt)) > (java.lang.Math.ulp(stepStart))) {
								hNew = java.lang.Math.abs(dt);
								reject = true;
							} 
						} 
					} 
				} 
				if (!reject) {
					firstStepAlreadyComputed = true;
					java.lang.System.arraycopy(yDot1, 0, yDot0, 0, y0.length);
				} 
			} 
			if (!reject) {
				final double nextStep = (stepStart) + (stepSize);
				java.lang.System.arraycopy(y1, 0, y, 0, y0.length);
				eventsHandlersManager.stepAccepted(nextStep, y);
				if (eventsHandlersManager.stop()) {
					lastStep = true;
				} 
				interpolator.storeTime(nextStep);
				for (org.apache.commons.math.ode.sampling.StepHandler handler : stepHandlers) {
					handler.handleStep(interpolator, lastStep);
				}
				stepStart = nextStep;
				if ((eventsHandlersManager.reset(stepStart, y)) && (!lastStep)) {
					firstStepAlreadyComputed = false;
				} 
				int optimalIter;
				if (k == 1) {
					optimalIter = 2;
					if (previousRejected) {
						optimalIter = 1;
					} 
				} else if (k <= targetIter) {
					optimalIter = k;
					if ((costPerTimeUnit[(k - 1)]) < ((orderControl1) * (costPerTimeUnit[k]))) {
						optimalIter = k - 1;
					} else if ((costPerTimeUnit[k]) < ((orderControl2) * (costPerTimeUnit[(k - 1)]))) {
						optimalIter = java.lang.Math.min((k + 1), ((sequence.length) - 2));
					} 
				} else {
					optimalIter = k - 1;
					if ((k > 2) && ((costPerTimeUnit[(k - 2)]) < ((orderControl1) * (costPerTimeUnit[(k - 1)])))) {
						optimalIter = k - 2;
					} 
					if ((costPerTimeUnit[k]) < ((orderControl2) * (costPerTimeUnit[optimalIter]))) {
						optimalIter = java.lang.Math.min(k, ((sequence.length) - 2));
					} 
				}
				if (previousRejected) {
					targetIter = java.lang.Math.min(optimalIter, k);
					hNew = java.lang.Math.min(java.lang.Math.abs(stepSize), optimalStep[targetIter]);
				} else {
					if (optimalIter <= k) {
						hNew = optimalStep[optimalIter];
					} else {
						if ((k < targetIter) && ((costPerTimeUnit[k]) < ((orderControl2) * (costPerTimeUnit[(k - 1)])))) {
							hNew = filterStep((((optimalStep[k]) * (costPerStep[(optimalIter + 1)])) / (costPerStep[k])), forward, false);
						} else {
							hNew = filterStep((((optimalStep[k]) * (costPerStep[optimalIter])) / (costPerStep[k])), forward, false);
						}
					}
					targetIter = optimalIter;
				}
				newStep = true;
			} 
			hNew = java.lang.Math.min(hNew, hInt);
			if (!forward) {
				hNew = -hNew;
			} 
			firstTime = false;
			if (reject) {
				lastStep = false;
				previousRejected = true;
			} else {
				previousRejected = false;
			}
		}
		return stepStart;
	}
}

