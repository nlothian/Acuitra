package com.acuitra.nlp;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NLPUtilsTest {

	@Test
	public void testFindTaggedWords() {
		String input ="[[\"What\",\"WP\"],[\"is\",\"VBZ\"],[\"the\",\"DT\"],[\"area\",\"NN\"],[\"of\",\"IN\"],[\"the\",\"DT\"],[[\"United\",\"NNP\"],[\"States\",\"NNPS\"]],[\"?\",\".\"]]";
		String inputs = "[[\"Who\",\"WP\"],[\"is\",\"VBZ\"],[[\"Bill\",\"NNP\"],[\"Clinton\",\"NNP\"]],[\"'s\",\"POS\"],[\"daughter\",\"NN\"],[\"?\",\".\"]]";
	}

	@Test
	public void testCheckForTaggedWord() {
			
		
		try {
			
			
			Assert.assertEquals("States", NLPUtils.checkForTaggedWord(mapString("[\"States\",\"NNPS\"]"), "NNP", "NNPS"));
			Assert.assertEquals("State", NLPUtils.checkForTaggedWord(mapString("[\"State\",\"NNP\"]"), "NNP", "NNPS"));
			Assert.assertEquals("State", NLPUtils.checkForTaggedWord(mapString("[\"State\",\"NNP\"]"), "NNP"));
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
			
	}

	private JsonNode mapString(String input) throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(input);
	}
	
}
