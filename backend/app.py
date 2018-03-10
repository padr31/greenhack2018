#!flask/bin/python
from flask import Flask, send_file, request
import json
from tree import Tree

from threading import Thread


app = Flask(__name__, static_url_path='')
treelist = [Tree("name", "story", 1261516661, 10, 20)]


@app.route('/')
def index():
    return "Nothing here :)"


@app.route('/plant', methods=["POST"])
def plant():
    body = json.loads(request.data.decode('utf-8'))
    tree = Tree(body["name"], body["story"], body["time"], body["lat"], body["lon"])
    treelist.append(tree)

    return json.dumps({})


@app.route('/forest')
def getlist():
    return json.dumps({"trees": [tree.tolist() for tree in treelist]})


@app.after_request
def after_request(response):
    header = response.headers
    header['Access-Control-Allow-Origin'] = '*'
    return response


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', threaded=True)
    #index()