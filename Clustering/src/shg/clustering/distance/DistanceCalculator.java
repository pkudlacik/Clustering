package shg.clustering.distance;

import java.util.Vector;

import shg.clustering.Cluster;
import shg.clustering.ClusterEx;

/**
 * Parent class for distance calculators between two clusters or vectors.
 * @author Przemysław Kudłacik
 */
public interface DistanceCalculator {
	/**
	 * Calculates distance between vectors.
	 * @param a one vector
	 * @param b second vector
	 * @return distance between vectors
	 */
	double calcDistance(Vector<Double> a, Vector<Double> b);
	/**
	 * Calculates distance between clusters.
	 * @param a one cluster
	 * @param b second cluster
	 * @return distance between clusters
	 */
	double calcDistance(Cluster a, Cluster b);

	/**
	 * Calculates distance between clusters.
	 * @param a one cluster
	 * @param b second cluster
	 * @return distance between clusters
	 */
	double calcDistance(ClusterEx a, ClusterEx b);
}
