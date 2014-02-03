package com.acuitra.stages.question;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import com.acuitra.pipeline.Context;
import com.acuitra.pipeline.ContextWithJerseyClient;
import com.acuitra.question.core.Question;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class NamedEntityRecognitionStage extends AbstractQuestionStage {	
	

	private ContextWithJerseyClient<String> context;
	private Question question;
	private String namedEntityRecognitionURL;	

	public NamedEntityRecognitionStage(String namedEntityRecognitionURL) {
		super();
		this.namedEntityRecognitionURL = namedEntityRecognitionURL;
	}

	@Override
	public void loadContext(Context<Question, List<String>> ctx) {
		this.question = ctx.getInput();
		
		this.context =  (ContextWithJerseyClient) ctx;		
	}

	@Override
	public void execute() {
		Client jerseyClient = context.getJerseyClient();		
		
		WebResource webResource = jerseyClient.resource(namedEntityRecognitionURL);		
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("text", question.getQuestion());
				
		
		ClientResponse response = webResource.queryParams(params).type("application/x-www-form-urlencoded").get(ClientResponse.class);
		
		String text = response.getEntity(String.class);
		System.out.println(text);
		
		List<String> outputs = new ArrayList<>();
		outputs.add(text);
		
		this.setOutput(outputs);			
	}


}
