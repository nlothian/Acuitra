# Ubuntu 12.04
FROM ubuntu:precise
MAINTAINER Nick Lothian nick.lothian@gmail.com


RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu precise main" | tee -a /etc/apt/sources.list
RUN echo "deb http://ppa.launchpad.net/natecarlson/maven3/ubuntu precise main" | tee -a /etc/apt/sources.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
RUN apt-get update
RUN echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN apt-get install oracle-java7-installer -y

RUN apt-get -y --force-yes install maven3


# GIT
RUN apt-get -y install git


# Get the service
RUN mkdir -p /var/local/acuitra
RUN git init /var/local/acuitra/
RUN cd /var/local/acuitra;git remote add -f origin https://github.com/nlothian/Acuitra.git
RUN cd /var/local/acuitra;git config core.sparseCheckout true
RUN cd /var/local/acuitra;echo services/question-service/*> .git/info/sparse-checkout
RUN cd /var/local/acuitra;git checkout master

RUN cd /var/local/acuitra/services/question-service;mvn3 package
# RUN java -jar /var/local/acuitra/services/question-service/target/acuitra-0.0.1-SNAPSHOT.jar server /var/local/acuitra/services/question-service/question-service.yaml
