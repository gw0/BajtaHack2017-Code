import time
import srmlib_ublox as srmlib

#Define constants.
DEVICE_URL = "https://x1.srm.bajtahack.si:25100"
https_check = srmlib.HTTPS_BASIC
verbose = True

client = srmlib.SRMClient(url=DEVICE_URL, https_check=https_check, verbose=verbose)


def allocatePin(gpioNum):
    client.post('/phy/gpio/alloc', data=gpioNum)

def configPin(gpioNum):
    data = {"dir": "in", "mode": "pulldown", "irq": "none", "debouncing": 100}
    client.put('/phy/gpio/{}/cfg/value'.format(gpioNum), data=data)

def readMotionSensor(gpioNum):
    motionSensor_url = srmlib.url_builder(url=DEVICE_URL, path='/phy/gpio/{}/value'.format(gpioNum))
    motion_object = srmlib.SRMClient(url=motionSensor_url, https_check=https_check, verbose=verbose)
    return  motion_object

def refreshMotionSensor(gpioNum):
    motion_object = readMotionSensor(gpioNum)
    motion_state = motion_object.get().content
    return int(motion_state)
