var request = require('request');


var tests = [
	{question:"What is the capital of Australia?", expectedAnswer: 'Canberra'},
	{question:"What is the capital of Canada?", expectedAnswer: 'Ottawa'},
	{question:"What is the blah or blah?", expectedAnswer: null},
	{question:"How tall is Scottie Pippen?", expectedAnswer: 2.032},
	{question:"Who is Bill Clinton's daughter?", expectedAnswer: "Chelsea Clinton"},
	{question:"Who is Bill Clinton's wife?", expectedAnswer: "Hillary Rodham Clinton"},
	{question:"List books by Zadie Smith", expectedAnswer: "White Teeth", numberExpectedAnswers: 3},
	{question:"What albums did Pearl Jam record?", expectedAnswer: "Vitalogy", numberExpectedAnswers: 11},
	{question:"Who directed Syriana?", expectedAnswer: "Stephen Gaghan"},
	{question:"What is the population of the US?", expectedAnswer: 317637627},
	{question:"What is the population of the U.S.?", expectedAnswer: 317637627},	
	{question:"What is the population of the United States?", expectedAnswer: 317637627},	
	{question:"What is the landmass of the Angola?", expectedAnswer: 1246700},
	{question:"Where is Abraham Lincoln buried?", expectedAnswer: "Lincoln Tomb", numberExpectedAnswers: 3},
	{question:"What is the revenue of IBM?", expectedAnswer: "US$ 106.916 billion"},
	{question:"In which country is the Limerick Lake?", expectedAnswer: "Canada"},
	{question:"Who is the author of WikiLeaks?", expectedAnswer: "Julian Assange"},
	{question:"Give me the homepage of Forbes.", expectedAnswer: "http://www.forbes.com"},
	{question:"When was Lance Armstrong born?", expectedAnswer: "1971-09-18", numberExpectedAnswers: 1},




];
var numTests = tests.length;
var numPassed = 0;

var waitingFor = numTests;

tests.forEach(function(entry) {
	ask(entry.question, entry.expectedAnswer, entry.numberExpectedAnswers);
})

function finished(passed) {
	waitingFor--;

	if (passed) {
		numPassed++;
	}

	if (waitingFor == 0) {
		complete(numTests, numPassed);
	}
}

function complete(numTests, numPassed) {
	console.log(numPassed + " passed out of " + numTests);
}

function ask(question, expectedAnswer, numberExpectedAnswers) {
	
	request('http://acuitra.cloudapp.net/ask?question=' + encodeURIComponent(question), function (error, response, body) {
	  if (!error && response.statusCode == 200) {
	    var json = JSON.parse(body);
	    //console.log(json);
	    //console.log(json[0].answer);

	    var passed = false;
	    var matched = (expectedAnswer == json[0].answer);
	    if (matched) {
	    	if (numberExpectedAnswers != null) {
	    		if (json.length == numberExpectedAnswers) {
		    		console.log(question + ": PASSED");
		    		passed = true;
		    	} else {
		    		console.log(question + ": FAILED. First answer correct, but expected " + numberExpectedAnswers + " answers and received " + json.length);
		    	}

	    	} else {
		    	console.log(question + ": PASSED");	    		
		    	passed = true;
	    	}

	    } else {
	    	console.log(question + ": FAILED. Expected " + expectedAnswer + " received " + json[0].answer);
	    	//console.log(json);
	    }

	    finished(passed);

	  }
	});
}

