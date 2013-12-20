package com.acuitra.question;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.client.JerseyClientConfiguration;
import com.yammer.dropwizard.config.Configuration;

public class QuestionConfiguration extends Configuration {
    @NotNull
    private String namedEntityRecognitionURL;	

	
    @Valid
    @NotNull
    @JsonProperty    
    private JerseyClientConfiguration httpClient = new JerseyClientConfiguration();
    
    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return httpClient;
    }

	public String getNamedEntityRecognitionURL() {
		return namedEntityRecognitionURL;
	}

	 
}
