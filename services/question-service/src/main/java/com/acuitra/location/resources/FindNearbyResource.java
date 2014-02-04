package com.acuitra.location.resources;

import java.text.MessageFormat;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.yammer.metrics.annotation.Timed;

@Path("/nearby")
@Produces(MediaType.APPLICATION_JSON)
public class FindNearbyResource {

	private Client jerseyClient;
	private String sparqlEndpointURL;

	public FindNearbyResource(Client jerseyClient, String sparqlEndpointURL) {
		super();
		
		this.jerseyClient = jerseyClient;
		this.sparqlEndpointURL = sparqlEndpointURL;
	}
	
	@GET
	@Timed
	//@Produces({"application/json"})
	public String nearPoint(@QueryParam("latitude") double latitude, 
			@QueryParam("longitude") double longitude,  
			@QueryParam("maxResults") @DefaultValue("5") int maxResults) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("PREFIX owl: <http://www.w3.org/2002/07/owl#> ");
		builder.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
		builder.append("PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> ");
		builder.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		builder.append("SELECT ?resource ?label WHERE '{' ");
		builder.append("	?resource rdfs:label ?label. ");
		builder.append("	?resource geo:lat ?targetLat. ");
		builder.append("	?resource geo:long ?targetLong. ");
		builder.append("	FILTER( ");
		builder.append("		\"{0,number}\"^^xsd:float - ?targetLat<= 0.02");
		builder.append("		&& ?targetLat - \"{0,number}\"^^xsd:float <= 0.02");
		builder.append("		&& \"{1,number}\"^^xsd:float - ?targetLong <= 0.02");		
		builder.append("		&& ?targetLong - \"{1,number}\"^^xsd:float <= 0.02 ");
		builder.append("		&& lang(?label) = \"en\" ");		
		builder.append("	).");
		builder.append("'}' LIMIT {2,number,integer}");
		
		String query = MessageFormat.format(builder.toString(), latitude, longitude, maxResults);
		
		jerseyClient.setReadTimeout(0);
		jerseyClient.setConnectTimeout(0);
		
		WebResource webResource = jerseyClient.resource(sparqlEndpointURL);
		
		
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("output", "json");
		params.add("query", query);
		
		// Run the query and get the response
		ClientResponse response = webResource.queryParams(params).get(ClientResponse.class);
		
		String output = response.getEntity(String.class);
		
		
		return output;
	}
	
}
