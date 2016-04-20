package org.apache.commons.math.stat.clustering;


public interface Clusterable<T> {
	double distanceFrom(T p);

	T centroidOf(java.util.Collection<T> p);
}

