package com.acuitra.pipeline;

import com.sun.jersey.api.client.Client;

public class ContextWithJerseyClient<T> extends Context<T, String> {
	final private Client jerseyClient;
	
	public ContextWithJerseyClient(Client jerseyClient) {
		super();
		
		this.jerseyClient = jerseyClient;
	}

	public Client getJerseyClient() {
		return jerseyClient;
	}
	
}
