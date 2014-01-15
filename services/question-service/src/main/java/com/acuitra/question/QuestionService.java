package com.acuitra.question;


import com.acuitra.question.resources.QuestionResource;
import com.sun.jersey.api.client.Client;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.client.JerseyClientBuilder;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class QuestionService extends Service<QuestionConfiguration> {

    public static void main(String[] args) throws Exception {
        new QuestionService().run(args);
    }	
	

	@Override
	public void initialize(Bootstrap<QuestionConfiguration> bootstrap) {
		bootstrap.setName("question");

	}

	@Override
	public void run(QuestionConfiguration config, Environment env) throws Exception {
		
	    final Client client = new JerseyClientBuilder().using(config.getJerseyClientConfiguration())
                .using(env)            
                .build();		    
		
		env.addResource(new QuestionResource(client, config.getNamedEntityRecognitionURL(), config.getSparqlEndpointURL()));

	}

}
