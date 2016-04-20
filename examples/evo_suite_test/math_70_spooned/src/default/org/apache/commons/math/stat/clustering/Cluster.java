package org.apache.commons.math.stat.clustering;


public class Cluster<T extends org.apache.commons.math.stat.clustering.Clusterable<T>> implements java.io.Serializable {
	private static final long serialVersionUID = -3442297081515880464L;

	private final java.util.List<T> points;

	private final T center;

	public Cluster(final T center) {
		this.center = center;
		points = new java.util.ArrayList<T>();
	}

	public void addPoint(final T point) {
		points.add(point);
	}

	public java.util.List<T> getPoints() {
		return points;
	}

	public T getCenter() {
		return center;
	}
}

