package org.apache.commons.math.ode.nonstiff;


class DormandPrince853StepInterpolator extends org.apache.commons.math.ode.nonstiff.RungeKuttaStepInterpolator {
	private static final long serialVersionUID = 7152276390558450974L;

	private static final double B_01 = 104257.0 / 1920240.0;

	private static final double B_06 = 3399327.0 / 763840.0;

	private static final double B_07 = 6.6578432E7 / 3.5198415E7;

	private static final double B_08 = (-1.674902723E9) / 2.887164E8;

	private static final double B_09 = 5.4980371265625E13 / 1.76692375811392E14;

	private static final double B_10 = (-734375.0) / 4826304.0;

	private static final double B_11 = 1.71414593E8 / 8.512614E8;

	private static final double B_12 = 137909.0 / 3084480.0;

	private static final double C14 = 1.0 / 10.0;

	private static final double K14_01 = (1.3481885573E10 / 2.4003E11) - (B_01);

	private static final double K14_06 = 0.0 - (B_06);

	private static final double K14_07 = (1.39418837528E11 / 5.49975234375E11) - (B_07);

	private static final double K14_08 = ((-1.1108320068443E13) / 4.51119375E13) - (B_08);

	private static final double K14_09 = ((-1.769651421925959E15) / 1.424938514608E16) - (B_09);

	private static final double K14_10 = (5.7799439E7 / 3.77055E8) - (B_10);

	private static final double K14_11 = (7.93322643029E11 / 9.673425E13) - (B_11);

	private static final double K14_12 = (1.458939311E9 / 1.9278E11) - (B_12);

	private static final double K14_13 = (-4149.0) / 500000.0;

	private static final double C15 = 1.0 / 5.0;

	private static final double K15_01 = (1.595561272731E12 / 5.01202735E13) - (B_01);

	private static final double K15_06 = (9.75183916491E11 / 3.445768803125E13) - (B_06);

	private static final double K15_07 = (3.8492013932672E13 / 7.18912673015625E14) - (B_07);

	private static final double K15_08 = ((-1.114881286517557E15) / 2.02987107675E16) - (B_08);

	private static final double K15_09 = 0.0 - (B_09);

	private static final double K15_10 = 0.0 - (B_10);

	private static final double K15_11 = ((-2.538710946863E12) / 2.343122786125E16) - (B_11);

	private static final double K15_12 = (8.824659001E9 / 2.306671678125E13) - (B_12);

	private static final double K15_13 = (-1.1518334563E10) / 3.38311846125E13;

	private static final double K15_14 = 1.912306948E9 / 1.3532473845E10;

	private static final double C16 = 7.0 / 9.0;

	private static final double K16_01 = ((-1.3613986967E10) / 3.1741908048E10) - (B_01);

	private static final double K16_06 = ((-4.755612631E9) / 1.012344804E9) - (B_06);

	private static final double K16_07 = (4.2939257944576E13 / 5.588559685701E12) - (B_07);

	private static final double K16_08 = (7.7881972900277E13 / 1.9140370552944E13) - (B_08);

	private static final double K16_09 = (2.2719829234375E13 / 6.3689648654052E13) - (B_09);

	private static final double K16_10 = 0.0 - (B_10);

	private static final double K16_11 = 0.0 - (B_11);

	private static final double K16_12 = 0.0 - (B_12);

	private static final double K16_13 = (-1.199007803E9) / 8.57031517296E11;

	private static final double K16_14 = 1.57882067E11 / 5.3564469831E10;

	private static final double K16_15 = (-2.90468882375E11) / 3.1741908048E10;

