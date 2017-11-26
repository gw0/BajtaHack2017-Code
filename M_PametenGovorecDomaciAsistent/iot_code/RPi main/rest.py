from flask import Flask, url_for, jsonify
import sqlite3
import requests
from flask_cors import CORS
import datetime
app = Flask(__name__)
cors = CORS(app, resources={r"/api/*": {"origins": "*"}})

@app.route('/')
def api_root():
	return 'Welcome'

@app.route('/checkAccess/<uid>')
def api_checkAccess(uid):
    #allowed_ids = ["45C2D665"]

    conn = sqlite3.connect('Collection.db')
    c = conn.cursor()
    for row in c.execute("SELECT CAST(CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END AS BIT) FROM USERS WHERE uid = '{}' AND enabled = 1".format(uid)):
        tmp = row[0]
        #print(tmp)

    conn.commit()    
    
    if tmp:
        allowed = True
    else:
        allowed = False
        r = requests.get('http://m2.srm.bajtahack.si/sendSMS')
        pod = r.json()
        if pod['success']:
                print "SMS poslan"

    ti = datetime.datetime.now()
    c.execute("INSERT INTO RFLOGINS(ts,user,allowed) VALUES ('{}','{}','{}')".format(ti, uid, allowed))

    conn.commit()
    conn.close()
    data = {
        'allowed'  : allowed
    }
    resp = jsonify(data)
    #resp.headers['Link'] = ''
    resp.status_code = 200
    return resp

@app.route('/api/dashboard')
def api_dashboard():
    conn = sqlite3.connect('Collection.db')

    c = conn.cursor()

    
    tmp ={}
    for row in c.execute('SELECT * FROM data ORDER BY ts DESC LIMIT 1'):
        tmp["ts"] = row[0]
        tmp["temp"] = round(row[1], 1)
        tmp["humi"] = round(row[2], 1)
        tmp["nid"] = row[4]

    resp = jsonify(tmp)
    conn.commit()
    conn.close()
    return resp

@app.route('/api/history/<limit>')
def api_history(limit):
    conn = sqlite3.connect('Collection.db')

    c = conn.cursor()

    data = []
    for row in c.execute('SELECT * FROM data LIMIT '+limit):
        tmp ={}
        tmp["ts"] = row[0]
        tmp["temp"] = round(row[1], 1)
        tmp["humi"] = round(row[2], 1)
        tmp["nid"] = row[4]
        data.append(tmp)
        
    resp = jsonify(data)
    conn.commit()
    conn.close()
    return resp


@app.route('/api/loginlogs/<limit>')
def api_loginlogs(limit):
    conn = sqlite3.connect('Collection.db')

    c = conn.cursor()

    data = []
    for row in c.execute('SELECT * FROM rflogins ORDER BY ts DESC LIMIT '+limit):
        tmp ={}
        tmp["ts"] = row[0]
        tmp["user"] = row[1]
        tmp["allowed"] = row[2]
        data.append(tmp)
        
    resp = jsonify(data)
    conn.commit()
    conn.close()
    return resp





if __name__ == '__main__':
	app.run(host="0.0.0.0", port=8080)
