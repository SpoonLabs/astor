package org.apache.commons.math.util;


public class OpenIntToFieldTest extends junit.framework.TestCase {
	private java.util.Map<java.lang.Integer, org.apache.commons.math.fraction.Fraction> javaMap = new java.util.HashMap<java.lang.Integer, org.apache.commons.math.fraction.Fraction>();

	private org.apache.commons.math.fraction.FractionField field = org.apache.commons.math.fraction.FractionField.getInstance();

	@java.lang.Override
	protected void setUp() throws java.lang.Exception {
		javaMap.put(50, new org.apache.commons.math.fraction.Fraction(100.0));
		javaMap.put(75, new org.apache.commons.math.fraction.Fraction(75.0));
		javaMap.put(25, new org.apache.commons.math.fraction.Fraction(500.0));
		javaMap.put(java.lang.Integer.MAX_VALUE, new org.apache.commons.math.fraction.Fraction(java.lang.Integer.MAX_VALUE));
		javaMap.put(0, new org.apache.commons.math.fraction.Fraction(-1.0));
		javaMap.put(1, new org.apache.commons.math.fraction.Fraction(0.0));
		javaMap.put(33, new org.apache.commons.math.fraction.Fraction(-0.1));
		javaMap.put(23234234, new org.apache.commons.math.fraction.Fraction(-242343.0));
		javaMap.put(23321, new org.apache.commons.math.fraction.Fraction(java.lang.Integer.MIN_VALUE));
		javaMap.put(-4444, new org.apache.commons.math.fraction.Fraction(332.0));
		javaMap.put(-1, new org.apache.commons.math.fraction.Fraction(-2323.0));
		javaMap.put(java.lang.Integer.MIN_VALUE, new org.apache.commons.math.fraction.Fraction(44.0));
		javaMap.putAll(generate());
	}

	private java.util.Map<java.lang.Integer, org.apache.commons.math.fraction.Fraction> generate() {
		java.util.Map<java.lang.Integer, org.apache.commons.math.fraction.Fraction> map = new java.util.HashMap<java.lang.Integer, org.apache.commons.math.fraction.Fraction>();
		java.util.Random r = new java.util.Random();
		double dd = 0;
		for (int i = 0 ; i < 2000 ; ++i)
			dd = r.nextDouble();
		try {
			map.put(r.nextInt(), new org.apache.commons.math.fraction.Fraction(dd));
		} catch (org.apache.commons.math.fraction.FractionConversionException e) {
			throw new java.lang.IllegalStateException(("Invalid :" + dd) , e);
		}
		return map;
	}

	private org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> createFromJavaMap(org.apache.commons.math.Field<org.apache.commons.math.fraction.Fraction> field) {
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map = new org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction>(field);
		for (java.util.Map.Entry<java.lang.Integer, org.apache.commons.math.fraction.Fraction> mapEntry : javaMap.entrySet()) {
			map.put(mapEntry.getKey(), mapEntry.getValue());
		}
		return map;
	}

	public void testPutAndGetWith0ExpectedSize() {
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map = new org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction>(field , 0);
		assertPutAndGet(map);
	}

	public void testPutAndGetWithExpectedSize() {
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map = new org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction>(field , 500);
		assertPutAndGet(map);
	}

	public void testPutAndGet() {
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map = new org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction>(field);
		assertPutAndGet(map);
	}

	private void assertPutAndGet(org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map) {
		assertPutAndGet(map, 0, new java.util.HashSet<java.lang.Integer>());
	}

	private void assertPutAndGet(org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map, int mapSize, java.util.Set<java.lang.Integer> keysInMap) {
		junit.framework.Assert.assertEquals(mapSize, map.size());
		for (java.util.Map.Entry<java.lang.Integer, org.apache.commons.math.fraction.Fraction> mapEntry : javaMap.entrySet()) {
			map.put(mapEntry.getKey(), mapEntry.getValue());
			if (!(keysInMap.contains(mapEntry.getKey())))
				++mapSize;
			
			junit.framework.Assert.assertEquals(mapSize, map.size());
			junit.framework.Assert.assertEquals(mapEntry.getValue(), map.get(mapEntry.getKey()));
		}
	}

