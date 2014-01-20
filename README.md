Acuitra
=======


On Ubuntu, the following is useful

For the Named Entity Recognition Service

* install Docker (see http://docker.io)
* install Git (sudo apt-get install git)
* git clone https://github.com/nlothian/Acuitra.git
* cd infrastructure/named-entity-service
* sudo docker build -t nltk .
* sudo docker run -d nltk /usr/bin/python /var/local/acuitra/services/named-entity-service/named-entity-ws.py
* TODO...


For the Question Service

* cd infrastrcture/question-service
* sudo docker build -t question-service .
* sudo docker run  -p 8080:8080 -i -t question-service java -jar /var/local/acuitra/services/question-service/target/acuitra-0.0.1-SNAPSHOT.jar server /var/local/acuitra/services/question-service/question-service.yaml




Then (assuming you have Jena setup on the correct port (TODO: document this!)):

* http://127.0.0.1:8080/ask?question=what%20is%20the%20capital%20of%20Australia

Hopefully you'll get an answer..
 


