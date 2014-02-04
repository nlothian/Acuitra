package com.acuitra.question.resources;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.acuitra.pipeline.ContextWithJerseyClient;
import com.acuitra.pipeline.Pipeline;
import com.acuitra.question.core.Answer;
import com.acuitra.question.core.Question;
import com.acuitra.stages.StageException;
import com.acuitra.stages.answer.ProcessSPARQLResultStage;
import com.acuitra.stages.answer.RunSPARQLQueryStage;
import com.acuitra.stages.question.QuepyStage;
import com.sun.jersey.api.client.Client;
import com.yammer.metrics.annotation.Timed;


@Path("/ask")
@Produces(MediaType.APPLICATION_JSON)
public class QuestionResource {
	
	private Client jerseyClient;
	private String namedEntityRecognitionURL;
	private String sparqlEndpointURL;
	private String quepyURL; 


	public QuestionResource(Client jerseyClient, String namedEntityRecognitionURL, String sparqlEndpointURL, String quepyURL) {
		this.jerseyClient = jerseyClient;
		this.namedEntityRecognitionURL = namedEntityRecognitionURL;
		this.sparqlEndpointURL = sparqlEndpointURL;
		this.quepyURL = quepyURL;
	}
	

	@GET
	@Timed
	public Answer ask(@QueryParam("question") String param) {
		Answer answer = new Answer();
		try {
			Pipeline<Question, List<String>> processQuestionPipeline = new Pipeline<>();		
			
	//		processQuestionPipeline.addStage(new NamedEntityRecognitionStage(namedEntityRecognitionURL));
	//		processQuestionPipeline.addStage(new ExtractTaggedEntityWordStage("NNP"));
	//		processQuestionPipeline.addStage(new ExtractTaggedEntityWordStage("NN"));
	//		processQuestionPipeline.addStage(new RequestedWordToRDFPredicate());
			
			processQuestionPipeline.addStage(new QuepyStage(quepyURL));
			
			
			Question question = new Question(param);
			
			ContextWithJerseyClient<Question> questionContext = new  ContextWithJerseyClient<>(jerseyClient);
			questionContext.setInput(question);
			
			processQuestionPipeline.execute(questionContext);
			
			
			Pipeline<Map<String,List<String>>, List<String>> generateAnswerPipeline = new Pipeline<>();
			ContextWithJerseyClient<Map<String,List<String>>> answerContext = new  ContextWithJerseyClient<>(jerseyClient);
			answerContext.setInput(questionContext.getPreviousOutputs());
			
			//generateAnswerPipeline.addStage(new SPARQLQueryStage(sparqlEndpointURL));
			generateAnswerPipeline.addStage(new RunSPARQLQueryStage(sparqlEndpointURL));
			generateAnswerPipeline.addStage(new ProcessSPARQLResultStage());
			
			
			
			generateAnswerPipeline.execute(answerContext);
			
			
	
			answer.setQuestion(question);
			//List answers = new ArrayList<>();
			//answers.add(e)
			answer.setAnswer(answerContext.getPreviousOutput(ProcessSPARQLResultStage.class.getName()));		
			
			answer.addDebugInfo(questionContext.getPreviousOutputs());
			answer.addDebugInfo(answerContext.getPreviousOutputs());
						
			
		} catch(StageException e) {
			answer.setErrorMessage(e.getLocalizedMessage());
		}
		
		return answer;
		
	}
	
}
