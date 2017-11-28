import json
import context
from doit import Doit
doit = Doit()


from flask import Flask, jsonify, request
app = Flask(__name__)

@app.errorhandler
def error404():
	return json.dumps({
		"status": 404,
		"data": [],
		"error": "Page not found!"
	}), 404

@app.route("/api/modules", methods=["GET"])
def get_modules():
	data = doit.get_modules()
	return jsonify(data), data["status"]

@app.route("/api/modules/<int:mid>", methods=["GET"])
def get_module(mid=-1):
	data = doit.get_module(mid)
	return jsonify(data), data["status"]

@app.route("/api/modules/<int:mid>/<cmd>", methods=["GET"])
def get_module_cmds(mid=-1, cmd=""):
	data = doit.get_module_cmds(mid, cmd)
	return jsonify(data), data["status"]

@app.route("/api/modules/<int:mid>/<cmd>", methods=["POST"])
def get_module_cmd_data(mid=-1, cmd=""):
	try:
		data = request.get_data()
		if len(data) == 0:
			data = []
		data = json.loads(request.get_data())
		data = doit.get_module_cmd_data(mid, cmd, data["data"])
		return jsonify(data), data["status"]
	except:
		return json.dumps({
			"status": 410,
			"data": [],
			"error": "Bad request"
		})
