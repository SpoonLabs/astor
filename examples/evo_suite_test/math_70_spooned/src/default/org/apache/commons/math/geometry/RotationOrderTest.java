package org.apache.commons.math.geometry;


public class RotationOrderTest extends junit.framework.TestCase {
	public RotationOrderTest(java.lang.String name) {
		super(name);
	}

	public void testName() {
		org.apache.commons.math.geometry.RotationOrder[] orders = new org.apache.commons.math.geometry.RotationOrder[]{ org.apache.commons.math.geometry.RotationOrder.XYZ , org.apache.commons.math.geometry.RotationOrder.XZY , org.apache.commons.math.geometry.RotationOrder.YXZ , org.apache.commons.math.geometry.RotationOrder.YZX , org.apache.commons.math.geometry.RotationOrder.ZXY , org.apache.commons.math.geometry.RotationOrder.ZYX , org.apache.commons.math.geometry.RotationOrder.XYX , org.apache.commons.math.geometry.RotationOrder.XZX , org.apache.commons.math.geometry.RotationOrder.YXY , org.apache.commons.math.geometry.RotationOrder.YZY , org.apache.commons.math.geometry.RotationOrder.ZXZ , org.apache.commons.math.geometry.RotationOrder.ZYZ };
		for (int i = 0 ; i < (orders.length) ; ++i) {
			junit.framework.Assert.assertEquals(getFieldName(orders[i]), orders[i].toString());
		}
	}

	private java.lang.String getFieldName(org.apache.commons.math.geometry.RotationOrder order) {
		try {
			java.lang.reflect.Field[] fields = org.apache.commons.math.geometry.RotationOrder.class.getFields();
			for (int i = 0 ; i < (fields.length) ; ++i) {
				if ((fields[i].get(null)) == order) {
					return fields[i].getName();
				} 
			}
		} catch (java.lang.IllegalAccessException iae) {
		}
		return "unknown";
	}
}

