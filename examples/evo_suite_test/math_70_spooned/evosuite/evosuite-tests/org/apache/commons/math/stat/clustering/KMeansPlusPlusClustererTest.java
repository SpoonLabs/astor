package org.apache.commons.math.stat.clustering;


public class KMeansPlusPlusClustererTest {
	@org.junit.Test
	public void dimension2() {
		org.apache.commons.math.stat.clustering.KMeansPlusPlusClusterer<org.apache.commons.math.stat.clustering.EuclideanIntegerPoint> transformer = new org.apache.commons.math.stat.clustering.KMeansPlusPlusClusterer<org.apache.commons.math.stat.clustering.EuclideanIntegerPoint>(new java.util.Random(1746432956321L));
		org.apache.commons.math.stat.clustering.EuclideanIntegerPoint[] points = new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint[]{ new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ -15 , 3 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ -15 , 4 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ -15 , 5 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ -14 , 3 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ -14 , 5 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ -13 , 3 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ -13 , 4 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ -13 , 5 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ -1 , 0 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ -1 , -1 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 0 , -1 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 1 , -1 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 1 , -2 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 13 , 3 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 13 , 4 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 14 , 4 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 14 , 7 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 16 , 5 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 16 , 6 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 17 , 4 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 17 , 7 }) };
		java.util.List<org.apache.commons.math.stat.clustering.Cluster<org.apache.commons.math.stat.clustering.EuclideanIntegerPoint>> clusters = transformer.cluster(java.util.Arrays.asList(points), 3, 10);
		org.junit.Assert.assertEquals(3, clusters.size());
		boolean cluster1Found = false;
		boolean cluster2Found = false;
		boolean cluster3Found = false;
		for (org.apache.commons.math.stat.clustering.Cluster<org.apache.commons.math.stat.clustering.EuclideanIntegerPoint> cluster : clusters) {
			int[] center = cluster.getCenter().getPoint();
			if ((center[0]) < 0) {
				cluster1Found = true;
				org.junit.Assert.assertEquals(8, cluster.getPoints().size());
				org.junit.Assert.assertEquals(-14, center[0]);
				org.junit.Assert.assertEquals(4, center[1]);
			} else {
				if ((center[1]) < 0) {
					cluster2Found = true;
					org.junit.Assert.assertEquals(5, cluster.getPoints().size());
					org.junit.Assert.assertEquals(0, center[0]);
					org.junit.Assert.assertEquals(-1, center[1]);
				} else {
					cluster3Found = true;
					org.junit.Assert.assertEquals(8, cluster.getPoints().size());
					org.junit.Assert.assertEquals(15, center[0]);
					org.junit.Assert.assertEquals(5, center[1]);
				}
			}
		}
		org.junit.Assert.assertTrue(cluster1Found);
		org.junit.Assert.assertTrue(cluster2Found);
		org.junit.Assert.assertTrue(cluster3Found);
	}

	@org.junit.Test
	public void testPerformClusterAnalysisDegenerate() {
		org.apache.commons.math.stat.clustering.KMeansPlusPlusClusterer<org.apache.commons.math.stat.clustering.EuclideanIntegerPoint> transformer = new org.apache.commons.math.stat.clustering.KMeansPlusPlusClusterer<org.apache.commons.math.stat.clustering.EuclideanIntegerPoint>(new java.util.Random(1746432956321L));
		org.apache.commons.math.stat.clustering.EuclideanIntegerPoint[] points = new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint[]{ new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 1959 , 325100 }) , new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 1960 , 373200 }) };
		java.util.List<org.apache.commons.math.stat.clustering.Cluster<org.apache.commons.math.stat.clustering.EuclideanIntegerPoint>> clusters = transformer.cluster(java.util.Arrays.asList(points), 1, 1);
		org.junit.Assert.assertEquals(1, clusters.size());
		org.junit.Assert.assertEquals(2, clusters.get(0).getPoints().size());
		org.apache.commons.math.stat.clustering.EuclideanIntegerPoint pt1 = new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 1959 , 325100 });
		org.apache.commons.math.stat.clustering.EuclideanIntegerPoint pt2 = new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(new int[]{ 1960 , 373200 });
		org.junit.Assert.assertTrue(clusters.get(0).getPoints().contains(pt1));
		org.junit.Assert.assertTrue(clusters.get(0).getPoints().contains(pt2));
	}
}

