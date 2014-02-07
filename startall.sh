#!/bin/bash

echo 'Starting Question Service'
#sudo docker run -d -p 8080:8080 question-service /var/local/acuitra/services/question-service/question-service.sh -r
sudo docker run -d -p 8080:8080 -v ~/Acuitra/:/var/local/acuitra question-service /var/local/acuitra/services/question-service/question-service.sh -r

echo 'Starting SPARQL service'
sudo docker run -d -p 3030:3030 -v /var/data:/var/data jena /jena-fuseki-1.0.1/fuseki-server --loc=/var/data/tdb/dbpedia /dbpedia

echo 'Starting Quepy service'
sudo docker run -d -p 5001:5001 quepy-service /usr/bin/python /var/local/acuitra/services/quepy-service/webservice.py

echo 'Starting webserver'
sudo docker run -p 80:80 -v ~/Acuitra/services/frontend-web/:/var/www -d acuitra-website
