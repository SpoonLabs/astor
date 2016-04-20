package org.apache.commons.math.util;


public class ContinuedFractionTest extends junit.framework.TestCase {
	public ContinuedFractionTest(java.lang.String name) {
		super(name);
	}

	public void testGoldenRatio() {
		org.apache.commons.math.util.ContinuedFraction cf = new org.apache.commons.math.util.ContinuedFraction() {
			@java.lang.Override
			public double getA(int n, double x) {
				return 1.0;
			}

			@java.lang.Override
			public double getB(int n, double x) {
				return 1.0;
			}
		};
		try {
			double gr = cf.evaluate(0.0, 1.0E-8);
			junit.framework.Assert.assertEquals(1.61803399, gr, 1.0E-8);
		} catch (org.apache.commons.math.MathException e) {
			junit.framework.Assert.fail(e.getMessage());
		}
	}
}

