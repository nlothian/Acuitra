package com.acuitra.stages.question;

public class RequestedWordToRDFPredicate extends AbstractQuestionStage {

	@Override
	public void execute() {
		String word = getContext().getPreviousOutput(ExtractTaggedEntityWordStage.class.getName() + "NN").get(0);
		System.out.println(word);
		
		String targettedWord = word.toUpperCase();
		
		if ("CAPITAL".equals(targettedWord)) {
			setOutput("<http://wifo5-04.informatik.uni-mannheim.de/factbook/ns#capital_name>");
		} else if ("AREA".equals(targettedWord)) {
			setOutput("<http://wifo5-04.informatik.uni-mannheim.de/factbook/ns#area_total>");
		} else if ("POPULATION".equals(targettedWord)) {
			setOutput("<http://wifo5-04.informatik.uni-mannheim.de/factbook/ns#literacy_totalpopulation>");
		}


	}

}
