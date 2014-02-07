package com.acuitra;


import com.acuitra.location.resources.FindNearbyResource;
import com.acuitra.location.resources.ResourceDetailsResource;
import com.acuitra.question.resources.QuestionResource;
import com.acuitra.servlet.filter.CorsHeadersFilter;
import com.sun.jersey.api.client.Client;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.client.JerseyClientBuilder;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class QuestionService extends Service<ServiceConfiguration> {

    public static void main(String[] args) throws Exception {
        new QuestionService().run(args);
    }	
	

	@Override
	public void initialize(Bootstrap<ServiceConfiguration> bootstrap) {
		bootstrap.setName("question");

	}

	@Override
	public void run(ServiceConfiguration config, Environment env) throws Exception {
		
	    final Client client = new JerseyClientBuilder().using(config.getJerseyClientConfiguration())
                .using(env)            
                .build();	
	    
	    client.setReadTimeout(100000);
	    client.setConnectTimeout(100000);
		
		env.addResource(new QuestionResource(client, config.getNamedEntityRecognitionURL(), config.getSparqlEndpointURL(), config.getQuepyURL()));
		env.addResource(new FindNearbyResource(client, config.getSparqlEndpointURL()));
		env.addResource(new ResourceDetailsResource(client, config.getSparqlEndpointURL()));
		
		
		env.addFilter(new CorsHeadersFilter(), "/*");

	}

}
