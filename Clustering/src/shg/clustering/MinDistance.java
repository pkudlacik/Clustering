package shg.clustering;

/**
 * Class storing information about minimum distance between clusters.
 * @author Przemysław Kudłacik
 */
class MinDistance implements Cloneable {
	double distance;
	int column, row;
	Cluster a, b;
	
	/**
	 * Default, empty constructor.
	 */
	public MinDistance(){
	}
	/**
	 * Constructor
	 * @param distance distance between clusters
	 * @param column column in distance array
	 * @param row row in distance array
	 * @param a cluster one
	 * @param b cluster two
	 */
	public MinDistance(double distance, int column, int row, Cluster a, Cluster b) {
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
	public MinDistance(MinDistance source){
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
	public void copy(MinDistance source){
		this.distance = source.distance;
		this.column = source.column;
		this.row = source.row;
		this.a = source.a;
		this.b = source.b;
	}
}
