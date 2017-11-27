#!/usr/bin/python

import requests
import time
import select
import socket
from threading import Timer,Thread,Event
import yagmail


server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
serverPort = 2017
server_socket.bind(('', serverPort))
server_socket.listen(1)



alarm_enabled = 0       #0 disabled     1 = enabled
alarm_activated = 0     #0 = off     1 = on
userAtHome = 0          #Number of users at home






class perpetualTimer():

    def __init__(self,t,hFunction):
        self.t=t
        self.hFunction = hFunction
        self.thread = Timer(self.t,self.handle_function)

    def handle_function(self):
        self.hFunction()
        self.thread = Timer(self.t,self.handle_function)
        self.thread.start()

    def start(self):
        self.thread.start()

    def cancel(self):
        self.thread.cancel()








cert='/home/pi/cert.pem'


#devices=[["out","MASTER_POWER","17"],["in","PIR","23"],["out","BUZZER","27"],["out","ALARM_INDICATOR","26"]]
devices=[["out","PIR_LED","1"],["out","MASTER_POWER","17"],["in","PIR","23"],["out","ALARM_INDICATOR","26"]]



def aloc(pin):
    payload = str(pin)
    r = requests.post("https://c1.srm.bajtahack.si:43100/phy/gpio/alloc", data=payload, verify=False)
    #print r.status_code
    #print r.content

def cfg(IO, pin):
    payload = "{\"dir\":\""+str(IO)+"\",\"mode\":\"floating\",\"irq\":\"none\",\"debouncing\":\"0\"}"
    r = requests.put('https://c1.srm.bajtahack.si:43100/phy/gpio/'+str(pin)+'/cfg/value', data=payload, verify=False)
    #print r.status_code
    #print r.content

def smokeSensorAlofAndCfg():
    payload = str(1)
    r = requests.post("https://c1.srm.bajtahack.si:43100/phy/i2c/alloc", data=payload, verify=False)
    #print r.status_code
    #print r.content
    
    payload = str(72)
    r = requests.post("https://c1.srm.bajtahack.si:43100/phy/i2c/1/slaves/alloc", data=payload, verify=False)
    #print r.status_code
    #print r.content
    
    payload = str(2)
    r = requests.post("https://c1.srm.bajtahack.si:43100/phy/i2c/1/slaves/72/datasize/value", data=payload, verify=False)
    #print r.status_code
    #print r.content
   

def readSmokeSensor():   
    s = "error"
    r = requests.get('https://c1.srm.bajtahack.si:43100/phy/i2c/1/slaves/72/value', verify=False)
    #print r.status_code
    s = r.content
    #print s
    return s  


    
def write(name, value):
    for dev in devices:
        if dev[1] == name:
            payload = str(value)
            r = requests.put('https://c1.srm.bajtahack.si:43100/phy/gpio/'+str(dev[2])+'/value', data=payload, verify=False)
            #print r.status_code
            #print r.content
            break

def read(name):
    s = "error"
    for dev in devices:
        if dev[1] == name:
            r = requests.get('https://c1.srm.bajtahack.si:43100/phy/gpio/'+str(dev[2])+'/value', verify=False)
            #print r.status_code
            s = r.content
            print s
            break
    return s   
    

    
    
def setBuzzer(status):
    print "Buzzer: "+str(status)
    payload = str(status)
    r = requests.put('https://c3.srm.bajtahack.si:43300/phy/gpio/27/value', data=payload, verify=False)
    print r.status_code
    print r.content
'''
def getBuzzer():
    s = "error"
    r = requests.get('https://c3.srm.bajtahack.si:43300/phy/gpio/27/value', verify=False)
    print r.status_code
    s = r.content
    print s
    return int(s)  
'''    
    
    
    
        
'''
    for dev in devices:
            aloc(dev[2])
            cfg(dev[0],dev[2])
           
    write(17,1)
    write(26,1)
'''





def sendMail():
    yag = yagmail.SMTP("peska97@gmail.com", 'lidijadugon')
    yag.send("peska97@gmail.com", "Subject", "Alarm!!!")


