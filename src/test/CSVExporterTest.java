package test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.CSVExporter;
import utilities.RestClient;

public class CSVExporterTest {
	private static JSONArray response;
	private static String fileString;
	private static final char DELIMITER = ';';
	@BeforeClass
	public static void setUp() throws IOException{
		response = RestClient.get("Berlin");
		CSVExporter.toCSV(response, "Berlin", DELIMITER);
		FileReader file = new FileReader("./" + CSVExporter.getDirectoryName() + "/Berlin.csv");
		BufferedReader fileReader = new BufferedReader(file);
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = fileReader.readLine()) != null) {
			sb.append(line);
		}
		fileString = sb.toString();
		fileReader.close();
	}
	@Test
	public void testCreatesDirectory() {
		String directoryName = CSVExporter.getDirectoryName();
		File f = new File(directoryName);
		assertTrue(f.exists());	
	}
	@Test
	public void testWritesData() throws JSONException {
		int index = Math.max((int) Math.random() * response.length(), response.length() - 1);
		JSONObject randomEntry = (JSONObject) response.get(index);
		String[] keys = CSVExporter.getHeaders();
		String locationAccessor = CSVExporter.getLocationFieldKey();
		JSONObject location = randomEntry.getJSONObject(locationAccessor);
		for(String key : keys) {
			if(key.equals("longitude") || key.equals("latitude"))
				assertTrue(fileString.contains(location.get(key).toString()));
			else
				assertTrue(fileString.contains(randomEntry.get(key).toString()));
		}
	}
	@Test
	public void testCustomSeparator() {
		assertTrue(fileString.contains("sep=" + DELIMITER));
	}

}
