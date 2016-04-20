package org.apache.commons.math.linear;


public class SparseFieldVectorTest extends junit.framework.TestCase {
	protected org.apache.commons.math.fraction.Fraction[][] ma1 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(8) , new org.apache.commons.math.fraction.Fraction(9) } };

	protected org.apache.commons.math.fraction.Fraction[] vec1 = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) };

	protected org.apache.commons.math.fraction.Fraction[] vec2 = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) };

	protected org.apache.commons.math.fraction.Fraction[] vec3 = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(8) , new org.apache.commons.math.fraction.Fraction(9) };

	protected org.apache.commons.math.fraction.Fraction[] vec4 = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(8) , new org.apache.commons.math.fraction.Fraction(9) };

	protected org.apache.commons.math.fraction.Fraction[] vec_null = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(0) };

	protected org.apache.commons.math.fraction.Fraction[] dvec1 = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(8) , new org.apache.commons.math.fraction.Fraction(9) };

	protected org.apache.commons.math.fraction.Fraction[][] mat1 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(8) , new org.apache.commons.math.fraction.Fraction(9) } };

	protected double entryTolerance = 1.0E-15;

	protected double normTolerance = 1.0E-13;

	protected org.apache.commons.math.fraction.FractionField field = org.apache.commons.math.fraction.FractionField.getInstance();

	public void testMapFunctions() throws org.apache.commons.math.fraction.FractionConversionException {
		org.apache.commons.math.linear.SparseFieldVector<org.apache.commons.math.fraction.Fraction> v1 = new org.apache.commons.math.linear.SparseFieldVector<org.apache.commons.math.fraction.Fraction>(field , vec1);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapAdd = v1.mapAdd(new org.apache.commons.math.fraction.Fraction(2));
		org.apache.commons.math.fraction.Fraction[] result_mapAdd = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) };
		assertEquals("compare vectors", result_mapAdd, v_mapAdd.getData());
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapAddToSelf = v1.copy();
		v_mapAddToSelf.mapAddToSelf(new org.apache.commons.math.fraction.Fraction(2));
		org.apache.commons.math.fraction.Fraction[] result_mapAddToSelf = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) };
		assertEquals("compare vectors", result_mapAddToSelf, v_mapAddToSelf.getData());
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapSubtract = v1.mapSubtract(new org.apache.commons.math.fraction.Fraction(2));
		org.apache.commons.math.fraction.Fraction[] result_mapSubtract = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(-1) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(1) };
		assertEquals("compare vectors", result_mapSubtract, v_mapSubtract.getData());
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapSubtractToSelf = v1.copy();
		v_mapSubtractToSelf.mapSubtractToSelf(new org.apache.commons.math.fraction.Fraction(2));
		org.apache.commons.math.fraction.Fraction[] result_mapSubtractToSelf = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(-1) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(1) };
		assertEquals("compare vectors", result_mapSubtractToSelf, v_mapSubtractToSelf.getData());
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapMultiply = v1.mapMultiply(new org.apache.commons.math.fraction.Fraction(2));
		org.apache.commons.math.fraction.Fraction[] result_mapMultiply = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(6) };
		assertEquals("compare vectors", result_mapMultiply, v_mapMultiply.getData());
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapMultiplyToSelf = v1.copy();
		v_mapMultiplyToSelf.mapMultiplyToSelf(new org.apache.commons.math.fraction.Fraction(2));
		org.apache.commons.math.fraction.Fraction[] result_mapMultiplyToSelf = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(6) };
		assertEquals("compare vectors", result_mapMultiplyToSelf, v_mapMultiplyToSelf.getData());
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapDivide = v1.mapDivide(new org.apache.commons.math.fraction.Fraction(2));
		org.apache.commons.math.fraction.Fraction[] result_mapDivide = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0.5) , new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(1.5) };
		assertEquals("compare vectors", result_mapDivide, v_mapDivide.getData());
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapDivideToSelf = v1.copy();
		v_mapDivideToSelf.mapDivideToSelf(new org.apache.commons.math.fraction.Fraction(2));
		org.apache.commons.math.fraction.Fraction[] result_mapDivideToSelf = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0.5) , new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(1.5) };
		assertEquals("compare vectors", result_mapDivideToSelf, v_mapDivideToSelf.getData());
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapInv = v1.mapInv();
		org.apache.commons.math.fraction.Fraction[] result_mapInv = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(0.5) , new org.apache.commons.math.fraction.Fraction(0.3333333333333333) };
		assertEquals("compare vectors", result_mapInv, v_mapInv.getData());
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_mapInvToSelf = v1.copy();
		v_mapInvToSelf.mapInvToSelf();
		org.apache.commons.math.fraction.Fraction[] result_mapInvToSelf = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(0.5) , new org.apache.commons.math.fraction.Fraction(0.3333333333333333) };
		assertEquals("compare vectors", result_mapInvToSelf, v_mapInvToSelf.getData());
	}

	public void testBasicFunctions() throws org.apache.commons.math.fraction.FractionConversionException {
		org.apache.commons.math.linear.SparseFieldVector<org.apache.commons.math.fraction.Fraction> v1 = new org.apache.commons.math.linear.SparseFieldVector<org.apache.commons.math.fraction.Fraction>(field , vec1);
		org.apache.commons.math.linear.SparseFieldVector<org.apache.commons.math.fraction.Fraction> v2 = new org.apache.commons.math.linear.SparseFieldVector<org.apache.commons.math.fraction.Fraction>(field , vec2);
		org.apache.commons.math.linear.SparseFieldVector<org.apache.commons.math.fraction.Fraction> v2_t = new org.apache.commons.math.linear.SparseFieldVector<org.apache.commons.math.fraction.Fraction>(field , vec2);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_add = v1.add(v2);
		org.apache.commons.math.fraction.Fraction[] result_add = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(9) };
		assertEquals("compare vect", v_add.getData(), result_add);
		org.apache.commons.math.linear.SparseFieldVector<org.apache.commons.math.fraction.Fraction> vt2 = new org.apache.commons.math.linear.SparseFieldVector<org.apache.commons.math.fraction.Fraction>(field , vec2);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_add_i = v1.add(vt2);
		org.apache.commons.math.fraction.Fraction[] result_add_i = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(9) };
		assertEquals("compare vect", v_add_i.getData(), result_add_i);
		org.apache.commons.math.linear.SparseFieldVector<org.apache.commons.math.fraction.Fraction> v_subtract = v1.subtract(v2);
		org.apache.commons.math.fraction.Fraction[] result_subtract = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(-3) , new org.apache.commons.math.fraction.Fraction(-3) , new org.apache.commons.math.fraction.Fraction(-3) };
		assertClose("compare vect", v_subtract.getData(), result_subtract, normTolerance);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_subtract_i = v1.subtract(vt2);
		org.apache.commons.math.fraction.Fraction[] result_subtract_i = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(-3) , new org.apache.commons.math.fraction.Fraction(-3) , new org.apache.commons.math.fraction.Fraction(-3) };
		assertClose("compare vect", v_subtract_i.getData(), result_subtract_i, normTolerance);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_ebeMultiply = v1.ebeMultiply(v2);
		org.apache.commons.math.fraction.Fraction[] result_ebeMultiply = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(10) , new org.apache.commons.math.fraction.Fraction(18) };
		assertClose("compare vect", v_ebeMultiply.getData(), result_ebeMultiply, normTolerance);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_ebeMultiply_2 = v1.ebeMultiply(v2_t);
		org.apache.commons.math.fraction.Fraction[] result_ebeMultiply_2 = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(10) , new org.apache.commons.math.fraction.Fraction(18) };
		assertClose("compare vect", v_ebeMultiply_2.getData(), result_ebeMultiply_2, normTolerance);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_ebeDivide = v1.ebeDivide(v2);
		org.apache.commons.math.fraction.Fraction[] result_ebeDivide = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0.25) , new org.apache.commons.math.fraction.Fraction(0.4) , new org.apache.commons.math.fraction.Fraction(0.5) };
		assertClose("compare vect", v_ebeDivide.getData(), result_ebeDivide, normTolerance);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> v_ebeDivide_2 = v1.ebeDivide(v2_t);
		org.apache.commons.math.fraction.Fraction[] result_ebeDivide_2 = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0.25) , new org.apache.commons.math.fraction.Fraction(0.4) , new org.apache.commons.math.fraction.Fraction(0.5) };
		assertClose("compare vect", v_ebeDivide_2.getData(), result_ebeDivide_2, normTolerance);
		org.apache.commons.math.fraction.Fraction dot = v1.dotProduct(v2);
		junit.framework.Assert.assertEquals("compare val ", new org.apache.commons.math.fraction.Fraction(32), dot);
		org.apache.commons.math.fraction.Fraction dot_2 = v1.dotProduct(v2_t);
		junit.framework.Assert.assertEquals("compare val ", new org.apache.commons.math.fraction.Fraction(32), dot_2);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m_outerProduct = v1.outerProduct(v2);
		junit.framework.Assert.assertEquals("compare val ", new org.apache.commons.math.fraction.Fraction(4), m_outerProduct.getEntry(0, 0));
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m_outerProduct_2 = v1.outerProduct(v2_t);
		junit.framework.Assert.assertEquals("compare val ", new org.apache.commons.math.fraction.Fraction(4), m_outerProduct_2.getEntry(0, 0));
	}

	public void testMisc() {
		org.apache.commons.math.linear.SparseFieldVector<org.apache.commons.math.fraction.Fraction> v1 = new org.apache.commons.math.linear.SparseFieldVector<org.apache.commons.math.fraction.Fraction>(field , vec1);
		java.lang.String out1 = v1.toString();
		junit.framework.Assert.assertTrue("some output ", ((out1.length()) != 0));
		try {
			v1.checkVectorDimensions(2);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testPredicates() {
		org.apache.commons.math.linear.SparseFieldVector<org.apache.commons.math.fraction.Fraction> v = new org.apache.commons.math.linear.SparseFieldVector<org.apache.commons.math.fraction.Fraction>(field , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) });
		v.setEntry(0, field.getZero());
		junit.framework.Assert.assertEquals(v, new org.apache.commons.math.linear.SparseFieldVector<org.apache.commons.math.fraction.Fraction>(field , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) }));
		junit.framework.Assert.assertNotSame(v, new org.apache.commons.math.linear.SparseFieldVector<org.apache.commons.math.fraction.Fraction>(field , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) }));
	}

	protected void assertEquals(java.lang.String msg, org.apache.commons.math.fraction.Fraction[] m, org.apache.commons.math.fraction.Fraction[] n) {
		if ((m.length) != (n.length)) {
			junit.framework.Assert.fail("vectors have different lengths");
		} 
		for (int i = 0 ; i < (m.length) ; i++) {
			junit.framework.Assert.assertEquals((((msg + " ") + i) + " elements differ"), m[i], n[i]);
		}
	}

	protected void assertClose(java.lang.String msg, org.apache.commons.math.fraction.Fraction[] m, org.apache.commons.math.fraction.Fraction[] n, double tolerance) {
		if ((m.length) != (n.length)) {
			junit.framework.Assert.fail("vectors have different lengths");
		} 
		for (int i = 0 ; i < (m.length) ; i++) {
			junit.framework.Assert.assertEquals((((msg + " ") + i) + " elements differ"), m[i].doubleValue(), n[i].doubleValue(), tolerance);
		}
	}
}

