var request = require('request');
request('http://acuitra.cloudapp.net/ask?question=What+is+the+capital+of+Australia?', function (error, response, body) {
  if (!error && response.statusCode == 200) {
    var json = JSON.parse(body);
    console.log(json);
    
    console.log(json[0].answer);
  }
});