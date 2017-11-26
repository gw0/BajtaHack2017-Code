from flask import Flask, request, jsonify, abort, Response
from functools import wraps
import json

import authentication
import devices
import volume
import lights

app = Flask(__name__)

def requires_auth(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        auth = request.headers.get('Authorization')
        authenticated_user = authentication.authenticate_user(auth)
        if authenticated_user == None:
            return Response(
                'Could not verify your access level for that URL.\n'
                'You have to login with proper credentials', 401,
                {'WWW-Authenticate': 'Basic realm="Login Required"'})
        return f(*args, **kwargs)
    return decorated


@app.route('/')
def index():
    pass


@app.route('/login', methods=['POST'])
def login():
    # todo later proper implementation
    if request.method == 'POST':
        data = request.get_json()
        content = data['id']
        password = data['password']

        response = { 'type' : 'Bearer', 'token' : 'masterToken' }

        return jsonify(response)
    else:
        abort(405)


@app.route('/device/<device_id>', methods=['GET', 'POST'])
@requires_auth
def device(device_id):
    if request.method == 'GET':
        pass
    elif request.method == 'PUT':
        data = request.get_json()
        return jsonify(data)
    elif request.method == 'DELETE':
        pass
    else:
        abort(405)

@app.route('/device', methods=['GET', 'POST'])
@requires_auth
def device_create():
    if request.method == 'GET':
        return jsonify(devices.get_all_devices_for_user(0))
    if request.method == 'POST':
        data = request.get_json()
        return jsonify(data)
    else:
        abort(405)


@app.route('/volumeUp', methods=['GET'])
def volume_up():
    if request.method == 'GET':
        vol = volume.increase_volume_by_one('1')
        return jsonify({'volume': vol})
    else:
        abort(405)


@app.route('/volumeDown', methods=['GET'])
def volume_down():
    if request.method == 'GET':
        vol = volume.decrease_volume_by_one('1')
        return jsonify({'volume': vol})
    else:
        abort(405)


@app.route('/lightsOn', methods=['GET'])
def lights_up():
    if request.method == 'GET':
        lights.lights_on()
        return jsonify({'status': 'OK'})
    else:
        abort(405)


@app.route('/lightsOff', methods=['GET'])
def lights_down():
    if request.method == 'GET':
        lights.lights_off()
        return jsonify({'status': 'OK'})
    else:
        abort(405)


if __name__ == '__main__':
    app.debug = True
    app.run(host='0.0.0.0', port=5000, threaded=True)