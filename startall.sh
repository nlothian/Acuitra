#!/bin/bash

echo 'Starting SPARQL service'
sudo docker run -d -p 3030:3030 -v /var/data:/var/data -name dbpedia_sparql jena /jena-fuseki-1.0.1/fuseki-server --loc=/var/data/tdb/dbpedia /dbpedia

echo 'Starting Quepy service'
sudo docker run -d -p 5001:5001 -name quepy quepy-service /usr/bin/python /var/local/acuitra/services/quepy-service/webservice.py

echo 'Starting Named Entity service'
sudo docker run -d -p 5000:5000 -name named_entity_service named-entity-service

echo 'Starting Question Service'
sudo docker run -d -p 80:8080 -v ~/Acuitra/:/var/local/acuitra -link dbpedia_sparql:dbpedia_sparql -link quepy:quepy -link named_entity_service:named_entity_service  -name question_service question-service

# echo 'Starting webserver'
#sudo docker run -d -p 80:80 -v ~/Acuitra/services/frontend-web/:/var/www -name acuitra_website acuitra-website 
