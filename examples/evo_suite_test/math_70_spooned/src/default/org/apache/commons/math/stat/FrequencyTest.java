package org.apache.commons.math.stat;


public final class FrequencyTest extends junit.framework.TestCase {
	private long oneL = 1;

	private long twoL = 2;

	private long threeL = 3;

	private int oneI = 1;

	private int twoI = 2;

	private int threeI = 3;

	private double tolerance = 1.0E-14;

	private org.apache.commons.math.stat.Frequency f = null;

	public FrequencyTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public void setUp() {
		f = new org.apache.commons.math.stat.Frequency();
	}

	@java.lang.SuppressWarnings(value = "deprecation")
	public void testCounts() {
		junit.framework.Assert.assertEquals("total count", 0, f.getSumFreq());
		f.addValue(oneL);
		f.addValue(twoL);
		f.addValue(1);
		f.addValue(oneI);
		junit.framework.Assert.assertEquals("one frequency count", 3, f.getCount(1));
		junit.framework.Assert.assertEquals("two frequency count", 1, f.getCount(2));
		junit.framework.Assert.assertEquals("three frequency count", 0, f.getCount(3));
		junit.framework.Assert.assertEquals("total count", 4, f.getSumFreq());
		junit.framework.Assert.assertEquals("zero cumulative frequency", 0, f.getCumFreq(0));
		junit.framework.Assert.assertEquals("one cumulative frequency", 3, f.getCumFreq(1));
		junit.framework.Assert.assertEquals("two cumulative frequency", 4, f.getCumFreq(2));
		junit.framework.Assert.assertEquals("Integer argument cum freq", 4, f.getCumFreq(java.lang.Integer.valueOf(2)));
		junit.framework.Assert.assertEquals("five cumulative frequency", 4, f.getCumFreq(5));
		junit.framework.Assert.assertEquals("foo cumulative frequency", 0, f.getCumFreq("foo"));
		f.clear();
		junit.framework.Assert.assertEquals("total count", 0, f.getSumFreq());
		f.addValue("one");
		f.addValue("One");
		f.addValue("oNe");
		f.addValue("Z");
		junit.framework.Assert.assertEquals("one cumulative frequency", 1, f.getCount("one"));
		junit.framework.Assert.assertEquals("Z cumulative pct", 0.5, f.getCumPct("Z"), tolerance);
		junit.framework.Assert.assertEquals("z cumulative pct", 1.0, f.getCumPct("z"), tolerance);
		junit.framework.Assert.assertEquals("Ot cumulative pct", 0.25, f.getCumPct("Ot"), tolerance);
		f.clear();
		f = null;
		org.apache.commons.math.stat.Frequency f = new org.apache.commons.math.stat.Frequency();
		f.addValue(1);
		f.addValue(java.lang.Integer.valueOf(1));
		f.addValue(java.lang.Long.valueOf(1));
		f.addValue(2);
		f.addValue(java.lang.Integer.valueOf(-1));
		junit.framework.Assert.assertEquals("1 count", 3, f.getCount(1));
		junit.framework.Assert.assertEquals("1 count", 3, f.getCount(java.lang.Integer.valueOf(1)));
		junit.framework.Assert.assertEquals("0 cum pct", 0.2, f.getCumPct(0), tolerance);
		junit.framework.Assert.assertEquals("1 pct", 0.6, f.getPct(java.lang.Integer.valueOf(1)), tolerance);
		junit.framework.Assert.assertEquals("-2 cum pct", 0, f.getCumPct(-2), tolerance);
		junit.framework.Assert.assertEquals("10 cum pct", 1, f.getCumPct(10), tolerance);
		f = null;
		f = new org.apache.commons.math.stat.Frequency(java.lang.String.CASE_INSENSITIVE_ORDER);
		f.addValue("one");
		f.addValue("One");
		f.addValue("oNe");
		f.addValue("Z");
		junit.framework.Assert.assertEquals("one count", 3, f.getCount("one"));
		junit.framework.Assert.assertEquals("Z cumulative pct -- case insensitive", 1, f.getCumPct("Z"), tolerance);
		junit.framework.Assert.assertEquals("z cumulative pct -- case insensitive", 1, f.getCumPct("z"), tolerance);
		f = null;
		f = new org.apache.commons.math.stat.Frequency();
		junit.framework.Assert.assertEquals(0L, f.getCount('a'));
		junit.framework.Assert.assertEquals(0L, f.getCumFreq('b'));
		org.apache.commons.math.TestUtils.assertEquals(java.lang.Double.NaN, f.getPct('a'), 0.0);
		org.apache.commons.math.TestUtils.assertEquals(java.lang.Double.NaN, f.getCumPct('b'), 0.0);
		f.addValue('a');
		f.addValue('b');
		f.addValue('c');
		f.addValue('d');
		junit.framework.Assert.assertEquals(1L, f.getCount('a'));
		junit.framework.Assert.assertEquals(2L, f.getCumFreq('b'));
		junit.framework.Assert.assertEquals(0.25, f.getPct('a'), 0.0);
		junit.framework.Assert.assertEquals(0.5, f.getCumPct('b'), 0.0);
		junit.framework.Assert.assertEquals(1.0, f.getCumPct('e'), 0.0);
	}

