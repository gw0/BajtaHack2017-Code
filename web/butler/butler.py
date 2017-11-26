"""Module for intelligent monitoring and control of lighting systems."""

import time
from web.butler import srmlib_ublox as srmlib  # pylint: disable=import-error

# Lux sensor flags
VISIBLE = 2  # channel 0 - channel 1
INFRARED = 1  # channel 1
FULLSPECTRUM = 0  # channel 0

ADDR = 0x29
READBIT = 0x01
COMMAND_BIT = 0xA0  # bits 7 and 5 for 'command normal'
CLEAR_BIT = 0x40  # Clears any pending interrupt (write 1 to clear)
WORD_BIT = 0x20  # 1 = read/write word (rather than byte)
BLOCK_BIT = 0x10  # 1 = using block read/write
ENABLE_POWERON = 0x01
ENABLE_POWEROFF = 0x00
ENABLE_AEN = 0x02
ENABLE_AIEN = 0x10
CONTROL_RESET = 0x80
LUX_DF = 408.0
LUX_COEFB = 1.64  # CH0 coefficient
LUX_COEFC = 0.59  # CH1 coefficient A
LUX_COEFD = 0.86  # CH2 coefficient B

REGISTER_ENABLE = 0x00
REGISTER_CONTROL = 0x01
REGISTER_THRESHHOLDL_LOW = 0x02
REGISTER_THRESHHOLDL_HIGH = 0x03
REGISTER_THRESHHOLDH_LOW = 0x04
REGISTER_THRESHHOLDH_HIGH = 0x05
REGISTER_INTERRUPT = 0x06
REGISTER_CRC = 0x08
REGISTER_ID = 0x0A
REGISTER_CHAN0_LOW = 0x14
REGISTER_CHAN0_HIGH = 0x15
REGISTER_CHAN1_LOW = 0x16
REGISTER_CHAN1_HIGH = 0x17
INTEGRATIONTIME_100MS = 0x00
INTEGRATIONTIME_200MS = 0x01
INTEGRATIONTIME_300MS = 0x02
INTEGRATIONTIME_400MS = 0x03
INTEGRATIONTIME_500MS = 0x04
INTEGRATIONTIME_600MS = 0x05

GAIN_LOW = 0x00  # low gain (1x)
GAIN_MED = 0x10  # medium gain (25x)
GAIN_HIGH = 0x20  # medium gain (428x)
GAIN_MAX = 0x30  # max gain (9876x)

# Parameters
CLIENT_LIGHT_URL = "https://o3.srm.bajtahack.si:20300"
RELAY_GPIO = "26"
LIGHT_GPIO = "18"

CLIENT_SRM_URL = "https://o1.srm.bajtahack.si:20100"
PIR_GPIO = "23"
LUX_I2C = "1"
LUX_I2C_SLAVE = "41"

HTTPS_CHECK = srmlib.HTTPS_BASIC
VERBOSE = False
NEEDS_RESET = True
LIGHTING_MODE = "auto"

CLIENT_LIGHT = srmlib.SRMClient(
    url=CLIENT_LIGHT_URL,
    https_check=HTTPS_CHECK,
    verbose=VERBOSE)
CLIENT_SRM = srmlib.SRMClient(
    url=CLIENT_SRM_URL,
    https_check=HTTPS_CHECK,
    verbose=VERBOSE)


def init_light():
    """Initialize light relay and status."""
    # Reboot
    print("Rebooting light platform...")
    CLIENT_LIGHT.reboot(wait=True)

    # Light Relay
    print("Initializing relay...")
    CLIENT_LIGHT.post('/phy/gpio/alloc', data=RELAY_GPIO)
    data = {"dir": "out", "mode": "floating", "irq": "none", "debouncing": 0}
    CLIENT_LIGHT.put('/phy/gpio/' + RELAY_GPIO + '/cfg/value', data=data)

    # Light Status
    print("Initializing light status...")
    CLIENT_LIGHT.post('/phy/gpio/alloc', data=LIGHT_GPIO)
    data = {"dir": "in", "mode": "pullup", "irq": "none", "debouncing": 0}
    CLIENT_LIGHT.put('/phy/gpio/' + LIGHT_GPIO + '/cfg/value', data=data)


def init_srm():
    """Initialize srm platform."""
    # Reboot
    print("Rebooting SRM platform...")
    CLIENT_SRM.reboot(wait=True)

    # Motion detector
    print("Initializing PIR sensor...")
    CLIENT_SRM.post('/phy/gpio/alloc', data=PIR_GPIO)
    data = {"dir": "in", "mode": "floating", "irq": "none", "debouncing": 0}
    CLIENT_SRM.put('/phy/gpio/' + PIR_GPIO + '/cfg/value', data=data)

    # Luminosity
    CLIENT_SRM.post('/phy/i2c/alloc', data=LUX_I2C)
    CLIENT_SRM.post('/phy/i2c/' + LUX_I2C +
                    '/slaves/alloc', data=LUX_I2C_SLAVE)

    CLIENT_SRM.put('/phy/i2c/' + LUX_I2C +
                   '/slaves/' + LUX_I2C_SLAVE +
                   '/datasize/value', data='4')
    # Enable power
    CLIENT_SRM.put('/phy/i2c/' + LUX_I2C +
                   '/slaves/' + LUX_I2C_SLAVE +
                   '/value', data='"A003"')
    # Configuration register
    lux_conf = ('"' +
                hex(COMMAND_BIT | REGISTER_CONTROL)[2:].upper() +
                hex(INTEGRATIONTIME_100MS | GAIN_MED)[2:].upper() +
                '"')
    print(lux_conf)
    CLIENT_SRM.put('/phy/i2c/' + LUX_I2C +
                   '/slaves/' + LUX_I2C_SLAVE +
                   '/value', data=lux_conf)


