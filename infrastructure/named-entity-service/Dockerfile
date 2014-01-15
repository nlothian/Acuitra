# NLTK REST Webservice
#
# VERSION 0.1



# Ubuntu 12.04
FROM ubuntu:precise
MAINTAINER Nick Lothian nick.lothian@gmail.com

# Expose port 5000
expose 5000

RUN apt-get update

# Setup python
RUN apt-get -y install build-essential python-dev
RUN apt-get -y install python-setuptools
RUN easy_install pip
RUN pip install setuptools --no-use-wheel --upgrade
RUN pip install -U numpy

# NLTK requirements
RUN pip install -U pyyaml nltk
RUN /usr/bin/python -m nltk.downloader treebank
RUN /usr/bin/python -m nltk.downloader words
RUN /usr/bin/python -m nltk.downloader maxent_ne_chunker
RUN /usr/bin/python -m nltk.downloader maxent_treebank_pos_tagger


# Flask
RUN pip install flask

# GIT
RUN apt-get -y install git


# Get the service
RUN mkdir -p /var/local/acuitra
RUN git init /var/local/acuitra/
RUN cd /var/local/acuitra;git remote add -f origin https://github.com/nlothian/Acuitra.git
RUN cd /var/local/acuitra;git config core.sparseCheckout true
RUN cd /var/local/acuitra;echo services/named-entity-service/*> .git/info/sparse-checkout
RUN cd /var/local/acuitra;git checkout master

# Run the service
# RUN /usr/bin/python /var/local/acuitra/services/named-entity-service/named-entity-ws.py  



  
