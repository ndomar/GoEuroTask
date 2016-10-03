package main;

import java.io.IOException;

import org.json.JSONArray;

import utilities.CSVExporter;
import utilities.RestClient;

/**
 * This class contains only the main method which will be run by the exported jar file.
 * It uses the other two classes in the utilities package to fetch the data from the GoEuro
 * server and write it to a .csv file.
 * 
 * @author Omar Nada
 *
 */
public class Main {
	
	/**
	 * The method given a city name as the first argument fetches the data from the GoEuro
	 * servers and writes it to a file. Optionally, the file name can be passed as the second
	 * argument
	 * @param args An array of string containing inputs (City name and file name)
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if(args.length == 0) {
			System.out.println("Please enter a city name and/or a filename");
			System.exit(1);
		}
		String cityName = args[0];
		String fileName = cityName;
		if(args.length > 1) 
			fileName = args[1];	
		long oldTime = System.currentTimeMillis();
		JSONArray response = RestClient.get(cityName);
		System.out.println("fetched in " + (System.currentTimeMillis() - oldTime) * 1.0 / 1000 + " seconds");
		if(response.length() == 0) {
			System.out.println("Empty response from the server. File will not be written");
			System.exit(1);
		}
		System.out.println("writing to file");
		oldTime = System.currentTimeMillis();
		CSVExporter.toCSV(response, fileName);
		System.out.println("file written in " + (System.currentTimeMillis() - oldTime) * 1.0 / 1000 + " seconds");
	}


}
