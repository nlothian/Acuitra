package com.acuitra.stages.answer;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import com.acuitra.ErrorCodes;
import com.acuitra.pipeline.ContextWithJerseyClient;
import com.acuitra.stages.StageException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class RunSPARQLQueryStage extends AbstractAnswerStage {
	
	public static final String EXPECTED_INPUT_NAME = "SPARQL-QUERY";
	

	String sparqlEndpointURL;
	
	public RunSPARQLQueryStage(String sparqlEndpointURL) {
		super();
		
		this.sparqlEndpointURL = sparqlEndpointURL;
		
	}	
	
	@Override
	public void execute() {
		Map<String,List<String>> input = getContext().getInput();
		
		
		// note this only runs the first query
		String query = input.get(EXPECTED_INPUT_NAME).get(0);
		if (query == null || query.length() == 0) {
			throw new StageException("Could not find query to run. Looked for key " + EXPECTED_INPUT_NAME, ErrorCodes.INTERNAL_ERROR);
		}
		Client jerseyClient = ((ContextWithJerseyClient) getContext()).getJerseyClient();
		WebResource webResource = jerseyClient.resource(sparqlEndpointURL);
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("output", "json");
		params.add("query", query);
		
		// Run the query and get the response
		ClientResponse response = webResource.queryParams(params).get(ClientResponse.class);
		
		String output = response.getEntity(String.class);
		this.setOutput(output);
		

		
		
	}

}
