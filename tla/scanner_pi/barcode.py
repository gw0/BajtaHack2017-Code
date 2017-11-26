import requests
from lxml import html
import subprocess
from RPi.GPIO import GPIO
import time

channel=23
GPIO.setup(channel, GPIO.IN, pull_up_down=GPIO.PUD_UP)


def submit_barcode():
    subprocess.call(["raspistill", '-o', 'slika.jpg'])
    r = requests.post('http://zxing.org/w/decode', files = { 'file': ('koda.png', koda, 'image/png')})

    if (r.text.split("<title>")[1].split("</title>")[0] == "Decode Succeeded"):
        print(r.text.split("Raw text</td><td><pre>")[1].split("</pre>")[0])
        return r.text.split("Raw text</td><td><pre>")[1].split("</pre>")[0]
    else:
        print("slaba slika")




while 1:
    if GPIO.input(channel) == GPIO.LOW:
        barcode = submit_barcode()
        r = requests.post('http://pohladi.ga/api/barcode', json={'barcode': barcode})
    time.sleep(0.01)



