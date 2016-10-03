package utilities;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The class is used to write fetched data to a csv file. the accessible method is only the toCSV
 * and the getters. Other methods are used internally by the class to write the file and documented.
 *  
 * @author Omar Nada
 *
 */
public final class CSVExporter {
	/*
	 * The following static variables are used when accessing the JSONObject and writing the file.
	 * Default_separator is used as the csv delimiter if not overwritten. 
	 * Headers are the JSONObject keys that are used to access the values and are written on the first
	 * line of the csv file. location field key is used to access the JSON Object containing the 
	 * lon and lat. Directory name is the name of the directory where the files will be written.
	 * The folder will be created in the working directory.
	 */
	private static final char DEFAULT_SEPARATOR = ',';
	private static final String[] HEADERS = new String[] {"_id", "name", "type", "latitude", "longitude"};
	private static final String LOCATION_FIELD_KEY = "geo_position";
	private static final String DIRECTORY_NAME = "Files";
	// public methods
	/**
	 * 
	 * Getters for private variables.
	 */
	public static char getDefaultSeparator() {
		return DEFAULT_SEPARATOR;
	}

	public static String[] getHeaders() {
		return HEADERS;
	}

	public static String getLocationFieldKey() {
		return LOCATION_FIELD_KEY;
	}

	public static String getDirectoryName() {
		return DIRECTORY_NAME;
	}
	/**
	 * 
	 * @param response JSONArray of the response
	 * @param city String containing the city name 
	 * @throws IOException thrown by the file writer when we can't write to the file for different reasons 
	 * (no space, file locked etc)
	 */
	public static void toCSV(JSONArray response, String city) throws IOException {
		
		String fileName = city + "-" + System.currentTimeMillis();
		System.out.println("using file name" + fileName);
		toCSV(response, fileName, ',');
	}
	
	/**
	 * Overloaded method of the above one where a separator is passed this time. 
	 * the method creates the directory if it was not created before, checks if the filename ends with
	 * .csv, and then creates a filewriter, writes the separator as the first line in the csv,
	 * passes it to the writeAllLines method to write the data.
	 * @param response JSONArray of the fetched data
	 * @param fileName String containing the name of the file.
	 * @param separator char the separator used in the csv
	 * @throws IOException thrown by the file writer when we can't write to the file for different reasons 
	 * (no space, file locked etc) 
	 * @see wirteAllLines
	 */
	public static void toCSV(JSONArray response, String fileName, char separator) throws IOException {
		if(response == null) {
			System.out.println("No data received");
			System.exit(1);
		}
		createDirectory("./" + DIRECTORY_NAME);
		if(!fileName.toLowerCase().endsWith(".csv"))
			fileName += ".csv";
		FileWriter fileWriter = new FileWriter("./" + DIRECTORY_NAME + "/" + fileName);
		
		if(separator == ' ')
			separator = DEFAULT_SEPARATOR;
		if(separator != ',') 
			fileWriter.write("sep=" + separator + "\n");
			
		writeAllLines(fileWriter, response, separator);
		
		fileWriter.flush();
		fileWriter.close();
	}
	/**
	 * The method is given the file writer, data, and separator and writes all the data to a file using
	 * the given file writer. it first calls the writeLine method to write the headers array
	 * then iterates through the JSONObjects, turns them into arrays and then writes them to the file.
	 * Transforming the JSONObject into an array first adds an extra O(n) to the overall complexity since now
	 * the data is iterated through once to transform it into an array and then once again to write the array
	 * to the file. While the extra work can be avoided by iterating and writing only once. We keep the 
	 * implementation this way to make the code more DRY.
	 * @param fileWriter Writer the writer used to write the file
	 * @param data JSONArray containing the fetched data
	 * @param separator char used as a delimiter of the csv file.
	 * @throws IOException thrown by the file writer when we can't write to the file for different reasons 
	 * (no space, file locked etc)
	 * @see writeLine
	 */
	private static void writeAllLines(Writer fileWriter, JSONArray data, char separator) throws IOException {
		// write the header first
		writeLine(fileWriter, HEADERS, separator);
		
		for(int i = 0; i < data.length(); i++) {
			try {
				JSONObject entry = data.getJSONObject(i);
				writeLine(fileWriter, toArray(entry), separator);
			} catch (JSONException e) {
				e.printStackTrace();
			}	
		}
	}
	/**
	 * The method given an array of strings, the separator and the writer writes a single line using 
	 * string builders to the file.
	 * @param fileWriter Writer the writer used to write the file
	 * @param line String[] containing the data to be written
	 * @param separator char used as a delimiter of the csv file. 
	 * @throws IOException IOException thrown by the file writer when we can't write to the file for different reasons 
	 * (no space, file locked etc)
	 */
	private static void writeLine(Writer fileWriter, String[] line, char separator) throws IOException {
		
		StringBuilder sb = new StringBuilder();
		boolean isFirstWord = true;
		for(String word : line) {
			if(!isFirstWord)
				sb.append(separator);
			sb.append("\"");
			sb.append(word);
			sb.append("\"");
			isFirstWord = false;
		}
		
		sb.append("\n");
		fileWriter.append(sb.toString());
	}
	/**
	 * The method transforms the JSONObject to an array of strings containing the values that will be
	 * written to the file
	 * @param data JSONObject containing one entry from the fetched data
	 * @return String[] array of strings containing values fetched.
	 */
	private static String[] toArray(JSONObject data) {
		String[] arr = new String[HEADERS.length];
		String field;
		int i = 0;
		JSONObject location = null;
		try {
			location = (JSONObject) data.get(LOCATION_FIELD_KEY);
		} catch (JSONException e) {
			System.out.println("Escaped field 'geo_location' value not found.");
		} finally {
			for(String header : HEADERS) {
				try {
					if((header.equals("longitude") || header.equals("latitude")) && location != null)
						field = location.get(header).toString();
					else
						field = data.get(header).toString();
				} catch(JSONException e) {
					System.out.println("Escaped field '" + header + "' value not found.");
					field = " ";
				}
				arr[i++] = field;
			}
		}
		return arr;
	}
	/**
	 * The method creates the directory where the files will be written if not created.
	 * @param directoryName String containing the directory name
	 */
	private static void createDirectory(String directoryName){
		File directory = new File(directoryName);
		if(!directory.exists()) {
			boolean created = directory.mkdir();
			if(created)
				System.out.println("directory created");
			else
				System.out.println("could not create directory");
		}
	}
	
}
