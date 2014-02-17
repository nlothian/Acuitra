package com.acuitra.question.resources;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.acuitra.ErrorCodes;
import com.acuitra.pipeline.ContextWithJerseyClient;
import com.acuitra.pipeline.ParallelPipelineRunner;
import com.acuitra.pipeline.Pipeline;
import com.acuitra.pipeline.RunnablePipeline;
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
			
			Question question = new Question(param);
			
			ContextWithJerseyClient<Question> questionContext = new  ContextWithJerseyClient<>(jerseyClient);
			questionContext.setInput(question);
			
			
			RunnablePipeline<Question, List<String>> processQuestionPipeline = new RunnablePipeline<>("Process Question Pipeline", questionContext);		
			
	//		processQuestionPipeline.addStage(new NamedEntityRecognitionStage(namedEntityRecognitionURL));
	//		processQuestionPipeline.addStage(new ExtractTaggedEntityWordStage("NNP"));
	//		processQuestionPipeline.addStage(new ExtractTaggedEntityWordStage("NN"));
	//		processQuestionPipeline.addStage(new RequestedWordToRDFPredicate());
			
			processQuestionPipeline.addStage(new QuepyStage(quepyURL));
			
			
			ParallelPipelineRunner<Question, List<String>> questionRunner = new ParallelPipelineRunner<>(10000);
			
			questionRunner.addPipeline(processQuestionPipeline);
			
			questionRunner.run(); // run the pipeline 
			
			if (!processQuestionPipeline.isComplete()) {
				throw new StageException("Processing questions took too long", ErrorCodes.PROCESSING_QUESTION_TIMEOUT);			
			}
			
			
			ContextWithJerseyClient<Map<String,List<String>>> answerContext = new  ContextWithJerseyClient<>(jerseyClient);
			answerContext.setInput(questionContext.getPreviousOutputs());


			ParallelPipelineRunner<Map<String,List<String>>, List<String>> answerGeneratorRunner = new ParallelPipelineRunner<>(10000);
			
			RunnablePipeline<Map<String,List<String>>, List<String>> generateAnswerPipeline = new RunnablePipeline<>("Generate Answers Pipelines", answerContext);
						
			generateAnswerPipeline.addStage(new RunSPARQLQueryStage(sparqlEndpointURL));
			generateAnswerPipeline.addStage(new ProcessSPARQLResultStage());
			
			answerGeneratorRunner.addPipeline(generateAnswerPipeline);
			
			answerGeneratorRunner.run(); // run the pipeline
			
			if (!generateAnswerPipeline.isComplete()) {
				throw new StageException("Generating answers took too long", ErrorCodes.GENERATING_ANSWERS_TIMEOUT);			
			}
			
	
			answer.setQuestion(question);
			//List answers = new ArrayList<>();
			//answers.add(e)
			answer.setAnswer(answerContext.getPreviousOutput(ProcessSPARQLResultStage.class.getName()));		
			
			answer.addDebugInfo(questionContext.getPreviousOutputs());
			answer.addDebugInfo(answerContext.getPreviousOutputs());
						
			
		} catch(StageException e) {
			answer.setErrorMessage(e.getLocalizedMessage());
			answer.setErrorCode(e.getErrorCode());
		}
		
		return answer;
		
	}
	
}
