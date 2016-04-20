package org.apache.commons.math.stat.clustering;


public class KMeansPlusPlusClusterer<T extends org.apache.commons.math.stat.clustering.Clusterable<T>> {
	private final java.util.Random random;

	public KMeansPlusPlusClusterer(final java.util.Random random) {
		this.random = random;
	}

	public java.util.List<org.apache.commons.math.stat.clustering.Cluster<T>> cluster(final java.util.Collection<T> points, final int k, final int maxIterations) {
		java.util.List<org.apache.commons.math.stat.clustering.Cluster<T>> clusters = org.apache.commons.math.stat.clustering.KMeansPlusPlusClusterer.chooseInitialCenters(points, k, random);
		org.apache.commons.math.stat.clustering.KMeansPlusPlusClusterer.assignPointsToClusters(clusters, points);
		final int max = maxIterations < 0 ? java.lang.Integer.MAX_VALUE : maxIterations;
		for (int count = 0 ; count < max ; count++) {
			boolean clusteringChanged = false;
			java.util.List<org.apache.commons.math.stat.clustering.Cluster<T>> newClusters = new java.util.ArrayList<org.apache.commons.math.stat.clustering.Cluster<T>>();
			for (final org.apache.commons.math.stat.clustering.Cluster<T> cluster : clusters) {
				final T newCenter = cluster.getCenter().centroidOf(cluster.getPoints());
				if (!(newCenter.equals(cluster.getCenter()))) {
					clusteringChanged = true;
				} 
				newClusters.add(new org.apache.commons.math.stat.clustering.Cluster<T>(newCenter));
			}
			if (!clusteringChanged) {
				return clusters;
			} 
			org.apache.commons.math.stat.clustering.KMeansPlusPlusClusterer.assignPointsToClusters(newClusters, points);
			clusters = newClusters;
		}
		return clusters;
	}

	private static <T extends org.apache.commons.math.stat.clustering.Clusterable<T>>void assignPointsToClusters(final java.util.Collection<org.apache.commons.math.stat.clustering.Cluster<T>> clusters, final java.util.Collection<T> points) {
		for (final T p : points) {
			org.apache.commons.math.stat.clustering.Cluster<T> cluster = org.apache.commons.math.stat.clustering.KMeansPlusPlusClusterer.getNearestCluster(clusters, p);
			cluster.addPoint(p);
		}
	}

	private static <T extends org.apache.commons.math.stat.clustering.Clusterable<T>>java.util.List<org.apache.commons.math.stat.clustering.Cluster<T>> chooseInitialCenters(final java.util.Collection<T> points, final int k, final java.util.Random random) {
		final java.util.List<T> pointSet = new java.util.ArrayList<T>(points);
		final java.util.List<org.apache.commons.math.stat.clustering.Cluster<T>> resultSet = new java.util.ArrayList<org.apache.commons.math.stat.clustering.Cluster<T>>();
		final T firstPoint = pointSet.remove(random.nextInt(pointSet.size()));
		resultSet.add(new org.apache.commons.math.stat.clustering.Cluster<T>(firstPoint));
		final double[] dx2 = new double[pointSet.size()];
		while ((resultSet.size()) < k) {
			int sum = 0;
			for (int i = 0 ; i < (pointSet.size()) ; i++) {
				final T p = pointSet.get(i);
				final org.apache.commons.math.stat.clustering.Cluster<T> nearest = org.apache.commons.math.stat.clustering.KMeansPlusPlusClusterer.getNearestCluster(resultSet, p);
				final double d = p.distanceFrom(nearest.getCenter());
				sum += d * d;
				dx2[i] = sum;
			}
			final double r = (random.nextDouble()) * sum;
			for (int i = 0 ; i < (dx2.length) ; i++) {
				if ((dx2[i]) >= r) {
					final T p = pointSet.remove(i);
					resultSet.add(new org.apache.commons.math.stat.clustering.Cluster<T>(p));
					break;
				} 
			}
		}
		return resultSet;
	}

	private static <T extends org.apache.commons.math.stat.clustering.Clusterable<T>>org.apache.commons.math.stat.clustering.Cluster<T> getNearestCluster(final java.util.Collection<org.apache.commons.math.stat.clustering.Cluster<T>> clusters, final T point) {
		double minDistance = java.lang.Double.MAX_VALUE;
		org.apache.commons.math.stat.clustering.Cluster<T> minCluster = null;
		for (final org.apache.commons.math.stat.clustering.Cluster<T> c : clusters) {
			final double distance = point.distanceFrom(c.getCenter());
			if (distance < minDistance) {
				minDistance = distance;
				minCluster = c;
			} 
		}
		return minCluster;
	}
}

