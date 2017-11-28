import json
import requests
import pprint



def postData(deviceId, data):
    url = "http://95.85.34.110:5000/devices/data/" + deviceId

    data_json = json.dumps(data)
    headers = {'Content-type': 'application/json'}
    response = requests.post(url, data=data_json, headers=headers)
