package shg.clustering;

/**
 * Class storing information about minimum distance between clusters.
 * @author Przemysław Kudłacik
 */
class MinDistanceEx implements Cloneable {
	double distance;
	int column, row;
	ClusterEx a, b;
	
	/**
	 * Default, empty constructor.
	 */
	public MinDistanceEx(){
	}
	/**
	 * Constructor
	 * @param distance distance between clusters
	 * @param column column in distance array
	 * @param row row in distance array
	 * @param a cluster one
	 * @param b cluster two
	 */
	public MinDistanceEx(double distance, int column, int row, ClusterEx a, ClusterEx b) {
		this.distance = distance;
		this.column = column;
		this.row = row;
		this.a = a;
		this.b = b;
	}
	
	/**
	 * Copy constructor
	 * @param source source to be copied
	 */
	public MinDistanceEx(MinDistanceEx source){
		this.distance = source.distance;
		this.column = source.column;
		this.row = source.row;
		this.a = source.a;
		this.b = source.b;
	}
	/**
	 * Copies data of given source to the object
	 * @param source source to be copied
	 */
	public void copy(MinDistanceEx source){
		this.distance = source.distance;
		this.column = source.column;
		this.row = source.row;
		this.a = source.a;
		this.b = source.b;
	}
}
