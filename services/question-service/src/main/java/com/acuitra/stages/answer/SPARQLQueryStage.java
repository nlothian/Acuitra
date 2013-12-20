package com.acuitra.stages.answer;

import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import com.acuitra.pipeline.ContextWithJerseyClient;
import com.acuitra.stages.question.ExtractTaggedEntityWordStage;
import com.acuitra.stages.question.RequestedWordToRDFPredicate;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;


public class SPARQLQueryStage extends AbstractAnswerStage {

	@Override
	public void execute() {
		
		Map<String,String> input = getContext().getInput();
		
		
		String target = input.get(ExtractTaggedEntityWordStage.class.getName() + "NNP");
		String predicate = input.get(RequestedWordToRDFPredicate.class.getName());
		
		Client jerseyClient = ((ContextWithJerseyClient) getContext()).getJerseyClient();		

		WebResource webResource = jerseyClient.resource("http://wifo5-03.informatik.uni-mannheim.de/factbook/sparql");		
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("output", "json");
		
		
		StringBuilder builder = new StringBuilder();
		builder.append("PREFIX db: <http://wifo5-04.informatik.uni-mannheim.de/factbook/resource/>");
		builder.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");
		builder.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>");
		builder.append("PREFIX d2r: <http://sites.wiwiss.fu-berlin.de/suhl/bizer/d2r-server/config.rdf#>");
		builder.append("PREFIX owl: <http://www.w3.org/2002/07/owl#>");
		builder.append("PREFIX map: <file:/var/www/wbsg.de/factbook/factbook.n3#>");
		builder.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>");
		builder.append("PREFIX factbook: <http://wifo5-04.informatik.uni-mannheim.de/factbook/ns#>");
		builder.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
		builder.append("SELECT DISTINCT ?resource ?value ");
		builder.append("WHERE { ");
		builder.append(" ?resource ");
		builder.append( predicate );
		builder.append(" ?value. ");
		builder.append("?resource <http://wifo5-04.informatik.uni-mannheim.de/factbook/ns#countryname_conventionalshortform> \"");
		builder.append(target);
		builder.append("\" }");
		
		params.add("query", builder.toString());

		ClientResponse response = webResource.queryParams(params).get(ClientResponse.class);
		
		String text = response.getEntity(String.class);
		
		System.out.println("P1");
		
		System.out.println(text);
		
		this.setOutput(text);
		
	}
		

}