	public void testPutAbsentOnExisting() {
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map = createFromJavaMap(field);
		int size = javaMap.size();
		for (java.util.Map.Entry<java.lang.Integer, org.apache.commons.math.fraction.Fraction> mapEntry : generateAbsent().entrySet()) {
			map.put(mapEntry.getKey(), mapEntry.getValue());
			junit.framework.Assert.assertEquals(++size, map.size());
			junit.framework.Assert.assertEquals(mapEntry.getValue(), map.get(mapEntry.getKey()));
		}
	}

	public void testPutOnExisting() {
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map = createFromJavaMap(field);
		for (java.util.Map.Entry<java.lang.Integer, org.apache.commons.math.fraction.Fraction> mapEntry : javaMap.entrySet()) {
			map.put(mapEntry.getKey(), mapEntry.getValue());
			junit.framework.Assert.assertEquals(javaMap.size(), map.size());
			junit.framework.Assert.assertEquals(mapEntry.getValue(), map.get(mapEntry.getKey()));
		}
	}

	public void testGetAbsent() {
		java.util.Map<java.lang.Integer, org.apache.commons.math.fraction.Fraction> generated = generateAbsent();
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map = createFromJavaMap(field);
		for (java.util.Map.Entry<java.lang.Integer, org.apache.commons.math.fraction.Fraction> mapEntry : generated.entrySet())
			junit.framework.Assert.assertTrue(field.getZero().equals(map.get(mapEntry.getKey())));
	}

	public void testGetFromEmpty() {
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map = new org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction>(field);
		junit.framework.Assert.assertTrue(field.getZero().equals(map.get(5)));
		junit.framework.Assert.assertTrue(field.getZero().equals(map.get(0)));
		junit.framework.Assert.assertTrue(field.getZero().equals(map.get(50)));
	}

	public void testRemove() {
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map = createFromJavaMap(field);
		int mapSize = javaMap.size();
		junit.framework.Assert.assertEquals(mapSize, map.size());
		for (java.util.Map.Entry<java.lang.Integer, org.apache.commons.math.fraction.Fraction> mapEntry : javaMap.entrySet()) {
			map.remove(mapEntry.getKey());
			junit.framework.Assert.assertEquals(--mapSize, map.size());
			junit.framework.Assert.assertTrue(field.getZero().equals(map.get(mapEntry.getKey())));
		}
		assertPutAndGet(map);
	}

	public void testRemove2() {
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map = createFromJavaMap(field);
		int mapSize = javaMap.size();
		int count = 0;
		java.util.Set<java.lang.Integer> keysInMap = new java.util.HashSet<java.lang.Integer>(javaMap.keySet());
		for (java.util.Map.Entry<java.lang.Integer, org.apache.commons.math.fraction.Fraction> mapEntry : javaMap.entrySet()) {
			keysInMap.remove(mapEntry.getKey());
			map.remove(mapEntry.getKey());
			junit.framework.Assert.assertEquals(--mapSize, map.size());
			junit.framework.Assert.assertTrue(field.getZero().equals(map.get(mapEntry.getKey())));
			if ((count++) > 5)
				break;
			
		}
		assertPutAndGet(map, mapSize, keysInMap);
	}

	public void testRemoveFromEmpty() {
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map = new org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction>(field);
		junit.framework.Assert.assertTrue(field.getZero().equals(map.remove(50)));
	}

	public void testRemoveAbsent() {
		java.util.Map<java.lang.Integer, org.apache.commons.math.fraction.Fraction> generated = generateAbsent();
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map = createFromJavaMap(field);
		int mapSize = map.size();
		for (java.util.Map.Entry<java.lang.Integer, org.apache.commons.math.fraction.Fraction> mapEntry : generated.entrySet()) {
			map.remove(mapEntry.getKey());
			junit.framework.Assert.assertEquals(mapSize, map.size());
			junit.framework.Assert.assertTrue(field.getZero().equals(map.get(mapEntry.getKey())));
		}
	}

	private java.util.Map<java.lang.Integer, org.apache.commons.math.fraction.Fraction> generateAbsent() {
		java.util.Map<java.lang.Integer, org.apache.commons.math.fraction.Fraction> generated = new java.util.HashMap<java.lang.Integer, org.apache.commons.math.fraction.Fraction>();
		do {
			generated.putAll(generate());
			for (java.lang.Integer key : javaMap.keySet())
				generated.remove(key);
		} while ((generated.size()) < 100 );
		return generated;
	}

