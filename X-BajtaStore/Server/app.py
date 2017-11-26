from flask import Flask, render_template, request
from model import *
 
app = Flask(__name__)

@app.route('/')
def home():
	return render_template('home.html')

@app.route('/store')
def store():
	apps_list=select_all_apps()
	return render_template('store.html', apps=apps_list)

@app.route('/apps/<user_id>')
def apps(user_id):
	apps_list=select_all_apps_from_user(user_id)
	return render_template('apps.html', apps=apps_list)

@app.route('/devices/<user_id>')
def devices(user_id):
	devices_list=select_all_devices_from_user(user_id)
	return render_template('devices.html', devices=devices_list)

@app.route('/devices/<user_id>/<device_id>')
def device(user_id, device_id):
	device_object=select_device(device_id)
	device_data_object=select_device_data(device_id)
	return render_template('device.html', device=device_object, device_data=device_data_object)

@app.route('/user/<user_id>')
def user_profile(user_id):
	user_object=select_user(user_id)
	return render_template('user_profile.html', user=user_object)

@app.route('/devices/data/<device_id>', methods=['POST'])
def devices_data(device_id):
	data = request.get_json()
	insert_device_data(device_id, data)
	return "OK"

@app.route('/devices/status/<device_name>', methods=['POST'])
def devices_status(device_name):
	data = request.get_json()
	status = data['status']
	update_device_status(device_name, status)
	return "OK"

	