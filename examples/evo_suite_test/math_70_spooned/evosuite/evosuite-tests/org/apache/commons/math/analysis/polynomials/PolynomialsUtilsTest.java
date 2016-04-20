package org.apache.commons.math.analysis.polynomials;


public class PolynomialsUtilsTest extends junit.framework.TestCase {
	public void testFirstChebyshevPolynomials() {
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createChebyshevPolynomial(3), "-3.0 x + 4.0 x^3");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createChebyshevPolynomial(2), "-1.0 + 2.0 x^2");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createChebyshevPolynomial(1), "x");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createChebyshevPolynomial(0), "1.0");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createChebyshevPolynomial(7), "-7.0 x + 56.0 x^3 - 112.0 x^5 + 64.0 x^7");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createChebyshevPolynomial(6), "-1.0 + 18.0 x^2 - 48.0 x^4 + 32.0 x^6");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createChebyshevPolynomial(5), "5.0 x - 20.0 x^3 + 16.0 x^5");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createChebyshevPolynomial(4), "1.0 - 8.0 x^2 + 8.0 x^4");
	}

	public void testChebyshevBounds() {
		for (int k = 0 ; k < 12 ; ++k) {
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Tk = org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createChebyshevPolynomial(k);
			for (double x = -1.0 ; x <= 1.0 ; x += 0.02) {
				junit.framework.Assert.assertTrue(((k + " ") + (Tk.value(x))), ((java.lang.Math.abs(Tk.value(x))) < (1.0 + 1.0E-12)));
			}
		}
	}

	public void testChebyshevDifferentials() {
		for (int k = 0 ; k < 12 ; ++k) {
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Tk0 = org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createChebyshevPolynomial(k);
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Tk1 = Tk0.polynomialDerivative();
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Tk2 = Tk1.polynomialDerivative();
			org.apache.commons.math.analysis.polynomials.PolynomialFunction g0 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ k * k });
			org.apache.commons.math.analysis.polynomials.PolynomialFunction g1 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 0 , -1 });
			org.apache.commons.math.analysis.polynomials.PolynomialFunction g2 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 1 , 0 , -1 });
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Tk0g0 = Tk0.multiply(g0);
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Tk1g1 = Tk1.multiply(g1);
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Tk2g2 = Tk2.multiply(g2);
			checkNullPolynomial(Tk0g0.add(Tk1g1.add(Tk2g2)));
		}
	}

	public void testFirstHermitePolynomials() {
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createHermitePolynomial(3), "-12.0 x + 8.0 x^3");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createHermitePolynomial(2), "-2.0 + 4.0 x^2");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createHermitePolynomial(1), "2.0 x");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createHermitePolynomial(0), "1.0");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createHermitePolynomial(7), "-1680.0 x + 3360.0 x^3 - 1344.0 x^5 + 128.0 x^7");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createHermitePolynomial(6), "-120.0 + 720.0 x^2 - 480.0 x^4 + 64.0 x^6");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createHermitePolynomial(5), "120.0 x - 160.0 x^3 + 32.0 x^5");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createHermitePolynomial(4), "12.0 - 48.0 x^2 + 16.0 x^4");
	}

	public void testHermiteDifferentials() {
		for (int k = 0 ; k < 12 ; ++k) {
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Hk0 = org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createHermitePolynomial(k);
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Hk1 = Hk0.polynomialDerivative();
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Hk2 = Hk1.polynomialDerivative();
			org.apache.commons.math.analysis.polynomials.PolynomialFunction g0 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 2 * k });
			org.apache.commons.math.analysis.polynomials.PolynomialFunction g1 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 0 , -2 });
			org.apache.commons.math.analysis.polynomials.PolynomialFunction g2 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 1 });
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Hk0g0 = Hk0.multiply(g0);
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Hk1g1 = Hk1.multiply(g1);
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Hk2g2 = Hk2.multiply(g2);
			checkNullPolynomial(Hk0g0.add(Hk1g1.add(Hk2g2)));
		}
	}

	public void testFirstLaguerrePolynomials() {
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLaguerrePolynomial(3), 6L, "6.0 - 18.0 x + 9.0 x^2 - x^3");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLaguerrePolynomial(2), 2L, "2.0 - 4.0 x + x^2");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLaguerrePolynomial(1), 1L, "1.0 - x");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLaguerrePolynomial(0), 1L, "1.0");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLaguerrePolynomial(7), 5040L, ("5040.0 - 35280.0 x + 52920.0 x^2 - 29400.0 x^3" + " + 7350.0 x^4 - 882.0 x^5 + 49.0 x^6 - x^7"));
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLaguerrePolynomial(6), 720L, ("720.0 - 4320.0 x + 5400.0 x^2 - 2400.0 x^3 + 450.0 x^4" + " - 36.0 x^5 + x^6"));
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLaguerrePolynomial(5), 120L, "120.0 - 600.0 x + 600.0 x^2 - 200.0 x^3 + 25.0 x^4 - x^5");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLaguerrePolynomial(4), 24L, "24.0 - 96.0 x + 72.0 x^2 - 16.0 x^3 + x^4");
	}

	public void testLaguerreDifferentials() {
		for (int k = 0 ; k < 12 ; ++k) {
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Lk0 = org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLaguerrePolynomial(k);
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Lk1 = Lk0.polynomialDerivative();
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Lk2 = Lk1.polynomialDerivative();
			org.apache.commons.math.analysis.polynomials.PolynomialFunction g0 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ k });
			org.apache.commons.math.analysis.polynomials.PolynomialFunction g1 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 1 , -1 });
			org.apache.commons.math.analysis.polynomials.PolynomialFunction g2 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 0 , 1 });
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Lk0g0 = Lk0.multiply(g0);
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Lk1g1 = Lk1.multiply(g1);
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Lk2g2 = Lk2.multiply(g2);
			checkNullPolynomial(Lk0g0.add(Lk1g1.add(Lk2g2)));
		}
	}

	public void testFirstLegendrePolynomials() {
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLegendrePolynomial(3), 2L, "-3.0 x + 5.0 x^3");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLegendrePolynomial(2), 2L, "-1.0 + 3.0 x^2");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLegendrePolynomial(1), 1L, "x");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLegendrePolynomial(0), 1L, "1.0");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLegendrePolynomial(7), 16L, "-35.0 x + 315.0 x^3 - 693.0 x^5 + 429.0 x^7");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLegendrePolynomial(6), 16L, "-5.0 + 105.0 x^2 - 315.0 x^4 + 231.0 x^6");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLegendrePolynomial(5), 8L, "15.0 x - 70.0 x^3 + 63.0 x^5");
		checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLegendrePolynomial(4), 8L, "3.0 - 30.0 x^2 + 35.0 x^4");
	}

	public void testLegendreDifferentials() {
		for (int k = 0 ; k < 12 ; ++k) {
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Pk0 = org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLegendrePolynomial(k);
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Pk1 = Pk0.polynomialDerivative();
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Pk2 = Pk1.polynomialDerivative();
			org.apache.commons.math.analysis.polynomials.PolynomialFunction g0 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ k * (k + 1) });
			org.apache.commons.math.analysis.polynomials.PolynomialFunction g1 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 0 , -2 });
			org.apache.commons.math.analysis.polynomials.PolynomialFunction g2 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 1 , 0 , -1 });
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Pk0g0 = Pk0.multiply(g0);
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Pk1g1 = Pk1.multiply(g1);
			org.apache.commons.math.analysis.polynomials.PolynomialFunction Pk2g2 = Pk2.multiply(g2);
			checkNullPolynomial(Pk0g0.add(Pk1g1.add(Pk2g2)));
		}
	}

	public void testHighDegreeLegendre() {
		org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLegendrePolynomial(40);
		double[] l40 = org.apache.commons.math.analysis.polynomials.PolynomialsUtils.createLegendrePolynomial(40).getCoefficients();
		double denominator = 2.74877906944E11;
		double[] numerators = new double[]{ +3.4461632205E10 , -2.82585384081E13 , +3.84787097990295E15 , -2.07785032914759296E17 , +5.9292943321033103E18 , -1.0330148347486655E20 , +1.197358103913226E21 , -9.763073770369382E21 , +5.817164788178423E22 , -2.6006148464797657E23 , +8.883152817712462E23 , -2.3457676271881395E24 , +4.8190226254191124E24 , -7.71043620067058E24 , +9.566652323054239E24 , -9.104813935044723E24 , +6.516550296251767E24 , -3.391858621221954E24 , +1.2113780790078407E24 , -2.6536589497469058E23 , +2.6876802183334046E22 };
		for (int i = 0 ; i < (l40.length) ; ++i) {
			if ((i % 2) == 0) {
				double ci = (numerators[(i / 2)]) / denominator;
				junit.framework.Assert.assertEquals(ci, l40[i], ((java.lang.Math.abs(ci)) * 1.0E-15));
			} else {
				junit.framework.Assert.assertEquals(0.0, l40[i], 0.0);
			}
		}
	}

	private void checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialFunction p, long denominator, java.lang.String reference) {
		org.apache.commons.math.analysis.polynomials.PolynomialFunction q = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ denominator });
		junit.framework.Assert.assertEquals(reference, p.multiply(q).toString());
	}

	private void checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialFunction p, java.lang.String reference) {
		junit.framework.Assert.assertEquals(reference, p.toString());
	}

	private void checkNullPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialFunction p) {
		for (double coefficient : p.getCoefficients()) {
			junit.framework.Assert.assertEquals(0.0, coefficient, 1.0E-13);
		}
	}
}

