package org.apache.commons.math.random;


public class MersenneTwister extends org.apache.commons.math.random.BitsStreamGenerator implements java.io.Serializable {
	private static final long serialVersionUID = 8661194735290153518L;

	private static final int N = 624;

	private static final int M = 397;

	private static final int[] MAG01 = new int[]{ 0 , -1727483681 };

	private int[] mt;

	private int mti;

	public MersenneTwister() {
		mt = new int[N];
		setSeed(java.lang.System.currentTimeMillis());
	}

	public MersenneTwister(int seed) {
		mt = new int[N];
		setSeed(seed);
	}

	public MersenneTwister(int[] seed) {
		mt = new int[N];
		setSeed(seed);
	}

	public MersenneTwister(long seed) {
		mt = new int[N];
		setSeed(seed);
	}

	@java.lang.Override
	public void setSeed(int seed) {
		long longMT = seed;
		mt[0] = ((int)(longMT));
		for (mti = 1 ; (mti) < (N) ; ++(mti)) {
			longMT = ((1812433253L * (longMT ^ (longMT >> 30))) + (mti)) & 4294967295L;
			mt[mti] = ((int)(longMT));
		}
	}

	@java.lang.Override
	public void setSeed(int[] seed) {
		if (seed == null) {
			setSeed(java.lang.System.currentTimeMillis());
			return ;
		} 
		setSeed(19650218);
		int i = 1;
		int j = 0;
		for (int k = java.lang.Math.max(N, seed.length) ; k != 0 ; k--) {
			long l0 = ((mt[i]) & 2147483647L) | ((mt[i]) < 0 ? 2147483648L : 0L);
			long l1 = ((mt[(i - 1)]) & 2147483647L) | ((mt[(i - 1)]) < 0 ? 2147483648L : 0L);
			long l = ((l0 ^ ((l1 ^ (l1 >> 30)) * 1664525L)) + (seed[j])) + j;
			mt[i] = ((int)(l & 4294967295L));
			i++;
			j++;
			if (i >= (N)) {
				mt[0] = mt[((N) - 1)];
				i = 1;
			} 
			if (j >= (seed.length)) {
				j = 0;
			} 
		}
		for (int k = (N) - 1 ; k != 0 ; k--) {
			long l0 = ((mt[i]) & 2147483647L) | ((mt[i]) < 0 ? 2147483648L : 0L);
			long l1 = ((mt[(i - 1)]) & 2147483647L) | ((mt[(i - 1)]) < 0 ? 2147483648L : 0L);
			long l = (l0 ^ ((l1 ^ (l1 >> 30)) * 1566083941L)) - i;
			mt[i] = ((int)(l & 4294967295L));
			i++;
			if (i >= (N)) {
				mt[0] = mt[((N) - 1)];
				i = 1;
			} 
		}
		mt[0] = -2147483648;
	}

	@java.lang.Override
	public void setSeed(long seed) {
		setSeed(new int[]{ ((int)(seed >>> 32)) , ((int)(seed & 4294967295L)) });
	}

	@java.lang.Override
	protected int next(int bits) {
		int y;
		if ((mti) >= (N)) {
			int mtNext = mt[0];
			for (int k = 0 ; k < ((N) - (M)) ; ++k) {
				int mtCurr = mtNext;
				mtNext = mt[(k + 1)];
				y = (mtCurr & -2147483648) | (mtNext & 2147483647);
				mt[k] = ((mt[(k + (M))]) ^ (y >>> 1)) ^ (MAG01[(y & 1)]);
			}
			for (int k = (N) - (M) ; k < ((N) - 1) ; ++k) {
				int mtCurr = mtNext;
				mtNext = mt[(k + 1)];
				y = (mtCurr & -2147483648) | (mtNext & 2147483647);
				mt[k] = ((mt[(k + ((M) - (N)))]) ^ (y >>> 1)) ^ (MAG01[(y & 1)]);
			}
			y = (mtNext & -2147483648) | ((mt[0]) & 2147483647);
			mt[((N) - 1)] = ((mt[((M) - 1)]) ^ (y >>> 1)) ^ (MAG01[(y & 1)]);
			mti = 0;
		} 
		y = mt[(mti)++];
		y ^= y >>> 11;
		y ^= (y << 7) & -1658038656;
		y ^= (y << 15) & -272236544;
		y ^= y >>> 18;
		return y >>> (32 - bits);
	}
}

