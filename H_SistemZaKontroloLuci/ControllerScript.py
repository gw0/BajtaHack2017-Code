import json
import time
import os
import requests
from http.server import BaseHTTPRequestHandler, HTTPServer

configured = False

lights = {}

url = 'https://localhost:55100/phy/'

def getInValue(Gid):
    #predpostavljamo, da je gpio ze inicializiran
    try:
        x = requests.get(url + 'gpio/' + Gid + '/value', verify=False)
        return int(x.text)
    except Exception as e:
        print(e)

def ifAlloc(Gid):
    try:
        r = requests.post(url + 'gpio/alloc', Gid, verify=False)
        return 1
    except Exception as e:
        print("Error " + e + " ,status code:" + str(r.status_code))
        return 0

def onStart(Gstates):
    global lights
    global configured
    toWrite = Gstates

    for key in Gstates:
        print(key)
        #inicializacija in GPiO-tov
        if(Gstates[key][1] == "in"):
            try:
                r = requests.post(url+'gpio/alloc',str(key),verify=False)
            except Exception as e:
                print("Error "+e+" ,status code:"+str(r.status_code))
            b = requests.put(url+'gpio/' + str(key) + '/cfg/value','{"dir":'+str(Gstates[key][1])+',"mode":"floating","irq":"none","debouncing":0}', verify=False)
            print(b.status_code, b.text)
            #branje iz vhoda
            x = getInValue(key)
            print(x)
            toWrite[key][0] = x
        #inicializacija outputov
        else:
            try:
                r = requests.post(url+'gpio/alloc',str(key),verify=False)
            except Exception as e:
                print("Error "+e+" ,status code:"+str(r.status_code))
            b = requests.put(url+'gpio/' + str(key) + '/cfg/value','{"dir":'+str(Gstates[key][1])+',"mode":"floating","irq":"none","debouncing":0}', verify=False)
            print(b.status_code, b.text)
            #nastavljanje posebnega pina
            if(key == "27"):
                temp = abs(Gstates[key][0]-1)
                s = requests.put(url+'gpio/'+str(key)+'value', str(temp), verify=False)
                print(s.text)
            else:
                s = requests.put(url + 'gpio/' + str(key) + 'value', str(Gstates[key][0]), verify=False)
        print(key, Gstates[key])
    file = open('./conf.json', 'w')
    file.write(json.dumps(toWrite))
    file.close()
    configured=True

def delRes(data):
    for key in data:
        d = requests.delete(url+'gpio/'+key+'/alloc','{"dir":'+str(data[key][1])+',"mode":"floating","irq":"none","debouncing":0}', verify=False)
        print(d.status_code)

#-------------------------------------------------------------------------------ShouldWork^


def updateGpio(data):
    global lights
    global configured
    changed = False
    for key in data:
        print(key,"in", data[key])
        if(data[key][1] != "in"):
            #if (key == "27"):
            #   temp = abs(data[key][0] - 1)
            #    s = requests.put(url + 'gpio/' + str(key) + '/value', str(temp), verify=False)
            print(data[key][0])
            print("vklaplam")
            r = requests.put(url + 'gpio/' + key + '/value', str(data[key][0]), verify=False)
            changed = True
        else:
            print("fck")
            #onStart(data)
    return changed


def updateConf(data):
    global lights
    global configured
    print("Updating...")
    gpioNew = data

    for key in data:
        if key in lights:
            lights[key] = gpioNew[key]
    updateGpio(lights)
    '''
    stays = {}
    leaves = {}
    if(configured):
        fileR = open('./conf.json',encoding="utf-8")
        gpioOld = json.load(fileR)
        if("16" in gpioOld):
            r = requests.get(url + 'gpio/16/value', verify=False)
            print(r.text)
            temp = r.text
            gpioOld["16"][0] = temp

        for key in gpioNew:
            if(key in gpioOld and gpioOld[key]!=0):
                stays[key] = gpioOld[key]
            else:
                stays[key] = gpioNew[key]
    else:
        stays = gpioNew
    '''

    #updateGpio(stays)

    saveFile = open("./conf.json", 'w')
    saveFile.write(json.dumps(gpioNew))
    saveFile.close()
    configured=True
    #delRes(leaves)




