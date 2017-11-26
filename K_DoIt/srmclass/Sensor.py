from Srm import *

class Sensor(Srm):
    def __init__(self, url):
        super(Sensor, self).__init__(url)

    def init_lumniosity(self):
        self.alloc_i2c(1)
        self.alloc_i2c_slave(1, 41)
        self.set_i2c_slave_datasize(1, 41, 4)
        self.set_i2c_slave_value(1, 41, '"A003"')
        self.set_i2c_slave_value(1, 41, '"A111"')
        self.set_i2c_slave_value(1, 41, '"B4"')

    def get_lumniosity(self):
        return self.get_i2c_slave_value(1, 41)

