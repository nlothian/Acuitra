
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



echo $JAVA_OPTS
