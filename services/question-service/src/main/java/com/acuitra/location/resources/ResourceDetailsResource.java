package com.acuitra.location.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.acuitra.ErrorCodes;
import com.acuitra.sparql.SparqlUtils;
import com.sun.jersey.api.client.Client;
import com.yammer.metrics.annotation.Timed;

@Path("/dbpedia/resource/")
@Produces(MediaType.APPLICATION_JSON)
public class ResourceDetailsResource {
	
	private Client jerseyClient;
	private String sparqlEndpointURL;

	public ResourceDetailsResource(Client jerseyClient, String sparqlEndpointURL) {
		super();
		
		this.jerseyClient = jerseyClient;
		this.sparqlEndpointURL = sparqlEndpointURL;
	}	
	
	@GET
	@Path("{name}/basic")
	@Timed
	public String basicDetails(@PathParam("name") String name) {
		if (SparqlUtils.isValidDBpediaResourceName(name)) {
			
			StringBuilder builder = new StringBuilder();
//			builder.append("PREFIX dbpedia: <http://dbpedia.org/resource/> ");
			builder.append("PREFIX dbp-ont: <http://dbpedia.org/ontology/> ");
			builder.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/> ");
			builder.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
			builder.append("SELECT ?name ?description ?thumbnail ?image WHERE '{' ");
//			builder.append("	dbpedia:{0} rdfs:label ?name . ");
//			builder.append("	dbpedia:{0} rdfs:comment ?description . ");
//			builder.append("	dbpedia:{0} dbp-ont:thumbnail ?thumbnail . ");
//			builder.append("	dbpedia:{0} foaf:depiction ?image . ");
			builder.append("	{0} rdfs:label ?name . ");
			builder.append("	OPTIONAL '{'{0} rdfs:comment ?description '}'. ");
			builder.append("	OPTIONAL '{'{0} dbp-ont:thumbnail ?thumbnail '}'. ");
			builder.append("	OPTIONAL '{'{0} foaf:depiction ?image '}'. ");						
			builder.append("	FILTER( ");
			builder.append("		lang(?name) = \"en\" ");		
			builder.append("	).");
			builder.append("'}'");
			
			//String subbedName = name.replace(",", "\\,");
			String subbedName = name;
			
			String output = SparqlUtils.runQuery(jerseyClient, sparqlEndpointURL, builder.toString(), subbedName);
			
			System.out.println(output);
			
			return SparqlUtils.sparqlResultSetToJsonTable(output);
						
			
		} else {
			return "{\"error\" : \"Invalid resource name\", \"errorCode\" : \"" + ErrorCodes.INVALID_RESOURCE + "\"}";
			
		}

		
	}


}
