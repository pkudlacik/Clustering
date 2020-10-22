package shg.clustering;

import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Cluster containing Vector<Double> elements
 * @author Przemysław Kudłacik
 *
 */
public class Cluster {
	//minimum values of elements
	private Vector<Double> minValues;
	//maximum values of elements
	private Vector<Double> maxValues;
	//average values of elements
	private Vector<Double> average;
	//intermediate vector - needed for calculation of average
	private Vector<Double> intermediate;
	//list of cluster elements
	private List<Vector<Double>> data = new LinkedList<Vector<Double>>();

	/**
	 * Retrieves minimum values of a cluster
	 * @return vector storing minimum values
	 */
	public Vector<Double> getMinValues() {
		return minValues;
	}
//	/**
//	 * Sets minimum values of a cluster
//	 */
//	public void setMinValues(Vector<Double> minValues) {
//		this.minValues = minValues;
//	}
	/**
	 * Retrieves maximum values of a cluster
	 * @return vector storing maximum values
	 */
	public Vector<Double> getMaxValues() {
		return maxValues;
	}
//	/**
//	 * Sets maximum values of a cluster
//	 */
//	public void setMaxValues(Vector<Double> maxValues) {
//		this.maxValues = maxValues;
//	}
	/**
	 * Retrieves average element of a cluster
	 * @return vector storing average values
	 */
	public Vector<Double> getAverage() {
		return average;
	}
//	/**
//	 * Sets average element of a cluster
//	 */
//	public void setAverage(Vector<Double> average) {
//		this.average = average;
//	}
	/**
	 * Retrieves clustered elements
	 * @return List of vectors representing elements
	 */
	public List<Vector<Double>> getData() {
		return data;
	}
//	/**
//	 * Sets cluster elements
//	 * @param data list of vectors representing elements
//	 */
//	public void setData(List<Vector<Double>> data) {
//		this.data = data;
//		//TODO: calculate intermediate, average max and min - for new data
//	}

	/**
	 * Adds new element to a cluster. Minimum, maximum and average values are updated.
	 * @param vector element of a cluster
	 */
	public void addVector(Vector<Double> vector){
		data.add(vector);
		if(data.size()==1){
			//first sample initiates every element
			average = new Vector<Double>(vector);
			intermediate = new Vector<Double>(vector);
			minValues = new Vector<Double>(vector);
			maxValues = new Vector<Double>(vector);
		} else {
			//expand size if needed
			int size = vector.size();
			int i_size = intermediate.size(); 
			if (size > i_size) {
				intermediate.setSize(size);
				average.setSize(size);
				minValues.setSize(size);
				maxValues.setSize(size);
				for(int i=i_size; i<size; i++){
					intermediate.set(i, 0.0);
					average.set(i, 0.0);
					minValues.set(i, vector.get(i)); 
					maxValues.set(i, vector.get(i)); 
				}
			}
			//update average, min and max
			int data_size = data.size();
			for(int i=0; i<size; i++){
				intermediate.set(i, intermediate.get(i)+vector.get(i));
				average.set(i, intermediate.get(i)/data_size);
				if (vector.get(i)<minValues.get(i)) minValues.set(i, vector.get(i));
				else if (vector.get(i)>maxValues.get(i)) maxValues.set(i, vector.get(i));
			}
		}
	}
	
	/**
	 * Merges cluster with the one specified by parameter. Methods adds elements of smaller cluster 
	 * (containing less elements) to bigger one. Inner fields are updated. 
	 * After the operation both clusters contain the same references of inner fields.
	 * @param cl cluster to be merged with this instance of an object
	 */
	public void merge(Cluster cl) {
		Cluster one = null, two = null;
		if (data.size() >= cl.data.size()) {
			one = this;
			two = cl;
		} else {
			one = cl;
			two = this;
		}
		for (Vector<Double> vector : two.getData()) {
			one.addVector(vector);
		}
		two.average = one.average;
		two.data = one.data;
		two.intermediate = one.intermediate;
		two.maxValues = one.maxValues;
		two.minValues = one.minValues;
	}
	
	/**
	 * Packs cluster elements into one average element.
	 * (clears list of elements, leaves one equal the average element) 
	 */
	public void pack() {
		if(data.size()>1){
			data.clear();
			data.add( new Vector<Double>(average));
			//update intermediate (intermediate=average)
			int size = intermediate.size();
			for(int i=0; i<size; i++){
				intermediate.set(i, average.get(i));
			}
		}
	}
	
	
	/**
	 * Prints text info about cluster: average element, minimum and maximum values
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Formatter f = new Formatter(sb);
		sb.append("Average:\n");
		for(Double d:average){
			f.format("[%5.2f] ", d);				
		}
		sb.append("\nMaximums:\n");
		for(Double d:maxValues){
			f.format("[%5.2f] ", d);				
		}
		sb.append("\nMinimums:\n");
		for(Double d:minValues){
			f.format("[%5.2f] ", d);				
		}
		sb.append("\n");
		
		return sb.toString();
	}

	/**
	 * Prints full text info about cluster: cluster elements, average, minimum and maximum values
	 */
	public String toStringFull() {
		StringBuilder sb = new StringBuilder();
		Formatter f = new Formatter(sb);
		sb.append("Data:\n");
		for(Vector<Double> v:data){
			for(Double d:v){
				f.format("[%5.2f] ", d);				
			}
			sb.append("\n");
		}
		sb.append("Average:\n");
		for(Double d:average){
			f.format("[%5.2f] ", d);				
		}
		sb.append("\nMaximums:\n");
		for(Double d:maxValues){
			f.format("[%5.2f] ", d);				
		}
		sb.append("\nMinimums:\n");
		for(Double d:minValues){
			f.format("[%5.2f] ", d);				
		}
		sb.append("\n");
		
		return sb.toString();
	}
}
