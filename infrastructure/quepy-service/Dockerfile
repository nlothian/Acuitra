# Quepy Webservice
#
# VERSION 0.1



# Ubuntu 12.04
FROM ubuntu:precise
MAINTAINER Nick Lothian nick.lothian@gmail.com

# Expose port 5001
expose 5001

RUN apt-get update


# Setup python
RUN apt-get -y install build-essential python-dev
RUN apt-get -y install python-setuptools
RUN easy_install pip
RUN pip install setuptools --no-use-wheel --upgrade

# NLTK requirements
RUN pip install -U pyyaml nltk
RUN /usr/bin/python -m nltk.downloader maxent_treebank_pos_tagger
RUN /usr/bin/python -m nltk.downloader wordnet

# numpy
RUN apt-get -y install python-numpy


# Flask
RUN pip install flask

# Quepy
RUN pip install quepy

# GIT
RUN apt-get -y install git

# Get the service
RUN mkdir -p /var/local/acuitra
RUN git init /var/local/acuitra/
RUN cd /var/local/acuitra;git remote add -f origin https://github.com/nlothian/Acuitra.git
RUN cd /var/local/acuitra;git config core.sparseCheckout true
RUN cd /var/local/acuitra;echo services/quepy-service/*> .git/info/sparse-checkout
RUN cd /var/local/acuitra;git checkout master

# Run the service
# RUN /usr/bin/python /var/local/acuitra/services/quepy-service/webservice.py  



  
