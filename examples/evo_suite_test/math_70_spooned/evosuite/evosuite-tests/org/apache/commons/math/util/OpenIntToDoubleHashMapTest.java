package org.apache.commons.math.util;


public class OpenIntToDoubleHashMapTest extends junit.framework.TestCase {
	private java.util.Map<java.lang.Integer, java.lang.Double> javaMap = new java.util.HashMap<java.lang.Integer, java.lang.Double>();

	@java.lang.Override
	protected void setUp() throws java.lang.Exception {
		javaMap.put(50, 100.0);
		javaMap.put(75, 75.0);
		javaMap.put(25, 500.0);
		javaMap.put(java.lang.Integer.MAX_VALUE, java.lang.Double.MAX_VALUE);
		javaMap.put(0, -1.0);
		javaMap.put(1, 0.0);
		javaMap.put(33, -0.1);
		javaMap.put(23234234, -242343.0);
		javaMap.put(23321, java.lang.Double.MIN_VALUE);
		javaMap.put(-4444, 332.0);
		javaMap.put(-1, -2323.0);
		javaMap.put(java.lang.Integer.MIN_VALUE, 44.0);
		javaMap.putAll(generate());
	}

	private java.util.Map<java.lang.Integer, java.lang.Double> generate() {
		java.util.Map<java.lang.Integer, java.lang.Double> map = new java.util.HashMap<java.lang.Integer, java.lang.Double>();
		java.util.Random r = new java.util.Random();
		for (int i = 0 ; i < 2000 ; ++i)
			map.put(r.nextInt(), r.nextDouble());
		return map;
	}

	private org.apache.commons.math.util.OpenIntToDoubleHashMap createFromJavaMap() {
		org.apache.commons.math.util.OpenIntToDoubleHashMap map = new org.apache.commons.math.util.OpenIntToDoubleHashMap();
		for (java.util.Map.Entry<java.lang.Integer, java.lang.Double> mapEntry : javaMap.entrySet()) {
			map.put(mapEntry.getKey(), mapEntry.getValue());
		}
		return map;
	}

	public void testPutAndGetWith0ExpectedSize() {
		org.apache.commons.math.util.OpenIntToDoubleHashMap map = new org.apache.commons.math.util.OpenIntToDoubleHashMap(0);
		assertPutAndGet(map);
	}

	public void testPutAndGetWithExpectedSize() {
		org.apache.commons.math.util.OpenIntToDoubleHashMap map = new org.apache.commons.math.util.OpenIntToDoubleHashMap(500);
		assertPutAndGet(map);
	}

	public void testPutAndGet() {
		org.apache.commons.math.util.OpenIntToDoubleHashMap map = new org.apache.commons.math.util.OpenIntToDoubleHashMap();
		assertPutAndGet(map);
	}

	private void assertPutAndGet(org.apache.commons.math.util.OpenIntToDoubleHashMap map) {
		assertPutAndGet(map, 0, new java.util.HashSet<java.lang.Integer>());
	}

	private void assertPutAndGet(org.apache.commons.math.util.OpenIntToDoubleHashMap map, int mapSize, java.util.Set<java.lang.Integer> keysInMap) {
		junit.framework.Assert.assertEquals(mapSize, map.size());
		for (java.util.Map.Entry<java.lang.Integer, java.lang.Double> mapEntry : javaMap.entrySet()) {
			map.put(mapEntry.getKey(), mapEntry.getValue());
			if (!(keysInMap.contains(mapEntry.getKey()))) {
				++mapSize;
			} 
			junit.framework.Assert.assertEquals(mapSize, map.size());
			junit.framework.Assert.assertEquals(mapEntry.getValue(), map.get(mapEntry.getKey()));
		}
	}

	public void testPutAbsentOnExisting() {
		org.apache.commons.math.util.OpenIntToDoubleHashMap map = createFromJavaMap();
		int size = javaMap.size();
		for (java.util.Map.Entry<java.lang.Integer, java.lang.Double> mapEntry : generateAbsent().entrySet()) {
			map.put(mapEntry.getKey(), mapEntry.getValue());
			junit.framework.Assert.assertEquals(++size, map.size());
			junit.framework.Assert.assertEquals(mapEntry.getValue(), map.get(mapEntry.getKey()));
		}
	}

