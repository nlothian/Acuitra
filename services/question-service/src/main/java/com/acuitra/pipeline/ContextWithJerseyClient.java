package com.acuitra.pipeline;

import com.sun.jersey.api.client.Client;

public class ContextWithJerseyClient<T,O> extends Context<T,O> {
	final private Client jerseyClient;
	
	public ContextWithJerseyClient(Client jerseyClient) {
		super();
		
		this.jerseyClient = jerseyClient;
	}

	public Client getJerseyClient() {
		return jerseyClient;
	}
	
}
