package run2;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CsvGenerator {
	int size;
	int min;
	int max; 
	
	
	/**
	 * Constructor
	 * @param size size of the set
	 * @param min minimum of the data
	 * @param max maximum of the data
	 */
	public CsvGenerator(int size, int min, int max) {
		this.size = size;
		this.min = min;
		this.max = max;
	}
	/**
	 * Generates a csv file 
	 * @param filePath
	 */
	public void generateCSV(String filePath) {
		List<Integer> data = generateListOfInts(size, min, max);
		writeCsv(filePath, data);		
	} 
	
	/**
	 * Generates a list of random integers
	 * @param size size of the list
	 * @param min the min size of the integers
	 * @param max the max size of the integers
	 * @return list of the generated integers
	 */
	public List<Integer> generateListOfInts(int size, int min, int max){
		  List<Integer> data = new ArrayList<Integer>();

		  Random random = new Random();
		  for(int i = 0; i <= size; i++) {
			  int j = random.nextInt(max - min) + min;
			  data.add(j);
		  }
		  
		  return data;
	}
	
	
	
	/**
	 * Creates a csv file with a list of integers
	 * @param filePath location of the created file
	 * @param data list of the integer in the file 
	 */
	public static void writeCsv(String filePath, List<Integer> data) {
		 FileWriter fileWriter = null;
		  try {
		   fileWriter = new FileWriter(filePath);
		   
		   fileWriter.append(String.valueOf(data.get(0)));
		   
		   for(int i = 1; i <= data.size()-1; i++) {
			fileWriter.append(",");   
		    fileWriter.append(String.valueOf(data.get(i)));
		   }
		   
		  } 
		  catch (Exception ex) {
		   ex.printStackTrace();
		  } 
		  finally {
		   try {
		    fileWriter.flush();
		    fileWriter.close();
		   } 
		   catch (Exception e) {
		    e.printStackTrace();
		   }
		  }
		 
	}
}
