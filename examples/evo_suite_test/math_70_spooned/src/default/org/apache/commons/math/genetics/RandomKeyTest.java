package org.apache.commons.math.genetics;


public class RandomKeyTest {
	@org.junit.Test(expected = java.lang.IllegalArgumentException.class)
	public void testConstructor1() {
		new org.apache.commons.math.genetics.DummyRandomKey(new java.lang.Double[]{ 0.2 , 0.3 , 1.2 });
	}

	@org.junit.Test(expected = java.lang.IllegalArgumentException.class)
	public void testConstructor2() {
		new org.apache.commons.math.genetics.DummyRandomKey(new java.lang.Double[]{ 0.2 , 0.3 , -0.2 });
	}

	@org.junit.Test
	public void testIsSame() {
		org.apache.commons.math.genetics.DummyRandomKey drk1 = new org.apache.commons.math.genetics.DummyRandomKey(new java.lang.Double[]{ 0.4 , 0.1 , 0.5 , 0.8 , 0.2 });
		org.apache.commons.math.genetics.DummyRandomKey drk2 = new org.apache.commons.math.genetics.DummyRandomKey(new java.lang.Double[]{ 0.4 , 0.1 , 0.5 , 0.8 , 0.2 });
		org.apache.commons.math.genetics.DummyRandomKey drk3 = new org.apache.commons.math.genetics.DummyRandomKey(new java.lang.Double[]{ 0.4 , 0.15 , 0.5 , 0.8 , 0.2 });
		org.apache.commons.math.genetics.DummyRandomKey drk4 = new org.apache.commons.math.genetics.DummyRandomKey(new java.lang.Double[]{ 0.4 , 0.25 , 0.5 , 0.8 , 0.2 });
		org.apache.commons.math.genetics.DummyRandomKey drk5 = new org.apache.commons.math.genetics.DummyRandomKey(new java.lang.Double[]{ 0.4 , 0.25 , 0.5 , 0.8 , 0.2 , 0.5 });
		org.junit.Assert.assertTrue(drk1.isSame(drk2));
		org.junit.Assert.assertTrue(drk2.isSame(drk3));
		org.junit.Assert.assertFalse(drk3.isSame(drk4));
		org.junit.Assert.assertFalse(drk4.isSame(drk5));
	}

	@org.junit.Test
	public void testDecode() {
		org.apache.commons.math.genetics.DummyRandomKey drk = new org.apache.commons.math.genetics.DummyRandomKey(new java.lang.Double[]{ 0.4 , 0.1 , 0.5 , 0.8 , 0.2 });
		java.util.List<java.lang.String> decoded = drk.decode(java.util.Arrays.asList(new java.lang.String[]{ "a" , "b" , "c" , "d" , "e" }));
		org.junit.Assert.assertEquals("b", decoded.get(0));
		org.junit.Assert.assertEquals("e", decoded.get(1));
		org.junit.Assert.assertEquals("a", decoded.get(2));
		org.junit.Assert.assertEquals("c", decoded.get(3));
		org.junit.Assert.assertEquals("d", decoded.get(4));
	}

	@org.junit.Test
	public void testRandomPermutation() {
		for (int i = 0 ; i < 10 ; i++) {
			org.apache.commons.math.genetics.DummyRandomKey drk = new org.apache.commons.math.genetics.DummyRandomKey(org.apache.commons.math.genetics.RandomKey.randomPermutation(20));
			org.junit.Assert.assertNotNull(drk);
		}
	}

	@org.junit.Test
	public void testIdentityPermutation() {
		org.apache.commons.math.genetics.DummyRandomKey drk = new org.apache.commons.math.genetics.DummyRandomKey(org.apache.commons.math.genetics.RandomKey.identityPermutation(5));
		java.util.List<java.lang.String> decoded = drk.decode(java.util.Arrays.asList(new java.lang.String[]{ "a" , "b" , "c" , "d" , "e" }));
		org.junit.Assert.assertEquals("a", decoded.get(0));
		org.junit.Assert.assertEquals("b", decoded.get(1));
		org.junit.Assert.assertEquals("c", decoded.get(2));
		org.junit.Assert.assertEquals("d", decoded.get(3));
		org.junit.Assert.assertEquals("e", decoded.get(4));
	}