def thread1():
    global alarm_enabled
    global alarm_activated
    global userAtHome
    
    pirStatus = read("PIR")
    write("PIR_LED",pirStatus)
    
    if alarm_enabled == 1:
        print 
        #print "Alarm check!!!",userAtHome,alarm_activated,pirStatus
        print 
        print
        if userAtHome == 0 and alarm_activated == 0 and int(pirStatus) == 1:
            setBuzzer(1)
            sendMail()
            alarm_activated = 1
    
    smoke_val = int(readSmokeSensor().replace('\"',''), 16)
    print smoke_val
    if smoke_val > 995 and alarm_activated == 0:
        setBuzzer(1)
        sendMail()
        alarm_activated = 1
    '''        
    if alarm_activated == 1:
        print "TOGGLE BUZZER"
        setBuzzer(int(not(getBuzzer())))
    '''
 





t1 = perpetualTimer(1,thread1)
t1.start()





smokeSensorAlofAndCfg()

read_list = [server_socket]
while(1):

    try:
        readable, writable, errored = select.select(read_list, [], [])
        for s in readable:
            if s is server_socket:
                client_socket, address = server_socket.accept()
                read_list.append(client_socket)
                #print "Connection from", address 
            else:
                data = s.recv(1024)
                #print data 
                if data:
                    #s.send(data)
                    
                    
                    '''
                    try:
                        s.send('HTTP/1.0 200 OK\r\n')
                        s.send('Access-Control-Allow-Origin: *\r\n')
                        s.send("Content-Type: text/html\r\n\r\n")
                        s.send('<html><body>ok</body></html>')
                    
                    except Exception, e:
                        logging.warning("SENDING ERROR: {0}".format(e))
                    '''
                    
                    
                    try:
                        data = data.split('\n')[0]
                        
                        if "|" in data:
                            raw_data = data.split('|')
                        elif "%7C" in data:
                            raw_data = data.split('%7C')

                        data = ""
                        
                        print raw_data
                        html = "-"
                        
                        if len(raw_data) > 0:    
                            for x in range(1,len(raw_data)-1):
                            
                                TCPdata = (raw_data[x]).split('=')
                                if TCPdata[0] == "action" and TCPdata[1] == "write":
                                    TCPdata_ = (raw_data[x+1]).split('=')
                                    print "TCPdata_: " , TCPdata_
                                    for dev in devices:
                                        if TCPdata_[0] in dev[1]:
                                            write(TCPdata_[0],TCPdata_[1])
                                    html = "OK"

                                if TCPdata[0] == "action" and TCPdata[1] == "read":
                                    TCPdata_ = (raw_data[x+1]).split('=')
                                    print "TCPdata_: " , TCPdata_
                                    for dev in devices:
                                        if TCPdata_[0] in dev[1]:
                                            html = read(TCPdata_[0])
                                
                                if TCPdata[0] == "alarm" and TCPdata[1] == "disable":
                                    alarm_enabled = 0
                                    alarm_activated = 0
                                    setBuzzer(0)
                                    html = "alarm disbled"
                                if TCPdata[0] == "alarm" and TCPdata[1] == "enable":
                                    alarm_enabled = 1
                                    html = "alarm enabled"
                                  
                                #if TCPdata[0] == "users" and TCPdata[1] == "enable":
                                
                                
                                print "TCP RX:" , TCPdata
                        

                        
                        
                    except Exception, e:
                        print("DATA PARSING ERROR: {0}".format(e))
                        
                    try:
                        s.send('HTTP/1.0 200 OK\r\n')
                        s.send('Access-Control-Allow-Origin: *\r\n')
                        s.send("Content-Type: text/html\r\n\r\n")
                        #s.send('<html><body>'+str(fb.replace("\n","<br>"))+'</body></html>')
                        s.send(html)
                        
                        s.close()
                        read_list.remove(s)
                    
                    except Exception, e:
                        print("SENDING ERROR: {0}".format(e))
                    

    except KeyboardInterrupt:
        print 'Interrupted!'
        break


