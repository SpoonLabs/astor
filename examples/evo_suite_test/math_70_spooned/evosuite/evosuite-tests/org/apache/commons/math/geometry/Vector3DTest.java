package org.apache.commons.math.geometry;


public class Vector3DTest extends junit.framework.TestCase {
	public Vector3DTest(java.lang.String name) {
		super(name);
	}

	public void testConstructors() {
		double r = (java.lang.Math.sqrt(2)) / 2;
		checkVector(new org.apache.commons.math.geometry.Vector3D(2 , new org.apache.commons.math.geometry.Vector3D(((java.lang.Math.PI) / 3) , ((-(java.lang.Math.PI)) / 4))), r, (r * (java.lang.Math.sqrt(3))), ((-2) * r));
		checkVector(new org.apache.commons.math.geometry.Vector3D(2 , org.apache.commons.math.geometry.Vector3D.PLUS_I , -3 , org.apache.commons.math.geometry.Vector3D.MINUS_K), 2, 0, 3);
		checkVector(new org.apache.commons.math.geometry.Vector3D(2 , org.apache.commons.math.geometry.Vector3D.PLUS_I , 5 , org.apache.commons.math.geometry.Vector3D.PLUS_J , -3 , org.apache.commons.math.geometry.Vector3D.MINUS_K), 2, 5, 3);
		checkVector(new org.apache.commons.math.geometry.Vector3D(2 , org.apache.commons.math.geometry.Vector3D.PLUS_I , 5 , org.apache.commons.math.geometry.Vector3D.PLUS_J , 5 , org.apache.commons.math.geometry.Vector3D.MINUS_J , -3 , org.apache.commons.math.geometry.Vector3D.MINUS_K), 2, 0, 3);
	}

