package org.apache.commons.math.estimation;


@java.lang.Deprecated
public class EstimatedParameterTest extends junit.framework.TestCase {
	public EstimatedParameterTest(java.lang.String name) {
		super(name);
	}

	public void testConstruction() {
		org.apache.commons.math.estimation.EstimatedParameter p1 = new org.apache.commons.math.estimation.EstimatedParameter("p1" , 1.0);
		junit.framework.Assert.assertTrue(p1.getName().equals("p1"));
		checkValue(p1.getEstimate(), 1.0);
		junit.framework.Assert.assertTrue(!(p1.isBound()));
		org.apache.commons.math.estimation.EstimatedParameter p2 = new org.apache.commons.math.estimation.EstimatedParameter("p2" , 2.0 , true);
		junit.framework.Assert.assertTrue(p2.getName().equals("p2"));
		checkValue(p2.getEstimate(), 2.0);
		junit.framework.Assert.assertTrue(p2.isBound());
	}

	public void testBound() {
		org.apache.commons.math.estimation.EstimatedParameter p = new org.apache.commons.math.estimation.EstimatedParameter("p" , 0.0);
		junit.framework.Assert.assertTrue(!(p.isBound()));
		p.setBound(true);
		junit.framework.Assert.assertTrue(p.isBound());
		p.setBound(false);
		junit.framework.Assert.assertTrue(!(p.isBound()));
	}

	public void testEstimate() {
		org.apache.commons.math.estimation.EstimatedParameter p = new org.apache.commons.math.estimation.EstimatedParameter("p" , 0.0);
		checkValue(p.getEstimate(), 0.0);
		for (double e = 0.0 ; e < 10.0 ; e += 0.5) {
			p.setEstimate(e);
			checkValue(p.getEstimate(), e);
		}
	}

	private void checkValue(double value, double expected) {
		junit.framework.Assert.assertTrue(((java.lang.Math.abs((value - expected))) < 1.0E-10));
	}
}

