import srmlib_ublox as srmlib

#Define constants.
DEVICE_URL = "https://x1.srm.bajtahack.si:25100"
https_check = srmlib.HTTPS_BASIC
verbose = True

client = srmlib.SRMClient(url=DEVICE_URL, https_check=https_check, verbose=verbose)

'''
 * Allocate new I2C port.
 *
 * @param {Numeric} portNumber Number of I2C port.
 * @return Promise with HTTP POST function.
'''
def initI2c(portNumber):
    client.post('/phy/i2c/alloc', data=portNumber)

'''
 * Allocate new I2C slave node on given port.
 *
 * @param {Numeric} portNumber Number of I2C port.
 * @param {Numeric} slaveAddress I2C slave address.
 * @return Promise with HTTP POST function.
'''
def initI2cSlave(portNumber, slaveAddress):
    client.post('/phy/i2c/{}/slaves/alloc'.format(portNumber), data=slaveAddress)

'''
 * Set datasize for an I2C slave node.
 *
 * @param {Numeric} portNumber Number of I2C port.
 * @param {Numeric} slaveAddress I2C slave address.
 * @param {Numeric} datasize I2C datasize parameter in bytes.
 * @return Promise with HTTP PUT function.
'''
def setDatasize(portNumber, slaveAddress, datasize):
    client.put('/phy/i2c/{}/slaves/{}/datasize/value'.format(portNumber, slaveAddress), data=datasize)

'''
 * Select register for read operation by writing its address.
 *
 * @param {Numeric} portNumber Number of I2C port.
 * @param {Numeric} slaveAddress I2C slave address.
 * @param {String} register Hexadecimal string address of a register.
 * @return Promise with HTTP PUT function.
'''
def selectRegister(portNumber, slaveAddress, register):
    client.put('/phy/i2c/{}/slaves/{}/value'.format(portNumber, slaveAddress), data=register)

'''
 * Read value from currently selected register.
 *
 * @param {Numeric} portNumber Number of I2C port.
 * @param {Numeric} slaveAddress I2C slave address.
 * @return Promise with HTTP GET function. Returns register value (hexadecimal string) on success.
'''
def readRegister(portNumber, slaveAddress):
    value_url = srmlib.url_builder(url=DEVICE_URL, path='/phy/i2c/{}/slaves/{}/value'.format(portNumber,slaveAddress))
    value = srmlib.SRMClient(url=value_url, https_check=https_check, verbose=verbose)
    return value

'''
 * Acquire fresh temperature value from sensor and set it in component.
'''
def refreshTemperature(portNumber, slaveAddress, temperature_register):
    selectRegister(portNumber, slaveAddress, temperature_register)
    temperature_object = readRegister(portNumber, slaveAddress)
    temperature_raw =temperature_object.get().content
    temperature_raw = str(temperature_raw)
    temperature_raw = temperature_raw[3:7]
    temperature = round((((int(temperature_raw, 16) / 65536) * 165) - 40), 2)
    #print('Temperature:', temperature)
    return float(temperature)

'''
 * Acquire fresh humidity value from sensor and set it in component.
'''
def refreshHumidity(portNumber, slaveAddress, humidity_register):
    selectRegister(portNumber, slaveAddress, humidity_register)
    humidity_object = readRegister(portNumber, slaveAddress)
    humidity_raw = humidity_object.get().content
    humidity_raw = str(humidity_raw)
    humidity_raw = humidity_raw[3:7]
    humidity = round(((int(humidity_raw, 16) / 65536) * 100), 2)
    #print('Humidity:', humidity)
    return float(humidity)
