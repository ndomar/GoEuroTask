package test;

import static org.junit.Assert.*;

import org.json.JSONArray;

import utilities.RestClient;

public class RestClientTest {

	@org.junit.Test
	public void testGetExistingCity() {
		JSONArray response = RestClient.get("Berlin");
		assertTrue(response != null);
		assertTrue(response.length() > 0);
		
	}
	@org.junit.Test
	public void testBadRequest() {
		// with spaces
		JSONArray response = RestClient.get("random city");
		assertTrue(response != null);
		assertTrue(response.length() == 0);
		
		// with forward slash
		response = RestClient.get("random/city");
		assertTrue(response != null);
		assertTrue(response.length() == 0);
	}
	@org.junit.Test
	public void testGetNonExistingCity() {
		JSONArray response = RestClient.get("randomcity");
		assertTrue(response != null);
		assertTrue(response.length() == 0);
	}

}
