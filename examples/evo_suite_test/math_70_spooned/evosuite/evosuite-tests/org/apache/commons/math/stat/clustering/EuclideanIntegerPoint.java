package org.apache.commons.math.stat.clustering;


public class EuclideanIntegerPoint implements java.io.Serializable , org.apache.commons.math.stat.clustering.Clusterable<org.apache.commons.math.stat.clustering.EuclideanIntegerPoint> {
	private static final long serialVersionUID = 3946024775784901369L;

	private final int[] point;

	public EuclideanIntegerPoint(final int[] point) {
		this.point = point;
	}

	public int[] getPoint() {
		return point;
	}

	public double distanceFrom(final org.apache.commons.math.stat.clustering.EuclideanIntegerPoint p) {
		return org.apache.commons.math.util.MathUtils.distance(point, p.getPoint());
	}

	public org.apache.commons.math.stat.clustering.EuclideanIntegerPoint centroidOf(final java.util.Collection<org.apache.commons.math.stat.clustering.EuclideanIntegerPoint> points) {
		int[] centroid = new int[getPoint().length];
		for (org.apache.commons.math.stat.clustering.EuclideanIntegerPoint p : points) {
			for (int i = 0 ; i < (centroid.length) ; i++) {
				centroid[i] += p.getPoint()[i];
			}
		}
		for (int i = 0 ; i < (centroid.length) ; i++) {
			centroid[i] /= points.size();
		}
		return new org.apache.commons.math.stat.clustering.EuclideanIntegerPoint(centroid);
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object other) {
		if (!(other instanceof org.apache.commons.math.stat.clustering.EuclideanIntegerPoint)) {
			return false;
		} 
		final int[] otherPoint = ((org.apache.commons.math.stat.clustering.EuclideanIntegerPoint)(other)).getPoint();
		if ((point.length) != (otherPoint.length)) {
			return false;
		} 
		for (int i = 0 ; i < (point.length) ; i++) {
			if ((point[i]) != (otherPoint[i])) {
				return false;
			} 
		}
		return true;
	}

	@java.lang.Override
	public int hashCode() {
		int hashCode = 0;
		for (java.lang.Integer i : point) {
			hashCode += ((i.hashCode()) * 13) + 7;
		}
		return hashCode;
	}

	@java.lang.Override
	public java.lang.String toString() {
		final java.lang.StringBuffer buff = new java.lang.StringBuffer("(");
		final int[] coordinates = getPoint();
		for (int i = 0 ; i < (coordinates.length) ; i++) {
			buff.append(coordinates[i]);
			if (i < ((coordinates.length) - 1)) {
				buff.append(",");
			} 
		}
		buff.append(")");
		return buff.toString();
	}
}

