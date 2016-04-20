package org.apache.commons.math.stat.data;


public abstract class CertifiedDataAbstractTest extends junit.framework.TestCase {
	private org.apache.commons.math.stat.descriptive.DescriptiveStatistics descriptives;

	private org.apache.commons.math.stat.descriptive.SummaryStatistics summaries;

	private java.util.Map<java.lang.String, java.lang.Double> certifiedValues;

	@java.lang.Override
	protected void setUp() throws java.lang.Exception {
		descriptives = new org.apache.commons.math.stat.descriptive.DescriptiveStatistics();
		summaries = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		certifiedValues = new java.util.HashMap<java.lang.String, java.lang.Double>();
		loadData();
	}

	private void loadData() throws java.io.IOException {
		java.io.BufferedReader in = null;
		try {
			java.net.URL resourceURL = getClass().getClassLoader().getResource(getResourceName());
			in = new java.io.BufferedReader(new java.io.InputStreamReader(resourceURL.openStream()));
			java.lang.String line = in.readLine();
			while (line != null) {
				line = line.trim();
				if (!(("".equals(line)) || (line.startsWith("#")))) {
					int n = line.indexOf('=');
					if (n == (-1)) {
						double value = java.lang.Double.parseDouble(line);
						descriptives.addValue(value);
						summaries.addValue(value);
					} else {
						java.lang.String name = line.substring(0, n).trim();
						java.lang.String valueString = line.substring((n + 1)).trim();
						java.lang.Double value = java.lang.Double.valueOf(valueString);
						certifiedValues.put(name, value);
					}
				} 
				line = in.readLine();
			}
		} finally {
			if (in != null) {
				in.close();
			} 
		}
	}

	protected abstract java.lang.String getResourceName();

	protected double getMaximumAbsoluteError() {
		return 1.0E-5;
	}

	@java.lang.Override
	protected void tearDown() throws java.lang.Exception {
		descriptives.clear();
		descriptives = null;
		summaries.clear();
		summaries = null;
		certifiedValues.clear();
		certifiedValues = null;
	}

	public void testCertifiedValues() {
		for (java.lang.String name : certifiedValues.keySet()) {
			java.lang.Double expectedValue = certifiedValues.get(name);
			java.lang.Double summariesValue = getProperty(summaries, name);
			if (summariesValue != null) {
				org.apache.commons.math.TestUtils.assertEquals((("summary value for " + name) + " is incorrect."), summariesValue.doubleValue(), expectedValue.doubleValue(), getMaximumAbsoluteError());
			} 
			java.lang.Double descriptivesValue = getProperty(descriptives, name);
			if (descriptivesValue != null) {
				org.apache.commons.math.TestUtils.assertEquals((("descriptive value for " + name) + " is incorrect."), descriptivesValue.doubleValue(), expectedValue.doubleValue(), getMaximumAbsoluteError());
			} 
		}
	}

	protected java.lang.Double getProperty(java.lang.Object bean, java.lang.String name) {
		try {
			java.lang.String prop = ("get" + (name.substring(0, 1).toUpperCase())) + (name.substring(1));
			java.lang.reflect.Method meth = bean.getClass().getMethod(prop, new java.lang.Class[0]);
			java.lang.Object property = meth.invoke(bean, new java.lang.Object[0]);
			if (meth.getReturnType().equals(java.lang.Double.TYPE)) {
				return ((java.lang.Double)(property));
			} else {
				if (meth.getReturnType().equals(java.lang.Long.TYPE)) {
					return java.lang.Double.valueOf(((java.lang.Long)(property)).doubleValue());
				} else {
					junit.framework.Assert.fail(("wrong type: " + (meth.getReturnType().getName())));
				}
			}
		} catch (java.lang.NoSuchMethodException nsme) {
		} catch (java.lang.reflect.InvocationTargetException ite) {
			junit.framework.Assert.fail(ite.getMessage());
		} catch (java.lang.IllegalAccessException iae) {
			junit.framework.Assert.fail(iae.getMessage());
		}
		return null;
	}
}