	@java.lang.SuppressWarnings(value = "deprecation")
	public void testPcts() {
		f.addValue(oneL);
		f.addValue(twoL);
		f.addValue(oneI);
		f.addValue(twoI);
		f.addValue(threeL);
		f.addValue(threeL);
		f.addValue(3);
		f.addValue(threeI);
		junit.framework.Assert.assertEquals("one pct", 0.25, f.getPct(1), tolerance);
		junit.framework.Assert.assertEquals("two pct", 0.25, f.getPct(java.lang.Long.valueOf(2)), tolerance);
		junit.framework.Assert.assertEquals("three pct", 0.5, f.getPct(threeL), tolerance);
		junit.framework.Assert.assertEquals("three (Object) pct", 0.5, f.getPct(((java.lang.Object)(java.lang.Integer.valueOf(3)))), tolerance);
		junit.framework.Assert.assertEquals("five pct", 0, f.getPct(5), tolerance);
		junit.framework.Assert.assertEquals("foo pct", 0, f.getPct("foo"), tolerance);
		junit.framework.Assert.assertEquals("one cum pct", 0.25, f.getCumPct(1), tolerance);
		junit.framework.Assert.assertEquals("two cum pct", 0.5, f.getCumPct(java.lang.Long.valueOf(2)), tolerance);
		junit.framework.Assert.assertEquals("Integer argument", 0.5, f.getCumPct(java.lang.Integer.valueOf(2)), tolerance);
		junit.framework.Assert.assertEquals("three cum pct", 1.0, f.getCumPct(threeL), tolerance);
		junit.framework.Assert.assertEquals("five cum pct", 1.0, f.getCumPct(5), tolerance);
		junit.framework.Assert.assertEquals("zero cum pct", 0.0, f.getCumPct(0), tolerance);
		junit.framework.Assert.assertEquals("foo cum pct", 0, f.getCumPct("foo"), tolerance);
	}

