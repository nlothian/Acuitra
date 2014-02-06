package com.acuitra.sparql;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;

import javax.ws.rs.core.MultivaluedMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class SparqlUtils {
	/**
	 * Attempt to do some basic validation to avoid injection attacks
	 * 
	 * It should be noted I'm not sure what the rules are for DBpedia resource names, so this is probably wrong. I know they can contain non-ASCII characters for example
	 * 
	 * @param resourceName
	 * @return
	 */
	public static boolean isValidDBpediaResourceName(String resourceName) {
		if (resourceName == null) {
			return false;
		} else {
			//return resourceName.matches("^[<>:/,_sa-zA-Z]+$");
			return true;
		}
	}


	public static String runQuery(Client jerseyClient, String sparqlEndpointURL, String query, Object... params) {		
		String subbedQuery = MessageFormat.format(query, params);
		
		WebResource webResource = jerseyClient.resource(sparqlEndpointURL);
		
		MultivaluedMap<String, String> reqParams = new MultivaluedMapImpl();
		reqParams.add("output", "json");
		reqParams.add("query", subbedQuery);
		
		// Run the query and get the response
		ClientResponse response = webResource.queryParams(reqParams).get(ClientResponse.class);
		
		return response.getEntity(String.class);
	}
		
	
	public static String sparqlResultSetToJsonTable(String jsonInput) {
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(jsonInput);
			
			// header section looks like this:
			//	head: {
			//		vars: [
			//		"name",
			//		"description",
			//		"thumbnail",
			//		"image"
			//		]
			//	},
			int columnCount = rootNode.path("head").path("vars").size();			
			String[] columnNames = new String[columnCount];			
			for (int i = 0; i < columnCount; i++) {
				columnNames[i] = rootNode.path("head").path("vars").get(i).asText(); 
			}
			
			// results section looks like this:
			//	results: {
			//		bindings: [
			//			{
			//				name: {
			//					type: "literal",
			//					xml:lang: "en",
			//					value: "Royal Adelaide Hospital"
			//				},
			//				description: {
			//					type: "literal",
			//					xml:lang: "en",
			//					value: "The Royal Adelaide Hospital (RAH) is Adelaide's largest hospital, with 680 beds. Founded in 1840, the Royal Adelaide provides tertiary health care services for South Australia and provides secondary care clinical services to residents of Adelaide's city centre and inner suburbs. The hospital is situated in the Adelaide Park Lands on the north side of North Terrace between Frome Road and the Adelaide Botanic Gardens."
			//				},
			//				thumbnail: {
			//				type: "uri",
			//					value: "http://upload.wikimedia.org/wikipedia/commons/thumb/1/13/Royal_Adelaide_Hospital,_Adelaide.jpg/200px-Royal_Adelaide_Hospital,_Adelaide.jpg"
			//				},
			//				image: {
			//					type: "uri",
			//					value: "http://upload.wikimedia.org/wikipedia/commons/1/13/Royal_Adelaide_Hospital,_Adelaide.jpg"
			//				}
			//			}
			//		]
			//	}
			
			
			int rowCount = rootNode.path("results").path("bindings").size();
			HashMap<String, String>[] rows = new HashMap[rowCount];
			
			for (int i = 0; i < rowCount; i++) {
				HashMap<String, String> row = new HashMap<>();
				
				for (int j = 0; j < columnCount; j++) {
					row.put(columnNames[j], rootNode.path("results").path("bindings").path(i).path(columnNames[j]).path("value").asText()); 
				}
				rows[i] = row;
			}
			
			ObjectMapper outputMapper = new ObjectMapper();
			
			JsonTable tbl = new JsonTable();
			tbl.setColumnNames(columnNames);
			tbl.setResults(rows);
			
			String output = outputMapper.writeValueAsString(tbl);
			
			return output;
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
}