	private static final double[][] D = new double[][]{ new double[]{ (-1.7751989329E10) / 2.10607656E9 , 4.272954039E9 / 7.53986464E9 , (-1.18476319744E11) / 3.8604839385E10 , 7.55123450731E11 / 3.166577316E11 , 3.6923844612348283E18 / 1.7441304416342505E18 , (-4.612609375E9) / 5.293382976E9 , 2.091772278379E12 / 9.336445866E11 , 2.136624137E9 / 3.38298912E9 , (-126493.0) / 1421424.0 , 9.835E7 / 5419179.0 , (-1.8878125E7) / 2053168.0 , (-1.944542619E9) / 4.38351368E8 } , new double[]{ 3.2941697297E10 / 3.15911484E9 , 4.56696183123E11 / 1.88496616E9 , 1.9132610714624E13 / 1.15814518155E11 , (-1.77904688592943E14) / 4.749865974E11 , (-4.8211399418367652E18) / 2.18016305204281312E17 , 3.0702015625E10 / 3.970037232E9 , (-8.5916079474274E13) / 2.8009337598E12 , (-5.919468007E9) / 6.3431046E8 , 2479159.0 / 157936.0 , (-1.875E7) / 602131.0 , (-1.9203125E7) / 2053168.0 , 1.5700361463E10 / 4.38351368E8 } , new double[]{ 1.2627015655E10 / 6.31822968E8 , (-7.2955222965E10) / 1.88496616E8 , (-1.314574495232E13) / 6.9488710893E10 , 3.0084216194513E13 / 5.6998391688E10 , (-2.9685876100664064E17) / 2.5648977082856624E16 , 5.69140625E8 / 8.2709109E7 , (-1.8684190637E10) / 1.8672891732E10 , 6.9644045E7 / 8.9549712E7 , (-1.1847025E7) / 4264272.0 , (-9.7865E8) / 1.6257537E7 , 5.19371875E8 / 6159504.0 , 5.256837225E9 / 4.38351368E8 } , new double[]{ (-4.50944925E8) / 1.7550638E7 , (-1.4532122925E10) / 9.4248308E7 , (-5.958769664E11) / 2.573655959E9 , 1.88748653015E11 / 5.27762886E8 , 2.5454854581152343E18 / 2.7252038150535164E16 , (-1.376953125E9) / 3.6759604E7 , 5.3995596795E10 / 5.18691437E8 , 2.10311225E8 / 7047894.0 , (-1718875.0) / 39484.0 , 5.8E7 / 602131.0 , (-1546875.0) / 39484.0 , (-1.262172375E9) / 8429834.0 } };

	private double[][] yDotKLast;

	private double[][] v;

	private boolean vectorsInitialized;

	public DormandPrince853StepInterpolator() {
		super();
		yDotKLast = null;
		v = null;
		vectorsInitialized = false;
	}

	public DormandPrince853StepInterpolator(final org.apache.commons.math.ode.nonstiff.DormandPrince853StepInterpolator interpolator) {
		super(interpolator);
		if ((interpolator.currentState) == null) {
			yDotKLast = null;
			v = null;
			vectorsInitialized = false;
		} else {
			final int dimension = interpolator.currentState.length;
			yDotKLast = new double[3][];
			for (int k = 0 ; k < (yDotKLast.length) ; ++k) {
				yDotKLast[k] = new double[dimension];
				java.lang.System.arraycopy(interpolator.yDotKLast[k], 0, yDotKLast[k], 0, dimension);
			}
			v = new double[7][];
			for (int k = 0 ; k < (v.length) ; ++k) {
				v[k] = new double[dimension];
				java.lang.System.arraycopy(interpolator.v[k], 0, v[k], 0, dimension);
			}
			vectorsInitialized = interpolator.vectorsInitialized;
		}
	}

	@java.lang.Override
	protected org.apache.commons.math.ode.sampling.StepInterpolator doCopy() {
		return new org.apache.commons.math.ode.nonstiff.DormandPrince853StepInterpolator(this);
	}

	@java.lang.Override
	public void reinitialize(final org.apache.commons.math.ode.AbstractIntegrator integrator, final double[] y, final double[][] yDotK, final boolean forward) {
		super.reinitialize(integrator, y, yDotK, forward);
		final int dimension = currentState.length;
		yDotKLast = new double[3][];
		for (int k = 0 ; k < (yDotKLast.length) ; ++k) {
			yDotKLast[k] = new double[dimension];
		}
		v = new double[7][];
		for (int k = 0 ; k < (v.length) ; ++k) {
			v[k] = new double[dimension];
		}
		vectorsInitialized = false;
	}

