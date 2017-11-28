from Srm import *
from Sensor import *
import math


k1 = Sensor("https://k1.srm.bajtahack.si:30100")
# k1.alloc_gpio(26)
# k1.set_gpio_dir(26, "in")
# print(k1.get_gpio_value(26))
# k1.alloc_i2c(1)
#k1.alloc_i2c_slave(1, 64)
#k1.set_i2c_slave_datasize(1, 41, 16)
# print(k1.get_i2c_slave_value(1, 41))
k1.init_lumniosity()
print(int("0x"+k1.get_lumniosity()[1:-1], 16)*100/(math.pow(16, 8)-1), "%")



