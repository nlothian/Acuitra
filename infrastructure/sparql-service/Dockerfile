
# Ubuntu 12.04 + Java
FROM nlothian/java
MAINTAINER Nick Lothian nick.lothian@gmail.com

EXPOSE 3030

RUN apt-get install wget -y
RUN wget http://www.apache.org/dist/jena/binaries/jena-fuseki-1.0.0-distribution.tar.gz
RUN wget http://www.apache.org/dist/jena/binaries/apache-jena-2.11.0.tar.gz

RUN tar -xf jena-fuseki-1.0.0-distribution.tar.gz
RUN tar -xf apache-jena-2.11.0.tar.gz

ENV FUSEKI_HOME /jena-fuseki-1.0.0
