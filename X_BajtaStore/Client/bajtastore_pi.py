#!/usr/bin/python

import httplib
import ssl

import socket
import sys

c_pi = httplib.HTTPSConnection("x2.srm.bajtahack.si:25200", context=ssl._create_unverified_context())
sockpi = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

server_address_pi = ('x2.srm.bajtahack.si', 25200) # rajs localhost, sam jebiga zdj
sockpi.connect(server_address_pi)
sockpi = ssl.wrap_socket(sockpi)

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

while True:
    try:
	server_address = ('95.85.34.110', 5002)
	sock.connect(server_address)

	try:
	    id = "IOT x2"
	    sock.sendall(id)
	    print "Connected, waiting..."
	    while True:
	        data = sock.recv(4086)
	        if data:
		    print data
		    data2 = data
	            data = data.split(" ")
		    print data[0]
		    if data[0] == "GET" or data[0] == "PUT":

	            	#c_pi.request(data[0], data[1])
	            	#response = c_pi.getresponse()
			sockpi.send(data2)
			print "SENT"

			#print response.msg
	            	#recvd = str(response.msg) + "\r\n\r\n" + response.read()
			recvd = sockpi.recv(4086)
			print "RECVD"
			print recvd
	            	#if recvd:
	                sock.sendall(recvd)
			#else:
			#    print "NO RESPONSE"

	            	#print response.status
	            	#print response.read()
	finally:
	    sock.close()
    except socket.error:
	pass
