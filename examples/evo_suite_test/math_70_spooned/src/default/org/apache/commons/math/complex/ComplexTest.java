package org.apache.commons.math.complex;


public class ComplexTest extends junit.framework.TestCase {
	private double inf = java.lang.Double.POSITIVE_INFINITY;

	private double neginf = java.lang.Double.NEGATIVE_INFINITY;

	private double nan = java.lang.Double.NaN;

	private double pi = java.lang.Math.PI;

	private org.apache.commons.math.complex.Complex oneInf = new org.apache.commons.math.complex.Complex(1 , inf);

	private org.apache.commons.math.complex.Complex oneNegInf = new org.apache.commons.math.complex.Complex(1 , neginf);

	private org.apache.commons.math.complex.Complex infOne = new org.apache.commons.math.complex.Complex(inf , 1);

	private org.apache.commons.math.complex.Complex infZero = new org.apache.commons.math.complex.Complex(inf , 0);

	private org.apache.commons.math.complex.Complex infNaN = new org.apache.commons.math.complex.Complex(inf , nan);

	private org.apache.commons.math.complex.Complex infNegInf = new org.apache.commons.math.complex.Complex(inf , neginf);

	private org.apache.commons.math.complex.Complex infInf = new org.apache.commons.math.complex.Complex(inf , inf);

	private org.apache.commons.math.complex.Complex negInfInf = new org.apache.commons.math.complex.Complex(neginf , inf);

	private org.apache.commons.math.complex.Complex negInfZero = new org.apache.commons.math.complex.Complex(neginf , 0);

	private org.apache.commons.math.complex.Complex negInfOne = new org.apache.commons.math.complex.Complex(neginf , 1);

	private org.apache.commons.math.complex.Complex negInfNaN = new org.apache.commons.math.complex.Complex(neginf , nan);

	private org.apache.commons.math.complex.Complex negInfNegInf = new org.apache.commons.math.complex.Complex(neginf , neginf);

	private org.apache.commons.math.complex.Complex oneNaN = new org.apache.commons.math.complex.Complex(1 , nan);

	private org.apache.commons.math.complex.Complex zeroInf = new org.apache.commons.math.complex.Complex(0 , inf);

	private org.apache.commons.math.complex.Complex zeroNaN = new org.apache.commons.math.complex.Complex(0 , nan);

	private org.apache.commons.math.complex.Complex nanInf = new org.apache.commons.math.complex.Complex(nan , inf);

	private org.apache.commons.math.complex.Complex nanNegInf = new org.apache.commons.math.complex.Complex(nan , neginf);

	private org.apache.commons.math.complex.Complex nanZero = new org.apache.commons.math.complex.Complex(nan , 0);

	public void testConstructor() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		junit.framework.Assert.assertEquals(3.0, z.getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(4.0, z.getImaginary(), 1.0E-5);
	}

