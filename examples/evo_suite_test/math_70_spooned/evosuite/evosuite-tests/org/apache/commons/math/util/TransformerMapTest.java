package org.apache.commons.math.util;


public class TransformerMapTest extends junit.framework.TestCase {
	public void testPutTransformer() {
		org.apache.commons.math.util.NumberTransformer expected = new org.apache.commons.math.util.DefaultTransformer();
		org.apache.commons.math.util.TransformerMap map = new org.apache.commons.math.util.TransformerMap();
		map.putTransformer(org.apache.commons.math.util.TransformerMapTest.class, expected);
		junit.framework.Assert.assertEquals(expected, map.getTransformer(org.apache.commons.math.util.TransformerMapTest.class));
	}

	public void testContainsClass() {
		org.apache.commons.math.util.NumberTransformer expected = new org.apache.commons.math.util.DefaultTransformer();
		org.apache.commons.math.util.TransformerMap map = new org.apache.commons.math.util.TransformerMap();
		map.putTransformer(org.apache.commons.math.util.TransformerMapTest.class, expected);
		junit.framework.Assert.assertTrue(map.containsClass(org.apache.commons.math.util.TransformerMapTest.class));
	}

	public void testContainsTransformer() {
		org.apache.commons.math.util.NumberTransformer expected = new org.apache.commons.math.util.DefaultTransformer();
		org.apache.commons.math.util.TransformerMap map = new org.apache.commons.math.util.TransformerMap();
		map.putTransformer(org.apache.commons.math.util.TransformerMapTest.class, expected);
		junit.framework.Assert.assertTrue(map.containsTransformer(expected));
	}

	public void testRemoveTransformer() {
		org.apache.commons.math.util.NumberTransformer expected = new org.apache.commons.math.util.DefaultTransformer();
		org.apache.commons.math.util.TransformerMap map = new org.apache.commons.math.util.TransformerMap();
		map.putTransformer(org.apache.commons.math.util.TransformerMapTest.class, expected);
		junit.framework.Assert.assertTrue(map.containsClass(org.apache.commons.math.util.TransformerMapTest.class));
		junit.framework.Assert.assertTrue(map.containsTransformer(expected));
		map.removeTransformer(org.apache.commons.math.util.TransformerMapTest.class);
		junit.framework.Assert.assertFalse(map.containsClass(org.apache.commons.math.util.TransformerMapTest.class));
		junit.framework.Assert.assertFalse(map.containsTransformer(expected));
	}

	public void testClear() {
		org.apache.commons.math.util.NumberTransformer expected = new org.apache.commons.math.util.DefaultTransformer();
		org.apache.commons.math.util.TransformerMap map = new org.apache.commons.math.util.TransformerMap();
		map.putTransformer(org.apache.commons.math.util.TransformerMapTest.class, expected);
		junit.framework.Assert.assertTrue(map.containsClass(org.apache.commons.math.util.TransformerMapTest.class));
		map.clear();
		junit.framework.Assert.assertFalse(map.containsClass(org.apache.commons.math.util.TransformerMapTest.class));
	}

	public void testClasses() {
		org.apache.commons.math.util.NumberTransformer expected = new org.apache.commons.math.util.DefaultTransformer();
		org.apache.commons.math.util.TransformerMap map = new org.apache.commons.math.util.TransformerMap();
		map.putTransformer(org.apache.commons.math.util.TransformerMapTest.class, expected);
		junit.framework.Assert.assertTrue(map.classes().contains(org.apache.commons.math.util.TransformerMapTest.class));
	}

	public void testTransformers() {
		org.apache.commons.math.util.NumberTransformer expected = new org.apache.commons.math.util.DefaultTransformer();
		org.apache.commons.math.util.TransformerMap map = new org.apache.commons.math.util.TransformerMap();
		map.putTransformer(org.apache.commons.math.util.TransformerMapTest.class, expected);
		junit.framework.Assert.assertTrue(map.transformers().contains(expected));
	}

	public void testSerial() {
		org.apache.commons.math.util.NumberTransformer expected = new org.apache.commons.math.util.DefaultTransformer();
		org.apache.commons.math.util.TransformerMap map = new org.apache.commons.math.util.TransformerMap();
		map.putTransformer(org.apache.commons.math.util.TransformerMapTest.class, expected);
		junit.framework.Assert.assertEquals(map, org.apache.commons.math.TestUtils.serializeAndRecover(map));
	}
}