def updateGstates():
    global lights
    print("updejtano")
    file = open("./conf.json", encoding="utf-8")
    temp = json.load(file)
    file.close()
    #temp["Gstates"]["16"][0] = r
    if("16" in temp):
        r = getInValue("16")
        temp["16"][0] = r
    #updateGpio(temp)
    file2 = open("./conf.json", 'w')
    file2.write(json.dumps(temp))
    file2.close()
    return temp


def switch(command,data):
    global lights
    print("Sem v switchu")
    print(data)
    if(command == "updateConf"):
        try:
            updateConf(data)
            return True
        except Exception as e:
            print(e)

    elif(command == "delRoom"):
        print("zbrisi sobo")
        return True
    else:
        return False


# HTTPRequestHandler class
class httpRequestServer(BaseHTTPRequestHandler):
    # GET
    global configured
    global lights

    def do_GET(self):
        global lights
        global configured
        try:
            print(self.client_address)
            ukaz = self.requestline.split(" ")[1].split("/")[1]
            if(ukaz == "status" and not configured):
                self.send_response(200)
                self.send_header('Content-type', 'text/html')
                self.end_headers()
                # Send message back to client
                response = {"configured":False}
                message = json.dumps(response)
                # Write content as utf-8 data
                self.wfile.write(bytes(message, "utf-8"))
                return
            elif(ukaz == "status" and configured):
                self.send_response(200)
                self.send_header('Content-type', 'text/html')
                self.end_headers()
                # Send message back to client
                message = json.dumps(updateGstates())
                # Write content as utf-8 data
                self.wfile.write(bytes(message, "utf-8"))
                return


            # Send response status code
            self.send_response(400)

            # Send headers
            self.send_header('Content-type', 'text/html')
            self.end_headers()

            # Send message back to client
            message = "Napacni GET ukaz"
            # Write content as utf-8 data
            self.wfile.write(bytes(message, "utf8"))
            return

        except Exception as e:
            print(e)
            self.send_error(404, 'command not found')

    def do_POST(self):
        global lights
        global configured
        ukaz = self.path.split("/")[1]
        print(ukaz)
        #zavrnem v kolikor je samo post in da ni konfiguriran
        if(not configured and ukaz != "updateConf"):
            self.send_response(404, "NO OK")
            self.send_header('Content-type', 'text/html')
            self.end_headers()

            # message = data
            # Write content as utf-8 data
            self.wfile.write(bytes(json.dumps('{"status":"not Configured"}'), "utf-8"))
            return

        #POST
        try:
            print(time.strftime("%Y-%m-%d/%H:%M:%S"))
            length = int(self.headers['Content-length'])
            data = json.loads(self.rfile.read(length).decode("utf-8"))
            print(data)
            if(switch(ukaz,data)):
                self.send_response(200, "OK")
                self.send_header('Content-type', 'text/html')
                self.end_headers()
                print("Switch je dau true")
                # Write content as utf-8 data
                updateGstates()
                self.wfile.write(bytes(json.dumps('{"status":"action succeded"}'),"utf-8"))
                print("updejtano")
                return
            else:
                self.send_response(404, "NO OK")
                self.send_header('Content-type', 'text/html')
                self.end_headers()

                # message = data
                # Write content as utf-8 data
                self.wfile.write(bytes(json.dumps('{"status":"unknown action"}'), "utf-8"))
                return

        except Exception as e:
            print(e.with_traceback())
            self.send_error(404, 'Error1')


def run():
    server_address = ('192.168.0.120', 40005)
    print('Starting Controller on '+server_address[0]+":"+str(server_address[1]))
    global configured
    global lights

    #preveri ce obstaja config
    if(os.path.isfile('./conf.json')):
        configured = True
        file = open('./conf.json', encoding="utf-8")
        config = json.load(file)
        lights = config
        print(config)
        onStart(config)
        file.close()


    # Choose port 8080, for port 80, which is normally used for a http server, you need root access
    httpd = HTTPServer(server_address, httpRequestServer)
    httpd.serve_forever()


run()