	public void testConstructorNaN() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(3.0 , java.lang.Double.NaN);
		junit.framework.Assert.assertTrue(z.isNaN());
		z = new org.apache.commons.math.complex.Complex(nan , 4.0);
		junit.framework.Assert.assertTrue(z.isNaN());
		z = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		junit.framework.Assert.assertFalse(z.isNaN());
	}

	public void testAbs() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		junit.framework.Assert.assertEquals(5.0, z.abs(), 1.0E-5);
	}

	public void testAbsNaN() {
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(org.apache.commons.math.complex.Complex.NaN.abs()));
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(inf , nan);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(z.abs()));
	}

	public void testAbsInfinite() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(inf , 0);
		junit.framework.Assert.assertEquals(inf, z.abs(), 0);
		z = new org.apache.commons.math.complex.Complex(0 , neginf);
		junit.framework.Assert.assertEquals(inf, z.abs(), 0);
		z = new org.apache.commons.math.complex.Complex(inf , neginf);
		junit.framework.Assert.assertEquals(inf, z.abs(), 0);
	}

	public void testAdd() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		org.apache.commons.math.complex.Complex y = new org.apache.commons.math.complex.Complex(5.0 , 6.0);
		org.apache.commons.math.complex.Complex z = x.add(y);
		junit.framework.Assert.assertEquals(8.0, z.getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(10.0, z.getImaginary(), 1.0E-5);
	}

	public void testAddNaN() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		org.apache.commons.math.complex.Complex z = x.add(org.apache.commons.math.complex.Complex.NaN);
		junit.framework.Assert.assertTrue(z.isNaN());
		z = new org.apache.commons.math.complex.Complex(1 , nan);
		org.apache.commons.math.complex.Complex w = x.add(z);
		junit.framework.Assert.assertEquals(w.getReal(), 4.0, 0);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(w.getImaginary()));
	}

	public void testAddInfinite() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(1 , 1);
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(inf , 0);
		org.apache.commons.math.complex.Complex w = x.add(z);
		junit.framework.Assert.assertEquals(w.getImaginary(), 1, 0);
		junit.framework.Assert.assertEquals(inf, w.getReal(), 0);
		x = new org.apache.commons.math.complex.Complex(neginf , 0);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(x.add(z).getReal()));
	}

	public void testConjugate() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		org.apache.commons.math.complex.Complex z = x.conjugate();
		junit.framework.Assert.assertEquals(3.0, z.getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(-4.0, z.getImaginary(), 1.0E-5);
	}

	public void testConjugateNaN() {
		org.apache.commons.math.complex.Complex z = org.apache.commons.math.complex.Complex.NaN.conjugate();
		junit.framework.Assert.assertTrue(z.isNaN());
	}

	public void testConjugateInfiinite() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(0 , inf);
		junit.framework.Assert.assertEquals(neginf, z.conjugate().getImaginary(), 0);
		z = new org.apache.commons.math.complex.Complex(0 , neginf);
		junit.framework.Assert.assertEquals(inf, z.conjugate().getImaginary(), 0);
	}

	public void testDivide() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		org.apache.commons.math.complex.Complex y = new org.apache.commons.math.complex.Complex(5.0 , 6.0);
		org.apache.commons.math.complex.Complex z = x.divide(y);
		junit.framework.Assert.assertEquals((39.0 / 61.0), z.getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals((2.0 / 61.0), z.getImaginary(), 1.0E-5);
	}

	public void testDivideReal() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(2.0 , 3.0);
		org.apache.commons.math.complex.Complex y = new org.apache.commons.math.complex.Complex(2.0 , 0.0);
		junit.framework.Assert.assertEquals(new org.apache.commons.math.complex.Complex(1.0 , 1.5), x.divide(y));
	}

	public void testDivideImaginary() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(2.0 , 3.0);
		org.apache.commons.math.complex.Complex y = new org.apache.commons.math.complex.Complex(0.0 , 2.0);
		junit.framework.Assert.assertEquals(new org.apache.commons.math.complex.Complex(1.5 , -1.0), x.divide(y));
	}

	public void testDivideInfinite() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3 , 4);
		org.apache.commons.math.complex.Complex w = new org.apache.commons.math.complex.Complex(neginf , inf);
		junit.framework.Assert.assertTrue(x.divide(w).equals(org.apache.commons.math.complex.Complex.ZERO));
		org.apache.commons.math.complex.Complex z = w.divide(x);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(z.getReal()));
		junit.framework.Assert.assertEquals(inf, z.getImaginary(), 0);
		w = new org.apache.commons.math.complex.Complex(inf , inf);
		z = w.divide(x);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(z.getImaginary()));
		junit.framework.Assert.assertEquals(inf, z.getReal(), 0);
		w = new org.apache.commons.math.complex.Complex(1 , inf);
		z = w.divide(w);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(z.getReal()));
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(z.getImaginary()));
	}

	public void testDivideZero() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		org.apache.commons.math.complex.Complex z = x.divide(org.apache.commons.math.complex.Complex.ZERO);
		junit.framework.Assert.assertEquals(z, org.apache.commons.math.complex.Complex.NaN);
	}

	public void testDivideNaN() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		org.apache.commons.math.complex.Complex z = x.divide(org.apache.commons.math.complex.Complex.NaN);
		junit.framework.Assert.assertTrue(z.isNaN());
	}

	public void testDivideNaNInf() {
		org.apache.commons.math.complex.Complex z = oneInf.divide(org.apache.commons.math.complex.Complex.ONE);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(z.getReal()));
		junit.framework.Assert.assertEquals(inf, z.getImaginary(), 0);
		z = negInfNegInf.divide(oneNaN);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(z.getReal()));
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(z.getImaginary()));
		z = negInfInf.divide(org.apache.commons.math.complex.Complex.ONE);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(z.getReal()));
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(z.getImaginary()));
	}

	public void testMultiply() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		org.apache.commons.math.complex.Complex y = new org.apache.commons.math.complex.Complex(5.0 , 6.0);
		org.apache.commons.math.complex.Complex z = x.multiply(y);
		junit.framework.Assert.assertEquals(-9.0, z.getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(38.0, z.getImaginary(), 1.0E-5);
	}

	public void testMultiplyNaN() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		org.apache.commons.math.complex.Complex z = x.multiply(org.apache.commons.math.complex.Complex.NaN);
		junit.framework.Assert.assertTrue(z.isNaN());
	}

	public void testMultiplyNaNInf() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(1 , 1);
		org.apache.commons.math.complex.Complex w = z.multiply(infOne);
		junit.framework.Assert.assertEquals(w.getReal(), inf, 0);
		junit.framework.Assert.assertEquals(w.getImaginary(), inf, 0);
		junit.framework.Assert.assertTrue(new org.apache.commons.math.complex.Complex(1 , 0).multiply(infInf).equals(org.apache.commons.math.complex.Complex.INF));
		junit.framework.Assert.assertTrue(new org.apache.commons.math.complex.Complex(-1 , 0).multiply(infInf).equals(org.apache.commons.math.complex.Complex.INF));
		junit.framework.Assert.assertTrue(new org.apache.commons.math.complex.Complex(1 , 0).multiply(negInfZero).equals(org.apache.commons.math.complex.Complex.INF));
		w = oneInf.multiply(oneNegInf);
		junit.framework.Assert.assertEquals(w.getReal(), inf, 0);
		junit.framework.Assert.assertEquals(w.getImaginary(), inf, 0);
		w = negInfNegInf.multiply(oneNaN);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(w.getReal()));
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(w.getImaginary()));
	}

	public void testScalarMultiply() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		double y = 2.0;
		org.apache.commons.math.complex.Complex z = x.multiply(y);
		junit.framework.Assert.assertEquals(6.0, z.getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(8.0, z.getImaginary(), 1.0E-5);
	}

	public void testScalarMultiplyNaN() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		org.apache.commons.math.complex.Complex z = x.multiply(java.lang.Double.NaN);
		junit.framework.Assert.assertTrue(z.isNaN());
	}

	public void testScalarMultiplyInf() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(1 , 1);
		org.apache.commons.math.complex.Complex w = z.multiply(java.lang.Double.POSITIVE_INFINITY);
		junit.framework.Assert.assertEquals(w.getReal(), inf, 0);
		junit.framework.Assert.assertEquals(w.getImaginary(), inf, 0);
		w = z.multiply(java.lang.Double.NEGATIVE_INFINITY);
		junit.framework.Assert.assertEquals(w.getReal(), inf, 0);
		junit.framework.Assert.assertEquals(w.getImaginary(), inf, 0);
	}

	public void testNegate() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		org.apache.commons.math.complex.Complex z = x.negate();
		junit.framework.Assert.assertEquals(-3.0, z.getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(-4.0, z.getImaginary(), 1.0E-5);
	}

	public void testNegateNaN() {
		org.apache.commons.math.complex.Complex z = org.apache.commons.math.complex.Complex.NaN.negate();
		junit.framework.Assert.assertTrue(z.isNaN());
	}

	public void testSubtract() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		org.apache.commons.math.complex.Complex y = new org.apache.commons.math.complex.Complex(5.0 , 6.0);
		org.apache.commons.math.complex.Complex z = x.subtract(y);
		junit.framework.Assert.assertEquals(-2.0, z.getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(-2.0, z.getImaginary(), 1.0E-5);
	}

	public void testSubtractNaN() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		org.apache.commons.math.complex.Complex z = x.subtract(org.apache.commons.math.complex.Complex.NaN);
		junit.framework.Assert.assertTrue(z.isNaN());
	}

	public void testEqualsNull() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		junit.framework.Assert.assertFalse(x.equals(null));
	}

	public void testEqualsClass() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		junit.framework.Assert.assertFalse(x.equals(this));
	}

	public void testEqualsSame() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		junit.framework.Assert.assertTrue(x.equals(x));
	}

	public void testEqualsTrue() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		org.apache.commons.math.complex.Complex y = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		junit.framework.Assert.assertTrue(x.equals(y));
	}

	public void testEqualsRealDifference() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(0.0 , 0.0);
		org.apache.commons.math.complex.Complex y = new org.apache.commons.math.complex.Complex((0.0 + (java.lang.Double.MIN_VALUE)) , 0.0);
		junit.framework.Assert.assertFalse(x.equals(y));
	}

	public void testEqualsImaginaryDifference() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(0.0 , 0.0);
		org.apache.commons.math.complex.Complex y = new org.apache.commons.math.complex.Complex(0.0 , (0.0 + (java.lang.Double.MIN_VALUE)));
		junit.framework.Assert.assertFalse(x.equals(y));
	}

	public void testEqualsNaN() {
		org.apache.commons.math.complex.Complex realNaN = new org.apache.commons.math.complex.Complex(java.lang.Double.NaN , 0.0);
		org.apache.commons.math.complex.Complex imaginaryNaN = new org.apache.commons.math.complex.Complex(0.0 , java.lang.Double.NaN);
		org.apache.commons.math.complex.Complex complexNaN = org.apache.commons.math.complex.Complex.NaN;
		junit.framework.Assert.assertTrue(realNaN.equals(imaginaryNaN));
		junit.framework.Assert.assertTrue(imaginaryNaN.equals(complexNaN));
		junit.framework.Assert.assertTrue(realNaN.equals(complexNaN));
	}

	public void testHashCode() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(0.0 , 0.0);
		org.apache.commons.math.complex.Complex y = new org.apache.commons.math.complex.Complex(0.0 , (0.0 + (java.lang.Double.MIN_VALUE)));
		junit.framework.Assert.assertFalse(((x.hashCode()) == (y.hashCode())));
		y = new org.apache.commons.math.complex.Complex((0.0 + (java.lang.Double.MIN_VALUE)) , 0.0);
		junit.framework.Assert.assertFalse(((x.hashCode()) == (y.hashCode())));
		org.apache.commons.math.complex.Complex realNaN = new org.apache.commons.math.complex.Complex(java.lang.Double.NaN , 0.0);
		org.apache.commons.math.complex.Complex imaginaryNaN = new org.apache.commons.math.complex.Complex(0.0 , java.lang.Double.NaN);
		junit.framework.Assert.assertEquals(realNaN.hashCode(), imaginaryNaN.hashCode());
		junit.framework.Assert.assertEquals(imaginaryNaN.hashCode(), org.apache.commons.math.complex.Complex.NaN.hashCode());
	}

	public void testAcos() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(3 , 4);
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(0.936812 , -2.30551);
		org.apache.commons.math.TestUtils.assertEquals(expected, z.acos(), 1.0E-5);
		org.apache.commons.math.TestUtils.assertEquals(new org.apache.commons.math.complex.Complex(java.lang.Math.acos(0) , 0), org.apache.commons.math.complex.Complex.ZERO.acos(), 1.0E-12);
	}

	public void testAcosInf() {
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, oneInf.acos());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, oneNegInf.acos());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infOne.acos());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfOne.acos());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infInf.acos());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infNegInf.acos());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfInf.acos());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfNegInf.acos());
	}

	public void testAcosNaN() {
		junit.framework.Assert.assertTrue(org.apache.commons.math.complex.Complex.NaN.acos().isNaN());
	}

	public void testAsin() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(3 , 4);
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(0.633984 , 2.30551);
		org.apache.commons.math.TestUtils.assertEquals(expected, z.asin(), 1.0E-5);
	}

	public void testAsinNaN() {
		junit.framework.Assert.assertTrue(org.apache.commons.math.complex.Complex.NaN.asin().isNaN());
	}

	public void testAsinInf() {
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, oneInf.asin());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, oneNegInf.asin());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infOne.asin());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfOne.asin());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infInf.asin());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infNegInf.asin());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfInf.asin());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfNegInf.asin());
	}

	public void testAtan() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(3 , 4);
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(1.44831 , 0.158997);
		org.apache.commons.math.TestUtils.assertEquals(expected, z.atan(), 1.0E-5);
	}

	public void testAtanInf() {
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, oneInf.atan());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, oneNegInf.atan());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infOne.atan());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfOne.atan());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infInf.atan());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infNegInf.atan());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfInf.atan());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfNegInf.atan());
	}

	public void testAtanNaN() {
		junit.framework.Assert.assertTrue(org.apache.commons.math.complex.Complex.NaN.atan().isNaN());
		junit.framework.Assert.assertTrue(org.apache.commons.math.complex.Complex.I.atan().isNaN());
	}

	public void testCos() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(3 , 4);
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(-27.03495 , -3.851153);
		org.apache.commons.math.TestUtils.assertEquals(expected, z.cos(), 1.0E-5);
	}

	public void testCosNaN() {
		junit.framework.Assert.assertTrue(org.apache.commons.math.complex.Complex.NaN.cos().isNaN());
	}

	public void testCosInf() {
		org.apache.commons.math.TestUtils.assertSame(infNegInf, oneInf.cos());
		org.apache.commons.math.TestUtils.assertSame(infInf, oneNegInf.cos());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infOne.cos());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfOne.cos());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infInf.cos());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infNegInf.cos());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfInf.cos());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfNegInf.cos());
	}

	public void testCosh() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(3 , 4);
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(-6.58066 , -7.58155);
		org.apache.commons.math.TestUtils.assertEquals(expected, z.cosh(), 1.0E-5);
	}

	public void testCoshNaN() {
		junit.framework.Assert.assertTrue(org.apache.commons.math.complex.Complex.NaN.cosh().isNaN());
	}

	public void testCoshInf() {
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, oneInf.cosh());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, oneNegInf.cosh());
		org.apache.commons.math.TestUtils.assertSame(infInf, infOne.cosh());
		org.apache.commons.math.TestUtils.assertSame(infNegInf, negInfOne.cosh());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infInf.cosh());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infNegInf.cosh());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfInf.cosh());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfNegInf.cosh());
	}

	public void testExp() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(3 , 4);
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(-13.12878 , -15.20078);
		org.apache.commons.math.TestUtils.assertEquals(expected, z.exp(), 1.0E-5);
		org.apache.commons.math.TestUtils.assertEquals(org.apache.commons.math.complex.Complex.ONE, org.apache.commons.math.complex.Complex.ZERO.exp(), 1.0E-11);
		org.apache.commons.math.complex.Complex iPi = org.apache.commons.math.complex.Complex.I.multiply(new org.apache.commons.math.complex.Complex(pi , 0));
		org.apache.commons.math.TestUtils.assertEquals(org.apache.commons.math.complex.Complex.ONE.negate(), iPi.exp(), 1.0E-11);
	}

	public void testExpNaN() {
		junit.framework.Assert.assertTrue(org.apache.commons.math.complex.Complex.NaN.exp().isNaN());
	}

	public void testExpInf() {
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, oneInf.exp());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, oneNegInf.exp());
		org.apache.commons.math.TestUtils.assertSame(infInf, infOne.exp());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.ZERO, negInfOne.exp());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infInf.exp());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infNegInf.exp());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfInf.exp());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfNegInf.exp());
	}

	public void testLog() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(3 , 4);
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(1.60944 , 0.927295);
		org.apache.commons.math.TestUtils.assertEquals(expected, z.log(), 1.0E-5);
	}

	public void testLogNaN() {
		junit.framework.Assert.assertTrue(org.apache.commons.math.complex.Complex.NaN.log().isNaN());
	}

	public void testLogInf() {
		org.apache.commons.math.TestUtils.assertEquals(new org.apache.commons.math.complex.Complex(inf , ((pi) / 2)), oneInf.log(), 1.0E-11);
		org.apache.commons.math.TestUtils.assertEquals(new org.apache.commons.math.complex.Complex(inf , ((-(pi)) / 2)), oneNegInf.log(), 1.0E-11);
		org.apache.commons.math.TestUtils.assertEquals(infZero, infOne.log(), 1.0E-11);
		org.apache.commons.math.TestUtils.assertEquals(new org.apache.commons.math.complex.Complex(inf , pi), negInfOne.log(), 1.0E-11);
		org.apache.commons.math.TestUtils.assertEquals(new org.apache.commons.math.complex.Complex(inf , ((pi) / 4)), infInf.log(), 1.0E-11);
		org.apache.commons.math.TestUtils.assertEquals(new org.apache.commons.math.complex.Complex(inf , ((-(pi)) / 4)), infNegInf.log(), 1.0E-11);
		org.apache.commons.math.TestUtils.assertEquals(new org.apache.commons.math.complex.Complex(inf , ((3.0 * (pi)) / 4)), negInfInf.log(), 1.0E-11);
		org.apache.commons.math.TestUtils.assertEquals(new org.apache.commons.math.complex.Complex(inf , (((-3.0) * (pi)) / 4)), negInfNegInf.log(), 1.0E-11);
	}

	public void testLogZero() {
		org.apache.commons.math.TestUtils.assertSame(negInfZero, org.apache.commons.math.complex.Complex.ZERO.log());
	}

	public void testPow() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3 , 4);
		org.apache.commons.math.complex.Complex y = new org.apache.commons.math.complex.Complex(5 , 6);
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(-1.860893 , 11.83677);
		org.apache.commons.math.TestUtils.assertEquals(expected, x.pow(y), 1.0E-5);
	}

	public void testPowNaNBase() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3 , 4);
		junit.framework.Assert.assertTrue(org.apache.commons.math.complex.Complex.NaN.pow(x).isNaN());
	}

	public void testPowNaNExponent() {
		org.apache.commons.math.complex.Complex x = new org.apache.commons.math.complex.Complex(3 , 4);
		junit.framework.Assert.assertTrue(x.pow(org.apache.commons.math.complex.Complex.NaN).isNaN());
	}

	public void testPowInf() {
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, org.apache.commons.math.complex.Complex.ONE.pow(oneInf));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, org.apache.commons.math.complex.Complex.ONE.pow(oneNegInf));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, org.apache.commons.math.complex.Complex.ONE.pow(infOne));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, org.apache.commons.math.complex.Complex.ONE.pow(infInf));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, org.apache.commons.math.complex.Complex.ONE.pow(infNegInf));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, org.apache.commons.math.complex.Complex.ONE.pow(negInfInf));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, org.apache.commons.math.complex.Complex.ONE.pow(negInfNegInf));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infOne.pow(org.apache.commons.math.complex.Complex.ONE));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfOne.pow(org.apache.commons.math.complex.Complex.ONE));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infInf.pow(org.apache.commons.math.complex.Complex.ONE));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infNegInf.pow(org.apache.commons.math.complex.Complex.ONE));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfInf.pow(org.apache.commons.math.complex.Complex.ONE));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfNegInf.pow(org.apache.commons.math.complex.Complex.ONE));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfNegInf.pow(infNegInf));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfNegInf.pow(negInfNegInf));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfNegInf.pow(infInf));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infInf.pow(infNegInf));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infInf.pow(negInfNegInf));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infInf.pow(infInf));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infNegInf.pow(infNegInf));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infNegInf.pow(negInfNegInf));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infNegInf.pow(infInf));
	}

	public void testPowZero() {
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, org.apache.commons.math.complex.Complex.ZERO.pow(org.apache.commons.math.complex.Complex.ONE));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, org.apache.commons.math.complex.Complex.ZERO.pow(org.apache.commons.math.complex.Complex.ZERO));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, org.apache.commons.math.complex.Complex.ZERO.pow(org.apache.commons.math.complex.Complex.I));
		org.apache.commons.math.TestUtils.assertEquals(org.apache.commons.math.complex.Complex.ONE, org.apache.commons.math.complex.Complex.ONE.pow(org.apache.commons.math.complex.Complex.ZERO), 1.0E-11);
		org.apache.commons.math.TestUtils.assertEquals(org.apache.commons.math.complex.Complex.ONE, org.apache.commons.math.complex.Complex.I.pow(org.apache.commons.math.complex.Complex.ZERO), 1.0E-11);
		org.apache.commons.math.TestUtils.assertEquals(org.apache.commons.math.complex.Complex.ONE, new org.apache.commons.math.complex.Complex(-1 , 3).pow(org.apache.commons.math.complex.Complex.ZERO), 1.0E-11);
	}

	public void testpowNull() {
		try {
			org.apache.commons.math.complex.Complex.ONE.pow(null);
			junit.framework.Assert.fail("Expecting NullPointerException");
		} catch (java.lang.NullPointerException ex) {
		}
	}

	public void testSin() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(3 , 4);
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(3.853738 , -27.01681);
		org.apache.commons.math.TestUtils.assertEquals(expected, z.sin(), 1.0E-5);
	}

	public void testSinInf() {
		org.apache.commons.math.TestUtils.assertSame(infInf, oneInf.sin());
		org.apache.commons.math.TestUtils.assertSame(infNegInf, oneNegInf.sin());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infOne.sin());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfOne.sin());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infInf.sin());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infNegInf.sin());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfInf.sin());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfNegInf.sin());
	}

	public void testSinNaN() {
		junit.framework.Assert.assertTrue(org.apache.commons.math.complex.Complex.NaN.sin().isNaN());
	}

	public void testSinh() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(3 , 4);
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(-6.54812 , -7.61923);
		org.apache.commons.math.TestUtils.assertEquals(expected, z.sinh(), 1.0E-5);
	}

	public void testSinhNaN() {
		junit.framework.Assert.assertTrue(org.apache.commons.math.complex.Complex.NaN.sinh().isNaN());
	}

	public void testSinhInf() {
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, oneInf.sinh());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, oneNegInf.sinh());
		org.apache.commons.math.TestUtils.assertSame(infInf, infOne.sinh());
		org.apache.commons.math.TestUtils.assertSame(negInfInf, negInfOne.sinh());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infInf.sinh());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infNegInf.sinh());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfInf.sinh());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfNegInf.sinh());
	}

	public void testSqrtRealPositive() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(3 , 4);
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(2 , 1);
		org.apache.commons.math.TestUtils.assertEquals(expected, z.sqrt(), 1.0E-5);
	}

	public void testSqrtRealZero() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(0.0 , 4);
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(1.41421 , 1.41421);
		org.apache.commons.math.TestUtils.assertEquals(expected, z.sqrt(), 1.0E-5);
	}

	public void testSqrtRealNegative() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(-3.0 , 4);
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(1 , 2);
		org.apache.commons.math.TestUtils.assertEquals(expected, z.sqrt(), 1.0E-5);
	}

	public void testSqrtImaginaryZero() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(-3.0 , 0.0);
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(0.0 , 1.73205);
		org.apache.commons.math.TestUtils.assertEquals(expected, z.sqrt(), 1.0E-5);
	}

	public void testSqrtImaginaryNegative() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(-3.0 , -4.0);
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(1.0 , -2.0);
		org.apache.commons.math.TestUtils.assertEquals(expected, z.sqrt(), 1.0E-5);
	}

	public void testSqrtPolar() {
		double r = 1;
		for (int i = 0 ; i < 5 ; i++) {
			r += i;
			double theta = 0;
			for (int j = 0 ; j < 11 ; j++) {
				theta += (pi) / 12;
				org.apache.commons.math.complex.Complex z = org.apache.commons.math.complex.ComplexUtils.polar2Complex(r, theta);
				org.apache.commons.math.complex.Complex sqrtz = org.apache.commons.math.complex.ComplexUtils.polar2Complex(java.lang.Math.sqrt(r), (theta / 2));
				org.apache.commons.math.TestUtils.assertEquals(sqrtz, z.sqrt(), 1.0E-11);
			}
		}
	}

	public void testSqrtNaN() {
		junit.framework.Assert.assertTrue(org.apache.commons.math.complex.Complex.NaN.sqrt().isNaN());
	}

	public void testSqrtInf() {
		org.apache.commons.math.TestUtils.assertSame(infNaN, oneInf.sqrt());
		org.apache.commons.math.TestUtils.assertSame(infNaN, oneNegInf.sqrt());
		org.apache.commons.math.TestUtils.assertSame(infZero, infOne.sqrt());
		org.apache.commons.math.TestUtils.assertSame(zeroInf, negInfOne.sqrt());
		org.apache.commons.math.TestUtils.assertSame(infNaN, infInf.sqrt());
		org.apache.commons.math.TestUtils.assertSame(infNaN, infNegInf.sqrt());
		org.apache.commons.math.TestUtils.assertSame(nanInf, negInfInf.sqrt());
		org.apache.commons.math.TestUtils.assertSame(nanNegInf, negInfNegInf.sqrt());
	}

	public void testSqrt1z() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(3 , 4);
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(4.08033 , -2.94094);
		org.apache.commons.math.TestUtils.assertEquals(expected, z.sqrt1z(), 1.0E-5);
	}

	public void testSqrt1zNaN() {
		junit.framework.Assert.assertTrue(org.apache.commons.math.complex.Complex.NaN.sqrt1z().isNaN());
	}

	public void testTan() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(3 , 4);
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(-1.87346E-4 , 0.999356);
		org.apache.commons.math.TestUtils.assertEquals(expected, z.tan(), 1.0E-5);
	}

	public void testTanNaN() {
		junit.framework.Assert.assertTrue(org.apache.commons.math.complex.Complex.NaN.tan().isNaN());
	}

	public void testTanInf() {
		org.apache.commons.math.TestUtils.assertSame(zeroNaN, oneInf.tan());
		org.apache.commons.math.TestUtils.assertSame(zeroNaN, oneNegInf.tan());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infOne.tan());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfOne.tan());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infInf.tan());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infNegInf.tan());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfInf.tan());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfNegInf.tan());
	}

	public void testTanCritical() {
		org.apache.commons.math.TestUtils.assertSame(infNaN, new org.apache.commons.math.complex.Complex(((pi) / 2) , 0).tan());
		org.apache.commons.math.TestUtils.assertSame(negInfNaN, new org.apache.commons.math.complex.Complex(((-(pi)) / 2) , 0).tan());
	}

	public void testTanh() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(3 , 4);
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(1.00071 , 0.00490826);
		org.apache.commons.math.TestUtils.assertEquals(expected, z.tanh(), 1.0E-5);
	}

	public void testTanhNaN() {
		junit.framework.Assert.assertTrue(org.apache.commons.math.complex.Complex.NaN.tanh().isNaN());
	}

	public void testTanhInf() {
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, oneInf.tanh());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, oneNegInf.tanh());
		org.apache.commons.math.TestUtils.assertSame(nanZero, infOne.tanh());
		org.apache.commons.math.TestUtils.assertSame(nanZero, negInfOne.tanh());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infInf.tanh());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, infNegInf.tanh());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfInf.tanh());
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, negInfNegInf.tanh());
	}

	public void testTanhCritical() {
		org.apache.commons.math.TestUtils.assertSame(nanInf, new org.apache.commons.math.complex.Complex(0 , ((pi) / 2)).tanh());
	}

	public void testMath221() {
		junit.framework.Assert.assertEquals(new org.apache.commons.math.complex.Complex(0 , -1), new org.apache.commons.math.complex.Complex(0 , 1).multiply(new org.apache.commons.math.complex.Complex(-1 , 0)));
	}

	public void testNthRoot_normal_thirdRoot() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(-2 , 2);
		org.apache.commons.math.complex.Complex[] thirdRootsOfZ = z.nthRoot(3).toArray(new org.apache.commons.math.complex.Complex[0]);
		junit.framework.Assert.assertEquals(3, thirdRootsOfZ.length);
		junit.framework.Assert.assertEquals(1.0, thirdRootsOfZ[0].getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(1.0, thirdRootsOfZ[0].getImaginary(), 1.0E-5);
		junit.framework.Assert.assertEquals(-1.3660254037844386, thirdRootsOfZ[1].getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(0.36602540378443843, thirdRootsOfZ[1].getImaginary(), 1.0E-5);
		junit.framework.Assert.assertEquals(0.366025403784439, thirdRootsOfZ[2].getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(-1.3660254037844384, thirdRootsOfZ[2].getImaginary(), 1.0E-5);
	}

	public void testNthRoot_normal_fourthRoot() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(5 , -2);
		org.apache.commons.math.complex.Complex[] fourthRootsOfZ = z.nthRoot(4).toArray(new org.apache.commons.math.complex.Complex[0]);
		junit.framework.Assert.assertEquals(4, fourthRootsOfZ.length);
		junit.framework.Assert.assertEquals(1.5164629308487783, fourthRootsOfZ[0].getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(-0.14469266210702247, fourthRootsOfZ[0].getImaginary(), 1.0E-5);
		junit.framework.Assert.assertEquals(0.14469266210702256, fourthRootsOfZ[1].getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(1.5164629308487783, fourthRootsOfZ[1].getImaginary(), 1.0E-5);
		junit.framework.Assert.assertEquals(-1.5164629308487783, fourthRootsOfZ[2].getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(0.14469266210702267, fourthRootsOfZ[2].getImaginary(), 1.0E-5);
		junit.framework.Assert.assertEquals(-0.14469266210702275, fourthRootsOfZ[3].getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(-1.5164629308487783, fourthRootsOfZ[3].getImaginary(), 1.0E-5);
	}

	public void testNthRoot_cornercase_thirdRoot_imaginaryPartEmpty() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(8 , 0);
		org.apache.commons.math.complex.Complex[] thirdRootsOfZ = z.nthRoot(3).toArray(new org.apache.commons.math.complex.Complex[0]);
		junit.framework.Assert.assertEquals(3, thirdRootsOfZ.length);
		junit.framework.Assert.assertEquals(2.0, thirdRootsOfZ[0].getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(0.0, thirdRootsOfZ[0].getImaginary(), 1.0E-5);
		junit.framework.Assert.assertEquals(-1.0, thirdRootsOfZ[1].getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(1.7320508075688774, thirdRootsOfZ[1].getImaginary(), 1.0E-5);
		junit.framework.Assert.assertEquals(-1.0, thirdRootsOfZ[2].getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(-1.732050807568877, thirdRootsOfZ[2].getImaginary(), 1.0E-5);
	}

	public void testNthRoot_cornercase_thirdRoot_realPartZero() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(0 , 2);
		org.apache.commons.math.complex.Complex[] thirdRootsOfZ = z.nthRoot(3).toArray(new org.apache.commons.math.complex.Complex[0]);
		junit.framework.Assert.assertEquals(3, thirdRootsOfZ.length);
		junit.framework.Assert.assertEquals(1.0911236359717216, thirdRootsOfZ[0].getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(0.6299605249474365, thirdRootsOfZ[0].getImaginary(), 1.0E-5);
		junit.framework.Assert.assertEquals(-1.0911236359717216, thirdRootsOfZ[1].getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(0.6299605249474365, thirdRootsOfZ[1].getImaginary(), 1.0E-5);
		junit.framework.Assert.assertEquals(-2.3144374213981936E-16, thirdRootsOfZ[2].getReal(), 1.0E-5);
		junit.framework.Assert.assertEquals(-1.2599210498948732, thirdRootsOfZ[2].getImaginary(), 1.0E-5);
	}

	public void testNthRoot_cornercase_NAN_Inf() {
		java.util.List<org.apache.commons.math.complex.Complex> roots = oneNaN.nthRoot(3);
		junit.framework.Assert.assertEquals(1, roots.size());
		junit.framework.Assert.assertEquals(org.apache.commons.math.complex.Complex.NaN, roots.get(0));
		roots = nanZero.nthRoot(3);
		junit.framework.Assert.assertEquals(1, roots.size());
		junit.framework.Assert.assertEquals(org.apache.commons.math.complex.Complex.NaN, roots.get(0));
		roots = nanInf.nthRoot(3);
		junit.framework.Assert.assertEquals(1, roots.size());
		junit.framework.Assert.assertEquals(org.apache.commons.math.complex.Complex.NaN, roots.get(0));
		roots = oneInf.nthRoot(3);
		junit.framework.Assert.assertEquals(1, roots.size());
		junit.framework.Assert.assertEquals(org.apache.commons.math.complex.Complex.INF, roots.get(0));
		roots = negInfInf.nthRoot(3);
		junit.framework.Assert.assertEquals(1, roots.size());
		junit.framework.Assert.assertEquals(org.apache.commons.math.complex.Complex.INF, roots.get(0));
	}

	public void testGetArgument() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(1 , 0);
		junit.framework.Assert.assertEquals(0.0, z.getArgument(), 1.0E-12);
		z = new org.apache.commons.math.complex.Complex(1 , 1);
		junit.framework.Assert.assertEquals(((java.lang.Math.PI) / 4), z.getArgument(), 1.0E-12);
		z = new org.apache.commons.math.complex.Complex(0 , 1);
		junit.framework.Assert.assertEquals(((java.lang.Math.PI) / 2), z.getArgument(), 1.0E-12);
		z = new org.apache.commons.math.complex.Complex(-1 , 1);
		junit.framework.Assert.assertEquals(((3 * (java.lang.Math.PI)) / 4), z.getArgument(), 1.0E-12);
		z = new org.apache.commons.math.complex.Complex(-1 , 0);
		junit.framework.Assert.assertEquals(java.lang.Math.PI, z.getArgument(), 1.0E-12);
		z = new org.apache.commons.math.complex.Complex(-1 , -1);
		junit.framework.Assert.assertEquals((((-3) * (java.lang.Math.PI)) / 4), z.getArgument(), 1.0E-12);
		z = new org.apache.commons.math.complex.Complex(0 , -1);
		junit.framework.Assert.assertEquals(((-(java.lang.Math.PI)) / 2), z.getArgument(), 1.0E-12);
		z = new org.apache.commons.math.complex.Complex(1 , -1);
		junit.framework.Assert.assertEquals(((-(java.lang.Math.PI)) / 4), z.getArgument(), 1.0E-12);
	}

	public void testGetArgumentInf() {
		junit.framework.Assert.assertEquals(((java.lang.Math.PI) / 4), infInf.getArgument(), 1.0E-12);
		junit.framework.Assert.assertEquals(((java.lang.Math.PI) / 2), oneInf.getArgument(), 1.0E-12);
		junit.framework.Assert.assertEquals(0.0, infOne.getArgument(), 1.0E-12);
		junit.framework.Assert.assertEquals(((java.lang.Math.PI) / 2), zeroInf.getArgument(), 1.0E-12);
		junit.framework.Assert.assertEquals(0.0, infZero.getArgument(), 1.0E-12);
		junit.framework.Assert.assertEquals(java.lang.Math.PI, negInfOne.getArgument(), 1.0E-12);
		junit.framework.Assert.assertEquals((((-3.0) * (java.lang.Math.PI)) / 4), negInfNegInf.getArgument(), 1.0E-12);
		junit.framework.Assert.assertEquals(((-(java.lang.Math.PI)) / 2), oneNegInf.getArgument(), 1.0E-12);
	}

	public void testGetArgumentNaN() {
		junit.framework.Assert.assertEquals(nan, nanZero.getArgument());
		junit.framework.Assert.assertEquals(nan, zeroNaN.getArgument());
		junit.framework.Assert.assertEquals(nan, org.apache.commons.math.complex.Complex.NaN.getArgument());
	}

	public void testSerial() {
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(3.0 , 4.0);
		junit.framework.Assert.assertEquals(z, org.apache.commons.math.TestUtils.serializeAndRecover(z));
		org.apache.commons.math.complex.Complex ncmplx = ((org.apache.commons.math.complex.Complex)(org.apache.commons.math.TestUtils.serializeAndRecover(oneNaN)));
		junit.framework.Assert.assertEquals(nanZero, ncmplx);
		junit.framework.Assert.assertTrue(ncmplx.isNaN());
		org.apache.commons.math.complex.Complex infcmplx = ((org.apache.commons.math.complex.Complex)(org.apache.commons.math.TestUtils.serializeAndRecover(infInf)));
		junit.framework.Assert.assertEquals(infInf, infcmplx);
		junit.framework.Assert.assertTrue(infcmplx.isInfinite());
		org.apache.commons.math.complex.ComplexTest.TestComplex tz = new org.apache.commons.math.complex.ComplexTest.TestComplex(3.0 , 4.0);
		junit.framework.Assert.assertEquals(tz, org.apache.commons.math.TestUtils.serializeAndRecover(tz));
		org.apache.commons.math.complex.ComplexTest.TestComplex ntcmplx = ((org.apache.commons.math.complex.ComplexTest.TestComplex)(org.apache.commons.math.TestUtils.serializeAndRecover(new org.apache.commons.math.complex.ComplexTest.TestComplex(oneNaN))));
		junit.framework.Assert.assertEquals(nanZero, ntcmplx);
		junit.framework.Assert.assertTrue(ntcmplx.isNaN());
		org.apache.commons.math.complex.ComplexTest.TestComplex inftcmplx = ((org.apache.commons.math.complex.ComplexTest.TestComplex)(org.apache.commons.math.TestUtils.serializeAndRecover(new org.apache.commons.math.complex.ComplexTest.TestComplex(infInf))));
		junit.framework.Assert.assertEquals(infInf, inftcmplx);
		junit.framework.Assert.assertTrue(inftcmplx.isInfinite());
	}

	public static class TestComplex extends org.apache.commons.math.complex.Complex {
		private static final long serialVersionUID = 3268726724160389237L;

		public TestComplex(double real ,double imaginary) {
			super(real, imaginary);
		}

		public TestComplex(org.apache.commons.math.complex.Complex other) {
			this(other.getReal(), other.getImaginary());
		}

		@java.lang.Override
		protected org.apache.commons.math.complex.ComplexTest.TestComplex createComplex(double real, double imaginary) {
			return new org.apache.commons.math.complex.ComplexTest.TestComplex(real , imaginary);
		}
	}
}

