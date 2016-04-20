package org.apache.commons.math.estimation;


@java.lang.Deprecated
public class WeightedMeasurementTest extends junit.framework.TestCase {
	public WeightedMeasurementTest(java.lang.String name) {
		super(name);
		p1 = null;
		p2 = null;
	}

	public void testConstruction() {
		org.apache.commons.math.estimation.WeightedMeasurement m = new org.apache.commons.math.estimation.WeightedMeasurementTest.MyMeasurement(3.0 , ((theoretical()) + 0.1) , this);
		checkValue(m.getWeight(), 3.0);
		checkValue(m.getMeasuredValue(), ((theoretical()) + 0.1));
	}

	public void testIgnored() {
		org.apache.commons.math.estimation.WeightedMeasurement m = new org.apache.commons.math.estimation.WeightedMeasurementTest.MyMeasurement(3.0 , ((theoretical()) + 0.1) , this);
		junit.framework.Assert.assertTrue(!(m.isIgnored()));
		m.setIgnored(true);
		junit.framework.Assert.assertTrue(m.isIgnored());
		m.setIgnored(false);
		junit.framework.Assert.assertTrue(!(m.isIgnored()));
	}

	public void testTheory() {
		org.apache.commons.math.estimation.WeightedMeasurement m = new org.apache.commons.math.estimation.WeightedMeasurementTest.MyMeasurement(3.0 , ((theoretical()) + 0.1) , this);
		checkValue(m.getTheoreticalValue(), theoretical());
		checkValue(m.getResidual(), 0.1);
		double oldP1 = p1.getEstimate();
		p1.setEstimate((oldP1 + ((m.getResidual()) / (m.getPartial(p1)))));
		checkValue(m.getResidual(), 0.0);
		p1.setEstimate(oldP1);
		checkValue(m.getResidual(), 0.1);
		double oldP2 = p2.getEstimate();
		p2.setEstimate((oldP2 + ((m.getResidual()) / (m.getPartial(p2)))));
		checkValue(m.getResidual(), 0.0);
		p2.setEstimate(oldP2);
		checkValue(m.getResidual(), 0.1);
	}

	@java.lang.Override
	public void setUp() {
		p1 = new org.apache.commons.math.estimation.EstimatedParameter("p1" , 1.0);
		p2 = new org.apache.commons.math.estimation.EstimatedParameter("p2" , 2.0);
	}

	@java.lang.Override
	public void tearDown() {
		p1 = null;
		p2 = null;
	}

	private void checkValue(double value, double expected) {
		junit.framework.Assert.assertTrue(((java.lang.Math.abs((value - expected))) < 1.0E-10));
	}

	private double theoretical() {
		return (3 * (p1.getEstimate())) - (p2.getEstimate());
	}

	private double partial(org.apache.commons.math.estimation.EstimatedParameter p) {
		if (p == (p1)) {
			return 3.0;
		} else if (p == (p2)) {
			return -1.0;
		} else {
			return 0.0;
		}
	}

	private static class MyMeasurement extends org.apache.commons.math.estimation.WeightedMeasurement {
		public MyMeasurement(double weight ,double measuredValue ,org.apache.commons.math.estimation.WeightedMeasurementTest testInstance) {
			super(weight, measuredValue);
			this.testInstance = testInstance;
		}

		@java.lang.Override
		public double getTheoreticalValue() {
			return testInstance.theoretical();
		}

		@java.lang.Override
		public double getPartial(org.apache.commons.math.estimation.EstimatedParameter p) {
			return testInstance.partial(p);
		}

		private transient org.apache.commons.math.estimation.WeightedMeasurementTest testInstance;

		private static final long serialVersionUID = -246712922500792332L;
	}

	private org.apache.commons.math.estimation.EstimatedParameter p1;

	private org.apache.commons.math.estimation.EstimatedParameter p2;
}

