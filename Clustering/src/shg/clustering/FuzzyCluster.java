package shg.clustering;

import java.util.Iterator;
import java.util.Vector;

import fuzzlib.FuzzySet;

/**
 * A fuzzy extension of Cluster class.
 * Approach based on vector of fuzzy sets with gaussian membership functions
 * representing an uncertainty. Each fuzzy set corresponds with appropriate value
 * in average, minimum and maximum vectors from given Cluster.    
 * @author Przemysław Kudłacik
 */
public class FuzzyCluster {
	//cluster
	private Cluster cluster = null;
	//vector of
	private Vector<FuzzySet> fuzzyVector = null; 
	
	/**
	 * Default, empty constructor.
	 */
	public FuzzyCluster() {
	}
	
	/**
	 * Constructor setting a Cluster object and performing default fuzzyfication (ratio = 0.5).
	 * @param cluster input cluster
	 */
	public FuzzyCluster(Cluster cluster){
		this.cluster = cluster;
		fuzzyfy(0.5);
	}

	/**
	 * Constructor setting a Cluster object and performing fuzzyfication with given fuzzyfication ratio.
	 * @param cluster input cluster
	 * @param ratio fuzzyfication ratio
	 */
	public FuzzyCluster(Cluster cluster, double ratio){
		this.cluster = cluster;
		fuzzyfy(ratio);
	}
	
	/**
	 * Fuzzyfication - calculating vector of fuzzy sets using default fuzzyfication ratio (0.5).
	 */
	public void fuzzyfy(){
		fuzzyfy(0.5);
	}
	
	/**
	 * Fuzzyfication - calculating vector of fuzzy sets using given fuzzyfication ratio.
	 * @param ratio fuzzyfication ratio
	 */
	public void fuzzyfy(double ratio){
		if (cluster== null) return;
		int size = cluster.getAverage().size();
		fuzzyVector = new Vector<FuzzySet>(size);
		Iterator<Double> it_max = cluster.getMaxValues().iterator();
		Iterator<Double> it_min = cluster.getMinValues().iterator();
		Iterator<Double> it_avg = cluster.getAverage().iterator();
		double min,max,avg;
		for (int i=0; i<size; i++){
			min = it_min.next();
			max = it_max.next();
			avg = it_avg.next();
			max = max - avg;
			min = avg - min;
			FuzzySet fs = new FuzzySet(25);
			fs.newGaussian(avg,min*ratio, max*ratio);
			fuzzyVector.add(fs);
		}
	}
	
	/**
	 * Prints vector of fuzzy sets (each set in subsequent line) 
	 * @return calculated vector of fuzzy sets in text form 
	 */
	@Override
	public String toString() {
		String str="";
		for(FuzzySet fs: fuzzyVector){
			str += fs + "\n";
		}
		
		return str;
	}
}
