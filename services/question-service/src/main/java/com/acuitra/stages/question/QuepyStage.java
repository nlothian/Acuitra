package com.acuitra.stages.question;

import javax.ws.rs.core.MultivaluedMap;

import com.acuitra.pipeline.Context;
import com.acuitra.pipeline.ContextWithJerseyClient;
import com.acuitra.question.core.Question;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class QuepyStage extends AbstractQuestionStage {

	private ContextWithJerseyClient<String> context;
	private Question question;
	private String quepyURL;
	
	public QuepyStage(String quepyURL) {
		super();
		this.quepyURL = quepyURL;
	}	

	@Override
	public void loadContext(Context<Question, String> ctx) {
		this.question = ctx.getInput();
		
		this.context =  (ContextWithJerseyClient) ctx;		

	}

	@Override
	public void execute() {
		Client jerseyClient = context.getJerseyClient();		
		
		WebResource webResource = jerseyClient.resource(quepyURL);		
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("text", question.getQuestion());
				
		
		ClientResponse response = webResource.queryParams(params).type("application/x-www-form-urlencoded").get(ClientResponse.class);

		String text = response.getEntity(String.class);
		System.out.println(text);
		
		this.setOutput(text);		
		
	}


}
