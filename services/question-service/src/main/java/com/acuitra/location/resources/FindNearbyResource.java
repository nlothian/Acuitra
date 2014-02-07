package com.acuitra.location.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.acuitra.sparql.SparqlUtils;
import com.sun.jersey.api.client.Client;
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
	public String nearPoint(@QueryParam("latitude") double latitude, 
			@QueryParam("longitude") double longitude,
			@QueryParam("distance") @DefaultValue("0.01") double distance,
			@QueryParam("maxResults") @DefaultValue("5") int maxResults) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("PREFIX owl: <http://www.w3.org/2002/07/owl#> ");
		builder.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
		builder.append("PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> ");
		builder.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		builder.append("SELECT DISTINCT ?resource ?label WHERE '{' ");
		builder.append("	?resource rdfs:label ?label. ");
		builder.append("	?resource geo:lat ?targetLat. ");
		builder.append("	?resource geo:long ?targetLong. ");
		builder.append("	FILTER( ");
		builder.append("		\"{0,number}\"^^xsd:float - ?targetLat<= {3,number} ");
		builder.append("		&& ?targetLat - \"{0,number}\"^^xsd:float <= {3,number} ");
		builder.append("		&& \"{1,number}\"^^xsd:float - ?targetLong <= {3,number} ");		
		builder.append("		&& ?targetLong - \"{1,number}\"^^xsd:float <= {3,number} ");
		builder.append("		&& lang(?label) = \"en\" ");		
		builder.append("	).");
		builder.append("'}' LIMIT {2,number,integer}");
		
		String output = SparqlUtils.runQuery(jerseyClient, sparqlEndpointURL, builder.toString(), latitude, longitude, maxResults, distance);
				
		
		return output;
	}
	
}
