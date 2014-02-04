package com.acuitra;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.client.JerseyClientConfiguration;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.util.Duration;

public class ServiceConfiguration extends Configuration {
    @NotNull
    private String namedEntityRecognitionURL;
    
    @NotNull
    private String sparqlEndpointURL;

    @NotNull 
    private String quepyURL;

    @Valid
    @NotNull
    @JsonProperty    
    private JerseyClientConfiguration httpClient;
    
    public ServiceConfiguration() {
    	super();
    	
    	httpClient =  new JerseyClientConfiguration();
    	httpClient.setConnectionTimeout(Duration.seconds(10));
    	httpClient.setTimeout(Duration.seconds(10));
        
    }
    
    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return httpClient;
    }

	public String getNamedEntityRecognitionURL() {
		return namedEntityRecognitionURL;
	}

    public String getSparqlEndpointURL() {
		return sparqlEndpointURL;
	}
	
    
	public String getQuepyURL() {
		return quepyURL;
	}
	
	 
}