	public void testPutOnExisting() {
		org.apache.commons.math.util.OpenIntToDoubleHashMap map = createFromJavaMap();
		for (java.util.Map.Entry<java.lang.Integer, java.lang.Double> mapEntry : javaMap.entrySet()) {
			map.put(mapEntry.getKey(), mapEntry.getValue());
			junit.framework.Assert.assertEquals(javaMap.size(), map.size());
			junit.framework.Assert.assertEquals(mapEntry.getValue(), map.get(mapEntry.getKey()));
		}
	}

	public void testGetAbsent() {
		java.util.Map<java.lang.Integer, java.lang.Double> generated = generateAbsent();
		org.apache.commons.math.util.OpenIntToDoubleHashMap map = createFromJavaMap();
		for (java.util.Map.Entry<java.lang.Integer, java.lang.Double> mapEntry : generated.entrySet())
			junit.framework.Assert.assertTrue(java.lang.Double.isNaN(map.get(mapEntry.getKey())));
	}

	public void testGetFromEmpty() {
		org.apache.commons.math.util.OpenIntToDoubleHashMap map = new org.apache.commons.math.util.OpenIntToDoubleHashMap();
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(map.get(5)));
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(map.get(0)));
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(map.get(50)));
	}

	public void testRemove() {
		org.apache.commons.math.util.OpenIntToDoubleHashMap map = createFromJavaMap();
		int mapSize = javaMap.size();
		junit.framework.Assert.assertEquals(mapSize, map.size());
		for (java.util.Map.Entry<java.lang.Integer, java.lang.Double> mapEntry : javaMap.entrySet()) {
			map.remove(mapEntry.getKey());
			junit.framework.Assert.assertEquals(--mapSize, map.size());
			junit.framework.Assert.assertTrue(java.lang.Double.isNaN(map.get(mapEntry.getKey())));
		}
		assertPutAndGet(map);
	}

	public void testRemove2() {
		org.apache.commons.math.util.OpenIntToDoubleHashMap map = createFromJavaMap();
		int mapSize = javaMap.size();
		int count = 0;
		java.util.Set<java.lang.Integer> keysInMap = new java.util.HashSet<java.lang.Integer>(javaMap.keySet());
		for (java.util.Map.Entry<java.lang.Integer, java.lang.Double> mapEntry : javaMap.entrySet()) {
			keysInMap.remove(mapEntry.getKey());
			map.remove(mapEntry.getKey());
			junit.framework.Assert.assertEquals(--mapSize, map.size());
			junit.framework.Assert.assertTrue(java.lang.Double.isNaN(map.get(mapEntry.getKey())));
			if ((count++) > 5) {
				break;
			} 
		}
		assertPutAndGet(map, mapSize, keysInMap);
	}

	public void testRemoveFromEmpty() {
		org.apache.commons.math.util.OpenIntToDoubleHashMap map = new org.apache.commons.math.util.OpenIntToDoubleHashMap();
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(map.remove(50)));
	}

	public void testRemoveAbsent() {
		java.util.Map<java.lang.Integer, java.lang.Double> generated = generateAbsent();
		org.apache.commons.math.util.OpenIntToDoubleHashMap map = createFromJavaMap();
		int mapSize = map.size();
		for (java.util.Map.Entry<java.lang.Integer, java.lang.Double> mapEntry : generated.entrySet()) {
			map.remove(mapEntry.getKey());
			junit.framework.Assert.assertEquals(mapSize, map.size());
			junit.framework.Assert.assertTrue(java.lang.Double.isNaN(map.get(mapEntry.getKey())));
		}
	}

	private java.util.Map<java.lang.Integer, java.lang.Double> generateAbsent() {
		java.util.Map<java.lang.Integer, java.lang.Double> generated = new java.util.HashMap<java.lang.Integer, java.lang.Double>();
		do {
			generated.putAll(generate());
			for (java.lang.Integer key : javaMap.keySet())
				generated.remove(key);
		} while ((generated.size()) < 100 );
		return generated;
	}

	public void testCopy() {
		org.apache.commons.math.util.OpenIntToDoubleHashMap copy = new org.apache.commons.math.util.OpenIntToDoubleHashMap(createFromJavaMap());
		junit.framework.Assert.assertEquals(javaMap.size(), copy.size());
		for (java.util.Map.Entry<java.lang.Integer, java.lang.Double> mapEntry : javaMap.entrySet())
			junit.framework.Assert.assertEquals(mapEntry.getValue(), copy.get(mapEntry.getKey()));
	}

	public void testContainsKey() {
		org.apache.commons.math.util.OpenIntToDoubleHashMap map = createFromJavaMap();
		for (java.util.Map.Entry<java.lang.Integer, java.lang.Double> mapEntry : javaMap.entrySet()) {
			junit.framework.Assert.assertTrue(map.containsKey(mapEntry.getKey()));
		}
		for (java.util.Map.Entry<java.lang.Integer, java.lang.Double> mapEntry : generateAbsent().entrySet()) {
			junit.framework.Assert.assertFalse(map.containsKey(mapEntry.getKey()));
		}
		for (java.util.Map.Entry<java.lang.Integer, java.lang.Double> mapEntry : javaMap.entrySet()) {
			int key = mapEntry.getKey();
			junit.framework.Assert.assertTrue(map.containsKey(key));
			map.remove(key);
			junit.framework.Assert.assertFalse(map.containsKey(key));
		}
	}

	public void testIterator() {
		org.apache.commons.math.util.OpenIntToDoubleHashMap map = createFromJavaMap();
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iterator = map.iterator();
		for (int i = 0 ; i < (map.size()) ; ++i) {
			junit.framework.Assert.assertTrue(iterator.hasNext());
			iterator.advance();
			int key = iterator.key();
			junit.framework.Assert.assertTrue(map.containsKey(key));
			junit.framework.Assert.assertEquals(javaMap.get(key), map.get(key), 0);
			junit.framework.Assert.assertEquals(javaMap.get(key), iterator.value(), 0);
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
		org.apache.commons.math.util.OpenIntToDoubleHashMap map = createFromJavaMap();
		org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator iterator = map.iterator();
		map.put(3, 3);
		try {
			iterator.advance();
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.util.ConcurrentModificationException cme) {
		}
	}

	public void testPutKeysWithCollisions() {
		org.apache.commons.math.util.OpenIntToDoubleHashMap map = new org.apache.commons.math.util.OpenIntToDoubleHashMap();
		int key1 = -1996012590;
		double value1 = 1.0;
		map.put(key1, value1);
		int key2 = 835099822;
		map.put(key2, value1);
		int key3 = 1008859686;
		map.put(key3, value1);
		junit.framework.Assert.assertEquals(value1, map.get(key3));
		junit.framework.Assert.assertEquals(3, map.size());
		map.remove(key2);
		double value2 = 2.0;
		map.put(key3, value2);
		junit.framework.Assert.assertEquals(value2, map.get(key3));
		junit.framework.Assert.assertEquals(2, map.size());
	}

	public void testPutKeysWithCollision2() {
		org.apache.commons.math.util.OpenIntToDoubleHashMap map = new org.apache.commons.math.util.OpenIntToDoubleHashMap();
		int key1 = 837989881;
		double value1 = 1.0;
		map.put(key1, value1);
		int key2 = 476463321;
		map.put(key2, value1);
		junit.framework.Assert.assertEquals(2, map.size());
		junit.framework.Assert.assertEquals(value1, map.get(key2));
		map.remove(key1);
		double value2 = 2.0;
		map.put(key2, value2);
		junit.framework.Assert.assertEquals(1, map.size());
		junit.framework.Assert.assertEquals(value2, map.get(key2));
	}
}

