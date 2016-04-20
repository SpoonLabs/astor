package org.apache.commons.math.stat.descriptive.summary;


public class ProductTest extends org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest {
	protected org.apache.commons.math.stat.descriptive.summary.Product stat;

	public ProductTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.UnivariateStatistic getUnivariateStatistic() {
		return new org.apache.commons.math.stat.descriptive.summary.Product();
	}

	@java.lang.Override
	public double getTolerance() {
		return 1.0E9;
	}

	@java.lang.Override
	public double expectedValue() {
		return this.product;
	}

	public double expectedWeightedValue() {
		return this.weightedProduct;
	}

	public void testSpecialValues() {
		org.apache.commons.math.stat.descriptive.summary.Product product = new org.apache.commons.math.stat.descriptive.summary.Product();
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(product.getResult()));
		product.increment(1);
		junit.framework.Assert.assertEquals(1, product.getResult(), 0);
		product.increment(java.lang.Double.POSITIVE_INFINITY);
		junit.framework.Assert.assertEquals(java.lang.Double.POSITIVE_INFINITY, product.getResult(), 0);
		product.increment(java.lang.Double.NEGATIVE_INFINITY);
		junit.framework.Assert.assertEquals(java.lang.Double.NEGATIVE_INFINITY, product.getResult(), 0);
		product.increment(java.lang.Double.NaN);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(product.getResult()));
		product.increment(1);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(product.getResult()));
	}

	public void testWeightedProduct() {
		org.apache.commons.math.stat.descriptive.summary.Product product = new org.apache.commons.math.stat.descriptive.summary.Product();
		junit.framework.Assert.assertEquals(expectedWeightedValue(), product.evaluate(testArray, testWeightsArray, 0, testArray.length), getTolerance());
		junit.framework.Assert.assertEquals(expectedValue(), product.evaluate(testArray, unitWeightsArray, 0, testArray.length), getTolerance());
	}
}

