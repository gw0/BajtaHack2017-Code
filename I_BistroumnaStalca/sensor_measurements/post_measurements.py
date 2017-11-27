#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Demo example of using the SRM module with a LED light and button in Python.

.. note:: For more information see `./README.pdf`.
"""

import time
import srmlib_ublox as srmlib
import requests

# Parameters
url = "https://i1.srm.bajtahack.si:42100"
https_check = srmlib.HTTPS_BASIC
verbose = True


# Create SRM manager
client = srmlib.SRMClient(url=url, https_check=https_check, verbose=verbose)

# Reset configuration just in case
#print("\nRebooting...")
client.reboot(wait=True)


# Initialize temp sensor
print("\nAllocate device")
client.post('/phy/i2c/alloc', '1')

print("\nAllocate slaves")
client.post('/phy/i2c/1/slaves/alloc', '64')

print ("\nSet datasize")
client.put('/phy/i2c/1/slaves/64/datasize/value', '2')


#initialize lum sensor
print("\nAllocate slaves")
client.post('/phy/i2c/1/slaves/alloc', '41')

print ("\nSet datasize")
client.put('/phy/i2c/1/slaves/41/datasize/value', '4')

client.put('/phy/i2c/1/slaves/41/value', '"A003"')

client.put('/phy/i2c/1/slaves/41/value', '"A111"')

# Perform some work
print("\nWorking...")
while True:
    # config temperature
    client.put('/phy/i2c/1/slaves/64/value', '"00"')

    # read temp state
    temp = -40
    while True:
        temp = client.get('/phy/i2c/1/slaves/64/value')
        b = temp.content.replace('"', '')
        a = bin(int(b, 16))
        temperature = int(a[2:], 2)
        t_calc = (temperature * 165 / 65536) - 40

        if t_calc == -40:
            continue
        else:
            break

    #read humidity
    # config humidity
    # read temp state
    client.put('/phy/i2c/1/slaves/64/value', '"01"')

    hum = 0
    while True:
        hum = client.get('/phy/i2c/1/slaves/64/value')
        b = hum.content.replace('"', '')
        a = bin(int(b, 16))
        hum = int(a[2:], 2)
        hum = (hum*100/65536)

        if hum == 0:
            continue
        else:
            break


    #read lum
    # config temperature
    client.put('/phy/i2c/1/slaves/41/value', '"B4"')
    lum = client.get('/phy/i2c/1/slaves/41/value')
    b = lum.content.replace('"', '')

    a = bin(int(b, 16))
    luminiosity = int(a[2:10], 2)

    #post
    r = requests.post("http://127.0.0.1:5000/measurements", data={'cowID': '9999', 'temperature': t_calc,
                                                                  'humidity': hum, 'luminosity': luminiosity})
    print(r.status_code, r.reason)

    time.sleep(1)
