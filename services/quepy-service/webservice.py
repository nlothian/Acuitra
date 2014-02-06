from flask import request
from flask import Flask
from flask import json

import quepy

dbpedia = quepy.install("dbpedia")

app = Flask(__name__)

@app.route("/question" ,methods=['GET', 'POST'])
def question():
	text = request.args.get('text', '')

	target, query, metadata = dbpedia.get_query(text)

        if isinstance(metadata, tuple):
		query_type = metadata[0]
		metadata = metadata[1]
        else:
		query_type = metadata
		metadata = None

	print target
	print metadata
	print query

        if query is None:
		data = {"error": "no query generated"}
	else:
		#data = {"query": query.replace('\n', ' ')}
		data = {"target": target, "query": query.replace('\n', ' ')}

	return json.dumps(data, sort_keys=True, indent=2)

@app.route("/")
def hello():
	return "ok"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001, debug=True)
    #app.run(port=5001, debug=True)

