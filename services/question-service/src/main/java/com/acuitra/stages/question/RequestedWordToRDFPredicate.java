package com.acuitra.stages.question;

public class RequestedWordToRDFPredicate extends AbstractQuestionStage {

	@Override
	public void execute() {
		String word = getContext().getPreviousOutput(ExtractTaggedEntityWordStage.class.getName() + "NN").get(0);
		System.out.println(word);
		
		String targettedWord = word.toUpperCase();
		
		if ("CAPITAL".equals(targettedWord)) {
			setOutput("<http://dbpedia.org/ontology/capital>");
		} else if ("AREA".equals(targettedWord)) {
			setOutput("<http://dbpedia.org/property/areaKm>");
		} else if ("POPULATION".equals(targettedWord)) {
			setOutput("<http://dbpedia.org/property/populationEstimate>");
		}


	}

}
