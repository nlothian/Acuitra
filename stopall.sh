#!/bin/bash

sudo docker stop dbpedia_sparql
sudo docker stop quepy
sudo docker stop question_service
sudo docker stop acuitra_website

sudo docker ps -a | grep Exit | awk '{print $1}' | sudo xargs docker rm
