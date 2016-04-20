package org.apache.commons.math.ode.jacobians;


public class FirstOrderIntegratorWithJacobiansTest {
	@org.junit.Test
	public void testLowAccuracyExternalDifferentiation() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator(1.0E-8 , 100.0 , 1.0E-4 , 1.0E-4);
		double hP = 1.0E-12;
		org.apache.commons.math.stat.descriptive.SummaryStatistics residualsP0 = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		org.apache.commons.math.stat.descriptive.SummaryStatistics residualsP1 = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		for (double b = 2.88 ; b < 3.08 ; b += 0.001) {
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobiansTest.Brusselator brusselator = new org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobiansTest.Brusselator(b);
			double[] y = new double[]{ 1.3 , b };
			integ.integrate(brusselator, 0, y, 20.0, y);
			double[] yP = new double[]{ 1.3 , b + hP };
			brusselator.setParameter(0, (b + hP));
			integ.integrate(brusselator, 0, yP, 20.0, yP);
			residualsP0.addValue(((((yP[0]) - (y[0])) / hP) - (brusselator.dYdP0())));
			residualsP1.addValue(((((yP[1]) - (y[1])) / hP) - (brusselator.dYdP1())));
		}
		org.junit.Assert.assertTrue((((residualsP0.getMax()) - (residualsP0.getMin())) > 600));
		org.junit.Assert.assertTrue(((residualsP0.getStandardDeviation()) > 30));
		org.junit.Assert.assertTrue((((residualsP1.getMax()) - (residualsP1.getMin())) > 800));
		org.junit.Assert.assertTrue(((residualsP1.getStandardDeviation()) > 50));
	}

	@org.junit.Test
	public void testHighAccuracyExternalDifferentiation() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator(1.0E-8 , 100.0 , 1.0E-10 , 1.0E-10);
		double hP = 1.0E-12;
		org.apache.commons.math.stat.descriptive.SummaryStatistics residualsP0 = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		org.apache.commons.math.stat.descriptive.SummaryStatistics residualsP1 = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		for (double b = 2.88 ; b < 3.08 ; b += 0.001) {
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobiansTest.Brusselator brusselator = new org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobiansTest.Brusselator(b);
			double[] y = new double[]{ 1.3 , b };
			integ.integrate(brusselator, 0, y, 20.0, y);
			double[] yP = new double[]{ 1.3 , b + hP };
			brusselator.setParameter(0, (b + hP));
			integ.integrate(brusselator, 0, yP, 20.0, yP);
			residualsP0.addValue(((((yP[0]) - (y[0])) / hP) - (brusselator.dYdP0())));
			residualsP1.addValue(((((yP[1]) - (y[1])) / hP) - (brusselator.dYdP1())));
		}
		org.junit.Assert.assertTrue((((residualsP0.getMax()) - (residualsP0.getMin())) > 0.02));
		org.junit.Assert.assertTrue((((residualsP0.getMax()) - (residualsP0.getMin())) < 0.03));
		org.junit.Assert.assertTrue(((residualsP0.getStandardDeviation()) > 0.003));
		org.junit.Assert.assertTrue(((residualsP0.getStandardDeviation()) < 0.004));
		org.junit.Assert.assertTrue((((residualsP1.getMax()) - (residualsP1.getMin())) > 0.04));
		org.junit.Assert.assertTrue((((residualsP1.getMax()) - (residualsP1.getMin())) < 0.05));
		org.junit.Assert.assertTrue(((residualsP1.getStandardDeviation()) > 0.006));
		org.junit.Assert.assertTrue(((residualsP1.getStandardDeviation()) < 0.007));
	}

	@org.junit.Test
	public void testInternalDifferentiation() {
	}

	@org.junit.Test
	public void testAnalyticalDifferentiation() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator(1.0E-8 , 100.0 , 1.0E-4 , 1.0E-4);
		org.apache.commons.math.stat.descriptive.SummaryStatistics residualsP0 = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		org.apache.commons.math.stat.descriptive.SummaryStatistics residualsP1 = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		for (double b = 2.88 ; b < 3.08 ; b += 0.001) {
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobiansTest.Brusselator brusselator = new org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobiansTest.Brusselator(b);
			brusselator.setParameter(0, b);
			double[] z = new double[]{ 1.3 , b };
			double[][] dZdZ0 = new double[2][2];
			double[][] dZdP = new double[2][1];
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians extInt = new org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians(integ , brusselator);
			extInt.setMaxEvaluations(5000);
			extInt.integrate(0, z, new double[][]{ new double[]{ 0.0 } , new double[]{ 1.0 } }, 20.0, z, dZdZ0, dZdP);
			org.junit.Assert.assertEquals(5000, extInt.getMaxEvaluations());
			org.junit.Assert.assertTrue(((extInt.getEvaluations()) > 510));
			org.junit.Assert.assertTrue(((extInt.getEvaluations()) < 610));
			org.junit.Assert.assertEquals(integ.getEvaluations(), extInt.getEvaluations());
			residualsP0.addValue(((dZdP[0][0]) - (brusselator.dYdP0())));
			residualsP1.addValue(((dZdP[1][0]) - (brusselator.dYdP1())));
		}
		org.junit.Assert.assertTrue((((residualsP0.getMax()) - (residualsP0.getMin())) < 0.004));
		org.junit.Assert.assertTrue(((residualsP0.getStandardDeviation()) < 8.0E-4));
		org.junit.Assert.assertTrue((((residualsP1.getMax()) - (residualsP1.getMin())) < 0.005));
		org.junit.Assert.assertTrue(((residualsP1.getStandardDeviation()) < 0.001));
	}

	@org.junit.Test
	public void testFinalResult() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator(1.0E-8 , 100.0 , 1.0E-10 , 1.0E-10);
		double[] y = new double[]{ 0.0 , 1.0 };
		org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobiansTest.Circle circle = new org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobiansTest.Circle(y , 1.0 , 1.0 , 0.1);
		double[][] dydy0 = new double[2][2];
		double[][] dydp = new double[2][3];
		double t = 18 * (java.lang.Math.PI);
		org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians extInt = new org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians(integ , circle);
		extInt.integrate(0, y, circle.exactDyDp(0), t, y, dydy0, dydp);
		for (int i = 0 ; i < (y.length) ; ++i) {
			org.junit.Assert.assertEquals(circle.exactY(t)[i], y[i], 1.0E-10);
		}
		for (int i = 0 ; i < (dydy0.length) ; ++i) {
			for (int j = 0 ; j < (dydy0[i].length) ; ++j) {
				org.junit.Assert.assertEquals(circle.exactDyDy0(t)[i][j], dydy0[i][j], 1.0E-10);
			}
		}
		for (int i = 0 ; i < (dydp.length) ; ++i) {
			for (int j = 0 ; j < (dydp[i].length) ; ++j) {
				org.junit.Assert.assertEquals(circle.exactDyDp(t)[i][j], dydp[i][j], 1.0E-8);
			}
		}
	}

	@org.junit.Test
	public void testStepHandlerResult() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator(1.0E-8 , 100.0 , 1.0E-10 , 1.0E-10);
		double[] y = new double[]{ 0.0 , 1.0 };
		final org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobiansTest.Circle circle = new org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobiansTest.Circle(y , 1.0 , 1.0 , 0.1);
		double[][] dydy0 = new double[2][2];
		double[][] dydp = new double[2][3];
		double t = 18 * (java.lang.Math.PI);
		final org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians extInt = new org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians(integ , circle);
		extInt.addStepHandler(new org.apache.commons.math.ode.jacobians.StepHandlerWithJacobians() {
			public void reset() {
			}

			public boolean requiresDenseOutput() {
				return false;
			}

			public void handleStep(org.apache.commons.math.ode.jacobians.StepInterpolatorWithJacobians interpolator, boolean isLast) throws org.apache.commons.math.ode.DerivativeException {
				double t = interpolator.getCurrentTime();
				double[] y = interpolator.getInterpolatedY();
				double[][] dydy0 = interpolator.getInterpolatedDyDy0();
				double[][] dydp = interpolator.getInterpolatedDyDp();
				org.junit.Assert.assertEquals(interpolator.getPreviousTime(), extInt.getCurrentStepStart(), 1.0E-10);
				org.junit.Assert.assertTrue(((extInt.getCurrentSignedStepsize()) < 0.5));
				for (int i = 0 ; i < (y.length) ; ++i) {
					org.junit.Assert.assertEquals(circle.exactY(t)[i], y[i], 1.0E-10);
				}
				for (int i = 0 ; i < (dydy0.length) ; ++i) {
					for (int j = 0 ; j < (dydy0[i].length) ; ++j) {
						org.junit.Assert.assertEquals(circle.exactDyDy0(t)[i][j], dydy0[i][j], 1.0E-10);
					}
				}
				for (int i = 0 ; i < (dydp.length) ; ++i) {
					for (int j = 0 ; j < (dydp[i].length) ; ++j) {
						org.junit.Assert.assertEquals(circle.exactDyDp(t)[i][j], dydp[i][j], 1.0E-8);
					}
				}
				double[] yDot = interpolator.getInterpolatedYDot();
				double[][] dydy0Dot = interpolator.getInterpolatedDyDy0Dot();
				double[][] dydpDot = interpolator.getInterpolatedDyDpDot();
				for (int i = 0 ; i < (yDot.length) ; ++i) {
					org.junit.Assert.assertEquals(circle.exactYDot(t)[i], yDot[i], 1.0E-11);
				}
				for (int i = 0 ; i < (dydy0Dot.length) ; ++i) {
					for (int j = 0 ; j < (dydy0Dot[i].length) ; ++j) {
						org.junit.Assert.assertEquals(circle.exactDyDy0Dot(t)[i][j], dydy0Dot[i][j], 1.0E-11);
					}
				}
				for (int i = 0 ; i < (dydpDot.length) ; ++i) {
					for (int j = 0 ; j < (dydpDot[i].length) ; ++j) {
						org.junit.Assert.assertEquals(circle.exactDyDpDot(t)[i][j], dydpDot[i][j], 1.0E-9);
					}
				}
			}
		});
		extInt.integrate(0, y, circle.exactDyDp(0), t, y, dydy0, dydp);
	}

	@org.junit.Test
	public void testEventHandler() throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		org.apache.commons.math.ode.FirstOrderIntegrator integ = new org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator(1.0E-8 , 100.0 , 1.0E-10 , 1.0E-10);
		double[] y = new double[]{ 0.0 , 1.0 };
		final org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobiansTest.Circle circle = new org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobiansTest.Circle(y , 1.0 , 1.0 , 0.1);
		double[][] dydy0 = new double[2][2];
		double[][] dydp = new double[2][3];
		double t = 18 * (java.lang.Math.PI);
		final org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians extInt = new org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians(integ , circle);
		extInt.addEventHandler(new org.apache.commons.math.ode.jacobians.EventHandlerWithJacobians() {
			public int eventOccurred(double t, double[] y, double[][] dydy0, double[][] dydp, boolean increasing) {
				org.junit.Assert.assertEquals(0.1, y[1], 1.0E-11);
				org.junit.Assert.assertTrue(!increasing);
				return org.apache.commons.math.ode.jacobians.EventHandlerWithJacobians.STOP;
			}

			public double g(double t, double[] y, double[][] dydy0, double[][] dydp) {
				return (y[1]) - 0.1;
			}

			public void resetState(double t, double[] y, double[][] dydy0, double[][] dydp) {
			}
		}, 10.0, 1.0E-10, 1000);
		double stopTime = extInt.integrate(0, y, circle.exactDyDp(0), t, y, dydy0, dydp);
		org.junit.Assert.assertTrue((stopTime < (5.0 * (java.lang.Math.PI))));
	}

	private static class Brusselator implements org.apache.commons.math.ode.jacobians.ODEWithJacobians , org.apache.commons.math.ode.jacobians.ParameterizedODE {
		private double b;

		public Brusselator(double b) {
			this.b = b;
		}

		public int getDimension() {
			return 2;
		}

		public void setParameter(int i, double p) {
			b = p;
		}

		public int getParametersDimension() {
			return 1;
		}

		public void computeDerivatives(double t, double[] y, double[] yDot) {
			double prod = ((y[0]) * (y[0])) * (y[1]);
			yDot[0] = (1 + prod) - (((b) + 1) * (y[0]));
			yDot[1] = ((b) * (y[0])) - prod;
		}

		public void computeJacobians(double t, double[] y, double[] yDot, double[][] dFdY, double[][] dFdP) {
			double p = (2 * (y[0])) * (y[1]);
			double y02 = (y[0]) * (y[0]);
			dFdY[0][0] = p - (1 + (b));
			dFdY[0][1] = y02;
			dFdY[1][0] = (b) - p;
			dFdY[1][1] = -y02;
			dFdP[0][0] = -(y[0]);
			dFdP[1][0] = y[0];
		}

		public double dYdP0() {
			return (-1088.232716447743) + ((1050.775747149553 + (((-339.012934631828) + (36.52917025056327 * (b))) * (b))) * (b));
		}

		public double dYdP1() {
			return 1502.824469929139 + (((-1438.6974831849952) + ((460.959476642384 - (49.43847385647082 * (b))) * (b))) * (b));
		}
	}

	private static class Circle implements org.apache.commons.math.ode.jacobians.ODEWithJacobians {
		private final double[] y0;

		private double cx;

		private double cy;

		private double omega;

		public Circle(double[] y0 ,double cx ,double cy ,double omega) {
			this.y0 = y0.clone();
			this.cx = cx;
			this.cy = cy;
			this.omega = omega;
		}

		public int getDimension() {
			return 2;
		}

		public int getParametersDimension() {
			return 3;
		}

		public void computeDerivatives(double t, double[] y, double[] yDot) {
			yDot[0] = (omega) * ((cy) - (y[1]));
			yDot[1] = (omega) * ((y[0]) - (cx));
		}

		public void computeJacobians(double t, double[] y, double[] yDot, double[][] dFdY, double[][] dFdP) {
			dFdY[0][0] = 0;
			dFdY[0][1] = -(omega);
			dFdY[1][0] = omega;
			dFdY[1][1] = 0;
			dFdP[0][0] = 0;
			dFdP[0][1] = omega;
			dFdP[0][2] = (cy) - (y[1]);
			dFdP[1][0] = -(omega);
			dFdP[1][1] = 0;
			dFdP[1][2] = (y[0]) - (cx);
		}

		public double[] exactY(double t) {
			double cos = java.lang.Math.cos(((omega) * t));
			double sin = java.lang.Math.sin(((omega) * t));
			double dx0 = (y0[0]) - (cx);
			double dy0 = (y0[1]) - (cy);
			return new double[]{ ((cx) + (cos * dx0)) - (sin * dy0) , ((cy) + (sin * dx0)) + (cos * dy0) };
		}

		public double[][] exactDyDy0(double t) {
			double cos = java.lang.Math.cos(((omega) * t));
			double sin = java.lang.Math.sin(((omega) * t));
			return new double[][]{ new double[]{ cos , -sin } , new double[]{ sin , cos } };
		}

		public double[][] exactDyDp(double t) {
			double cos = java.lang.Math.cos(((omega) * t));
			double sin = java.lang.Math.sin(((omega) * t));
			double dx0 = (y0[0]) - (cx);
			double dy0 = (y0[1]) - (cy);
			return new double[][]{ new double[]{ 1 - cos , sin , (-t) * ((sin * dx0) + (cos * dy0)) } , new double[]{ -sin , 1 - cos , t * ((cos * dx0) - (sin * dy0)) } };
		}

		public double[] exactYDot(double t) {
			double oCos = (omega) * (java.lang.Math.cos(((omega) * t)));
			double oSin = (omega) * (java.lang.Math.sin(((omega) * t)));
			double dx0 = (y0[0]) - (cx);
			double dy0 = (y0[1]) - (cy);
			return new double[]{ ((-oSin) * dx0) - (oCos * dy0) , (oCos * dx0) - (oSin * dy0) };
		}

		public double[][] exactDyDy0Dot(double t) {
			double oCos = (omega) * (java.lang.Math.cos(((omega) * t)));
			double oSin = (omega) * (java.lang.Math.sin(((omega) * t)));
			return new double[][]{ new double[]{ -oSin , -oCos } , new double[]{ oCos , -oSin } };
		}

		public double[][] exactDyDpDot(double t) {
			double cos = java.lang.Math.cos(((omega) * t));
			double sin = java.lang.Math.sin(((omega) * t));
			double oCos = (omega) * cos;
			double oSin = (omega) * sin;
			double dx0 = (y0[0]) - (cx);
			double dy0 = (y0[1]) - (cy);
			return new double[][]{ new double[]{ oSin , oCos , (((-sin) * dx0) - (cos * dy0)) - (t * ((oCos * dx0) - (oSin * dy0))) } , new double[]{ -oCos , oSin , ((cos * dx0) - (sin * dy0)) + (t * (((-oSin) * dx0) - (oCos * dy0))) } };
		}
	}
}

