import time
import srmlib_ublox as srmlib
import sqlite3
import datetime

# Parameters for M3
https_check = srmlib.HTTPS_BASIC
verbose = False

conn = sqlite3.connect('Collection.db')

def readTemp(client):
	client.put('/phy/i2c/1/slaves/64/value', data='"00"')
	data = client.get('/phy/i2c/1/slaves/64/value').content
	buf = []
	buf.append(int(data[1:3],16))
	buf.append(int(data[3:5],16))
	# Convert the data
	temp = (buf[0] * 256) + buf[1]
	cTemp = (temp / 65536.0) * 165.0 - 40
	return cTemp

def readHumi(client):
	client.put('/phy/i2c/1/slaves/64/value', data='"01"')
	data = client.get('/phy/i2c/1/slaves/64/value').content
	buf = []
	buf.append(int(data[1:3],16))
	buf.append(int(data[3:5],16))
	# Convert the data
	humidity = (buf[0] * 256) + buf[1]
	humidity = (humidity / 65536.0) * 100.0
	return humidity

def readLum(client):
	client.put('/phy/i2c/1/slaves/41/value', data='"B4"')
	data = client.get('/phy/i2c/1/slaves/41/value').content

def readCo(client):
	data = client.get('/phy/i2c/1/slaves/72/value').content
	return int(data[2:5],16)

def avgCo(arr):
	return sum(arr) / len(arr)



m3 = srmlib.SRMClient(url="https://m3.srm.bajtahack.si:46300", https_check=https_check, verbose=verbose)
m3ceoSamp = []

a = srmlib.SRMClient(url="https://193.2.178.251", https_check=https_check, verbose=verbose)

print("\nWorking...")

ti = datetime.datetime.now()
c = conn.cursor()
#read seansor a#print("Read A!")#print(readTemp(a))#print(readHumi(a))#print(readCo(a))
print("Read M3!")
temp = readTemp(m3)
hum = readHumi(m3)
m3ceoSamp.append(readCo(m3))

if(len(m3ceoSamp) > 10):
	m3ceoSamp.pop(0)

print(avgCo(m3ceoSamp))
c.execute("INSERT INTO data VALUES ('{}',{},{},{},{})".format(ti, temp, hum, avgCo(m3ceoSamp),3))

conn.commit()

conn.close()
