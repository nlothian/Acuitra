package com.acuitra.question.resources;

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
import com.acuitra.stages.answer.ProcessSPARQLResultStage;
import com.acuitra.stages.answer.SPARQLQueryStage;
import com.acuitra.stages.question.ExtractTaggedEntityWordStage;
import com.acuitra.stages.question.NamedEntityRecognitionStage;
import com.acuitra.stages.question.RequestedWordToRDFPredicate;
import com.sun.jersey.api.client.Client;
import com.yammer.metrics.annotation.Timed;


@Path("/ask")
@Produces(MediaType.APPLICATION_JSON)
public class QuestionResource {
	
	private Client jerseyClient;
	private String namedEntityRecognitionURL;


	public QuestionResource(Client jerseyClient, String namedEntityRecognitionURL) {
		this.jerseyClient = jerseyClient;
		this.namedEntityRecognitionURL = namedEntityRecognitionURL;
	}
	

	@GET
	@Timed
	public Answer ask(@QueryParam("question") String param) {
		Pipeline<Question, String> processQuestionPipeline = new Pipeline<>();
		
		processQuestionPipeline.addStage(new NamedEntityRecognitionStage(namedEntityRecognitionURL));
		processQuestionPipeline.addStage(new ExtractTaggedEntityWordStage("NNP"));
		processQuestionPipeline.addStage(new ExtractTaggedEntityWordStage("NN"));
		processQuestionPipeline.addStage(new RequestedWordToRDFPredicate());		
		
		
		Question question = new Question(param);
		
		ContextWithJerseyClient<Question> questionContext = new  ContextWithJerseyClient<>(jerseyClient);
		questionContext.setInput(question);
		
		processQuestionPipeline.execute(questionContext);
		
		
		Pipeline<Map<String,String>, String> generateAnswerPipeline = new Pipeline<>();
		ContextWithJerseyClient<Map<String,String>> answerContext = new  ContextWithJerseyClient<>(jerseyClient);
		answerContext.setInput(questionContext.getPreviousOutputs());
		
		generateAnswerPipeline.addStage(new SPARQLQueryStage());
		generateAnswerPipeline.addStage(new ProcessSPARQLResultStage());
		
		generateAnswerPipeline.execute(answerContext);
		
		
		Answer answer = new Answer();
		answer.setQuestion(question);
		answer.setAnswer(answerContext.getPreviousOutput(ProcessSPARQLResultStage.class.getName()));		
		
		answer.addDebugInfo(questionContext.getPreviousOutputs());
		answer.addDebugInfo(answerContext.getPreviousOutputs());
		
		
		return answer;
		
	}
	
}
