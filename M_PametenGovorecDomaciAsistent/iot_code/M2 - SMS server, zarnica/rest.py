from flask import Flask, url_for, jsonify
app = Flask(__name__)

@app.route('/')
def api_root():
	return 'Welcome M2'

@app.route('/sendSMS')
def api_checkAccess():

    bashCommand = "sh prepareSMS.sh"
    import subprocess
    process = subprocess.Popen(bashCommand.split(), stdout=subprocess.PIPE)
    tst1 = process.communicate()
    output, error = tst1
    parsed = ((output.split("SMS/"))[-1]).split(" ")[0]

    bashCommand = "sudo mmcli -m 0 -s "+parsed+" --send"
    process = subprocess.Popen(bashCommand.split(), stdout=subprocess.PIPE)
    tst1 = process.communicate()
    output, error = tst1
    
    data = {
        'success'  : True
    }
    resp = jsonify(data)
    resp.status_code = 200
    return resp




if __name__ == '__main__':
	app.run(host="0.0.0.0", port=80)
