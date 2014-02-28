#!/bin/bash  

usage()
{
cat << EOF
usage: $0 options

Use this script to drive the question-service

OPTIONS
  -h		Show this message
  -u		Update source by copying from host, then rebuild
  -r		Run the service
EOF
}

update() {
    mkdir -p /tmp/acuitra/services/question-service
    cp -r $QUESTION_SERVICE_HOME/* /tmp/acuitra/services/question-service/
    cd /tmp/acuitra/services/question-service/
	mvn3 package
}

run() {
	update
	cd /tmp/acuitra/services/question-service

    JAVA_OPTS=
    if [  "$DBPEDIA_SPARQL_PORT_3030_TCP_PROTO" ] ; then
        JAVA_OPTS="$JAVA_OPTS "-Ddw.sparqlEndpointURL=http://"$DBPEDIA_SPARQL_PORT_3030_TCP_ADDR":"$DBPEDIA_SPARQL_PORT_3030_TCP_PORT"/dbpedia/query
    fi
    if [  "$QUEPY_PORT_5001_TCP_PROTO" ] ; then
        JAVA_OPTS="$JAVA_OPTS "-Ddw.quepyURL=http://"$QUEPY_PORT_5001_TCP_ADDR":"$QUEPY_PORT_5001_TCP_PORT"/question
    fi
    if [  "$NAMED_ENTITY_SERVICE_PORT_5000_TCP_PROTO" ] ; then
        JAVA_OPTS="$JAVA_OPTS "-Ddw.namedEntityRecognitionURL=http://"$NAMED_ENTITY_SERVICE_PORT_5000_TCP_ADDR":"$NAMED_ENTITY_SERVICE_PORT_5000_TCP_PORT"/ner
    fi
	java $JAVA_OPTS -jar ./target/acuitra-0.0.1-SNAPSHOT.jar server question-service.yaml
}

while getopts "hur" OPTION
do
     case $OPTION in
         h)
             usage
             exit 1
             ;;
         u)
             update
             ;;
         r)
             run
             ;;
     esac
done
