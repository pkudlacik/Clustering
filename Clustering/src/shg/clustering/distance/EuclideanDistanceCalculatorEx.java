package shg.clustering.distance;

import java.util.Vector;

import shg.clustering.Cluster;

/**
 * Calculates euclidean distance between clusters or vectors.
 * In case of vectors there is no difference comparing to EuclideanDistanceCalculator class.
 * In case of clusters, a weighted distance is obtained, combined of euclidean distance between
 * clusters' centers of gravity and their closest corners. The weight can be adjusted by setDistanceWeight method.  
 * @author Przemysław Kudłacik
 */
public class EuclideanDistanceCalculatorEx extends EuclideanDistanceCalculator{
	private double weight = 0.5;
		
	/**
	 * Sets the weight of distance between centers of gravity (COGs) of two compared clusters in final distance result.
	 * By default the weight equals 0.5, which means that the final result is obtained as 0.5 of distance between COGs
	 * plus 0.5 of distance between the closest corners of clusters.
	 * i.e. if the weight equals 0.3, then the final result is obtained as 0.3 of distance between COGs
	 * plus 0.7 of distance between the closest corners.
	 * @param weight weight to be applied in computation of result
	 */
	public void setDistanceWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public double calcDistance(Cluster a, Cluster b) {
		double result=0, temp, temp2;
		double av_result;
		int min = a.getAverage().size();
		if (min > b.getAverage().size()) min = b.getAverage().size();
		//calculate distance for averages
		for (int i=0; i<min; i++){
			temp = a.getAverage().get(i)-b.getAverage().get(i);
			result += temp*temp;
		}
		av_result = result;
		result = 0;
		//calculate distance between "corners"
		for (int i=0; i<min; i++){
			temp = a.getMinValues().get(i)-b.getMinValues().get(i);
			temp *= temp;
			temp2 = a.getMinValues().get(i)-b.getMaxValues().get(i);
			temp2 *= temp2;
			if (temp2<temp) temp=temp2;
			temp2 = a.getMaxValues().get(i)-b.getMinValues().get(i);
			temp2 *= temp2;
			if (temp2<temp) temp=temp2;
			temp2 = a.getMaxValues().get(i)-b.getMaxValues().get(i);
			temp2 *= temp2;
			if (temp2<temp) temp=temp2;
			result += temp;
		}		
		
		//return weighted result: 0.5 of distance between COGs and 0.5 distance between closest corners
		return Math.sqrt(av_result)*weight+Math.sqrt(result)*(1.0-weight);
	}

}
