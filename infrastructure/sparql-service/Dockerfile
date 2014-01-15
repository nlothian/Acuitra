

# Ubuntu 12.04
FROM ubuntu:precise
MAINTAINER Nick Lothian nick.lothian@gmail.com


RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu precise main" | tee -a /etc/apt/sources.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
RUN apt-get update
RUN echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN apt-get install oracle-java7-installer -y


RUN apt-get install wget -y
RUN wget http://www.apache.org/dist/jena/binaries/jena-fuseki-1.0.0-distribution.tar.gz
RUN wget http://www.apache.org/dist/jena/binaries/apache-jena-2.11.0.tar.gz

RUN tar -xf jena-fuseki-1.0.0-distribution.tar.gz
RUN tar -xf apache-jena-2.11.0.tar.gz

ENV FUSEKI_HOME /jena-fuseki-1.0.0
