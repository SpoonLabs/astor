package org.apache.commons.math.linear;


public class EigenDecompositionImplTest extends junit.framework.TestCase {
	private double[] refValues;

	private org.apache.commons.math.linear.RealMatrix matrix;

	public EigenDecompositionImplTest(java.lang.String name) {
		super(name);
	}

	public void testDimension1() {
		org.apache.commons.math.linear.RealMatrix matrix = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 1.5 } });
		org.apache.commons.math.linear.EigenDecomposition ed = new org.apache.commons.math.linear.EigenDecompositionImpl(matrix , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		junit.framework.Assert.assertEquals(1.5, ed.getRealEigenvalue(0), 1.0E-15);
	}

	public void testDimension2() {
		org.apache.commons.math.linear.RealMatrix matrix = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 59.0 , 12.0 } , new double[]{ 12.0 , 66.0 } });
		org.apache.commons.math.linear.EigenDecomposition ed = new org.apache.commons.math.linear.EigenDecompositionImpl(matrix , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		junit.framework.Assert.assertEquals(75.0, ed.getRealEigenvalue(0), 1.0E-15);
		junit.framework.Assert.assertEquals(50.0, ed.getRealEigenvalue(1), 1.0E-15);
	}

	public void testDimension3() {
		org.apache.commons.math.linear.RealMatrix matrix = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 39632.0 , -4824.0 , -16560.0 } , new double[]{ -4824.0 , 8693.0 , 7920.0 } , new double[]{ -16560.0 , 7920.0 , 17300.0 } });
		org.apache.commons.math.linear.EigenDecomposition ed = new org.apache.commons.math.linear.EigenDecompositionImpl(matrix , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		junit.framework.Assert.assertEquals(50000.0, ed.getRealEigenvalue(0), 3.0E-11);
		junit.framework.Assert.assertEquals(12500.0, ed.getRealEigenvalue(1), 3.0E-11);
		junit.framework.Assert.assertEquals(3125.0, ed.getRealEigenvalue(2), 3.0E-11);
	}

	public void testDimension3MultipleRoot() {
		org.apache.commons.math.linear.RealMatrix matrix = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 5 , 10 , 15 } , new double[]{ 10 , 20 , 30 } , new double[]{ 15 , 30 , 45 } });
		org.apache.commons.math.linear.EigenDecomposition ed = new org.apache.commons.math.linear.EigenDecompositionImpl(matrix , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		junit.framework.Assert.assertEquals(70.0, ed.getRealEigenvalue(0), 3.0E-11);
		junit.framework.Assert.assertEquals(0.0, ed.getRealEigenvalue(1), 3.0E-11);
		junit.framework.Assert.assertEquals(0.0, ed.getRealEigenvalue(2), 3.0E-11);
	}

	public void testDimension4WithSplit() {
		org.apache.commons.math.linear.RealMatrix matrix = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 0.784 , -0.288 , 0.0 , 0.0 } , new double[]{ -0.288 , 0.616 , 0.0 , 0.0 } , new double[]{ 0.0 , 0.0 , 0.164 , -0.048 } , new double[]{ 0.0 , 0.0 , -0.048 , 0.136 } });
		org.apache.commons.math.linear.EigenDecomposition ed = new org.apache.commons.math.linear.EigenDecompositionImpl(matrix , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		junit.framework.Assert.assertEquals(1.0, ed.getRealEigenvalue(0), 1.0E-15);
		junit.framework.Assert.assertEquals(0.4, ed.getRealEigenvalue(1), 1.0E-15);
		junit.framework.Assert.assertEquals(0.2, ed.getRealEigenvalue(2), 1.0E-15);
		junit.framework.Assert.assertEquals(0.1, ed.getRealEigenvalue(3), 1.0E-15);
	}

	public void testDimension4WithoutSplit() {
		org.apache.commons.math.linear.RealMatrix matrix = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 0.5608 , -0.2016 , 0.1152 , -0.2976 } , new double[]{ -0.2016 , 0.4432 , -0.2304 , 0.1152 } , new double[]{ 0.1152 , -0.2304 , 0.3088 , -0.1344 } , new double[]{ -0.2976 , 0.1152 , -0.1344 , 0.3872 } });
		org.apache.commons.math.linear.EigenDecomposition ed = new org.apache.commons.math.linear.EigenDecompositionImpl(matrix , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		junit.framework.Assert.assertEquals(1.0, ed.getRealEigenvalue(0), 1.0E-15);
		junit.framework.Assert.assertEquals(0.4, ed.getRealEigenvalue(1), 1.0E-15);
		junit.framework.Assert.assertEquals(0.2, ed.getRealEigenvalue(2), 1.0E-15);
		junit.framework.Assert.assertEquals(0.1, ed.getRealEigenvalue(3), 1.0E-15);
	}

	public void testMath308() {
		double[] mainTridiagonal = new double[]{ 22.330154644539597 , 46.65485522478641 , 17.393672330044705 , 54.46687435351116 , 80.17800767709437 };
		double[] secondaryTridiagonal = new double[]{ 13.04450406501361 , -5.977590941539671 , 2.9040909856707517 , 7.1570352792841225 };
		double[] refEigenValues = new double[]{ 82.044413207204 , 53.45669769989451 , 52.53627852011388 , 18.84796973375426 , 14.138204224043099 };
		org.apache.commons.math.linear.RealVector[] refEigenVectors = new org.apache.commons.math.linear.RealVector[]{ new org.apache.commons.math.linear.ArrayRealVector(new double[]{ -4.62690386766E-4 , -0.002118073109055 , 0.011530080757413 , 0.252322434584915 , 0.967572088232592 }) , new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 0.314647769490148 , 0.750806415553905 , -0.16770031202576 , -0.537092972407375 , 0.14385496812778 }) , new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 0.222368839324646 , 0.514921891363332 , -0.021377019336614 , 0.801196801016305 , -0.20744699124774 }) , new org.apache.commons.math.linear.ArrayRealVector(new double[]{ -0.713933751051495 , 0.19058211355393 , -0.671410443368332 , 0.05605605595505 , -0.006541576993581 }) , new org.apache.commons.math.linear.ArrayRealVector(new double[]{ -0.584677060845929 , 0.367177264979103 , 0.721453187784497 , -0.052971054621812 , 0.005740715188257 }) };
		org.apache.commons.math.linear.EigenDecomposition decomposition = new org.apache.commons.math.linear.EigenDecompositionImpl(mainTridiagonal , secondaryTridiagonal , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		double[] eigenValues = decomposition.getRealEigenvalues();
		for (int i = 0 ; i < (refEigenValues.length) ; ++i) {
			junit.framework.Assert.assertEquals(refEigenValues[i], eigenValues[i], 1.0E-5);
			junit.framework.Assert.assertEquals(0, refEigenVectors[i].subtract(decomposition.getEigenvector(i)).getNorm(), 2.0E-7);
		}
	}

	public void testMathpbx02() {
		double[] mainTridiagonal = new double[]{ 7484.860960227216 , 18405.28129035345 , 13855.225609560746 , 10016.708722343366 , 559.8117399576674 , 6750.190788301587 , 71.21428769782159 };
		double[] secondaryTridiagonal = new double[]{ -4175.088570476366 , 1975.7955858241994 , 5193.178422374075 , 1995.286659169179 , 75.34535882933804 , -234.0808002076056 };
		double[] refEigenValues = new double[]{ 20654.744890306974 , 16828.208208485466 , 6893.155912634995 , 6757.08301667534 , 5887.799885688559 , 64.30908992324038 , 57.99262879273634 };
		org.apache.commons.math.linear.RealVector[] refEigenVectors = new org.apache.commons.math.linear.RealVector[]{ new org.apache.commons.math.linear.ArrayRealVector(new double[]{ -0.270356342026904 , 0.852811091326997 , 0.399639490702077 , 0.19879465781399 , 0.019739323307666 , 1.06983022327E-4 , -1.216636321E-6 }) , new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 0.179995273578326 , -0.402807848153042 , 0.701870993525734 , 0.555058211014888 , 0.068079148898236 , 5.09139115227E-4 , -7.112235617E-6 }) , new org.apache.commons.math.linear.ArrayRealVector(new double[]{ -0.399582721284727 , -0.056629954519333 , -0.514406488522827 , 0.71116816451858 , 0.225548081276367 , 0.125943999652923 , -0.004321507456014 }) , new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 0.058515721572821 , 0.010200130057739 , 0.063516274916536 , -0.090696087449378 , -0.017148420432597 , 0.991318870265707 , -0.034707338554096 }) , new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 0.855205995537564 , 0.327134656629775 , -0.265382397060548 , 0.282690729026706 , 0.105736068025572 , -0.009138126622039 , 3.67751821196E-4 }) , new org.apache.commons.math.linear.ArrayRealVector(new double[]{ -0.002913069901144 , -0.005177515777101 , 0.041906334478672 , -0.109315918416258 , 0.436192305456741 , 0.026307315639535 , 0.891797507436344 }) , new org.apache.commons.math.linear.ArrayRealVector(new double[]{ -0.005738311176435 , -0.010207611670378 , 0.082662420517928 , -0.215733886094368 , 0.861606487840411 , -0.025478530652759 , -0.451080697503958 }) };
		org.apache.commons.math.linear.EigenDecomposition decomposition = new org.apache.commons.math.linear.EigenDecompositionImpl(mainTridiagonal , secondaryTridiagonal , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		double[] eigenValues = decomposition.getRealEigenvalues();
		for (int i = 0 ; i < (refEigenValues.length) ; ++i) {
			junit.framework.Assert.assertEquals(refEigenValues[i], eigenValues[i], 0.001);
			if ((refEigenVectors[i].dotProduct(decomposition.getEigenvector(i))) < 0) {
				junit.framework.Assert.assertEquals(0, refEigenVectors[i].add(decomposition.getEigenvector(i)).getNorm(), 1.0E-5);
			} else {
				junit.framework.Assert.assertEquals(0, refEigenVectors[i].subtract(decomposition.getEigenvector(i)).getNorm(), 1.0E-5);
			}
		}
	}

	public void testMathpbx03() {
		double[] mainTridiagonal = new double[]{ 1809.0978259647177 , 3395.4763425956166 , 1832.1894584712693 , 3804.364873592377 , 806.0482458637571 , 2403.656427234185 , 28.48691431556015 };
		double[] secondaryTridiagonal = new double[]{ -656.8932064545833 , -469.30804108920734 , -1021.7714889369421 , -1152.540497328983 , -939.9765163817368 , -12.885877015422391 };
		double[] refEigenValues = new double[]{ 4603.121913685183 , 3691.195818048971 , 2743.442955402465 , 1657.5964421073218 , 1336.7978190953313 , 30.12986520967752 , 17.035352085224986 };
		org.apache.commons.math.linear.RealVector[] refEigenVectors = new org.apache.commons.math.linear.RealVector[]{ new org.apache.commons.math.linear.ArrayRealVector(new double[]{ -0.036249830202337 , 0.154184732411519 , -0.346016328392363 , 0.867540105133093 , -0.294483395433451 , 0.125854235969548 , -3.54507444044E-4 }) , new org.apache.commons.math.linear.ArrayRealVector(new double[]{ -0.318654191697157 , 0.912992309960507 , -0.129270874079777 , -0.184150038178035 , 0.096521712579439 , -0.070468788536461 , 2.47918177736E-4 }) , new org.apache.commons.math.linear.ArrayRealVector(new double[]{ -0.051394668681147 , 0.073102235876933 , 0.173502042943743 , -0.188311980310942 , -0.327158794289386 , 0.905206581432676 , -0.004296342252659 }) , new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 0.838150199198361 , 0.193305209055716 , -0.457341242126146 , -0.166933875895419 , 0.094512811358535 , 0.119062381338757 , -9.41755685226E-4 }) , new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 0.438071395458547 , 0.314969169786246 , 0.768480630802146 , 0.227919171600705 , -0.193317045298647 , -0.170305467485594 , 0.001677380536009 }) , new org.apache.commons.math.linear.ArrayRealVector(new double[]{ -0.003726503878741 , -0.010091946369146 , -0.067152015137611 , -0.113798146542187 , -0.313123000097908 , -0.118940107954918 , 0.932862311396062 }) , new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 0.009373003194332 , 0.0255703775594 , 0.170955836081348 , 0.29195451980575 , 0.807824267665706 , 0.320108347088646 , 0.360202112392266 }) };
		org.apache.commons.math.linear.EigenDecomposition decomposition = new org.apache.commons.math.linear.EigenDecompositionImpl(mainTridiagonal , secondaryTridiagonal , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		double[] eigenValues = decomposition.getRealEigenvalues();
		for (int i = 0 ; i < (refEigenValues.length) ; ++i) {
			junit.framework.Assert.assertEquals(refEigenValues[i], eigenValues[i], 1.0E-4);
			if ((refEigenVectors[i].dotProduct(decomposition.getEigenvector(i))) < 0) {
				junit.framework.Assert.assertEquals(0, refEigenVectors[i].add(decomposition.getEigenvector(i)).getNorm(), 1.0E-5);
			} else {
				junit.framework.Assert.assertEquals(0, refEigenVectors[i].subtract(decomposition.getEigenvector(i)).getNorm(), 1.0E-5);
			}
		}
	}

	public void testTridiagonal() {
		java.util.Random r = new java.util.Random(4366663527842L);
		double[] ref = new double[30];
		for (int i = 0 ; i < (ref.length) ; ++i) {
			if (i < 5) {
				ref[i] = (2 * (r.nextDouble())) - 1;
			} else {
				ref[i] = (1.0E-4 * (r.nextDouble())) + 6;
			}
		}
		java.util.Arrays.sort(ref);
		org.apache.commons.math.linear.TriDiagonalTransformer t = new org.apache.commons.math.linear.TriDiagonalTransformer(org.apache.commons.math.linear.EigenDecompositionImplTest.createTestMatrix(r, ref));
		org.apache.commons.math.linear.EigenDecomposition ed = new org.apache.commons.math.linear.EigenDecompositionImpl(t.getMainDiagonalRef() , t.getSecondaryDiagonalRef() , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		double[] eigenValues = ed.getRealEigenvalues();
		junit.framework.Assert.assertEquals(ref.length, eigenValues.length);
		for (int i = 0 ; i < (ref.length) ; ++i) {
			junit.framework.Assert.assertEquals(ref[(((ref.length) - i) - 1)], eigenValues[i], 2.0E-14);
		}
	}

	public void testDimensions() {
		final int m = matrix.getRowDimension();
		org.apache.commons.math.linear.EigenDecomposition ed = new org.apache.commons.math.linear.EigenDecompositionImpl(matrix , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		junit.framework.Assert.assertEquals(m, ed.getV().getRowDimension());
		junit.framework.Assert.assertEquals(m, ed.getV().getColumnDimension());
		junit.framework.Assert.assertEquals(m, ed.getD().getColumnDimension());
		junit.framework.Assert.assertEquals(m, ed.getD().getColumnDimension());
		junit.framework.Assert.assertEquals(m, ed.getVT().getRowDimension());
		junit.framework.Assert.assertEquals(m, ed.getVT().getColumnDimension());
	}

	public void testEigenvalues() {
		org.apache.commons.math.linear.EigenDecomposition ed = new org.apache.commons.math.linear.EigenDecompositionImpl(matrix , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		double[] eigenValues = ed.getRealEigenvalues();
		junit.framework.Assert.assertEquals(refValues.length, eigenValues.length);
		for (int i = 0 ; i < (refValues.length) ; ++i) {
			junit.framework.Assert.assertEquals(refValues[i], eigenValues[i], 3.0E-15);
		}
	}

	public void testBigMatrix() {
		java.util.Random r = new java.util.Random(17748333525117L);
		double[] bigValues = new double[200];
		for (int i = 0 ; i < (bigValues.length) ; ++i) {
			bigValues[i] = (2 * (r.nextDouble())) - 1;
		}
		java.util.Arrays.sort(bigValues);
		org.apache.commons.math.linear.EigenDecomposition ed = new org.apache.commons.math.linear.EigenDecompositionImpl(org.apache.commons.math.linear.EigenDecompositionImplTest.createTestMatrix(r, bigValues) , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		double[] eigenValues = ed.getRealEigenvalues();
		junit.framework.Assert.assertEquals(bigValues.length, eigenValues.length);
		for (int i = 0 ; i < (bigValues.length) ; ++i) {
			junit.framework.Assert.assertEquals(bigValues[(((bigValues.length) - i) - 1)], eigenValues[i], 2.0E-14);
		}
	}

	public void testEigenvectors() {
		org.apache.commons.math.linear.EigenDecomposition ed = new org.apache.commons.math.linear.EigenDecompositionImpl(matrix , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		for (int i = 0 ; i < (matrix.getRowDimension()) ; ++i) {
			double lambda = ed.getRealEigenvalue(i);
			org.apache.commons.math.linear.RealVector v = ed.getEigenvector(i);
			org.apache.commons.math.linear.RealVector mV = matrix.operate(v);
			junit.framework.Assert.assertEquals(0, mV.subtract(v.mapMultiplyToSelf(lambda)).getNorm(), 1.0E-13);
		}
	}

	public void testAEqualVDVt() {
		org.apache.commons.math.linear.EigenDecomposition ed = new org.apache.commons.math.linear.EigenDecompositionImpl(matrix , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		org.apache.commons.math.linear.RealMatrix v = ed.getV();
		org.apache.commons.math.linear.RealMatrix d = ed.getD();
		org.apache.commons.math.linear.RealMatrix vT = ed.getVT();
		double norm = v.multiply(d).multiply(vT).subtract(matrix).getNorm();
		junit.framework.Assert.assertEquals(0, norm, 6.0E-13);
	}

	public void testVOrthogonal() {
		org.apache.commons.math.linear.RealMatrix v = new org.apache.commons.math.linear.EigenDecompositionImpl(matrix , org.apache.commons.math.util.MathUtils.SAFE_MIN).getV();
		org.apache.commons.math.linear.RealMatrix vTv = v.transpose().multiply(v);
		org.apache.commons.math.linear.RealMatrix id = org.apache.commons.math.linear.MatrixUtils.createRealIdentityMatrix(vTv.getRowDimension());
		junit.framework.Assert.assertEquals(0, vTv.subtract(id).getNorm(), 2.0E-13);
	}

	public void testDiagonal() {
		double[] diagonal = new double[]{ -3.0 , -2.0 , 2.0 , 5.0 };
		org.apache.commons.math.linear.RealMatrix m = org.apache.commons.math.linear.EigenDecompositionImplTest.createDiagonalMatrix(diagonal, diagonal.length, diagonal.length);
		org.apache.commons.math.linear.EigenDecomposition ed = new org.apache.commons.math.linear.EigenDecompositionImpl(m , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		junit.framework.Assert.assertEquals(diagonal[0], ed.getRealEigenvalue(3), 2.0E-15);
		junit.framework.Assert.assertEquals(diagonal[1], ed.getRealEigenvalue(2), 2.0E-15);
		junit.framework.Assert.assertEquals(diagonal[2], ed.getRealEigenvalue(1), 2.0E-15);
		junit.framework.Assert.assertEquals(diagonal[3], ed.getRealEigenvalue(0), 2.0E-15);
	}

	public void testRepeatedEigenvalue() {
		org.apache.commons.math.linear.RealMatrix repeated = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 3 , 2 , 4 } , new double[]{ 2 , 0 , 2 } , new double[]{ 4 , 2 , 3 } });
		org.apache.commons.math.linear.EigenDecomposition ed = new org.apache.commons.math.linear.EigenDecompositionImpl(repeated , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		checkEigenValues(new double[]{ 8 , -1 , -1 }, ed, 1.0E-12);
		checkEigenVector(new double[]{ 2 , 1 , 2 }, ed, 1.0E-12);
	}

	public void testDistinctEigenvalues() {
		org.apache.commons.math.linear.RealMatrix distinct = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 3 , 1 , -4 } , new double[]{ 1 , 3 , -4 } , new double[]{ -4 , -4 , 8 } });
		org.apache.commons.math.linear.EigenDecomposition ed = new org.apache.commons.math.linear.EigenDecompositionImpl(distinct , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		checkEigenValues(new double[]{ 2 , 0 , 12 }, ed, 1.0E-12);
		checkEigenVector(new double[]{ 1 , -1 , 0 }, ed, 1.0E-12);
		checkEigenVector(new double[]{ 1 , 1 , 1 }, ed, 1.0E-12);
		checkEigenVector(new double[]{ -1 , -1 , 2 }, ed, 1.0E-12);
	}

	public void testZeroDivide() {
		org.apache.commons.math.linear.RealMatrix indefinite = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 0.0 , 1.0 , -1.0 } , new double[]{ 1.0 , 1.0 , 0.0 } , new double[]{ -1.0 , 0.0 , 1.0 } });
		org.apache.commons.math.linear.EigenDecomposition ed = new org.apache.commons.math.linear.EigenDecompositionImpl(indefinite , org.apache.commons.math.util.MathUtils.SAFE_MIN);
		checkEigenValues(new double[]{ 2 , 1 , -1 }, ed, 1.0E-12);
		double isqrt3 = 1 / (java.lang.Math.sqrt(3.0));
		checkEigenVector(new double[]{ isqrt3 , isqrt3 , -isqrt3 }, ed, 1.0E-12);
		double isqrt2 = 1 / (java.lang.Math.sqrt(2.0));
		checkEigenVector(new double[]{ 0.0 , -isqrt2 , -isqrt2 }, ed, 1.0E-12);
		double isqrt6 = 1 / (java.lang.Math.sqrt(6.0));
		checkEigenVector(new double[]{ 2 * isqrt6 , -isqrt6 , isqrt6 }, ed, 1.0E-12);
	}

	protected void checkEigenValues(double[] targetValues, org.apache.commons.math.linear.EigenDecomposition ed, double tolerance) {
		double[] observed = ed.getRealEigenvalues();
		for (int i = 0 ; i < (observed.length) ; i++) {
			junit.framework.Assert.assertTrue(isIncludedValue(observed[i], targetValues, tolerance));
			junit.framework.Assert.assertTrue(isIncludedValue(targetValues[i], observed, tolerance));
		}
	}

	private boolean isIncludedValue(double value, double[] searchArray, double tolerance) {
		boolean found = false;
		int i = 0;
		while ((!found) && (i < (searchArray.length))) {
			if ((java.lang.Math.abs((value - (searchArray[i])))) < tolerance) {
				found = true;
			} 
			i++;
		}
		return found;
	}

	protected void checkEigenVector(double[] eigenVector, org.apache.commons.math.linear.EigenDecomposition ed, double tolerance) {
		junit.framework.Assert.assertTrue(isIncludedColumn(eigenVector, ed.getV(), tolerance));
	}

	private boolean isIncludedColumn(double[] column, org.apache.commons.math.linear.RealMatrix searchMatrix, double tolerance) {
		boolean found = false;
		int i = 0;
		while ((!found) && (i < (searchMatrix.getColumnDimension()))) {
			double multiplier = 1.0;
			boolean matching = true;
			int j = 0;
			while (matching && (j < (searchMatrix.getRowDimension()))) {
				double colEntry = searchMatrix.getEntry(j, i);
				if ((((java.lang.Math.abs((multiplier - 1.0))) <= (java.lang.Math.ulp(1.0))) && ((java.lang.Math.abs(colEntry)) > 1.0E-14)) && ((java.lang.Math.abs(column[j])) > 1.0E-14)) {
					multiplier = colEntry / (column[j]);
				} 
				if ((java.lang.Math.abs((((column[j]) * multiplier) - colEntry))) > tolerance) {
					matching = false;
				} 
				j++;
			}
			found = matching;
			i++;
		}
		return found;
	}

	@java.lang.Override
	public void setUp() {
		refValues = new double[]{ 2.003 , 2.002 , 2.001 , 1.001 , 1.0 , 0.001 };
		matrix = org.apache.commons.math.linear.EigenDecompositionImplTest.createTestMatrix(new java.util.Random(35992629946426L), refValues);
	}

	@java.lang.Override
	public void tearDown() {
		refValues = null;
		matrix = null;
	}

	static org.apache.commons.math.linear.RealMatrix createTestMatrix(final java.util.Random r, final double[] eigenValues) {
		final int n = eigenValues.length;
		final org.apache.commons.math.linear.RealMatrix v = org.apache.commons.math.linear.EigenDecompositionImplTest.createOrthogonalMatrix(r, n);
		final org.apache.commons.math.linear.RealMatrix d = org.apache.commons.math.linear.EigenDecompositionImplTest.createDiagonalMatrix(eigenValues, n, n);
		return v.multiply(d).multiply(v.transpose());
	}

	public static org.apache.commons.math.linear.RealMatrix createOrthogonalMatrix(final java.util.Random r, final int size) {
		final double[][] data = new double[size][size];
		for (int i = 0 ; i < size ; ++i) {
			final double[] dataI = data[i];
			double norm2 = 0;
			do {
				for (int j = 0 ; j < size ; ++j) {
					dataI[j] = (2 * (r.nextDouble())) - 1;
				}
				for (int k = 0 ; k < i ; ++k) {
					final double[] dataK = data[k];
					double dotProduct = 0;
					for (int j = 0 ; j < size ; ++j) {
						dotProduct += (dataI[j]) * (dataK[j]);
					}
					for (int j = 0 ; j < size ; ++j) {
						dataI[j] -= dotProduct * (dataK[j]);
					}
				}
				norm2 = 0;
				for (final double dataIJ : dataI) {
					norm2 += dataIJ * dataIJ;
				}
				final double inv = 1.0 / (java.lang.Math.sqrt(norm2));
				for (int j = 0 ; j < size ; ++j) {
					dataI[j] *= inv;
				}
			} while ((norm2 * size) < 0.01 );
		}
		return org.apache.commons.math.linear.MatrixUtils.createRealMatrix(data);
	}

	public static org.apache.commons.math.linear.RealMatrix createDiagonalMatrix(final double[] diagonal, final int rows, final int columns) {
		final double[][] dData = new double[rows][columns];
		for (int i = 0 ; i < (java.lang.Math.min(rows, columns)) ; ++i) {
			dData[i][i] = diagonal[i];
		}
		return org.apache.commons.math.linear.MatrixUtils.createRealMatrix(dData);
	}
}

