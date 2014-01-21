#!/bin/bash  

usage()
{
cat << EOF
usage: $0 options

Use this script to drive the question-service

OPTIONS
  -h		Show this message
  -u		Update source via Git, then rebuild
  -r		Run the service
EOF
}

update() {
	git pull
	mvn3 package
}

run() {
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