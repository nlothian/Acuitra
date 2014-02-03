# BUILDAS sudo docker build -t question-service .

FROM nlothian/mvn3
MAINTAINER Nick Lothian nick.lothian@gmail.com


# GIT
RUN apt-get -y install git


# Get the service
ENV QUESTION_SERVICE_HOME /var/local/acuitra/services/question-service

RUN mkdir -p /var/local/acuitra
RUN git init /var/local/acuitra/
RUN cd /var/local/acuitra;git remote add -f origin https://github.com/nlothian/Acuitra.git
RUN cd /var/local/acuitra;git config core.sparseCheckout true
RUN cd /var/local/acuitra;echo services/question-service/*> .git/info/sparse-checkout
RUN cd /var/local/acuitra;git checkout master

RUN cd /var/local/acuitra/services/question-service;mvn3 package
# RUN java -jar /var/local/acuitra/services/question-service/target/acuitra-0.0.1-SNAPSHOT.jar server /var/local/acuitra/services/question-service/question-service.yaml
