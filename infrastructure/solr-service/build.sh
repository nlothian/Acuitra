#!/bin/bash

usage()
{
cat << EOF
usage: $0 options

Use this script to build the Docker container

OPTIONS
  -h            Show this message
  -v            Verson of Solr to download. See http://www.us.apache.org/dist/lucene/solr/ for versions
EOF
}

while getopts ":v:" opt; do
  case $opt in
    v)
      SOLR_VERSION=$OPTARG
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
      ;;
    :)
      echo "Option -$OPTARG requires an argument." >&2
      usage
      exit 1
      ;;
  esac
done

if [[ -z "$SOLR_VERSION" ]];
then
    usage;
    exit 1;
fi

wget -nc http://www.us.apache.org/dist/lucene/solr/$SOLR_VERSION/solr-$SOLR_VERSION.tgz
cp solr-$SOLR_VERSION.tgz solr.tgz
sudo docker build -t solr .

