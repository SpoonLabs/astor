package org.apache.commons.math.util;


public class ResizableDoubleArrayTest extends org.apache.commons.math.util.DoubleArrayAbstractTest {
	public ResizableDoubleArrayTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	protected void tearDown() throws java.lang.Exception {
		da = null;
		ra = null;
	}

	@java.lang.Override
	protected void setUp() throws java.lang.Exception {
		da = new org.apache.commons.math.util.ResizableDoubleArray();
		ra = new org.apache.commons.math.util.ResizableDoubleArray();
	}

	public void testConstructors() {
		float defaultExpansionFactor = 2.0F;
		float defaultContractionCriteria = 2.5F;
		int defaultMode = org.apache.commons.math.util.ResizableDoubleArray.MULTIPLICATIVE_MODE;
		org.apache.commons.math.util.ResizableDoubleArray testDa = new org.apache.commons.math.util.ResizableDoubleArray(2);
		junit.framework.Assert.assertEquals(0, testDa.getNumElements());
		junit.framework.Assert.assertEquals(2, testDa.getInternalLength());
		junit.framework.Assert.assertEquals(defaultExpansionFactor, testDa.getExpansionFactor(), 0);
		junit.framework.Assert.assertEquals(defaultContractionCriteria, testDa.getContractionCriteria(), 0);
		junit.framework.Assert.assertEquals(defaultMode, testDa.getExpansionMode());
		try {
			da = new org.apache.commons.math.util.ResizableDoubleArray(-1);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		testDa = new org.apache.commons.math.util.ResizableDoubleArray(2 , 2.0F);
		junit.framework.Assert.assertEquals(0, testDa.getNumElements());
		junit.framework.Assert.assertEquals(2, testDa.getInternalLength());
		junit.framework.Assert.assertEquals(defaultExpansionFactor, testDa.getExpansionFactor(), 0);
		junit.framework.Assert.assertEquals(defaultContractionCriteria, testDa.getContractionCriteria(), 0);
		junit.framework.Assert.assertEquals(defaultMode, testDa.getExpansionMode());
		try {
			da = new org.apache.commons.math.util.ResizableDoubleArray(2 , 0.5F);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		testDa = new org.apache.commons.math.util.ResizableDoubleArray(2 , 3.0F);
		junit.framework.Assert.assertEquals(3.0F, testDa.getExpansionFactor(), 0);
		junit.framework.Assert.assertEquals(3.5F, testDa.getContractionCriteria(), 0);
		testDa = new org.apache.commons.math.util.ResizableDoubleArray(2 , 2.0F , 3.0F);
		junit.framework.Assert.assertEquals(0, testDa.getNumElements());
		junit.framework.Assert.assertEquals(2, testDa.getInternalLength());
		junit.framework.Assert.assertEquals(defaultExpansionFactor, testDa.getExpansionFactor(), 0);
		junit.framework.Assert.assertEquals(3.0F, testDa.getContractionCriteria(), 0);
		junit.framework.Assert.assertEquals(defaultMode, testDa.getExpansionMode());
		try {
			da = new org.apache.commons.math.util.ResizableDoubleArray(2 , 2.0F , 1.5F);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		testDa = new org.apache.commons.math.util.ResizableDoubleArray(2 , 2.0F , 3.0F , org.apache.commons.math.util.ResizableDoubleArray.ADDITIVE_MODE);
		junit.framework.Assert.assertEquals(0, testDa.getNumElements());
		junit.framework.Assert.assertEquals(2, testDa.getInternalLength());
		junit.framework.Assert.assertEquals(defaultExpansionFactor, testDa.getExpansionFactor(), 0);
		junit.framework.Assert.assertEquals(3.0F, testDa.getContractionCriteria(), 0);
		junit.framework.Assert.assertEquals(org.apache.commons.math.util.ResizableDoubleArray.ADDITIVE_MODE, testDa.getExpansionMode());
		try {
			da = new org.apache.commons.math.util.ResizableDoubleArray(2 , 2.0F , 2.5F , -1);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		testDa = new org.apache.commons.math.util.ResizableDoubleArray(2 , 2.0F , 3.0F , org.apache.commons.math.util.ResizableDoubleArray.ADDITIVE_MODE);
		testDa.addElement(2.0);
		testDa.addElement(3.2);
		org.apache.commons.math.util.ResizableDoubleArray copyDa = new org.apache.commons.math.util.ResizableDoubleArray(testDa);
		junit.framework.Assert.assertEquals(copyDa, testDa);
		junit.framework.Assert.assertEquals(testDa, copyDa);
	}

	public void testSetElementArbitraryExpansion() {
		da.addElement(2.0);
		da.addElement(4.0);
		da.addElement(6.0);
		da.setElement(1, 3.0);
		da.setElement(1000, 3.4);
		junit.framework.Assert.assertEquals("The number of elements should now be 1001, it isn't", da.getNumElements(), 1001);
		junit.framework.Assert.assertEquals("Uninitialized Elements are default value of 0.0, index 766 wasn't", 0.0, da.getElement(760), java.lang.Double.MIN_VALUE);
		junit.framework.Assert.assertEquals("The 1000th index should be 3.4, it isn't", 3.4, da.getElement(1000), java.lang.Double.MIN_VALUE);
		junit.framework.Assert.assertEquals("The 0th index should be 2.0, it isn't", 2.0, da.getElement(0), java.lang.Double.MIN_VALUE);
		da.clear();
		da.addElement(2.0);
		da.addElement(4.0);
		da.addElement(6.0);
		junit.framework.Assert.assertEquals(4, ((org.apache.commons.math.util.ResizableDoubleArray)(da)).getInternalLength());
		junit.framework.Assert.assertEquals(3, da.getNumElements());
		da.setElement(3, 7.0);
		junit.framework.Assert.assertEquals(4, ((org.apache.commons.math.util.ResizableDoubleArray)(da)).getInternalLength());
		junit.framework.Assert.assertEquals(4, da.getNumElements());
		da.setElement(10, 10.0);
		junit.framework.Assert.assertEquals(11, ((org.apache.commons.math.util.ResizableDoubleArray)(da)).getInternalLength());
		junit.framework.Assert.assertEquals(11, da.getNumElements());
		da.setElement(9, 10.0);
		junit.framework.Assert.assertEquals(11, ((org.apache.commons.math.util.ResizableDoubleArray)(da)).getInternalLength());
		junit.framework.Assert.assertEquals(11, da.getNumElements());
		try {
			da.setElement(-2, 3);
			junit.framework.Assert.fail("Expecting ArrayIndexOutOfBoundsException for negative index");
		} catch (java.lang.ArrayIndexOutOfBoundsException ex) {
		}
		org.apache.commons.math.util.ResizableDoubleArray testDa = new org.apache.commons.math.util.ResizableDoubleArray(2 , 2.0F , 3.0F , org.apache.commons.math.util.ResizableDoubleArray.ADDITIVE_MODE);
		junit.framework.Assert.assertEquals(2, testDa.getInternalLength());
		testDa.addElement(1.0);
		testDa.addElement(1.0);
		junit.framework.Assert.assertEquals(2, testDa.getInternalLength());
		testDa.addElement(1.0);
		junit.framework.Assert.assertEquals(4, testDa.getInternalLength());
	}

	@java.lang.Override
	public void testAdd1000() {
		super.testAdd1000();
		junit.framework.Assert.assertEquals(("Internal Storage length should be 1024 if we started out with initial capacity of " + "16 and an expansion factor of 2.0"), 1024, ((org.apache.commons.math.util.ResizableDoubleArray)(da)).getInternalLength());
	}

	@java.lang.Override
	public void testAddElementRolling() {
		super.testAddElementRolling();
		da.clear();
		da.addElement(1);
		da.addElement(2);
		da.addElementRolling(3);
		junit.framework.Assert.assertEquals(3, da.getElement(1), 0);
		da.addElementRolling(4);
		junit.framework.Assert.assertEquals(3, da.getElement(0), 0);
		junit.framework.Assert.assertEquals(4, da.getElement(1), 0);
		da.addElement(5);
		junit.framework.Assert.assertEquals(5, da.getElement(2), 0);
		da.addElementRolling(6);
		junit.framework.Assert.assertEquals(4, da.getElement(0), 0);
		junit.framework.Assert.assertEquals(5, da.getElement(1), 0);
		junit.framework.Assert.assertEquals(6, da.getElement(2), 0);
		org.apache.commons.math.util.ResizableDoubleArray testDa = new org.apache.commons.math.util.ResizableDoubleArray(2 , 2.0F , 2.5F , org.apache.commons.math.util.ResizableDoubleArray.ADDITIVE_MODE);
		junit.framework.Assert.assertEquals(2, testDa.getInternalLength());
		testDa.addElement(1.0);
		testDa.addElement(2.0);
		testDa.addElement(3.0);
		junit.framework.Assert.assertEquals(1.0, testDa.getElement(0), 0);
		junit.framework.Assert.assertEquals(2.0, testDa.getElement(1), 0);
		junit.framework.Assert.assertEquals(3.0, testDa.getElement(2), 0);
		junit.framework.Assert.assertEquals(4, testDa.getInternalLength());
		junit.framework.Assert.assertEquals(3, testDa.getNumElements());
		testDa.addElementRolling(4.0);
		junit.framework.Assert.assertEquals(2.0, testDa.getElement(0), 0);
		junit.framework.Assert.assertEquals(3.0, testDa.getElement(1), 0);
		junit.framework.Assert.assertEquals(4.0, testDa.getElement(2), 0);
		junit.framework.Assert.assertEquals(4, testDa.getInternalLength());
		junit.framework.Assert.assertEquals(3, testDa.getNumElements());
		testDa.addElementRolling(5.0);
		junit.framework.Assert.assertEquals(3.0, testDa.getElement(0), 0);
		junit.framework.Assert.assertEquals(4.0, testDa.getElement(1), 0);
		junit.framework.Assert.assertEquals(5.0, testDa.getElement(2), 0);
		junit.framework.Assert.assertEquals(4, testDa.getInternalLength());
		junit.framework.Assert.assertEquals(3, testDa.getNumElements());
		try {
			testDa.getElement(4);
			junit.framework.Assert.fail("Expecting ArrayIndexOutOfBoundsException");
		} catch (java.lang.ArrayIndexOutOfBoundsException ex) {
		}
		try {
			testDa.getElement(-1);
			junit.framework.Assert.fail("Expecting ArrayIndexOutOfBoundsException");
		} catch (java.lang.ArrayIndexOutOfBoundsException ex) {
		}
	}

	public void testSetNumberOfElements() {
		da.addElement(1.0);
		da.addElement(1.0);
		da.addElement(1.0);
		da.addElement(1.0);
		da.addElement(1.0);
		da.addElement(1.0);
		junit.framework.Assert.assertEquals("Number of elements should equal 6", da.getNumElements(), 6);
		((org.apache.commons.math.util.ResizableDoubleArray)(da)).setNumElements(3);
		junit.framework.Assert.assertEquals("Number of elements should equal 3", da.getNumElements(), 3);
		try {
			((org.apache.commons.math.util.ResizableDoubleArray)(da)).setNumElements(-3);
			junit.framework.Assert.fail("Setting number of elements to negative should've thrown an exception");
		} catch (java.lang.IllegalArgumentException iae) {
		}
		((org.apache.commons.math.util.ResizableDoubleArray)(da)).setNumElements(1024);
		junit.framework.Assert.assertEquals("Number of elements should now be 1024", da.getNumElements(), 1024);
		junit.framework.Assert.assertEquals("Element 453 should be a default double", da.getElement(453), 0.0, java.lang.Double.MIN_VALUE);
	}

	public void testWithInitialCapacity() {
		org.apache.commons.math.util.ResizableDoubleArray eDA2 = new org.apache.commons.math.util.ResizableDoubleArray(2);
		junit.framework.Assert.assertEquals("Initial number of elements should be 0", 0, eDA2.getNumElements());
		org.apache.commons.math.random.RandomData randomData = new org.apache.commons.math.random.RandomDataImpl();
		int iterations = randomData.nextInt(100, 1000);
		for (int i = 0 ; i < iterations ; i++) {
			eDA2.addElement(i);
		}
		junit.framework.Assert.assertEquals(("Number of elements should be equal to " + iterations), iterations, eDA2.getNumElements());
		eDA2.addElement(2.0);
		junit.framework.Assert.assertEquals(("Number of elements should be equals to " + (iterations + 1)), (iterations + 1), eDA2.getNumElements());
	}

	public void testWithInitialCapacityAndExpansionFactor() {
		org.apache.commons.math.util.ResizableDoubleArray eDA3 = new org.apache.commons.math.util.ResizableDoubleArray(3 , 3.0F , 3.5F);
		junit.framework.Assert.assertEquals("Initial number of elements should be 0", 0, eDA3.getNumElements());
		org.apache.commons.math.random.RandomData randomData = new org.apache.commons.math.random.RandomDataImpl();
		int iterations = randomData.nextInt(100, 3000);
		for (int i = 0 ; i < iterations ; i++) {
			eDA3.addElement(i);
		}
		junit.framework.Assert.assertEquals(("Number of elements should be equal to " + iterations), iterations, eDA3.getNumElements());
		eDA3.addElement(2.0);
		junit.framework.Assert.assertEquals(("Number of elements should be equals to " + (iterations + 1)), (iterations + 1), eDA3.getNumElements());
		junit.framework.Assert.assertEquals("Expansion factor should equal 3.0", 3.0F, eDA3.getExpansionFactor(), java.lang.Double.MIN_VALUE);
	}

	public void testDiscard() {
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		junit.framework.Assert.assertEquals("Number of elements should be 11", 11, da.getNumElements());
		((org.apache.commons.math.util.ResizableDoubleArray)(da)).discardFrontElements(5);
		junit.framework.Assert.assertEquals("Number of elements should be 6", 6, da.getNumElements());
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		junit.framework.Assert.assertEquals("Number of elements should be 10", 10, da.getNumElements());
		((org.apache.commons.math.util.ResizableDoubleArray)(da)).discardMostRecentElements(2);
		junit.framework.Assert.assertEquals("Number of elements should be 8", 8, da.getNumElements());
		try {
			((org.apache.commons.math.util.ResizableDoubleArray)(da)).discardFrontElements(-1);
			junit.framework.Assert.fail("Trying to discard a negative number of element is not allowed");
		} catch (java.lang.Exception e) {
		}
		try {
			((org.apache.commons.math.util.ResizableDoubleArray)(da)).discardMostRecentElements(-1);
			junit.framework.Assert.fail("Trying to discard a negative number of element is not allowed");
		} catch (java.lang.Exception e) {
		}
		try {
			((org.apache.commons.math.util.ResizableDoubleArray)(da)).discardFrontElements(10000);
			junit.framework.Assert.fail("You can't discard more elements than the array contains");
		} catch (java.lang.Exception e) {
		}
		try {
			((org.apache.commons.math.util.ResizableDoubleArray)(da)).discardMostRecentElements(10000);
			junit.framework.Assert.fail("You can't discard more elements than the array contains");
		} catch (java.lang.Exception e) {
		}
	}

	public void testSubstitute() {
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		da.addElement(2.0);
		junit.framework.Assert.assertEquals("Number of elements should be 11", 11, da.getNumElements());
		((org.apache.commons.math.util.ResizableDoubleArray)(da)).substituteMostRecentElement(24);
		junit.framework.Assert.assertEquals("Number of elements should be 11", 11, da.getNumElements());
		try {
			((org.apache.commons.math.util.ResizableDoubleArray)(da)).discardMostRecentElements(10);
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("Trying to discard a negative number of element is not allowed");
		}
		((org.apache.commons.math.util.ResizableDoubleArray)(da)).substituteMostRecentElement(24);
		junit.framework.Assert.assertEquals("Number of elements should be 1", 1, da.getNumElements());
	}

	public void testMutators() {
		((org.apache.commons.math.util.ResizableDoubleArray)(da)).setContractionCriteria(10.0F);
		junit.framework.Assert.assertEquals(10.0F, ((org.apache.commons.math.util.ResizableDoubleArray)(da)).getContractionCriteria(), 0);
		((org.apache.commons.math.util.ResizableDoubleArray)(da)).setExpansionFactor(8.0F);
		junit.framework.Assert.assertEquals(8.0F, ((org.apache.commons.math.util.ResizableDoubleArray)(da)).getExpansionFactor(), 0);
		try {
			((org.apache.commons.math.util.ResizableDoubleArray)(da)).setExpansionFactor(11.0F);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		((org.apache.commons.math.util.ResizableDoubleArray)(da)).setExpansionMode(org.apache.commons.math.util.ResizableDoubleArray.ADDITIVE_MODE);
		junit.framework.Assert.assertEquals(org.apache.commons.math.util.ResizableDoubleArray.ADDITIVE_MODE, ((org.apache.commons.math.util.ResizableDoubleArray)(da)).getExpansionMode());
		try {
			((org.apache.commons.math.util.ResizableDoubleArray)(da)).setExpansionMode(-1);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testEqualsAndHashCode() throws java.lang.Exception {
		org.apache.commons.math.util.ResizableDoubleArray first = new org.apache.commons.math.util.ResizableDoubleArray();
		java.lang.Double other = new java.lang.Double(2);
		junit.framework.Assert.assertFalse(first.equals(other));
		other = null;
		junit.framework.Assert.assertFalse(first.equals(other));
		junit.framework.Assert.assertTrue(first.equals(first));
		org.apache.commons.math.util.ResizableDoubleArray second = new org.apache.commons.math.util.ResizableDoubleArray();
		verifyEquality(first, second);
		org.apache.commons.math.util.ResizableDoubleArray third = new org.apache.commons.math.util.ResizableDoubleArray(3 , 2.0F , 2.0F);
		verifyInequality(third, first);
		org.apache.commons.math.util.ResizableDoubleArray fourth = new org.apache.commons.math.util.ResizableDoubleArray(3 , 2.0F , 2.0F);
		org.apache.commons.math.util.ResizableDoubleArray fifth = new org.apache.commons.math.util.ResizableDoubleArray(2 , 2.0F , 2.0F);
		verifyEquality(third, fourth);
		verifyInequality(third, fifth);
		third.addElement(4.1);
		third.addElement(4.2);
		third.addElement(4.3);
		fourth.addElement(4.1);
		fourth.addElement(4.2);
		fourth.addElement(4.3);
		verifyEquality(third, fourth);
		fourth.addElement(4.4);
		verifyInequality(third, fourth);
		third.addElement(4.4);
		verifyEquality(third, fourth);
		fourth.addElement(4.4);
		verifyInequality(third, fourth);
		third.addElement(4.4);
		verifyEquality(third, fourth);
		fourth.addElementRolling(4.5);
		third.addElementRolling(4.5);
		verifyEquality(third, fourth);
		third.discardFrontElements(1);
		verifyInequality(third, fourth);
		fourth.discardFrontElements(1);
		verifyEquality(third, fourth);
		third.discardMostRecentElements(2);
		fourth.discardMostRecentElements(2);
		verifyEquality(third, fourth);
		third.addElement(18);
		fourth.addElement(17);
		third.addElement(17);
		fourth.addElement(18);
		verifyInequality(third, fourth);
		org.apache.commons.math.util.ResizableDoubleArray.copy(fourth, fifth);
		verifyEquality(fourth, fifth);
		verifyEquality(fourth, new org.apache.commons.math.util.ResizableDoubleArray(fourth));
		verifyEquality(fourth, fourth.copy());
	}

	private void verifyEquality(org.apache.commons.math.util.ResizableDoubleArray a, org.apache.commons.math.util.ResizableDoubleArray b) {
		junit.framework.Assert.assertTrue(b.equals(a));
		junit.framework.Assert.assertTrue(a.equals(b));
		junit.framework.Assert.assertEquals(a.hashCode(), b.hashCode());
	}

	private void verifyInequality(org.apache.commons.math.util.ResizableDoubleArray a, org.apache.commons.math.util.ResizableDoubleArray b) {
		junit.framework.Assert.assertFalse(b.equals(a));
		junit.framework.Assert.assertFalse(a.equals(b));
		junit.framework.Assert.assertFalse(((a.hashCode()) == (b.hashCode())));
	}
}