	public void testCoordinates() {
		org.apache.commons.math.geometry.Vector3D v = new org.apache.commons.math.geometry.Vector3D(1 , 2 , 3);
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(((v.getX()) - 1))) < 1.0E-12));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(((v.getY()) - 2))) < 1.0E-12));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(((v.getZ()) - 3))) < 1.0E-12));
	}

	public void testNorm1() {
		junit.framework.Assert.assertEquals(0.0, org.apache.commons.math.geometry.Vector3D.ZERO.getNorm1());
		junit.framework.Assert.assertEquals(6.0, new org.apache.commons.math.geometry.Vector3D(1 , -2 , 3).getNorm1(), 0);
	}

	public void testNorm() {
		junit.framework.Assert.assertEquals(0.0, org.apache.commons.math.geometry.Vector3D.ZERO.getNorm());
		junit.framework.Assert.assertEquals(java.lang.Math.sqrt(14), new org.apache.commons.math.geometry.Vector3D(1 , 2 , 3).getNorm(), 1.0E-12);
	}

	public void testNormInf() {
		junit.framework.Assert.assertEquals(0.0, org.apache.commons.math.geometry.Vector3D.ZERO.getNormInf());
		junit.framework.Assert.assertEquals(3.0, new org.apache.commons.math.geometry.Vector3D(1 , -2 , 3).getNormInf(), 0);
	}

	public void testDistance1() {
		org.apache.commons.math.geometry.Vector3D v1 = new org.apache.commons.math.geometry.Vector3D(1 , -2 , 3);
		org.apache.commons.math.geometry.Vector3D v2 = new org.apache.commons.math.geometry.Vector3D(-4 , 2 , 0);
		junit.framework.Assert.assertEquals(0.0, org.apache.commons.math.geometry.Vector3D.distance1(org.apache.commons.math.geometry.Vector3D.MINUS_I, org.apache.commons.math.geometry.Vector3D.MINUS_I), 0);
		junit.framework.Assert.assertEquals(12.0, org.apache.commons.math.geometry.Vector3D.distance1(v1, v2), 1.0E-12);
		junit.framework.Assert.assertEquals(v1.subtract(v2).getNorm1(), org.apache.commons.math.geometry.Vector3D.distance1(v1, v2), 1.0E-12);
	}

	public void testDistance() {
		org.apache.commons.math.geometry.Vector3D v1 = new org.apache.commons.math.geometry.Vector3D(1 , -2 , 3);
		org.apache.commons.math.geometry.Vector3D v2 = new org.apache.commons.math.geometry.Vector3D(-4 , 2 , 0);
		junit.framework.Assert.assertEquals(0.0, org.apache.commons.math.geometry.Vector3D.distance(org.apache.commons.math.geometry.Vector3D.MINUS_I, org.apache.commons.math.geometry.Vector3D.MINUS_I), 0);
		junit.framework.Assert.assertEquals(java.lang.Math.sqrt(50), org.apache.commons.math.geometry.Vector3D.distance(v1, v2), 1.0E-12);
		junit.framework.Assert.assertEquals(v1.subtract(v2).getNorm(), org.apache.commons.math.geometry.Vector3D.distance(v1, v2), 1.0E-12);
	}

	public void testDistanceSq() {
		org.apache.commons.math.geometry.Vector3D v1 = new org.apache.commons.math.geometry.Vector3D(1 , -2 , 3);
		org.apache.commons.math.geometry.Vector3D v2 = new org.apache.commons.math.geometry.Vector3D(-4 , 2 , 0);
		junit.framework.Assert.assertEquals(0.0, org.apache.commons.math.geometry.Vector3D.distanceSq(org.apache.commons.math.geometry.Vector3D.MINUS_I, org.apache.commons.math.geometry.Vector3D.MINUS_I), 0);
		junit.framework.Assert.assertEquals(50.0, org.apache.commons.math.geometry.Vector3D.distanceSq(v1, v2), 1.0E-12);
		junit.framework.Assert.assertEquals(((org.apache.commons.math.geometry.Vector3D.distance(v1, v2)) * (org.apache.commons.math.geometry.Vector3D.distance(v1, v2))), org.apache.commons.math.geometry.Vector3D.distanceSq(v1, v2), 1.0E-12);
	}

	public void testDistanceInf() {
		org.apache.commons.math.geometry.Vector3D v1 = new org.apache.commons.math.geometry.Vector3D(1 , -2 , 3);
		org.apache.commons.math.geometry.Vector3D v2 = new org.apache.commons.math.geometry.Vector3D(-4 , 2 , 0);
		junit.framework.Assert.assertEquals(0.0, org.apache.commons.math.geometry.Vector3D.distanceInf(org.apache.commons.math.geometry.Vector3D.MINUS_I, org.apache.commons.math.geometry.Vector3D.MINUS_I), 0);
		junit.framework.Assert.assertEquals(5.0, org.apache.commons.math.geometry.Vector3D.distanceInf(v1, v2), 1.0E-12);
		junit.framework.Assert.assertEquals(v1.subtract(v2).getNormInf(), org.apache.commons.math.geometry.Vector3D.distanceInf(v1, v2), 1.0E-12);
	}

	public void testSubtract() {
		org.apache.commons.math.geometry.Vector3D v1 = new org.apache.commons.math.geometry.Vector3D(1 , 2 , 3);
		org.apache.commons.math.geometry.Vector3D v2 = new org.apache.commons.math.geometry.Vector3D(-3 , -2 , -1);
		v1 = v1.subtract(v2);
		checkVector(v1, 4, 4, 4);
		checkVector(v2.subtract(v1), -7, -6, -5);
		checkVector(v2.subtract(3, v1), -15, -14, -13);
	}

	public void testAdd() {
		org.apache.commons.math.geometry.Vector3D v1 = new org.apache.commons.math.geometry.Vector3D(1 , 2 , 3);
		org.apache.commons.math.geometry.Vector3D v2 = new org.apache.commons.math.geometry.Vector3D(-3 , -2 , -1);
		v1 = v1.add(v2);
		checkVector(v1, -2, 0, 2);
		checkVector(v2.add(v1), -5, -2, 1);
		checkVector(v2.add(3, v1), -9, -2, 5);
	}

	public void testScalarProduct() {
		org.apache.commons.math.geometry.Vector3D v = new org.apache.commons.math.geometry.Vector3D(1 , 2 , 3);
		v = v.scalarMultiply(3);
		checkVector(v, 3, 6, 9);
		checkVector(v.scalarMultiply(0.5), 1.5, 3, 4.5);
	}

	public void testVectorialProducts() {
		org.apache.commons.math.geometry.Vector3D v1 = new org.apache.commons.math.geometry.Vector3D(2 , 1 , -4);
		org.apache.commons.math.geometry.Vector3D v2 = new org.apache.commons.math.geometry.Vector3D(3 , 1 , -1);
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(((org.apache.commons.math.geometry.Vector3D.dotProduct(v1, v2)) - 11))) < 1.0E-12));
		org.apache.commons.math.geometry.Vector3D v3 = org.apache.commons.math.geometry.Vector3D.crossProduct(v1, v2);
		checkVector(v3, 3, -10, -1);
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(org.apache.commons.math.geometry.Vector3D.dotProduct(v1, v3))) < 1.0E-12));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(org.apache.commons.math.geometry.Vector3D.dotProduct(v2, v3))) < 1.0E-12));
	}

	public void testAngular() {
		junit.framework.Assert.assertEquals(0, org.apache.commons.math.geometry.Vector3D.PLUS_I.getAlpha(), 1.0E-10);
		junit.framework.Assert.assertEquals(0, org.apache.commons.math.geometry.Vector3D.PLUS_I.getDelta(), 1.0E-10);
		junit.framework.Assert.assertEquals(((java.lang.Math.PI) / 2), org.apache.commons.math.geometry.Vector3D.PLUS_J.getAlpha(), 1.0E-10);
		junit.framework.Assert.assertEquals(0, org.apache.commons.math.geometry.Vector3D.PLUS_J.getDelta(), 1.0E-10);
		junit.framework.Assert.assertEquals(0, org.apache.commons.math.geometry.Vector3D.PLUS_K.getAlpha(), 1.0E-10);
		junit.framework.Assert.assertEquals(((java.lang.Math.PI) / 2), org.apache.commons.math.geometry.Vector3D.PLUS_K.getDelta(), 1.0E-10);
		org.apache.commons.math.geometry.Vector3D u = new org.apache.commons.math.geometry.Vector3D(-1 , 1 , -1);
		junit.framework.Assert.assertEquals(((3 * (java.lang.Math.PI)) / 4), u.getAlpha(), 1.0E-10);
		junit.framework.Assert.assertEquals(((-1.0) / (java.lang.Math.sqrt(3))), java.lang.Math.sin(u.getDelta()), 1.0E-10);
	}

	public void testAngularSeparation() {
		org.apache.commons.math.geometry.Vector3D v1 = new org.apache.commons.math.geometry.Vector3D(2 , -1 , 4);
		org.apache.commons.math.geometry.Vector3D k = v1.normalize();
		org.apache.commons.math.geometry.Vector3D i = k.orthogonal();
		org.apache.commons.math.geometry.Vector3D v2 = k.scalarMultiply(java.lang.Math.cos(1.2)).add(i.scalarMultiply(java.lang.Math.sin(1.2)));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(((org.apache.commons.math.geometry.Vector3D.angle(v1, v2)) - 1.2))) < 1.0E-12));
	}

	public void testNormalize() {
		junit.framework.Assert.assertEquals(1.0, new org.apache.commons.math.geometry.Vector3D(5 , -4 , 2).normalize().getNorm(), 1.0E-12);
		try {
			org.apache.commons.math.geometry.Vector3D.ZERO.normalize();
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.lang.ArithmeticException ae) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail(("wrong exception caught: " + (e.getMessage())));
		}
	}

	public void testOrthogonal() {
		org.apache.commons.math.geometry.Vector3D v1 = new org.apache.commons.math.geometry.Vector3D(0.1 , 2.5 , 1.3);
		junit.framework.Assert.assertEquals(0.0, org.apache.commons.math.geometry.Vector3D.dotProduct(v1, v1.orthogonal()), 1.0E-12);
		org.apache.commons.math.geometry.Vector3D v2 = new org.apache.commons.math.geometry.Vector3D(2.3 , -0.003 , 7.6);
		junit.framework.Assert.assertEquals(0.0, org.apache.commons.math.geometry.Vector3D.dotProduct(v2, v2.orthogonal()), 1.0E-12);
		org.apache.commons.math.geometry.Vector3D v3 = new org.apache.commons.math.geometry.Vector3D(-1.7 , 1.4 , 0.2);
		junit.framework.Assert.assertEquals(0.0, org.apache.commons.math.geometry.Vector3D.dotProduct(v3, v3.orthogonal()), 1.0E-12);
		try {
			new org.apache.commons.math.geometry.Vector3D(0 , 0 , 0).orthogonal();
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.lang.ArithmeticException ae) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail(("wrong exception caught: " + (e.getMessage())));
		}
	}

	public void testAngle() {
		junit.framework.Assert.assertEquals(0.22572612855273394, org.apache.commons.math.geometry.Vector3D.angle(new org.apache.commons.math.geometry.Vector3D(1 , 2 , 3), new org.apache.commons.math.geometry.Vector3D(4 , 5 , 6)), 1.0E-12);
		junit.framework.Assert.assertEquals(7.985956206861066E-8, org.apache.commons.math.geometry.Vector3D.angle(new org.apache.commons.math.geometry.Vector3D(1 , 2 , 3), new org.apache.commons.math.geometry.Vector3D(2 , 4 , 6.000001)), 1.0E-12);
		junit.framework.Assert.assertEquals(3.141592573730231, org.apache.commons.math.geometry.Vector3D.angle(new org.apache.commons.math.geometry.Vector3D(1 , 2 , 3), new org.apache.commons.math.geometry.Vector3D(-2 , -4 , -6.000001)), 1.0E-12);
		try {
			org.apache.commons.math.geometry.Vector3D.angle(org.apache.commons.math.geometry.Vector3D.ZERO, org.apache.commons.math.geometry.Vector3D.PLUS_I);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.lang.ArithmeticException ae) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail(("wrong exception caught: " + (e.getMessage())));
		}
	}

	private void checkVector(org.apache.commons.math.geometry.Vector3D v, double x, double y, double z) {
		junit.framework.Assert.assertEquals(x, v.getX(), 1.0E-12);
		junit.framework.Assert.assertEquals(y, v.getY(), 1.0E-12);
		junit.framework.Assert.assertEquals(z, v.getZ(), 1.0E-12);
	}
}

