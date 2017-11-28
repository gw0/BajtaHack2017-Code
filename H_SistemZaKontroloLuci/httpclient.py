import json
import time
file = open("./piUp.json", encoding="utf-8")
#branje iz datoteke
print("Prebrano iz datoteke/poslano strezniku: ")
jsonFile = json.load(file)
print(jsonFile)
file.close()

import requests
started = time.time()
r = requests.post('http://192.168.0.120:30005/updateConf', json=jsonFile, verify=False)
#r = requests.get('http://192.168.0.120:30005/status',verify=False)
finish = time.time() - started
print("Odgovor streznika: ")
#response = r.text
#response = json.loads(r.json())
#print(r.json())
print(r.text)
response = r.json()
#print(response["status"],"status code:"+str(r.status_code))
print("configured:" + response["configured"])
print(response)
print("Trajalo je: "+str(round(finish,3))+"s")

