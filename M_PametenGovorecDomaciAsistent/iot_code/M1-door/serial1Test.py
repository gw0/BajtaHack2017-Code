import serial
import OPi.GPIO as GPIO
import atexit
from time import sleep
import srmlib_ublox as srmlib
import requests
import json

GPIO.setmode(GPIO.BOARD)
GPIO.setup(18, GPIO.OUT)
ser = serial.Serial('/dev/ttyACM0', 9600)

def _json_object_hook(d): return namedtuple('X', d.keys())(*d.values())
def json2obj(data): return json.loads(data, object_hook=_json_object_hook)

def izhod():
        GPIO.cleanup()
        client.delete('/phy/gpio/{}/alloc'.format(led_gpio))

atexit.register(izhod)

# Parameters
url = "https://localhost:46100"
https_check = srmlib.HTTPS_BASIC
verbose = True
led_gpio = 18

# Create SRM manager
client = srmlib.SRMClient(url=url, https_check=https_check, verbose=verbose)

# Reset configuration just in case
#print("\nRebooting...")
#client.reboot(wait=True)

# Initialize LED light controller
print("\nInitialize LED light controller...")
client.post('/phy/gpio/alloc', data=led_gpio)
data = {"dir": "out", "mode": "floating", "irq": "none", "debouncing": 0}
client.put('/phy/gpio/{}/cfg/value'.format(led_gpio), data=data)

led = srmlib.SRMClient(url="https://localhost:46100/phy/gpio/{}/value".format(led_gpio), https_check=https_check, verbose=verbose)


while 1 :
        row = ser.readline()
        row = row.split(":")
        if row[0] == "Card UID":
                id = ((row[-1]).replace(" ", "")).replace("\n", "")
                id = id.replace("0d".decode("hex"), "")
                print(id)
                r = requests.get('http://193.2.176.67:8080/checkAccess/{}'.format(id))
                pod = r.json()
                if pod['allowed']:
                        print("dela")
                        led.put(data=1)
                        sleep(2)
                        led.put(data=0)
