vagrant@vagrant-ubuntu-precise-64:~/docker-nltk$ cat Dockerfile
# NLTK REST Webservice
#
# VERSION 0.1



# Ubuntu 12.04
FROM ubuntu:precise
MAINTAINER Nick Lothian nick.lothian@gmail.com


RUN apt-get update

# Setup python
RUN apt-get -y install build-essential python-dev
RUN apt-get -y install python-setuptools
RUN easy_install pip
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