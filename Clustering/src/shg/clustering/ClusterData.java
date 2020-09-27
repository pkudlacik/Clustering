package shg.clustering;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Data containing elements for clusterization (list of Vector<Double> objects).
 * Class allows the user to load vectors from text file or to build the list
 * manually using standard methods of collections.
 * 
 * @author Przemys³aw Kud³acik
 */
public class ClusterData {

	// list of vectors
	List<Vector<Double>> data = new LinkedList<Vector<Double>>();
	// vector of minimum values
	Vector<Double> mins = new Vector<Double>();
	// vector of maximum values
	Vector<Double> maxs = new Vector<Double>();

	// minimum size of vector
	private int min_size = 0;
	// maximum size of vector
	private int max_size = 0;

	// regular expression describing one data row in the input file
	String regexp = "[ \\t]*(-?\\d+([.,]\\d+)?[ ;\\t]+)*(-?\\d+([.,]\\d+)?[ ;\\t]*)";
	// regular expression for separator between values of one row
	String sep_regexp = "[ ;\\t]";
	char decimal_separator = '.';

	/**
	 * @return list of gathered elements
	 */
	public List<Vector<Double>> getData() {
		return data;
	}

	/**
	 * @return number of loaded vectors
	 */
	public int getSize() {
		return data.size();
	}

	/**
	 * @return vector of minimum values
	 */
	public Vector<Double> getMins() {
		return mins;
	}

	/**
	 * @return vector of maximum values
	 */
	public Vector<Double> getMaxs() {
		return maxs;
	}

	/**
	 * @return minimum size of loaded vectors
	 */
	public int getSizeOfSamples() {
		return min_size;
	}

	/**
	 * @return maximum size of loaded vectors
	 */
	public int getMaxSizeOfSamples() {
		return max_size;
	}

	public void setDecimalSeparator(char decimal_separator) {
		this.decimal_separator = decimal_separator;
	}

	public void setRowRegularExpression(String regexp) {
		this.regexp = regexp;
	}
	
	/**
	 * Loads vectors of floating point values from a file.
	 * 
	 * @param file file name
	 * @return true if everything was ok, false otherwise
	 */
	public boolean loadFile(String file) {
		return loadFile(file, -1, -1);
	}

	/**
	 * Loads vectors of floating point values from a file.
	 * 
	 * @param file            file name
	 * @param numberOfVectors defines a number of vectors to be loaded
	 * @param offset          indicates how many first vectors should be omitted
	 * @return true if everything was ok, false otherwise
	 */
	public boolean loadFile(String file, int numberOfVectors, int offset ) {
		FileInputStream fis = null;
		int counter = 0;
		try {
			fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String strLine;
			while ((strLine = br.readLine()) != null && counter != numberOfVectors) {
				Vector<Double> vector = new Vector<Double>();
				String[] splitted = strLine.split(sep_regexp);
				int index = 0;
				boolean error = false;
				for (String v : splitted) {
					try {
						v = v.trim();
						if (!v.isEmpty()) {
							if (decimal_separator != '.') {
								v = v.replace(decimal_separator, '.');
							}
							Double d = Double.parseDouble(v);
							vector.add(d);
							if (vector.size() > mins.size()) {
								mins.add(d);
								maxs.add(d);
							} else {
								if (mins.get(index) > d) {
									mins.set(index, d);
								} else {
									if (maxs.get(index) < d) {
										maxs.set(index, d);
									}
								}
							}
							index++;
						}
					} catch (NumberFormatException e) {
						error = true;
					}
					;
				}
				if (!error && !vector.isEmpty()) {
					counter++;
					if (counter > offset)
						data.add(vector);
				}
			}
		} catch (IOException e) {
			System.out.println("error in accessing/reading a file: " + file);
			return false;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					System.out.println("error in closing a file: " + file);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Loads vectors of floating point values from a file.
	 * 
	 * @param file            file name
	 * @param numberOfVectors defines a number of vectors to be loaded
	 * @param offset          indicates how many first vectors should be omitted
	 * @return true if everything was ok, false otherwise
	 */
	public boolean loadFileRegExp(String file, int numberOfVectors, int offset) {
		FileInputStream fis = null;
		int counter = 0;
		try {
			fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			Pattern pattern = Pattern.compile(regexp);
			String strLine;
			while ((strLine = br.readLine()) != null && counter != numberOfVectors) {
				Matcher matcher = pattern.matcher(strLine);
				if (matcher.matches()) {
					Vector<Double> vector = new Vector<Double>();
					String[] split = strLine.split(sep_regexp);
					int index = 0;
					for (String v : split) {
						try {
							v = v.trim();
							if (!v.isEmpty()) {
								if (decimal_separator != '.') {
									v = v.replace(decimal_separator, '.');
								}
								Double d = Double.parseDouble(v);
								vector.add(d);
								if (vector.size() > mins.size()) {
									mins.add(d);
									maxs.add(d);
								} else {
									if (mins.get(index) > d) {
										mins.set(index, d);
									} else {
										if (maxs.get(index) < d) {
											maxs.set(index, d);
										}
									}
								}
								index++;
							}
						} catch (NumberFormatException e) {
						}
						;
					}
					if (!vector.isEmpty()) {
						counter++;
						if (counter > offset)
							data.add(vector);
					}
				} // if matcher.matches()
			}
		} catch (IOException e) {
			System.out.println("error in accessing/reading a file: " + file);
			return false;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					System.out.println("error in closing a file: " + file);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Adds vector of Double elements to the data set.
	 * 
	 * @param vector input vector of data
	 */
	public void addVector(Vector<Double> vector) {
		for (int i = 0; i < vector.size(); i++) {
			double d = vector.get(i);
			if (i >= maxs.size()) {
				maxs.add(d);
				mins.add(d);
			} else {
				if (mins.get(i) > d) {
					mins.set(i, d);
				} else {
					if (maxs.get(i) < d) {
						maxs.set(i, d);
					}
				}
			}
		}
		data.add(vector);
	}

	/**
	 * Scales all values in loaded list to [0,1] range.
	 */
	public void normalize() {
		// calculate range
		int size = maxs.size();
		for (int i = 0; i < size; i++) {
			Double d = maxs.get(i);
			d -= mins.get(i);
			maxs.set(i, d);
		}

		// normalize
		for (Vector<Double> v : data) {
			size = v.size();
			for (int i = 0; i < size; i++) {
				v.set(i, (v.get(i) - mins.get(i)) / maxs.get(i));
			}
		}

		// update mins and maxs
		for (int i = 0; i < size; i++) {
			maxs.set(i, 1.0);
			mins.set(i, 0.0);
		}
	}

	/**
	 * @return text info about minimum and maximum values stored in loaded set
	 */
	public String toStringMinsMaxs() {
		String output = "";

		output += "min: ";
		boolean first = true;
		for (Double d : mins) {
			if (first) {
				first = false;
			} else {
				output += ", ";
			}
			output += d;
		}
		output += "\nmax: ";
		first = true;
		for (Double d : maxs) {
			if (first) {
				first = false;
			} else {
				output += ", ";
			}
			output += d;
		}

		return output;
	}

	/**
	 * @return loaded vectors in text form
	 */
	@Override
	public String toString() {
		String output = "";

		for (Vector<Double> v : data) {
			boolean first = true;
			for (Double d : v) {
				if (first) {
					first = false;
				} else {
					output += ", ";
				}
				output += d;
			}
			output += "\n";
		}

		output += "Number of loaded vectors: " + data.size();
		output += "\n" + toStringMinsMaxs();

		return output;
	}

}