	@java.lang.SuppressWarnings(value = "deprecation")
	public void testAdd() {
		char aChar = 'a';
		char bChar = 'b';
		java.lang.String aString = "a";
		f.addValue(aChar);
		f.addValue(bChar);
		try {
			f.addValue(aString);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			f.addValue(2);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		junit.framework.Assert.assertEquals("a pct", 0.5, f.getPct(aChar), tolerance);
		junit.framework.Assert.assertEquals("b cum pct", 1.0, f.getCumPct(bChar), tolerance);
		junit.framework.Assert.assertEquals("a string pct", 0.0, f.getPct(aString), tolerance);
		junit.framework.Assert.assertEquals("a string cum pct", 0.0, f.getCumPct(aString), tolerance);
		f = new org.apache.commons.math.stat.Frequency();
		f.addValue("One");
		try {
			f.addValue(new java.lang.Integer("One"));
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	@java.lang.SuppressWarnings(value = "deprecation")
	public void testAddNonComparable() {
		try {
			f.addValue(new java.lang.Object());
			junit.framework.Assert.fail("Expected IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException expected) {
		}
		f.clear();
		f.addValue(1);
		try {
			f.addValue(new java.lang.Object());
			junit.framework.Assert.fail("Expected IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException expected) {
		}
	}

	public void testEmptyTable() {
		junit.framework.Assert.assertEquals("freq sum, empty table", 0, f.getSumFreq());
		junit.framework.Assert.assertEquals("count, empty table", 0, f.getCount(0));
		junit.framework.Assert.assertEquals("count, empty table", 0, f.getCount(java.lang.Integer.valueOf(0)));
		junit.framework.Assert.assertEquals("cum freq, empty table", 0, f.getCumFreq(0));
		junit.framework.Assert.assertEquals("cum freq, empty table", 0, f.getCumFreq("x"));
		junit.framework.Assert.assertTrue("pct, empty table", java.lang.Double.isNaN(f.getPct(0)));
		junit.framework.Assert.assertTrue("pct, empty table", java.lang.Double.isNaN(f.getPct(java.lang.Integer.valueOf(0))));
		junit.framework.Assert.assertTrue("cum pct, empty table", java.lang.Double.isNaN(f.getCumPct(0)));
		junit.framework.Assert.assertTrue("cum pct, empty table", java.lang.Double.isNaN(f.getCumPct(java.lang.Integer.valueOf(0))));
	}

	public void testToString() {
		f.addValue(oneL);
		f.addValue(twoL);
		f.addValue(oneI);
		f.addValue(twoI);
		java.lang.String s = f.toString();
		junit.framework.Assert.assertNotNull(s);
		java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.StringReader(s));
		try {
			java.lang.String line = reader.readLine();
			junit.framework.Assert.assertNotNull(line);
			line = reader.readLine();
			junit.framework.Assert.assertNotNull(line);
			line = reader.readLine();
			junit.framework.Assert.assertNotNull(line);
			line = reader.readLine();
			junit.framework.Assert.assertNull(line);
		} catch (java.io.IOException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	@java.lang.SuppressWarnings(value = "deprecation")
	public void testIntegerValues() {
		java.lang.Comparable<?> obj1 = null;
		obj1 = java.lang.Integer.valueOf(1);
		java.lang.Integer int1 = java.lang.Integer.valueOf(1);
		f.addValue(obj1);
		f.addValue(int1);
		f.addValue(2);
		f.addValue(java.lang.Long.valueOf(2));
		junit.framework.Assert.assertEquals("Integer 1 count", 2, f.getCount(1));
		junit.framework.Assert.assertEquals("Integer 1 count", 2, f.getCount(java.lang.Integer.valueOf(1)));
		junit.framework.Assert.assertEquals("Integer 1 count", 2, f.getCount(java.lang.Long.valueOf(1)));
		junit.framework.Assert.assertEquals("Integer 1 cumPct", 0.5, f.getCumPct(1), tolerance);
		junit.framework.Assert.assertEquals("Integer 1 cumPct", 0.5, f.getCumPct(java.lang.Long.valueOf(1)), tolerance);
		junit.framework.Assert.assertEquals("Integer 1 cumPct", 0.5, f.getCumPct(java.lang.Integer.valueOf(1)), tolerance);
		java.util.Iterator<?> it = f.valuesIterator();
		while (it.hasNext()) {
			junit.framework.Assert.assertTrue(((it.next()) instanceof java.lang.Long));
		}
	}

	public void testSerial() {
		f.addValue(oneL);
		f.addValue(twoL);
		f.addValue(oneI);
		f.addValue(twoI);
		junit.framework.Assert.assertEquals(f, org.apache.commons.math.TestUtils.serializeAndRecover(f));
	}
}

