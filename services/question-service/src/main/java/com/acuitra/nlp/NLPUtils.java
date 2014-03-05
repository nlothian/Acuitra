package com.acuitra.nlp;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class NLPUtils {
	public static List<String> findTaggedWords(JsonNode rootNode, boolean lookForSequence, String... tags) {
		ArrayList<String> result = new ArrayList<>();
		
		int size = rootNode.size();
		for (int i = 0; i < size; i++) {
			JsonNode child = rootNode.get(i);
			List<String> words = findTaggedWords(child, lookForSequence, tags);
			boolean foundFirstWordInPossibleSequence = false;
			if (words.size() > 0) {
				// we found the word or the sequence of words in a subchild
				return words;
			} else {
				String word = null;
				if ((word = checkForTaggedWord(child, tags)) != null) {
					foundFirstWordInPossibleSequence = true;
					result.add(word);
					if (!lookForSequence) {
						return result;
					}
				} else {
					// didn't find the word
					foundFirstWordInPossibleSequence = false;
				}
				
				if ((result.size() > 0) && (!foundFirstWordInPossibleSequence)) {
					// found at least one word, and we are no longer in the same sequence
					return result;
				}
			}
			
		}
		
		
		return result;

	}

	public static String checkForTaggedWord(JsonNode child, String... tags) {		
		for (int i = 0; i < tags.length; i++) {
			if ((child.size() == 2) && (tags[i].equals(child.get(1).asText()))) {
				return child.get(0).asText();
			} 		
		}
		
		return null;
		
	}	
}
