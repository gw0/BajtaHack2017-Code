import select
import socket
import sys
import sqlite3
import traceback
import time
import httplib

MAX_DATA = 4096

DATABASE_SELECT = "select * from devices where name = '{0}'"
DATABASE_UPDATE = "UPDATE devices set name = '{0}', status = {1} where name = '{0}'"
DATABASE_INSERT = "INSERT INTO devices (name, status, user_id, app_id) VALUES ('{0}',{1},1,1)"

webpage = httplib.HTTPConnection("localhost:5000")

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.setblocking(0)
sock.setsockopt(socket.SOL_SOCKET,socket.SO_KEEPALIVE,1)

# overrides value (in seconds) shown by sysctl net.ipv4.tcp_keepalive_time
sock.setsockopt(socket.SOL_TCP, socket.TCP_KEEPIDLE, 5)
# overrides value shown by sysctl net.ipv4.tcp_keepalive_probes
sock.setsockopt(socket.SOL_TCP, socket.TCP_KEEPCNT, 4)
# overrides value shown by sysctl net.ipv4.tcp_keepalive_intvl
sock.setsockopt(socket.SOL_TCP, socket.TCP_KEEPINTVL, 1)

server_address = ('95.85.34.110', 5002)
sock.bind(server_address)

sock.listen(5)

inputs = [sock]
outputs = []

devices = {}

def dbase(name, status):
    data = '{{"status":{}}}'.format(status)
    webpage.request("POST", "/devices/status/{}".format(name), data, headers = {'Content-type': 'application/json', 'Accept': 'text/plain'})
    r = webpage.getresponse()
    print r.status
    print data

#dbase("x2", 0)
#exit()

def close_connection(s):
    inputs.remove(s)
    print "CLOSE"
    for name in devices:
        c = devices[name]
        if c == s:
            print "remove " + name
            dbase(name, 0)


def get(connection, message):
    if "x2" in devices:
        print message
        conn = devices["x2"]
        conn.sendall(message)
        data = conn.recv(MAX_DATA)
        connection.send(data)
        close_connection(connection)
        connection.close()

def add_connection(s):
    connection, client_address = s.accept()
    print "connection from", client_address
    inputs.append(connection)

def handle_data(s, data):
        d = data.split(" ")
        if d[0] == "IOT":
            print ""
            print "\tGot: " + d[1]
            print ""
            dbase(d[1], 1)
            devices[d[1]] = s
        else:
            print data
            get(s, data)

def handle_event():
    readable, writable, exceptional = select.select(inputs, outputs, inputs)
    for s in readable:
        if s is sock:
            add_connection(s)
        else:
            try:
                data = s.recv(MAX_DATA)
                if data:
                    handle_data(s, data)
                else:
                    close_connection(s)
            except socket.error:
                close_connection(s)

try:
    while True:
        handle_event()
finally:
    sock.close()
    for s in inputs:
        s.close()
