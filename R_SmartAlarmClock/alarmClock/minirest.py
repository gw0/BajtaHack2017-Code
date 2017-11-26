from flask import Flask, jsonify, request
from datetime import datetime
import alarmClock
from flask_cors import CORS


app = Flask(__name__)
CORS(app)

@app.route("/", methods=['GET', 'PUT'])
def allAlarms():
    if request.method == 'GET':
        return jsonify(alarmClock.readData(True))

    elif request.method == 'PUT':
        try:
            nDate = datetime.now()
            newA = { 'alarm': request.form['time'], 'lastTrigger': None }
            alarmClock.addAlarm(newA)
            print(newA)
        except IndexError:
            return jsonify({ 'error': "Bad request!" })

        return jsonify({ 'msg': "Success!" })

@app.route("/<aid>", methods=['DELETE'])
def delAlarm(aid):
    aid = aid.replace('-',":")
    alarmClock.deleteAlarm(aid)

    return jsonify({ 'msg': "Success, i think!?" })

if __name__ == '__main__':
   app.run('0.0.0.0',ssl_context='adhoc')