	@org.junit.Test
	public void testComparatorPermutation() {
		java.util.List<java.lang.String> data = java.util.Arrays.asList(new java.lang.String[]{ "x" , "b" , "c" , "z" , "b" });
		java.util.List<java.lang.Double> permutation = org.apache.commons.math.genetics.RandomKey.comparatorPermutation(data, new java.util.Comparator<java.lang.String>() {
			public int compare(java.lang.String o1, java.lang.String o2) {
				return o1.compareTo(o2);
			}
		});
		java.lang.Double[] permArr = new java.lang.Double[data.size()];
		permArr = permutation.toArray(permArr);
		org.junit.Assert.assertArrayEquals(new java.lang.Double[]{ 0.6 , 0.0 , 0.4 , 0.8 , 0.2 }, permArr);
		java.util.List<java.lang.String> decodedData = new org.apache.commons.math.genetics.DummyRandomKey(permutation).decode(data);
		org.junit.Assert.assertEquals("b", decodedData.get(0));
		org.junit.Assert.assertEquals("b", decodedData.get(1));
		org.junit.Assert.assertEquals("c", decodedData.get(2));
		org.junit.Assert.assertEquals("x", decodedData.get(3));
		org.junit.Assert.assertEquals("z", decodedData.get(4));
		permutation = org.apache.commons.math.genetics.RandomKey.comparatorPermutation(data, new java.util.Comparator<java.lang.String>() {
			public int compare(java.lang.String o1, java.lang.String o2) {
				return o2.compareTo(o1);
			}
		});
		permArr = new java.lang.Double[data.size()];
		permArr = permutation.toArray(permArr);
		org.junit.Assert.assertArrayEquals(new java.lang.Double[]{ 0.2 , 0.6 , 0.4 , 0.0 , 0.8 }, permArr);
		decodedData = new org.apache.commons.math.genetics.DummyRandomKey(permutation).decode(data);
		org.junit.Assert.assertEquals("z", decodedData.get(0));
		org.junit.Assert.assertEquals("x", decodedData.get(1));
		org.junit.Assert.assertEquals("c", decodedData.get(2));
		org.junit.Assert.assertEquals("b", decodedData.get(3));
		org.junit.Assert.assertEquals("b", decodedData.get(4));
	}

	@org.junit.Test
	public void testInducedPermutation() {
		java.util.List<java.lang.String> origData = java.util.Arrays.asList(new java.lang.String[]{ "a" , "b" , "c" , "d" , "d" });
		java.util.List<java.lang.String> permutedData = java.util.Arrays.asList(new java.lang.String[]{ "d" , "b" , "c" , "a" , "d" });
		org.apache.commons.math.genetics.DummyRandomKey drk = new org.apache.commons.math.genetics.DummyRandomKey(org.apache.commons.math.genetics.RandomKey.inducedPermutation(origData, permutedData));
		java.util.List<java.lang.String> decoded = drk.decode(origData);
		org.junit.Assert.assertEquals("d", decoded.get(0));
		org.junit.Assert.assertEquals("b", decoded.get(1));
		org.junit.Assert.assertEquals("c", decoded.get(2));
		org.junit.Assert.assertEquals("a", decoded.get(3));
		org.junit.Assert.assertEquals("d", decoded.get(4));
		try {
			org.apache.commons.math.genetics.RandomKey.inducedPermutation(java.util.Arrays.asList(new java.lang.String[]{ "a" , "b" , "c" , "d" , "d" }), java.util.Arrays.asList(new java.lang.String[]{ "a" , "b" , "c" , "d" }));
			org.junit.Assert.fail("Uncaught exception");
		} catch (java.lang.IllegalArgumentException e) {
		}
		try {
			org.apache.commons.math.genetics.RandomKey.inducedPermutation(java.util.Arrays.asList(new java.lang.String[]{ "a" , "b" , "c" , "d" , "d" }), java.util.Arrays.asList(new java.lang.String[]{ "a" , "b" , "c" , "d" , "f" }));
			org.junit.Assert.fail("Uncaught exception");
		} catch (java.lang.IllegalArgumentException e) {
		}
	}

	@org.junit.Test
	public void testEqualRepr() {
		org.apache.commons.math.genetics.DummyRandomKey drk = new org.apache.commons.math.genetics.DummyRandomKey(new java.lang.Double[]{ 0.2 , 0.2 , 0.5 });
		java.util.List<java.lang.String> decodedData = drk.decode(java.util.Arrays.asList(new java.lang.String[]{ "a" , "b" , "c" }));
		org.junit.Assert.assertEquals("a", decodedData.get(0));
		org.junit.Assert.assertEquals("b", decodedData.get(1));
		org.junit.Assert.assertEquals("c", decodedData.get(2));
	}
}

