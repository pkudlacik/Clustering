package shg.clustering;

import java.io.CharArrayWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import shg.clustering.distance.DistanceCalculator;
import shg.clustering.distance.EuclideanDistanceCalculator;

/**
 * Class responsible for clusterization.
 * ClusterData (which is a list of Vector<Double> elements) is needed to perform clusterization.
 * After the process user can obtain list of clusters (List<Cluster>).
 * @author Przemysław Kudłacik
 *
 */
public class Clusterer {
	//input data - list of vectors
	ClusterData input_data = null;
	
	//list of clusters
	private LinkedList<Cluster> clusters = null;
	//distance calculator
	private DistanceCalculator distCalc = new EuclideanDistanceCalculator();
	//dynamic array of distances - temp memory for calculation
	private List<List<Double>> dists = null;
	//minimum distance
	MinDistance min = new MinDistance();
	//minimum distances - temp memory for calculation
	LinkedList<MinDistance> min_dists = null;
	
	private double distanceThreshold = 1.0;
	private int numberOfClusters = 1;

	/**
	 * Retrieves assigned distance threshold - a maximum distance for which elements are clustered
	 * @return distance threshold set for clustering. 1.0 is default. 
	 */
	public double getDistanceThreshold() {
		return distanceThreshold;
	}

	/**
	 * Assigns distance threshold - a maximum distance for which elements are clustered
	 * @param distanceThreshold a double value representing the threshold. 1.0 is default.
	 */
	public void setDistanceThreshold(double distanceThreshold) {
		this.distanceThreshold = distanceThreshold;
	}
	
	/**
	 * Retrieves assigned number of clusters - a maximum number for which clustering is performed
	 * @return configured maximum number of clusters. 1 is default. 
	 */	
	public int getNumberOfClusters() {
		return numberOfClusters;
	}

	/**
	 * Assigns number of clusters - a maximum number for which clustering is performed
	 * @param numberOfClusters an integer value specifying desired number. 1 is default. 
	 */
	public void setNumberOfClusters(int numberOfClusters) {
		this.numberOfClusters = numberOfClusters;
	}

	/**
	 * Assigns a DistanceCalculator object, which is needed for calculating distances between
	 * subsequent clusters.  
	 * @param calculator DistanceCalculator object
	 */
	public void setDistanceCalculator(DistanceCalculator calculator) {
		this.distCalc = calculator;
	}

	/**
	 * Assigns object with data for clusterization.
	 */
	public void setData(ClusterData data) {
		this.input_data = data;
	}

	/**
	 * Retrieves object with data for clusterization. 
	 */
	public ClusterData getData() {
		return input_data;
	}

	/**
	 * Retrieves calculated clusters.
	 */
	public List<Cluster> getClusters() {
		return clusters;
	}

	/**
	 * Creates a list of clusters based on input data.
	 * Each input element becomes a cluster - launched before the clusterization.
	 */
	private void _create_list_of_clusters(){
		clusters = new LinkedList<Cluster>();
		Cluster cluster = null;
		for (Vector<Double> d : input_data.data) {
			cluster = new Cluster();
			cluster.addVector(d);
			clusters.add(cluster);
		}
	}
	
	/**
	 * Creates and initiates an array of distances between clusters.
	 * DistanceCalculator is used for this process.
	 */
	private void _create_and_init_distances() {
		dists = new LinkedList<List<Double>>();
		min_dists = new LinkedList<MinDistance>();
		min.distance = -1.0;
		MinDistance tmp_min = new MinDistance();
		Cluster one, two;
		boolean first;
		double tmp;
		int i=1, j;
		
		
		ListIterator<Cluster> it_one = clusters.listIterator();
		if (it_one.hasNext()) it_one.next(); //omit the first element
		while (it_one.hasNext()){
			one = it_one.next();
			j=0;
			first = true;
			LinkedList<Double> dists_row = new LinkedList<Double>();
			ListIterator<Cluster> it_two = clusters.listIterator();
			while ((two = it_two.next()) != one){
				//calculate distance
				tmp = distCalc.calcDistance(one, two);
				dists_row.add(tmp);
				//and save minimum for the row
				if (first) { 
					tmp_min.distance = tmp;
					tmp_min.row = i;
					tmp_min.column = j;
					tmp_min.a=two;
					tmp_min.b=one;
					first = false;
				} else {
					if (tmp_min.distance > tmp) {
						tmp_min.distance = tmp;
						tmp_min.column = j;
						tmp_min.a=two;
						tmp_min.b=one;
					}
				}				
				j++;
			}
			i++;
			dists.add(dists_row);
			min_dists.add(new MinDistance(tmp_min));
			if (min.distance == -1.0) {
				min.copy(tmp_min);
			} else {
				if (min.distance > tmp_min.distance) {
					min.copy(tmp_min);
				}
			}
		}
	}