	@java.lang.Override
	public void storeTime(final double t) {
		super.storeTime(t);
		vectorsInitialized = false;
	}

	@java.lang.Override
	protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) throws org.apache.commons.math.ode.DerivativeException {
		if (!(vectorsInitialized)) {
			if ((v) == null) {
				v = new double[7][];
				for (int k = 0 ; k < 7 ; ++k) {
					v[k] = new double[interpolatedState.length];
				}
			} 
			finalizeStep();
			for (int i = 0 ; i < (interpolatedState.length) ; ++i) {
				final double yDot1 = yDotK[0][i];
				final double yDot6 = yDotK[5][i];
				final double yDot7 = yDotK[6][i];
				final double yDot8 = yDotK[7][i];
				final double yDot9 = yDotK[8][i];
				final double yDot10 = yDotK[9][i];
				final double yDot11 = yDotK[10][i];
				final double yDot12 = yDotK[11][i];
				final double yDot13 = yDotK[12][i];
				final double yDot14 = yDotKLast[0][i];
				final double yDot15 = yDotKLast[1][i];
				final double yDot16 = yDotKLast[2][i];
				v[0][i] = ((((((((B_01) * yDot1) + ((B_06) * yDot6)) + ((B_07) * yDot7)) + ((B_08) * yDot8)) + ((B_09) * yDot9)) + ((B_10) * yDot10)) + ((B_11) * yDot11)) + ((B_12) * yDot12);
				v[1][i] = yDot1 - (v[0][i]);
				v[2][i] = ((v[0][i]) - (v[1][i])) - (yDotK[12][i]);
				for (int k = 0 ; k < (D.length) ; ++k) {
					v[(k + 3)][i] = ((((((((((((D[k][0]) * yDot1) + ((D[k][1]) * yDot6)) + ((D[k][2]) * yDot7)) + ((D[k][3]) * yDot8)) + ((D[k][4]) * yDot9)) + ((D[k][5]) * yDot10)) + ((D[k][6]) * yDot11)) + ((D[k][7]) * yDot12)) + ((D[k][8]) * yDot13)) + ((D[k][9]) * yDot14)) + ((D[k][10]) * yDot15)) + ((D[k][11]) * yDot16);
				}
			}
			vectorsInitialized = true;
		} 
		final double eta = 1 - theta;
		final double twoTheta = 2 * theta;
		final double theta2 = theta * theta;
		final double dot1 = 1 - twoTheta;
		final double dot2 = theta * (2 - (3 * theta));
		final double dot3 = twoTheta * (1 + (theta * (twoTheta - 3)));
		final double dot4 = theta2 * (3 + (theta * ((5 * theta) - 8)));
		final double dot5 = theta2 * (3 + (theta * ((-12) + (theta * (15 - (6 * theta))))));
		final double dot6 = (theta2 * theta) * (4 + (theta * ((-15) + (theta * (18 - (7 * theta))))));
		for (int i = 0 ; i < (interpolatedState.length) ; ++i) {
			interpolatedState[i] = (currentState[i]) - (oneMinusThetaH * ((v[0][i]) - (theta * ((v[1][i]) + (theta * ((v[2][i]) + (eta * ((v[3][i]) + (theta * ((v[4][i]) + (eta * ((v[5][i]) + (theta * (v[6][i]))))))))))))));
			interpolatedDerivatives[i] = ((((((v[0][i]) + (dot1 * (v[1][i]))) + (dot2 * (v[2][i]))) + (dot3 * (v[3][i]))) + (dot4 * (v[4][i]))) + (dot5 * (v[5][i]))) + (dot6 * (v[6][i]));
		}
	}

	@java.lang.Override
	protected void doFinalize() throws org.apache.commons.math.ode.DerivativeException {
		if ((currentState) == null) {
			return ;
		} 
		double s;
		final double[] yTmp = new double[currentState.length];
		for (int j = 0 ; j < (currentState.length) ; ++j) {
			s = (((((((((K14_01) * (yDotK[0][j])) + ((K14_06) * (yDotK[5][j]))) + ((K14_07) * (yDotK[6][j]))) + ((K14_08) * (yDotK[7][j]))) + ((K14_09) * (yDotK[8][j]))) + ((K14_10) * (yDotK[9][j]))) + ((K14_11) * (yDotK[10][j]))) + ((K14_12) * (yDotK[11][j]))) + ((K14_13) * (yDotK[12][j]));
			yTmp[j] = (currentState[j]) + ((h) * s);
		}
		integrator.computeDerivatives(((previousTime) + ((C14) * (h))), yTmp, yDotKLast[0]);
		for (int j = 0 ; j < (currentState.length) ; ++j) {
			s = ((((((((((K15_01) * (yDotK[0][j])) + ((K15_06) * (yDotK[5][j]))) + ((K15_07) * (yDotK[6][j]))) + ((K15_08) * (yDotK[7][j]))) + ((K15_09) * (yDotK[8][j]))) + ((K15_10) * (yDotK[9][j]))) + ((K15_11) * (yDotK[10][j]))) + ((K15_12) * (yDotK[11][j]))) + ((K15_13) * (yDotK[12][j]))) + ((K15_14) * (yDotKLast[0][j]));
			yTmp[j] = (currentState[j]) + ((h) * s);
		}
		integrator.computeDerivatives(((previousTime) + ((C15) * (h))), yTmp, yDotKLast[1]);
		for (int j = 0 ; j < (currentState.length) ; ++j) {
			s = (((((((((((K16_01) * (yDotK[0][j])) + ((K16_06) * (yDotK[5][j]))) + ((K16_07) * (yDotK[6][j]))) + ((K16_08) * (yDotK[7][j]))) + ((K16_09) * (yDotK[8][j]))) + ((K16_10) * (yDotK[9][j]))) + ((K16_11) * (yDotK[10][j]))) + ((K16_12) * (yDotK[11][j]))) + ((K16_13) * (yDotK[12][j]))) + ((K16_14) * (yDotKLast[0][j]))) + ((K16_15) * (yDotKLast[1][j]));
			yTmp[j] = (currentState[j]) + ((h) * s);
		}
		integrator.computeDerivatives(((previousTime) + ((C16) * (h))), yTmp, yDotKLast[2]);
	}

	@java.lang.Override
	public void writeExternal(final java.io.ObjectOutput out) throws java.io.IOException {
		try {
			finalizeStep();
		} catch (org.apache.commons.math.ode.DerivativeException e) {
			throw org.apache.commons.math.MathRuntimeException.createIOException(e);
		}
		final int dimension = (currentState) == null ? -1 : currentState.length;
		out.writeInt(dimension);
		for (int i = 0 ; i < dimension ; ++i) {
			out.writeDouble(yDotKLast[0][i]);
			out.writeDouble(yDotKLast[1][i]);
			out.writeDouble(yDotKLast[2][i]);
		}
		super.writeExternal(out);
	}

	@java.lang.Override
	public void readExternal(final java.io.ObjectInput in) throws java.io.IOException {
		yDotKLast = new double[3][];
		final int dimension = in.readInt();
		yDotKLast[0] = dimension < 0 ? null : new double[dimension];
		yDotKLast[1] = dimension < 0 ? null : new double[dimension];
		yDotKLast[2] = dimension < 0 ? null : new double[dimension];
		for (int i = 0 ; i < dimension ; ++i) {
			yDotKLast[0][i] = in.readDouble();
			yDotKLast[1][i] = in.readDouble();
			yDotKLast[2][i] = in.readDouble();
		}
		super.readExternal(in);
	}
}

