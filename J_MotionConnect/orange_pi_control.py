import requests
import urllib3
import config
import time
import json

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)


def POST(url, data):
    response = requests.post(url, data=data, verify=False)
    response.raise_for_status()


def GET(url):
    response = requests.get(url, verify=False)
    return response.json()


def PUT(url, data):
    response = requests.put(url, data=data, verify=False)
    response.raise_for_status()


def DELETE(url, data):
    response = requests.put(url, data=data, verify=False)
    response.raise_for_status()


def init_procedure():
    if True:
        POST('{}/phy/gpio/alloc'.format(config.orange_2_url), '2')
        POST('{}/phy/gpio/alloc'.format(config.orange_2_url), '24')
        POST('{}/phy/gpio/alloc'.format(config.orange_2_url), '25')
        POST('{}/phy/gpio/alloc'.format(config.orange_2_url), '16')
    led_init_data = {"dir": "out", "mode": "floating", "irq": "none", "debouncing": 0}
    PUT('{}/phy/gpio/2/cfg/value'.format(config.orange_2_url), json.dumps(led_init_data))
    PUT('{}/phy/gpio/24/cfg/value'.format(config.orange_2_url), json.dumps(led_init_data))
    PUT('{}/phy/gpio/25/cfg/value'.format(config.orange_2_url), json.dumps(led_init_data))
    PUT('{}/phy/gpio/16/cfg/value'.format(config.orange_2_url), json.dumps(led_init_data))
    turn_led_off(2)
    turn_led_off(24)
    turn_led_off(25)
    turn_led_off(16)


def turn_led_on(gpio_pin):
    PUT('{}/phy/gpio/{}/value'.format(config.orange_2_url, str(gpio_pin)), '1')

def turn_led_off(gpio_pin):
    PUT('{}/phy/gpio/{}/value'.format(config.orange_2_url, str(gpio_pin)), '0')

def i2c_3d():
    if False:
        POST('{}/phy/i2c/alloc'.format(config.orange_2_url), '1')
        POST('{}/phy/i2c/1/slaves/alloc'.format(config.orange_2_url), '72')
        PUT('{}/phy/i2c/1/slaves/72/datasize/value'.format(config.orange_2_url), '2')

    response = GET('{}/phy/i2c/1/slaves/72/value'.format(config.orange_2_url))
    print response
    temphi = int(response[0:2], 16)
    templo = int(response[2:4], 16)
    print temphi
    print templo
    result = (temphi << 8) | (templo & 0xFF)

    print result

    return response

if __name__ == '__main__':
    init_procedure()
