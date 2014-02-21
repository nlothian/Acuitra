package com.acuitra.stages.integrated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.acuitra.pipeline.Context;
import com.acuitra.pipeline.ContextWithJerseyClient;
import com.acuitra.pipeline.Pipeline;
import com.acuitra.pipeline.Stage;
import com.acuitra.question.core.Answer;
import com.acuitra.question.core.Question;
import com.acuitra.stages.StageException;
import com.acuitra.stages.answer.ProcessSPARQLResultStage;
import com.acuitra.stages.answer.RunSPARQLQueryStage;
import com.acuitra.stages.question.QuepyStage;
import com.sun.jersey.api.client.Client;

public class IntegratedQuepyStage implements Stage<Question, List<Answer>> {

	private ContextWithJerseyClient<Question, List<Answer>> context;
	private List<Answer> answers = new ArrayList<>();
	private String quepyURL;
	private String sparqlEndpointURL;
	private Client jerseyClient;

	public IntegratedQuepyStage(String quepyURL, String sparqlEndpointURL, Client jerseyClient) {
		super();
		this.quepyURL = quepyURL;
		this.sparqlEndpointURL = sparqlEndpointURL;
		this.jerseyClient = jerseyClient;
	}		
	
	@Override
	public void loadContext(Context<Question, List<Answer>> ctx) {
		this.context =  (ContextWithJerseyClient<Question, List<Answer>>) ctx;		
	}


	@Override
	public void execute() {

		Question question = context.getInput();
		
		try {
			
			
			// use Quepy to parse the question
			Pipeline<Question, List<String>> questionPipeline = new Pipeline<>();
			questionPipeline.addStage(new QuepyStage(quepyURL));
			
			ContextWithJerseyClient<Question, List<String>> questionCtx = new ContextWithJerseyClient<Question, List<String>>(jerseyClient);
			questionCtx.setInput(question);
			
			questionPipeline.execute(questionCtx);
	
			
			// try to obtain an answer
			Pipeline<Map<String,List<String>>, List<String>> generateQuepyAnswerPipeline = new Pipeline<>();
			
			generateQuepyAnswerPipeline.addStage(new RunSPARQLQueryStage(sparqlEndpointURL));
			generateQuepyAnswerPipeline.addStage(new ProcessSPARQLResultStage(RunSPARQLQueryStage.class.getName()));
			
			
			ContextWithJerseyClient<Map<String,List<String>>, List<String>> answerContext = new  ContextWithJerseyClient<>(jerseyClient);		
			answerContext.setInput(questionCtx.getPreviousOutputs());
			
			generateQuepyAnswerPipeline.execute(answerContext);
			
			Map<String, List<String>> answerMap = answerContext.getPreviousOutputs();
			List<String> answerValues = answerMap.get(ProcessSPARQLResultStage.class.getName());
			
			
			for (String answerValue : answerValues) {
				Answer answer = new Answer();		
				answer.setQuestion(question);
				answer.setAnswer(answerValue);

				answers.add(answer);
			}
			
			
			
		} catch (StageException e) {
			Answer answer = new Answer();
			answer.setQuestion(question);
			answer.setErrorMessage(e.getLocalizedMessage());
			answer.setErrorCode(e.getErrorCode());
		}
		
		
		
		
	}

	@Override
	public List<Answer> getOutput() {
		return answers;
	}

	@Override
	public String getKeyName() {
		return this.getClass().getName();
	}

}
