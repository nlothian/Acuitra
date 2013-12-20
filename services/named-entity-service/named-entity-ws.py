import nltk
from flask import request
from flask import Flask
from flask import json

app = Flask(__name__)

@app.route("/ner" ,methods=['GET', 'POST'])
def ner():
    text = request.args.get('text', '')

    tokens = nltk.word_tokenize(text)
    tagged = nltk.pos_tag(tokens)
    entities = nltk.chunk.ne_chunk(tagged)

    return json.dumps(entities)

@app.route("/")
def hello():
    return "ok"



if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)