package org.apache.commons.math.stat.clustering;


public class EuclideanIntegerPointTest {
	@org.junit.Test
	public void testArrayIsReference() {
		int[] array = new int[]{ -3 , -2 , -1 , 0 , 1 };
		org.junit.Assert.assertTrue((array == (new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(array).getPoint())));
	}

	@org.junit.Test
	public void testDistance() {
		org.apache.commons.math.stat.clustering.EuclideanIntegerPoint e1 = new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ -3 , -2 , -1 , 0 , 1 });
		org.apache.commons.math.stat.clustering.EuclideanIntegerPoint e2 = new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 1 , 0 , -1 , 1 , 1 });
		org.junit.Assert.assertEquals(java.lang.Math.sqrt(21.0), e1.distanceFrom(e2), 1.0E-15);
		org.junit.Assert.assertEquals(0.0, e1.distanceFrom(e1), 1.0E-15);
		org.junit.Assert.assertEquals(0.0, e2.distanceFrom(e2), 1.0E-15);
	}

	@org.junit.Test
	public void testCentroid() {
		java.util.List<org.apache.commons.math.stat.clustering.EuclideanIntegerPoint> list = new java.util.ArrayList<org.apache.commons.math.stat.clustering.EuclideanIntegerPoint>();
		list.add(new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 1 , 3 }));
		list.add(new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 2 , 2 }));
		list.add(new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 3 , 3 }));
		list.add(new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 2 , 4 }));
		org.apache.commons.math.stat.clustering.EuclideanIntegerPoint c = list.get(0).centroidOf(list);
		org.junit.Assert.assertEquals(2, c.getPoint()[0]);
		org.junit.Assert.assertEquals(3, c.getPoint()[1]);
	}

	@org.junit.Test
	public void testSerial() {
		org.apache.commons.math.stat.clustering.EuclideanIntegerPoint p = new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ -3 , -2 , -1 , 0 , 1 });
		org.junit.Assert.assertEquals(p, org.apache.commons.math.TestUtils.serializeAndRecover(p));
	}
}

