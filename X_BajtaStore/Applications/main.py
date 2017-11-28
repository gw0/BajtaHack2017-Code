from temperature_humidity import *
from return_json import *
from motion_sensor import *
from postData import *

import srmlib_ublox as srmlib
import time

from paramiko import SSHClient
import paramiko

#Define constants.
DEVICE_URL = "https://x1.srm.bajtahack.si:25100"
https_check = srmlib.HTTPS_BASIC
verbose = True

I2C_PORT = 1
HDC_ADDRESS = 0x40
LUMNINOSITY_ADDRESS = 0x29
HDC_TEMP_REGISTER = '"00"'
HDC_HUMI_REGISTER = '"01"'

DEVICE_ID = '1'

flag = 0
MOTION_SENSOR_PIN = 23

humidity_value = 20
temperature_value = 20
motion_value = 0

# Reset configuration just in case
print("\nRebooting...")
#client.reboot(wait=True)


client = srmlib.SRMClient(url=DEVICE_URL, https_check=https_check, verbose=verbose)


#communication I2C
#initI2c(I2C_PORT)
#initI2cSlave(I2C_PORT, HDC_ADDRESS)
#setDatasize(I2C_PORT, HDC_ADDRESS, 2)

#communication Motion sensor
#allocatePin(MOTION_SENSOR_PIN)
#configPin(MOTION_SENSOR_PIN)


while 1:
    try:
        temperature_value = refreshTemperature(I2C_PORT, HDC_ADDRESS, HDC_TEMP_REGISTER)
    except:
        print("Error: getting temperature_value")

    if (int(temperature_value) >= 22 and flag == 0):
        host="x1.srm.bajtahack.si"
        user="root"
        password = "xenon25"
        client = SSHClient()
        client.load_system_host_keys()
        client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        client.connect(host, username=user, password=password)
        stdin, stdout, stderr = client.exec_command('python /home/pi/sendSMS.py ' + str(temperature_value))
        print("stderr: ", stderr.readlines())
        print("pwd: ", stdout.readlines())
        flag = 1

    if (int(temperature_value) < 22):
        flag = 0

    try:
        humidity_value = refreshHumidity(I2C_PORT, HDC_ADDRESS, HDC_HUMI_REGISTER)
    except:
        print("Error: getting humidity_value")

    try:
        motion_value = refreshMotionSensor(MOTION_SENSOR_PIN)
    except:
        print("Error: getting motion_value")

    print("Temp:", temperature_value, "Vlaga:", humidity_value, "Gibanje:", motion_value)

    #add data to JSON format to send to MySQL server
    dataJson = dataToJSON(temperature_value, humidity_value, motion_value)

    try:
        postData(DEVICE_ID, dataJson)
        print('post')
    except:
        print("Error posting data.")

    time.sleep(1)