	/**
	 * Clustering method using temporal array of distances.
	 * Much faster than brute-force method.
	 */
	private void _cluster() {
		boolean first,minimum_row;
		MinDistance tmp;
		double tmp_double;
		Cluster one,two,to_delete;
		int idx, column, row;
		List<Double> tmp_dist_list, dist_list=null;
		ListIterator<Cluster> cl_it;
		
//		System.out.println("\nBefore clustering:");
//		printDistances();
//		System.out.printf("min: %5.2f [%d,%d]\n", min.distance, min.column, min.row);
		
		//min has to be initialized before the method call
		while (min.distance < distanceThreshold && clusters.size() > numberOfClusters) {
			// 1. Join two clusters described by the shortest distance (variable "min"):

			// for tests only			
//			System.out.println("\nBefore merging:");
//			printDistances();
			
// v- for tests only			
//			min.column = 1;
//			min.row = 2;
//			min.a = clusters.get(min.column);
//			min.b = clusters.get(min.row);
// ^- for tests only			
			
			one = min.a;
			two = min.b;

			// adjust row index for indexing of distance row list (which is lower by 1)
			row = min.row - 1;
			if (row == 0)
				minimum_row = true;
			else
				minimum_row = false;

			// 1.a Merge clusters
			two.merge(one);
			// 1.b Delete reference to the first one from cluster list
			//clusters.remove(one);
			// - more optimal solution is applied : delete later while loop through clusters
			// because .remove() for linked list is O(N)

			// 1.c Delete row and column of distances
			column = min.column - 1;
			if (column < 0)
				column = 0;
			idx = column;
			row--;
			if (minimum_row)
				row = 0;
			ListIterator<List<Double>> list_it = dists.listIterator(idx);
			ListIterator<MinDistance> mins_it = min_dists.listIterator(idx);
			if (list_it.hasNext()){
				list_it.next();
				list_it.remove(); //remove row of distances
				mins_it.next();  
				mins_it.remove(); //remove relevant min_dist
			}
		
			while (list_it.hasNext()) {
				tmp_dist_list = list_it.next();
//				if (min.column < 0){
//					printDistances();
//				}
				tmp_dist_list.remove(min.column);
				if (idx == row)
					dist_list = tmp_dist_list; // save reference for further calculation
				idx++; // increment row count

				//update mins - needed for mins between deleted row and merged row ("one" and "two" clusters)
				tmp = mins_it.next();
				// mark chosen mins to be recalculated
				//rethink this !!! - especially the second condition - maybe it is not always needed to recalculate all rows !!!
				if (tmp.column == min.column || idx > row){
					tmp.a = null;
				}
				tmp.row--;
				if (tmp.column > min.column)
					tmp.column--;
			}

// for tests only			
//			System.out.println("\nAfter deleting row and column:");
//			printDistances();

			// 1.d Recalculate distances for the row of joined clusters (the new cluster)
			if (!minimum_row) {
				dist_list.clear();
				cl_it = clusters.listIterator();
				idx = 0;
				tmp = new MinDistance();
				first = true;
				to_delete = one;
				while ((one = cl_it.next()) != two) {
					// calculate distance
					
					//it is more optimal to remove cluster here (remove while "looping" anyway)
					// - remove and continue the loop
					if (one == to_delete) {
						cl_it.remove();
					} else {
						tmp_double = distCalc.calcDistance(one, two);
						dist_list.add(tmp_double);
						// and save minimum for the row
						if (first) {
							tmp.distance = tmp_double;
							tmp.row = row + 1;
							tmp.column = idx;
							tmp.a = one;
							tmp.b = two;
							first = false;
						} else {
							if (tmp.distance > tmp_double) {
								tmp.distance = tmp_double;
								tmp.column = idx;
								tmp.a = one;
								tmp.b = two;
							}
						}
						idx++;
					}
				}
				min_dists.set(row, tmp);
			} else{
				clusters.remove(one); //remove only
			}

//			System.out.println("\nAfter row recalculation:");
//			printDistances();

			// 1.f Recalculate distance to the new cluster for each remaining row of distances
			column = row + 1;
			if (minimum_row)
				column = 0;
			if (column <= dists.size()) {
				idx = column+1;
				list_it = dists.listIterator(column);
				ListIterator<MinDistance> min_dist_it = min_dists.listIterator(column);
				MinDistance tmp_min_dist;
				cl_it = clusters.listIterator(idx);
				while (list_it.hasNext()) { //for each row (starting from appropriate position)
					one = cl_it.next();
					tmp_double = distCalc.calcDistance(one, two);
					dist_list = list_it.next();
					dist_list.set(column, tmp_double);
					
					//check if min_dist was marked to be searched- if not analyzing of a whole row is not needed
					//!!! now it is not implemented - whole search of this rows is always performed
					tmp_min_dist = min_dist_it.next();
					if (tmp_min_dist.a != null && tmp_min_dist.distance > tmp_double ){
						
					}
					idx++;
				}
			} //if column...
			
//			System.out.println("\nAfter column recalculation:");
//			printDistances();
		
			// 2. Find minimum distance and update if needed
			column = 0;
			row = 1;
			ListIterator<Cluster> cl_tmp_a = clusters.listIterator();
			ListIterator<Cluster> cl_tmp_b;
			Cluster tmp_cluster;
			list_it = dists.listIterator();

			one = cl_tmp_a.next(); //skip first (0) row
			
			first = true;
			for (MinDistance elem : min_dists) {
				tmp = elem;
				one = cl_tmp_a.next();
				dist_list = list_it.next();
				
				//update a row of distances if needed
				if (tmp.a == null){
					ListIterator<Double> dist_it = dist_list.listIterator();
					cl_tmp_b = clusters.listIterator();
					column=0;
					boolean new_first = true;
					while(dist_it.hasNext()){
						tmp_double = dist_it.next();
						tmp_cluster = cl_tmp_b.next();
						if (new_first || tmp_double < elem.distance) {
							elem.distance = tmp_double;
							elem.row = row;
							elem.column = column;
							elem.a = tmp_cluster;
							elem.b = one;
							new_first = false;
						}
						column++;
					}
				}
				
				if (first) {
					min.copy(tmp);
					first = false;
				} else {
					if (min.distance > tmp.distance) {
						min.copy(tmp);
					}
				}
				row++;
			}

//			System.out.println("\nAfter run:");
//			printDistances();
//			System.out.printf("min: %5.2f [%d,%d]\n", min.distance, min.column, min.row);

		}// clustering while
	}
	