	public void testCopy() {
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> copy = new org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction>(createFromJavaMap(field));
		junit.framework.Assert.assertEquals(javaMap.size(), copy.size());
		for (java.util.Map.Entry<java.lang.Integer, org.apache.commons.math.fraction.Fraction> mapEntry : javaMap.entrySet())
			junit.framework.Assert.assertEquals(mapEntry.getValue(), copy.get(mapEntry.getKey()));
	}

	public void testContainsKey() {
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map = createFromJavaMap(field);
		for (java.util.Map.Entry<java.lang.Integer, org.apache.commons.math.fraction.Fraction> mapEntry : javaMap.entrySet()) {
			junit.framework.Assert.assertTrue(map.containsKey(mapEntry.getKey()));
		}
		for (java.util.Map.Entry<java.lang.Integer, org.apache.commons.math.fraction.Fraction> mapEntry : generateAbsent().entrySet()) {
			junit.framework.Assert.assertFalse(map.containsKey(mapEntry.getKey()));
		}
		for (java.util.Map.Entry<java.lang.Integer, org.apache.commons.math.fraction.Fraction> mapEntry : javaMap.entrySet()) {
			int key = mapEntry.getKey();
			junit.framework.Assert.assertTrue(map.containsKey(key));
			map.remove(key);
			junit.framework.Assert.assertFalse(map.containsKey(key));
		}
	}

	public void testIterator() {
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map = createFromJavaMap(field);
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction>.Iterator iterator = map.iterator();
		for (int i = 0 ; i < (map.size()) ; ++i) {
			junit.framework.Assert.assertTrue(iterator.hasNext());
			iterator.advance();
			int key = iterator.key();
			junit.framework.Assert.assertTrue(map.containsKey(key));
			junit.framework.Assert.assertEquals(javaMap.get(key), map.get(key));
			junit.framework.Assert.assertEquals(javaMap.get(key), iterator.value());
			junit.framework.Assert.assertTrue(javaMap.containsKey(key));
		}
		junit.framework.Assert.assertFalse(iterator.hasNext());
		try {
			iterator.advance();
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.util.NoSuchElementException nsee) {
		}
	}

	public void testConcurrentModification() {
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map = createFromJavaMap(field);
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction>.Iterator iterator = map.iterator();
		map.put(3, new org.apache.commons.math.fraction.Fraction(3));
		try {
			iterator.advance();
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.util.ConcurrentModificationException cme) {
		}
	}

	public void testPutKeysWithCollisions() {
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map = new org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction>(field);
		int key1 = -1996012590;
		org.apache.commons.math.fraction.Fraction value1 = new org.apache.commons.math.fraction.Fraction(1);
		map.put(key1, value1);
		int key2 = 835099822;
		map.put(key2, value1);
		int key3 = 1008859686;
		map.put(key3, value1);
		junit.framework.Assert.assertEquals(value1, map.get(key3));
		junit.framework.Assert.assertEquals(3, map.size());
		map.remove(key2);
		org.apache.commons.math.fraction.Fraction value2 = new org.apache.commons.math.fraction.Fraction(2);
		map.put(key3, value2);
		junit.framework.Assert.assertEquals(value2, map.get(key3));
		junit.framework.Assert.assertEquals(2, map.size());
	}

	public void testPutKeysWithCollision2() {
		org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction> map = new org.apache.commons.math.util.OpenIntToFieldHashMap<org.apache.commons.math.fraction.Fraction>(field);
		int key1 = 837989881;
		org.apache.commons.math.fraction.Fraction value1 = new org.apache.commons.math.fraction.Fraction(1);
		map.put(key1, value1);
		int key2 = 476463321;
		map.put(key2, value1);
		junit.framework.Assert.assertEquals(2, map.size());
		junit.framework.Assert.assertEquals(value1, map.get(key2));
		map.remove(key1);
		org.apache.commons.math.fraction.Fraction value2 = new org.apache.commons.math.fraction.Fraction(2);
		map.put(key2, value2);
		junit.framework.Assert.assertEquals(1, map.size());
		junit.framework.Assert.assertEquals(value2, map.get(key2));
	}
}