if NEEDS_RESET:
    init_light()
    init_srm()

print("Construct relay object...")
RELAY_URL = srmlib.url_builder(
    url=CLIENT_LIGHT_URL,
    path='/phy/gpio/{}/value'.format(RELAY_GPIO))
RELAY = srmlib.SRMClient(
    url=RELAY_URL,
    https_check=HTTPS_CHECK,
    verbose=VERBOSE)


def switch_light_relay():
    """Switch the state of the light relay."""
    RELAY.put(data=str(1 - int(RELAY.get().content)))


print("Construct light object...")
LIGHT_URL = srmlib.url_builder(
    url=CLIENT_LIGHT_URL,
    path='/phy/gpio/' + LIGHT_GPIO + '/value')
LIGHT = srmlib.SRMClient(
    url=LIGHT_URL,
    https_check=HTTPS_CHECK,
    verbose=VERBOSE)

print("Construct PIR object...")
PIR_URL = srmlib.url_builder(
    url=CLIENT_SRM_URL,
    path='/phy/gpio/' + PIR_GPIO + '/value')
PIR = srmlib.SRMClient(
    url=PIR_URL,
    https_check=HTTPS_CHECK,
    verbose=VERBOSE)

print("Construct lux object...")
LUX_URL = srmlib.url_builder(
    url=CLIENT_SRM_URL,
    path=('/phy/i2c/' + LUX_I2C +
          '/slaves/' + LUX_I2C_SLAVE +
          '/value'))
LUX = srmlib.SRMClient(
    url=LUX_URL,
    https_check=HTTPS_CHECK,
    verbose=VERBOSE)


def get_light_status():
    return 1 - int(LIGHT.get().content)


def get_pir_status():
    return int(PIR.get().content)


# print("\nWorking...")
# lux_avg = -1  # pylint: disable=invalid-name
# while True:
#     LIGHT_GET = get_light_status()
#     PIR_GET = get_pir_status()
#
#     # Lux (WIP)
#     LUX_ENABLE = ('"' +
#                   hex(COMMAND_BIT | REGISTER_ENABLE)[2:].upper() +
#                   hex(ENABLE_POWERON | ENABLE_AEN | ENABLE_AIEN)[2:].upper() +
#                   '"')
#     CLIENT_SRM.put('/phy/i2c/' + LUX_I2C +
#                    '/slaves/' + LUX_I2C_SLAVE +
#                    '/value', data=LUX_ENABLE)
#
#     LUX_CH0_CONF = ('"' +
#                     hex(COMMAND_BIT | REGISTER_CHAN0_LOW)[2:].upper() +
#                     '"')
#     CLIENT_SRM.put('/phy/i2c/' + LUX_I2C +
#                    '/slaves/' + LUX_I2C_SLAVE +
#                    '/value', data=LUX_CH0_CONF)
#     LUX_GET_FULL = int(LUX.get().content[1:-1][:4], base=16)
#
#     LUX_CH1_CONF = ('"' +
#                     hex(COMMAND_BIT | REGISTER_CHAN1_LOW)[2:].upper() +
#                     '"')
#     CLIENT_SRM.put('/phy/i2c/' + LUX_I2C +
#                    '/slaves/' + LUX_I2C_SLAVE +
#                    '/value', data=LUX_CH1_CONF)
#     LUX_GET_IR = int(LUX.get().content[1:-1][:4], base=16)
#
#     LUX_GET_VISIBLE = LUX_GET_FULL - LUX_GET_IR
#
#     LUX_NUM = LUX_GET_VISIBLE
#     # pylint: disable=invalid-name
#     lux_avg = LUX_NUM if lux_avg == -1 else (LUX_NUM + lux_avg * 10) / 11
#
#     # Print states
#     print("---------------------------------")
#     print("Light state: {}".format(LIGHT_GET))
#     print("  PIR state: {}".format(PIR_GET))
#     print("  lux state: {}".format(LUX_NUM))
#     print("  lux state: {}".format(lux_avg))
#
#     # Is light switch needed
#     MOTION_SWITCH = LIGHT_GET != PIR_GET
#     LUX_SWITCH = False  # TODO
#     if LIGHTING_MODE == "auto" and (MOTION_SWITCH or LUX_SWITCH):
#         switch_light_relay()
#
#     time.sleep(0.3)
