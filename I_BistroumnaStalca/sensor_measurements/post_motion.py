#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Demo example of using the SRM module with a LED light and button in Python.

.. note:: For more information see `./README.pdf`.
"""

import time
import srmlib_ublox as srmlib
import struct
import binascii
import pandas as pd
import requests
import numpy as np
import RecognitionMSE

# Parameters
url = "https://i1.srm.bajtahack.si:42100"
https_check = srmlib.HTTPS_BASIC
verbose = True

# Create SRM manager
client = srmlib.SRMClient(url=url, https_check=https_check, verbose=verbose)

# # Reset configuration just in case
# #print("\nRebooting...")
# client.reboot(wait=True)
#
# # Initialize temp sensor
# print("\nAllocate device")
# client.post('/phy/i2c/alloc', '1')

# Initialize
print("\nAllocate device")
client.post('/phy/gpio/alloc', '23')

# define as output
client.put('/phy/gpio/' + '23' + '/cfg/value', '{"dir":"in","mode":"pulldown","irq":"none","debouncing":0}')

n = 10
povprecje_sample = np.zeros(n)
# Perform some work
print("\nWorking...")
lum_prev = 0
idx = 0

while True:
    lum_curr = int(client.get('/phy/gpio/' + '23' + '/value').content)
    povprecje_sample[idx % n] = lum_curr
    time.sleep(1)
    idx += 1

    if np.mean(povprecje_sample) > 0.5:
        stanje_krave = True
        print "tu sem"
        # read cow number
        cowID = RecognitionMSE.DoRecognition()
        r = requests.post("http://127.0.0.1:5000/measurements", data={'cowID': cowID, 'milk': np.random.rand(),
                                                                      'food': np.random.rand()})




