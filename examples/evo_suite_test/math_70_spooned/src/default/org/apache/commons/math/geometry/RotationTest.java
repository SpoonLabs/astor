package org.apache.commons.math.geometry;


public class RotationTest extends junit.framework.TestCase {
	public RotationTest(java.lang.String name) {
		super(name);
	}

	public void testIdentity() {
		org.apache.commons.math.geometry.Rotation r = org.apache.commons.math.geometry.Rotation.IDENTITY;
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_I), org.apache.commons.math.geometry.Vector3D.PLUS_I);
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_J), org.apache.commons.math.geometry.Vector3D.PLUS_J);
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_K), org.apache.commons.math.geometry.Vector3D.PLUS_K);
		checkAngle(r.getAngle(), 0);
		r = new org.apache.commons.math.geometry.Rotation(-1 , 0 , 0 , 0 , false);
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_I), org.apache.commons.math.geometry.Vector3D.PLUS_I);
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_J), org.apache.commons.math.geometry.Vector3D.PLUS_J);
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_K), org.apache.commons.math.geometry.Vector3D.PLUS_K);
		checkAngle(r.getAngle(), 0);
		r = new org.apache.commons.math.geometry.Rotation(42 , 0 , 0 , 0 , true);
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_I), org.apache.commons.math.geometry.Vector3D.PLUS_I);
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_J), org.apache.commons.math.geometry.Vector3D.PLUS_J);
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_K), org.apache.commons.math.geometry.Vector3D.PLUS_K);
		checkAngle(r.getAngle(), 0);
	}

	public void testAxisAngle() {
		org.apache.commons.math.geometry.Rotation r = new org.apache.commons.math.geometry.Rotation(new org.apache.commons.math.geometry.Vector3D(10 , 10 , 10) , ((2 * (java.lang.Math.PI)) / 3));
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_I), org.apache.commons.math.geometry.Vector3D.PLUS_J);
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_J), org.apache.commons.math.geometry.Vector3D.PLUS_K);
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_K), org.apache.commons.math.geometry.Vector3D.PLUS_I);
		double s = 1 / (java.lang.Math.sqrt(3));
		checkVector(r.getAxis(), new org.apache.commons.math.geometry.Vector3D(s , s , s));
		checkAngle(r.getAngle(), ((2 * (java.lang.Math.PI)) / 3));
		try {
			new org.apache.commons.math.geometry.Rotation(new org.apache.commons.math.geometry.Vector3D(0 , 0 , 0) , ((2 * (java.lang.Math.PI)) / 3));
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.lang.ArithmeticException e) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("unexpected exception");
		}
		r = new org.apache.commons.math.geometry.Rotation(org.apache.commons.math.geometry.Vector3D.PLUS_K , (1.5 * (java.lang.Math.PI)));
		checkVector(r.getAxis(), new org.apache.commons.math.geometry.Vector3D(0 , 0 , -1));
		checkAngle(r.getAngle(), (0.5 * (java.lang.Math.PI)));
		r = new org.apache.commons.math.geometry.Rotation(org.apache.commons.math.geometry.Vector3D.PLUS_J , java.lang.Math.PI);
		checkVector(r.getAxis(), org.apache.commons.math.geometry.Vector3D.PLUS_J);
		checkAngle(r.getAngle(), java.lang.Math.PI);
		checkVector(org.apache.commons.math.geometry.Rotation.IDENTITY.getAxis(), org.apache.commons.math.geometry.Vector3D.PLUS_I);
	}

	public void testRevert() {
		org.apache.commons.math.geometry.Rotation r = new org.apache.commons.math.geometry.Rotation(0.001 , 0.36 , 0.48 , 0.8 , true);
		org.apache.commons.math.geometry.Rotation reverted = r.revert();
		checkRotation(r.applyTo(reverted), 1, 0, 0, 0);
		checkRotation(reverted.applyTo(r), 1, 0, 0, 0);
		junit.framework.Assert.assertEquals(r.getAngle(), reverted.getAngle(), 1.0E-12);
		junit.framework.Assert.assertEquals(-1, org.apache.commons.math.geometry.Vector3D.dotProduct(r.getAxis(), reverted.getAxis()), 1.0E-12);
	}

	public void testVectorOnePair() {
		org.apache.commons.math.geometry.Vector3D u = new org.apache.commons.math.geometry.Vector3D(3 , 2 , 1);
		org.apache.commons.math.geometry.Vector3D v = new org.apache.commons.math.geometry.Vector3D(-4 , 2 , 2);
		org.apache.commons.math.geometry.Rotation r = new org.apache.commons.math.geometry.Rotation(u , v);
		checkVector(r.applyTo(u.scalarMultiply(v.getNorm())), v.scalarMultiply(u.getNorm()));
		checkAngle(new org.apache.commons.math.geometry.Rotation(u , u.negate()).getAngle(), java.lang.Math.PI);
		try {
			new org.apache.commons.math.geometry.Rotation(u , org.apache.commons.math.geometry.Vector3D.ZERO);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException e) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("unexpected exception");
		}
	}

	public void testVectorTwoPairs() {
		org.apache.commons.math.geometry.Vector3D u1 = new org.apache.commons.math.geometry.Vector3D(3 , 0 , 0);
		org.apache.commons.math.geometry.Vector3D u2 = new org.apache.commons.math.geometry.Vector3D(0 , 5 , 0);
		org.apache.commons.math.geometry.Vector3D v1 = new org.apache.commons.math.geometry.Vector3D(0 , 0 , 2);
		org.apache.commons.math.geometry.Vector3D v2 = new org.apache.commons.math.geometry.Vector3D(-2 , 0 , 2);
		org.apache.commons.math.geometry.Rotation r = new org.apache.commons.math.geometry.Rotation(u1 , u2 , v1 , v2);
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_I), org.apache.commons.math.geometry.Vector3D.PLUS_K);
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_J), org.apache.commons.math.geometry.Vector3D.MINUS_I);
		r = new org.apache.commons.math.geometry.Rotation(u1 , u2 , u1.negate() , u2.negate());
		org.apache.commons.math.geometry.Vector3D axis = r.getAxis();
		if ((org.apache.commons.math.geometry.Vector3D.dotProduct(axis, org.apache.commons.math.geometry.Vector3D.PLUS_K)) > 0) {
			checkVector(axis, org.apache.commons.math.geometry.Vector3D.PLUS_K);
		} else {
			checkVector(axis, org.apache.commons.math.geometry.Vector3D.MINUS_K);
		}
		checkAngle(r.getAngle(), java.lang.Math.PI);
		double sqrt = (java.lang.Math.sqrt(2)) / 2;
		r = new org.apache.commons.math.geometry.Rotation(org.apache.commons.math.geometry.Vector3D.PLUS_I , org.apache.commons.math.geometry.Vector3D.PLUS_J , new org.apache.commons.math.geometry.Vector3D(0.5 , 0.5 , sqrt) , new org.apache.commons.math.geometry.Vector3D(0.5 , 0.5 , -sqrt));
		checkRotation(r, sqrt, 0.5, 0.5, 0);
		r = new org.apache.commons.math.geometry.Rotation(u1 , u2 , u1 , org.apache.commons.math.geometry.Vector3D.crossProduct(u1, u2));
		checkRotation(r, sqrt, -sqrt, 0, 0);
		checkRotation(new org.apache.commons.math.geometry.Rotation(u1 , u2 , u1 , u2), 1, 0, 0, 0);
		try {
			new org.apache.commons.math.geometry.Rotation(u1 , u2 , org.apache.commons.math.geometry.Vector3D.ZERO , v2);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException e) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("unexpected exception");
		}
	}

	public void testMatrix() throws org.apache.commons.math.geometry.NotARotationMatrixException {
		try {
			new org.apache.commons.math.geometry.Rotation(new double[][]{ new double[]{ 0.0 , 1.0 , 0.0 } , new double[]{ 1.0 , 0.0 , 0.0 } } , 1.0E-7);
		} catch (org.apache.commons.math.geometry.NotARotationMatrixException nrme) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail(("wrong exception caught: " + (e.getMessage())));
		}
		try {
			new org.apache.commons.math.geometry.Rotation(new double[][]{ new double[]{ 0.445888 , 0.797184 , -0.40704 } , new double[]{ 0.82176 , -0.18432 , 0.5392 } , new double[]{ -0.354816 , 0.574912 , 0.73728 } } , 1.0E-7);
		} catch (org.apache.commons.math.geometry.NotARotationMatrixException nrme) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail(("wrong exception caught: " + (e.getMessage())));
		}
		try {
			new org.apache.commons.math.geometry.Rotation(new double[][]{ new double[]{ 0.4 , 0.8 , -0.4 } , new double[]{ -0.4 , 0.6 , 0.7 } , new double[]{ 0.8 , -0.2 , 0.5 } } , 1.0E-15);
		} catch (org.apache.commons.math.geometry.NotARotationMatrixException nrme) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail(("wrong exception caught: " + (e.getMessage())));
		}
		checkRotation(new org.apache.commons.math.geometry.Rotation(new double[][]{ new double[]{ 0.445888 , 0.797184 , -0.40704 } , new double[]{ -0.354816 , 0.574912 , 0.73728 } , new double[]{ 0.82176 , -0.18432 , 0.5392 } } , 1.0E-10), 0.8, 0.288, 0.384, 0.36);
		checkRotation(new org.apache.commons.math.geometry.Rotation(new double[][]{ new double[]{ 0.5392 , 0.73728 , 0.40704 } , new double[]{ 0.18432 , -0.574912 , 0.797184 } , new double[]{ 0.82176 , -0.354816 , -0.445888 } } , 1.0E-10), 0.36, 0.8, 0.288, 0.384);
		checkRotation(new org.apache.commons.math.geometry.Rotation(new double[][]{ new double[]{ -0.445888 , 0.797184 , -0.40704 } , new double[]{ 0.354816 , 0.574912 , 0.73728 } , new double[]{ 0.82176 , 0.18432 , -0.5392 } } , 1.0E-10), 0.384, 0.36, 0.8, 0.288);
		checkRotation(new org.apache.commons.math.geometry.Rotation(new double[][]{ new double[]{ -0.5392 , 0.73728 , 0.40704 } , new double[]{ -0.18432 , -0.574912 , 0.797184 } , new double[]{ 0.82176 , 0.354816 , 0.445888 } } , 1.0E-10), 0.288, 0.384, 0.36, 0.8);
		double[][] m1 = new double[][]{ new double[]{ 0.0 , 1.0 , 0.0 } , new double[]{ 0.0 , 0.0 , 1.0 } , new double[]{ 1.0 , 0.0 , 0.0 } };
		org.apache.commons.math.geometry.Rotation r = new org.apache.commons.math.geometry.Rotation(m1 , 1.0E-7);
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_I), org.apache.commons.math.geometry.Vector3D.PLUS_K);
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_J), org.apache.commons.math.geometry.Vector3D.PLUS_I);
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_K), org.apache.commons.math.geometry.Vector3D.PLUS_J);
		double[][] m2 = new double[][]{ new double[]{ 0.83203 , -0.55012 , -0.07139 } , new double[]{ 0.48293 , 0.78164 , -0.39474 } , new double[]{ 0.27296 , 0.29396 , 0.91602 } };
		r = new org.apache.commons.math.geometry.Rotation(m2 , 1.0E-12);
		double[][] m3 = r.getMatrix();
		double d00 = (m2[0][0]) - (m3[0][0]);
		double d01 = (m2[0][1]) - (m3[0][1]);
		double d02 = (m2[0][2]) - (m3[0][2]);
		double d10 = (m2[1][0]) - (m3[1][0]);
		double d11 = (m2[1][1]) - (m3[1][1]);
		double d12 = (m2[1][2]) - (m3[1][2]);
		double d20 = (m2[2][0]) - (m3[2][0]);
		double d21 = (m2[2][1]) - (m3[2][1]);
		double d22 = (m2[2][2]) - (m3[2][2]);
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(d00)) < 6.0E-6));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(d01)) < 6.0E-6));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(d02)) < 6.0E-6));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(d10)) < 6.0E-6));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(d11)) < 6.0E-6));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(d12)) < 6.0E-6));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(d20)) < 6.0E-6));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(d21)) < 6.0E-6));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(d22)) < 6.0E-6));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(d00)) > 4.0E-7));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(d01)) > 4.0E-7));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(d02)) > 4.0E-7));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(d10)) > 4.0E-7));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(d11)) > 4.0E-7));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(d12)) > 4.0E-7));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(d20)) > 4.0E-7));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(d21)) > 4.0E-7));
		junit.framework.Assert.assertTrue(((java.lang.Math.abs(d22)) > 4.0E-7));
		for (int i = 0 ; i < 3 ; ++i) {
			for (int j = 0 ; j < 3 ; ++j) {
				double m3tm3 = (((m3[i][0]) * (m3[j][0])) + ((m3[i][1]) * (m3[j][1]))) + ((m3[i][2]) * (m3[j][2]));
				if (i == j) {
					junit.framework.Assert.assertTrue(((java.lang.Math.abs((m3tm3 - 1.0))) < 1.0E-10));
				} else {
					junit.framework.Assert.assertTrue(((java.lang.Math.abs(m3tm3)) < 1.0E-10));
				}
			}
		}
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_I), new org.apache.commons.math.geometry.Vector3D(m3[0][0] , m3[1][0] , m3[2][0]));
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_J), new org.apache.commons.math.geometry.Vector3D(m3[0][1] , m3[1][1] , m3[2][1]));
		checkVector(r.applyTo(org.apache.commons.math.geometry.Vector3D.PLUS_K), new org.apache.commons.math.geometry.Vector3D(m3[0][2] , m3[1][2] , m3[2][2]));
		double[][] m4 = new double[][]{ new double[]{ 1.0 , 0.0 , 0.0 } , new double[]{ 0.0 , -1.0 , 0.0 } , new double[]{ 0.0 , 0.0 , -1.0 } };
		r = new org.apache.commons.math.geometry.Rotation(m4 , 1.0E-7);
		checkAngle(r.getAngle(), java.lang.Math.PI);
		try {
			double[][] m5 = new double[][]{ new double[]{ 0.0 , 0.0 , 1.0 } , new double[]{ 0.0 , 1.0 , 0.0 } , new double[]{ 1.0 , 0.0 , 0.0 } };
			r = new org.apache.commons.math.geometry.Rotation(m5 , 1.0E-7);
			junit.framework.Assert.fail((("got " + r) + ", should have caught an exception"));
		} catch (org.apache.commons.math.geometry.NotARotationMatrixException e) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testAngles() throws org.apache.commons.math.geometry.CardanEulerSingularityException {
		org.apache.commons.math.geometry.RotationOrder[] CardanOrders = new org.apache.commons.math.geometry.RotationOrder[]{ org.apache.commons.math.geometry.RotationOrder.XYZ , org.apache.commons.math.geometry.RotationOrder.XZY , org.apache.commons.math.geometry.RotationOrder.YXZ , org.apache.commons.math.geometry.RotationOrder.YZX , org.apache.commons.math.geometry.RotationOrder.ZXY , org.apache.commons.math.geometry.RotationOrder.ZYX };
		for (int i = 0 ; i < (CardanOrders.length) ; ++i) {
			for (double alpha1 = 0.1 ; alpha1 < 6.2 ; alpha1 += 0.3) {
				for (double alpha2 = -1.55 ; alpha2 < 1.55 ; alpha2 += 0.3) {
					for (double alpha3 = 0.1 ; alpha3 < 6.2 ; alpha3 += 0.3) {
						org.apache.commons.math.geometry.Rotation r = new org.apache.commons.math.geometry.Rotation(CardanOrders[i] , alpha1 , alpha2 , alpha3);
						double[] angles = r.getAngles(CardanOrders[i]);
						checkAngle(angles[0], alpha1);
						checkAngle(angles[1], alpha2);
						checkAngle(angles[2], alpha3);
					}
				}
			}
		}
		org.apache.commons.math.geometry.RotationOrder[] EulerOrders = new org.apache.commons.math.geometry.RotationOrder[]{ org.apache.commons.math.geometry.RotationOrder.XYX , org.apache.commons.math.geometry.RotationOrder.XZX , org.apache.commons.math.geometry.RotationOrder.YXY , org.apache.commons.math.geometry.RotationOrder.YZY , org.apache.commons.math.geometry.RotationOrder.ZXZ , org.apache.commons.math.geometry.RotationOrder.ZYZ };
		for (int i = 0 ; i < (EulerOrders.length) ; ++i) {
			for (double alpha1 = 0.1 ; alpha1 < 6.2 ; alpha1 += 0.3) {
				for (double alpha2 = 0.05 ; alpha2 < 3.1 ; alpha2 += 0.3) {
					for (double alpha3 = 0.1 ; alpha3 < 6.2 ; alpha3 += 0.3) {
						org.apache.commons.math.geometry.Rotation r = new org.apache.commons.math.geometry.Rotation(EulerOrders[i] , alpha1 , alpha2 , alpha3);
						double[] angles = r.getAngles(EulerOrders[i]);
						checkAngle(angles[0], alpha1);
						checkAngle(angles[1], alpha2);
						checkAngle(angles[2], alpha3);
					}
				}
			}
		}
	}

	public void testSingularities() {
		org.apache.commons.math.geometry.RotationOrder[] CardanOrders = new org.apache.commons.math.geometry.RotationOrder[]{ org.apache.commons.math.geometry.RotationOrder.XYZ , org.apache.commons.math.geometry.RotationOrder.XZY , org.apache.commons.math.geometry.RotationOrder.YXZ , org.apache.commons.math.geometry.RotationOrder.YZX , org.apache.commons.math.geometry.RotationOrder.ZXY , org.apache.commons.math.geometry.RotationOrder.ZYX };
		double[] singularCardanAngle = new double[]{ (java.lang.Math.PI) / 2 , (-(java.lang.Math.PI)) / 2 };
		for (int i = 0 ; i < (CardanOrders.length) ; ++i) {
			for (int j = 0 ; j < (singularCardanAngle.length) ; ++j) {
				org.apache.commons.math.geometry.Rotation r = new org.apache.commons.math.geometry.Rotation(CardanOrders[i] , 0.1 , singularCardanAngle[j] , 0.3);
				try {
					r.getAngles(CardanOrders[i]);
					junit.framework.Assert.fail("an exception should have been caught");
				} catch (org.apache.commons.math.geometry.CardanEulerSingularityException cese) {
				} catch (java.lang.Exception e) {
					junit.framework.Assert.fail(("wrong exception caught: " + (e.getMessage())));
				}
			}
		}
		org.apache.commons.math.geometry.RotationOrder[] EulerOrders = new org.apache.commons.math.geometry.RotationOrder[]{ org.apache.commons.math.geometry.RotationOrder.XYX , org.apache.commons.math.geometry.RotationOrder.XZX , org.apache.commons.math.geometry.RotationOrder.YXY , org.apache.commons.math.geometry.RotationOrder.YZY , org.apache.commons.math.geometry.RotationOrder.ZXZ , org.apache.commons.math.geometry.RotationOrder.ZYZ };
		double[] singularEulerAngle = new double[]{ 0 , java.lang.Math.PI };
		for (int i = 0 ; i < (EulerOrders.length) ; ++i) {
			for (int j = 0 ; j < (singularEulerAngle.length) ; ++j) {
				org.apache.commons.math.geometry.Rotation r = new org.apache.commons.math.geometry.Rotation(EulerOrders[i] , 0.1 , singularEulerAngle[j] , 0.3);
				try {
					r.getAngles(EulerOrders[i]);
					junit.framework.Assert.fail("an exception should have been caught");
				} catch (org.apache.commons.math.geometry.CardanEulerSingularityException cese) {
				} catch (java.lang.Exception e) {
					junit.framework.Assert.fail(("wrong exception caught: " + (e.getMessage())));
				}
			}
		}
	}

	public void testQuaternion() {
		org.apache.commons.math.geometry.Rotation r1 = new org.apache.commons.math.geometry.Rotation(new org.apache.commons.math.geometry.Vector3D(2 , -3 , 5) , 1.7);
		double n = 23.5;
		org.apache.commons.math.geometry.Rotation r2 = new org.apache.commons.math.geometry.Rotation((n * (r1.getQ0())) , (n * (r1.getQ1())) , (n * (r1.getQ2())) , (n * (r1.getQ3())) , true);
		for (double x = -0.9 ; x < 0.9 ; x += 0.2) {
			for (double y = -0.9 ; y < 0.9 ; y += 0.2) {
				for (double z = -0.9 ; z < 0.9 ; z += 0.2) {
					org.apache.commons.math.geometry.Vector3D u = new org.apache.commons.math.geometry.Vector3D(x , y , z);
					checkVector(r2.applyTo(u), r1.applyTo(u));
				}
			}
		}
		r1 = new org.apache.commons.math.geometry.Rotation(0.288 , 0.384 , 0.36 , 0.8 , false);
		checkRotation(r1, -(r1.getQ0()), -(r1.getQ1()), -(r1.getQ2()), -(r1.getQ3()));
	}

	public void testCompose() {
		org.apache.commons.math.geometry.Rotation r1 = new org.apache.commons.math.geometry.Rotation(new org.apache.commons.math.geometry.Vector3D(2 , -3 , 5) , 1.7);
		org.apache.commons.math.geometry.Rotation r2 = new org.apache.commons.math.geometry.Rotation(new org.apache.commons.math.geometry.Vector3D(-1 , 3 , 2) , 0.3);
		org.apache.commons.math.geometry.Rotation r3 = r2.applyTo(r1);
		for (double x = -0.9 ; x < 0.9 ; x += 0.2) {
			for (double y = -0.9 ; y < 0.9 ; y += 0.2) {
				for (double z = -0.9 ; z < 0.9 ; z += 0.2) {
					org.apache.commons.math.geometry.Vector3D u = new org.apache.commons.math.geometry.Vector3D(x , y , z);
					checkVector(r2.applyTo(r1.applyTo(u)), r3.applyTo(u));
				}
			}
		}
	}

	public void testComposeInverse() {
		org.apache.commons.math.geometry.Rotation r1 = new org.apache.commons.math.geometry.Rotation(new org.apache.commons.math.geometry.Vector3D(2 , -3 , 5) , 1.7);
		org.apache.commons.math.geometry.Rotation r2 = new org.apache.commons.math.geometry.Rotation(new org.apache.commons.math.geometry.Vector3D(-1 , 3 , 2) , 0.3);
		org.apache.commons.math.geometry.Rotation r3 = r2.applyInverseTo(r1);
		for (double x = -0.9 ; x < 0.9 ; x += 0.2) {
			for (double y = -0.9 ; y < 0.9 ; y += 0.2) {
				for (double z = -0.9 ; z < 0.9 ; z += 0.2) {
					org.apache.commons.math.geometry.Vector3D u = new org.apache.commons.math.geometry.Vector3D(x , y , z);
					checkVector(r2.applyInverseTo(r1.applyTo(u)), r3.applyTo(u));
				}
			}
		}
	}

	public void testApplyInverseTo() {
		org.apache.commons.math.geometry.Rotation r = new org.apache.commons.math.geometry.Rotation(new org.apache.commons.math.geometry.Vector3D(2 , -3 , 5) , 1.7);
		for (double lambda = 0 ; lambda < 6.2 ; lambda += 0.2) {
			for (double phi = -1.55 ; phi < 1.55 ; phi += 0.2) {
				org.apache.commons.math.geometry.Vector3D u = new org.apache.commons.math.geometry.Vector3D(((java.lang.Math.cos(lambda)) * (java.lang.Math.cos(phi))) , ((java.lang.Math.sin(lambda)) * (java.lang.Math.cos(phi))) , java.lang.Math.sin(phi));
				r.applyInverseTo(r.applyTo(u));
				checkVector(u, r.applyInverseTo(r.applyTo(u)));
				checkVector(u, r.applyTo(r.applyInverseTo(u)));
			}
		}
		r = org.apache.commons.math.geometry.Rotation.IDENTITY;
		for (double lambda = 0 ; lambda < 6.2 ; lambda += 0.2) {
			for (double phi = -1.55 ; phi < 1.55 ; phi += 0.2) {
				org.apache.commons.math.geometry.Vector3D u = new org.apache.commons.math.geometry.Vector3D(((java.lang.Math.cos(lambda)) * (java.lang.Math.cos(phi))) , ((java.lang.Math.sin(lambda)) * (java.lang.Math.cos(phi))) , java.lang.Math.sin(phi));
				checkVector(u, r.applyInverseTo(r.applyTo(u)));
				checkVector(u, r.applyTo(r.applyInverseTo(u)));
			}
		}
		r = new org.apache.commons.math.geometry.Rotation(org.apache.commons.math.geometry.Vector3D.PLUS_K , java.lang.Math.PI);
		for (double lambda = 0 ; lambda < 6.2 ; lambda += 0.2) {
			for (double phi = -1.55 ; phi < 1.55 ; phi += 0.2) {
				org.apache.commons.math.geometry.Vector3D u = new org.apache.commons.math.geometry.Vector3D(((java.lang.Math.cos(lambda)) * (java.lang.Math.cos(phi))) , ((java.lang.Math.sin(lambda)) * (java.lang.Math.cos(phi))) , java.lang.Math.sin(phi));
				checkVector(u, r.applyInverseTo(r.applyTo(u)));
				checkVector(u, r.applyTo(r.applyInverseTo(u)));
			}
		}
	}

	private void checkVector(org.apache.commons.math.geometry.Vector3D v1, org.apache.commons.math.geometry.Vector3D v2) {
		junit.framework.Assert.assertTrue(((v1.subtract(v2).getNorm()) < 1.0E-10));
	}

	private void checkAngle(double a1, double a2) {
		junit.framework.Assert.assertEquals(a1, org.apache.commons.math.util.MathUtils.normalizeAngle(a2, a1), 1.0E-10);
	}

	private void checkRotation(org.apache.commons.math.geometry.Rotation r, double q0, double q1, double q2, double q3) {
		junit.framework.Assert.assertEquals(0, org.apache.commons.math.geometry.Rotation.distance(r, new org.apache.commons.math.geometry.Rotation(q0 , q1 , q2 , q3 , false)), 1.0E-12);
	}
}

