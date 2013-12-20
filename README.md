Acuitra
=======


On Ubuntu, the following is useful

* install Docker (see http://docker.io)
* install Git (sudo apt-get install git)
* git clone https://github.com/nlothian/Acuitra.git
* cd infrastructure/named-entity-service
* sudo docker build -t nltk .
* sudo docker run -d nltk /usr/bin/python /var/local/acuitra/services/named-entity-service/named-entity-ws.py
* TODO...