	/**
	 * Clustering process. Merges clusters iteratively until minimum distance
	 * between clusters exceeds given distance threshold. 
	 */
	public void clusterData() {
		if (input_data == null)
			return;

		_create_list_of_clusters();
		_create_and_init_distances();
		_cluster();
	}

	/**
	 * Continue clustering i.e. after setting a new threshold. 
	 */
	public void clusterContinue() {
		_cluster();
	}

	/**
	 * Packs all obtained clusters (replaces all elements of a cluster by one average element)
	 */
	public void packClusters() {
		for (Cluster c : clusters) {
			c.pack();
		}
	}
	
	/**
	 * Brute-force method of finding minimum distance between clusters.
	 * Complexity n^2. Used in each iteration of brute-force clusterization.
	 */
	private void _find_min_distance_bruteforce() {
		min.distance = -1.0;
		MinDistance tmp_min = new MinDistance();
		Cluster one, two;
		boolean first;
		double tmp;
		int i=1, j;
	
		ListIterator<Cluster> it_one = clusters.listIterator();
		if (it_one.hasNext()) it_one.next(); //omit the first element
		while (it_one.hasNext()){
			one = it_one.next();
			j=0;
			first = true;
			ListIterator<Cluster> it_two = clusters.listIterator();
			while ((two = it_two.next()) != one){
				//calculate distance
				tmp = distCalc.calcDistance(one, two);
				//and save minimum for the row
				if (first) { 
					tmp_min.distance = tmp;
					tmp_min.row = i;
					tmp_min.column = j;
					tmp_min.a=two;
					tmp_min.b=one;
					first = false;
				} else {
					if (tmp_min.distance > tmp) {
						tmp_min.distance = tmp;
						tmp_min.column = j;
						tmp_min.a=two;
						tmp_min.b=one;
					}
				}				
				j++;
			}
			i++;
			if (min.distance == -1.0) { //handle the first assignment
				min.copy(tmp_min);
			} else { //find minimum distance
				if (min.distance > tmp_min.distance) {
					min.copy(tmp_min);
				}
			}
		}
	}

