import requests


class Srm:
    def __init__(self, url):
        self.url = url
        r = requests.get(self.url, verify=False)
        print("INIT:", r.status_code, r.reason)

    def alloc_gpio(self, gpio):
        r = requests.post(self.url + "/phy/gpio/alloc", data=str(gpio), verify=False)
        print("GPIO ALLOC:", r.status_code, r.reason)

    def set_gpio_dir(self, gpio, direction="in"):
        r = requests.get(self.url + "/phy/gpio/" + str(gpio) + "/cfg/value", verify=False)
        print("DIR:", r.status_code, r.reason)
        cfg = r.json()
        cfg["dir"] = direction
        r = requests.put(self.url + "/phy/gpio/" + str(gpio) + "/cfg/value", json=cfg, verify=False)
        print("DIR:", r.status_code, r.reason)

    def set_gpio_value(self, gpio, value):
        r = requests.put(self.url + "/phy/gpio/" + str(gpio) + "/value", data=str(value), verify=False)
        print("SET GPIO VALUE", r.status_code, r.reason)

    def get_gpio_value(self, gpio):
        r = requests.get(self.url + "/phy/gpio/" + str(gpio) + "/value", verify=False)
        print("GET GPIO VALUE", r.status_code, r.reason)
        return r.text

    def alloc_i2c(self, i2c):
        r = requests.post(self.url + "/phy/i2c/alloc", data=str(i2c), verify=False)
        print("I2C ALLOC:", r.status_code, r.reason)

    def alloc_i2c_slave(self, i2c, slave):
        r = requests.post(self.url + "/phy/i2c/"+str(i2c)+"/slaves/alloc", data=str(slave), verify=False)
        print("I2C SLAVE ALLOC:", r.status_code, r.reason)

    def get_i2c_slave_value(self, i2c, slave):
        r = requests.get(self.url + "/phy/i2c/" + str(i2c) + "/slaves/"+str(slave)+"/value", verify=False)
        print("GET I2C SLAVE VALUE", r.status_code, r.reason)
        return r.text

    def set_i2c_slave_datasize(self, i2c, slave, size):
        r = requests.put(self.url + "/phy/i2c/" + str(i2c) + "/slaves/" + str(slave) + "/datasize/value", data=str(size), verify=False)
        print("SET_I2C_SLAVE_DATASIZE", r.status_code, r.reason)

    def set_i2c_slave_value(self, i2c, slave, value):
        r = requests.put(self.url + "/phy/i2c/" + str(i2c) + "/slaves/"+str(slave)+"/value", data=value, verify=False)
        print("SET I2C SLAVE VALUE", r.status_code, r.reason)
