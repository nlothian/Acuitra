package com.acuitra.stages.question;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import com.acuitra.ErrorCodes;
import com.acuitra.pipeline.Context;
import com.acuitra.pipeline.ContextWithJerseyClient;
import com.acuitra.question.core.Question;
import com.acuitra.stages.StageException;
import com.acuitra.stages.answer.RunSPARQLQueryStage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 
 * Passes a natural language question to a Quepy webservice, and expects a SPARQL query to run to be returned
 * 
 * @author nlothian
 *
 */
public class QuepyStage extends AbstractQuestionStage {

	private ContextWithJerseyClient<String> context;
	private Question question;
	private String quepyURL;
	
	public QuepyStage(String quepyURL) {
		super();
		this.quepyURL = quepyURL;
	}	

	@Override
	public void loadContext(Context<Question, List<String>> ctx) {
		this.question = ctx.getInput();
		
		this.context =  (ContextWithJerseyClient) ctx;		

	}

	@Override
	public void execute() {
		Client jerseyClient = context.getJerseyClient();		
		
		// setup the query
		WebResource webResource = jerseyClient.resource(quepyURL);		
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("text", question.getQuestion());
				
		// get the response
		ClientResponse response = webResource.queryParams(params).type("application/x-www-form-urlencoded").get(ClientResponse.class);

		String text = response.getEntity(String.class);
		//System.out.println(text);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(text);
			String errorMessage = rootNode.path("error").asText();
			if (!"".equals(errorMessage)) {
				throw new StageException(errorMessage, ErrorCodes.COULD_NOT_UNDERSTAND_QUESTION);
			}
			
			// Note that we could have multiple answers (or the answer could be a list) and this only extracts the first result
			String query = rootNode.path("query").asText();
			
			// set the query where the Run Query stage expects it
			List<String> queries = new ArrayList<>();
			queries.add(query);
			context.addOutput(RunSPARQLQueryStage.EXPECTED_INPUT_NAME, queries);
			
			setOutput(text);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
		
		
	}


}
