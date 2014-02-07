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
	java -jar ./target/acuitra-0.0.1-SNAPSHOT.jar server question-service.yaml
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
