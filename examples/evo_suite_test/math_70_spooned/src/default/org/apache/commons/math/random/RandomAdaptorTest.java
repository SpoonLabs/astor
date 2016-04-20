package org.apache.commons.math.random;


public class RandomAdaptorTest extends org.apache.commons.math.random.RandomDataTest {
	public RandomAdaptorTest(java.lang.String name) {
		super(name);
	}

	public void testAdaptor() {
		org.apache.commons.math.random.RandomAdaptorTest.ConstantGenerator generator = new org.apache.commons.math.random.RandomAdaptorTest.ConstantGenerator();
		java.util.Random random = org.apache.commons.math.random.RandomAdaptor.createAdaptor(generator);
		checkConstant(random);
		org.apache.commons.math.random.RandomAdaptor randomAdaptor = new org.apache.commons.math.random.RandomAdaptor(generator);
		checkConstant(randomAdaptor);
	}

	private void checkConstant(java.util.Random random) {
		byte[] bytes = new byte[]{ 0 };
		random.nextBytes(bytes);
		junit.framework.Assert.assertEquals(0, bytes[0]);
		junit.framework.Assert.assertEquals(false, random.nextBoolean());
		junit.framework.Assert.assertEquals(0, random.nextDouble(), 0);
		junit.framework.Assert.assertEquals(0, random.nextFloat(), 0);
		junit.framework.Assert.assertEquals(0, random.nextGaussian(), 0);
		junit.framework.Assert.assertEquals(0, random.nextInt());
		junit.framework.Assert.assertEquals(0, random.nextInt(1));
		junit.framework.Assert.assertEquals(0, random.nextLong());
		random.setSeed(100);
		junit.framework.Assert.assertEquals(0, random.nextDouble(), 0);
	}

	private static class ConstantGenerator implements org.apache.commons.math.random.RandomGenerator {
		public boolean nextBoolean() {
			return false;
		}

		public void nextBytes(byte[] bytes) {
		}

		public double nextDouble() {
			return 0;
		}

		public float nextFloat() {
			return 0;
		}

		public double nextGaussian() {
			return 0;
		}

		public int nextInt() {
			return 0;
		}

		public int nextInt(int n) {
			return 0;
		}

		public long nextLong() {
			return 0;
		}

		public void setSeed(int seed) {
		}

		public void setSeed(int[] seed) {
		}

		public void setSeed(long seed) {
		}
	}
}

