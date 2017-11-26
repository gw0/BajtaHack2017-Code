import REQUEST
import time
import requests


def humidity(s):
    a = int(s, 16)
    return (a / (1<<16)) * 100.0

def temp(s):
    a = int(s, 16)
    return (a / (1<<16)) * 165.0 - 40.0

def sensor_init():
    REQUEST.POST('https://f3.srm.bajtahack.si:48300/phy/i2c/alloc', '1')
    REQUEST.POST('https://f3.srm.bajtahack.si:48300/phy/i2c/1/slaves/alloc', '64')
    REQUEST.POST('https://f3.srm.bajtahack.si:48300/phy/gpio/alloc', '2')
    REQUEST.PUT('https://f3.srm.bajtahack.si:48300/phy/gpio/2/cfg/value', '{"dir":"out","mode":"floating","irq":"none","debouncing":0}')
    REQUEST.POST('https://f3.srm.bajtahack.si:48300/phy/gpio/alloc', '24')

def read_temperature():
    REQUEST.PUT('/phy/i2c/1/slaves/64/value', '"00"')
    return temp(REQUEST.GET('/phy/i2c/1/slaves/64/value').text)

def read_humidity():
    REQUEST.PUT('/phy/i2c/1/slaves/64/value', '"01"')
    return humidity(REQUEST.GET('/phy/i2c/1/slaves/64/value').text)

last_send = time.time()

sensor_init()
while 1:
    last = int(REQUEST.GET("/phy/gpio/24"))
    if(last==0):
        opn=time.time()
        REQUEST.PUT("/phy/gpio/2","0")
    time.sleep(0.2)
    if(time.time() - opn > 30):
        REQUEST.PUT("/phy/gpio/2","1")

    if time.time() > last_send + 30:
        requests.post('http://pohladi.ga/api/measurement', json={'temp': read_temperature(), 'humidity': read_humidity()})
