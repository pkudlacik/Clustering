package shg.clustering.distance;

import java.util.Vector;

import shg.clustering.Cluster;
import shg.clustering.ClusterEx;

/**
 * Calculates euclidean distance between clusters or vectors.
 * @author Przemysław Kudłacik
 */
public class EuclideanDistanceCalculator implements DistanceCalculator{
	@Override
	public double calcDistance(Vector<Double> a, Vector<Double> b) {
		double result=0, temp;
		int min = a.size();
		if (min > b.size()) min = b.size();
		for (int i=0; i<min; i++){
			temp = a.get(i)-b.get(i);
			result += temp*temp;
		}
		return Math.sqrt(result);
	}
	
	@Override
	public double calcDistance(Cluster a, Cluster b) {
		double result=0, temp;
		int min = a.getAverage().size();
		if (min > b.getAverage().size()) min = b.getAverage().size();
		for (int i=0; i<min; i++){
			temp = a.getAverage().get(i)-b.getAverage().get(i);
			result += temp*temp;
		}
		return Math.sqrt(result);
	}

	@Override
	public double calcDistance(ClusterEx a, ClusterEx b) {
		double result=0, temp;
		int min = a.getAverage().size();
		if (min > b.getAverage().size()) min = b.getAverage().size();
		for (int i=0; i<min; i++){
			temp = a.getAverage().get(i)-b.getAverage().get(i);
			result += temp*temp;
		}
		return Math.sqrt(result);
	}

}
