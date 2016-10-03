package utilities;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;

/**
 * A basic class that provides functionality only for the GET method.
 * @author Omar Nada
 *
 */

public final class RestClient {
	/*
	 * The following parameter holds the bas url where the data will be fetched from.
	 */
	private static final String BASE_URL = "http://api.goeuro.com/api/v2/position/suggest/en/";
	/**
	 * the method fetches the servers using the base url and the city name and then
	 * parses the received string into a JSONArray if the http status code is successful.
	 * @param city the name of the city to be fetched.
	 * @return returns a JSONArray of the fetched data
	 */
	public static JSONArray get(String city) {
		city = city.replaceAll(" ", "").replaceAll("/", "");
		System.out.println("Fetching from server");
		try {
			URL url = new URL(BASE_URL + city);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (!(conn.getResponseCode() + "").startsWith("2")) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			StringBuilder sb = new StringBuilder();
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			JSONArray responseJSON = new JSONArray(sb.toString());
			conn.disconnect();
			return responseJSON;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
		
}