	/**
	 * Brute-force clusterization - uses brute-force method for calculating minimum distance. 
	 * As the faster method, this approach also merges clusters iteratively until minimum distance
	 * between clusters exceeds given distance threshold. 
	 */
	private void _cluster_slowly() {
		_find_min_distance_bruteforce();

		// min has to be initialized before the method call
		while (min.distance < distanceThreshold && clusters.size() > numberOfClusters) {
			// 1. Join two clusters described by the shortest distance:

			// 1.a Merge clusters
			min.a.merge(min.b);
			clusters.remove(min.b);

			// 2. Find minimum distance
			_find_min_distance_bruteforce();
		}
	}
	
	
	/**
	 * Brute-force clusterization - uses brute-force method for calculating minimum distance. 
	 * As the faster method, this approach also merges clusters iteratively until minimum distance
	 * between clusters exceeds given distance threshold. 
	 */
	public void clusterDataWithoutDistanceBuffer() {
		if (input_data == null)
			return;

		_create_list_of_clusters();
		_cluster_slowly();
	}

	/**
	 * Prints array of distances between clusters to the console - helpful for debugging. 
	 */
	public void printDistances(){
		if (clusters.isEmpty()) return;
		if (dists == null) return;

		System.out.print("   ");
		for(Cluster cl:clusters){
			System.out.printf("(%5.2f) ", cl.getAverage().get(0));
		}
		System.out.println();
		System.out.print("   ");
		for(Cluster cl:clusters){
			if (cl.getAverage().size()>1)
				System.out.printf("(%5.2f) ", cl.getAverage().get(1));
		}
		int i=0;
		System.out.println();
		System.out.print("   ");
		for(Cluster cl:clusters){
			System.out.printf("   %d    ", i);
			i++;
		}
		System.out.println();
		
		System.out.println(" 0    *");
		i=1;
		for(List<Double> l:dists){
			System.out.printf(" %d ",i);
			for(Double d: l){
				System.out.printf("[%5.2f] ", d);
			}
			System.out.println("   *");
			i++;
		}
		System.out.println(" ---");
		System.out.print("min");
		for(MinDistance d:min_dists){
			System.out.printf("{%5.2f} ",d.distance);			
		}
		System.out.println();
		System.out.print("col");
		for(MinDistance d:min_dists){
			System.out.printf("   %d    ",d.column);			
		}
		System.out.println();
		System.out.print("row");
		for(MinDistance d:min_dists){
			System.out.printf("   %d    ",d.row);			
		}
		System.out.println();
	}
	
	/**
	 * @return text info about clusters (calls toString() method of each cluster)
	 */
	public String clustersToString(){
		String str="";
		int i=0;
		for (Cluster cl:clusters){
			str+="<=== Cluster " + i++ + "\n";
			str+=cl;			
			str+="\n";
		}
		return str;
	}
	
	/**
	 * Writes text data of each cluster to file.
	 * First line contains number of clusters.
	 * Data of each cluster is presented below in the following format:
	 * line 1-average vector, line 2-maximum values, line 3-minimum values. 
	 * @param fileName name of file
	 */
	public void clustersToFile(String fileName){
		FileWriter fos = null;
		try {
			fos = new FileWriter(fileName);
			PrintWriter printer = new PrintWriter(fos);
			Formatter f = new Formatter(printer);
			printer.println(clusters.size());
			for (Cluster cl:clusters){
				for(Double d:cl.getAverage()){
					f.format("%5.2f ", d);
				}
				printer.println();
				for(Double d:cl.getMaxValues()){
					f.format("%5.2f ", d);				
				}
				printer.println();
				for(Double d:cl.getMinValues()){
					f.format("%5.2f ", d);				
				}
				printer.println();
			}
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	if (fos != null){
        		try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
    }

	/**
	 * Writes average vectors representing clusters in subsequent line of a text file.
	 * @param fileName name of file
	 */
	public void clustersAvegageToFile(String fileName){
		FileWriter fos = null;
		try {
			fos = new FileWriter(fileName);
			PrintWriter printer = new PrintWriter(fos);
			Formatter f = new Formatter(printer);
			for (Cluster cl:clusters){
				for(Double d:cl.getAverage()){
					f.format("%5.2f ", d);
				}
				printer.println();
			}
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	if (fos != null){
        		try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
    }

	/**
	 * Writes average vectors representing clusters in subsequent line of a string.
	 * @return string data
	 */
	public String clustersAvegageToString(){
		CharArrayWriter printer = new CharArrayWriter();
		Formatter f = new Formatter(printer);
		for (Cluster cl:clusters){
			for(Double d:cl.getAverage()){
				f.format("%5.2f ", d);
			}
			f.format("\n");
		}
        return printer.toString();
    }
	
	/**
	 * Writes full information about clusters to string.
	 * @return string info
	 */
	public String clustersToStringFull(){
		String str="";
		int i=0;
		for (Cluster cl:clusters){
			str+="<=== Cluster " + i++ + "\n";
			str+=cl.toStringFull();			
			str+="\n";
		}
		return str;
	}
}
