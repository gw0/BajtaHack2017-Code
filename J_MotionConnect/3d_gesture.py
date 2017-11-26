# -*- coding: utf-8 -*-

#  Implementation for raspberry pi written by Matteo Destro for Futura Group srl
#  Reimplemented for orange pi by Kaja Jarm
#  www.Futurashop.it
#  www.open-electronics.org
#
#  BSD license, all text above must be included in any redistribution
#
#  ===========================================================================


from pyA20.gpio import gpio
from pyA20.gpio import port
from pyA20.gpio import connector
import time
from MGC3130_DefVar import *

import smbus
import sys

import orange_pi_control

I2Cbus = smbus.SMBus(0)

Addr_MGC3130       = 0x42	#  MGC3130 hardware address

MGC3130_SDA_LINE   = port.PA12
MGC3130_SCL_LINE   = port.PA11
MGC3130_TS_LINE    = port.PA6
MGC3130_RESET_LINE = port.PG6

led = port.PA12
led1 = port.PA11
led2 = port.PA6

gpio.init()

if False:
    gpio.setcfg(led, gpio.OUTPUT)
    gpio.setcfg(led1, gpio.OUTPUT)
    gpio.setcfg(led2, gpio.OUTPUT)

    while(1):
        gpio.output(led2, 1)
        time.sleep(0.1)
        gpio.output(led2, 0)
        time.sleep(0.6)

_Data = c_ubyte * 256
_i2caddr = 0x42
_Counter = 0
_FirstTelegram = False
_GestInfo = GestureInfo()
_TouchInfo = TouchInfo()
_xyz = Coordinates()
_GestOutput = Gesture()

# ===================================================================
#	Enable/Disable code to debug
EnablePrintMGC3130RawFirmwareInfo = True
EnablePrintMGC3130RawData = True
EnablePrintMGC3130Gesture = True
EnablePrintMGC3130xyz = False


# ===================================================================

# ===================================================================
def MGC3130_SetAdd(Addr):
    global _Data
    _i2caddr = Addr


# ===================================================================

# ===================================================================
def MGC3130_Begin(GPIO, Ts, Rst):
    global _FirstTelegram
    print("MGC3130 initialization in progress...wait")
    gpio.setcfg(Ts, gpio.INPUT)  # MGC3130 TS Line. Input mode
    gpio.pullup(Ts, gpio.PULLUP)  # Enable pull-down
    # GPIO.add_event_detect(Ts, GPIO.FALLING, bouncetime=100)
    gpio.setcfg(Rst, gpio.OUTPUT)  # MGC3130 RESET Line
    gpio.output(Rst, gpio.HIGH)
    gpio.output(Rst, gpio.LOW)
    time.sleep(0.250)  # Delay 250mSec
    gpio.output(Rst, gpio.HIGH)  # Remove Reset MGC3130 Device
    time.sleep(0.250)  # Delay 250mSec
    _FirstTelegram = True
    print("MGC3130 device is ready")


# ===================================================================


# ===================================================================
def MGC3130_ReleaseTsLine(GPIO, Ts):
    # print("Release TS Line")
    gpio.output(Ts, gpio.HIGH)
    time.sleep(0.050)
    gpio.setcfg(Ts, gpio.INPUT)  # MGC3130 TS Line. Input mode
    gpio.pullup(Ts, gpio.PULLUP)  # Enable pull-down


# ===================================================================

# ===================================================================
def MGC3130_GetTsLineStatus(GPIO, Ts):
    if (gpio.input(Ts) == gpio.LOW):
        # print("Get TS Line")
        time.sleep(0.050)
        gpio.setcfg(Ts, gpio.OUTPUT)  # MGC3130 TS Line. Output mode
        gpio.output(Ts, gpio.LOW)
        return True
    else:
        return False


# ===================================================================

# ===================================================================
def MGC3130_GetEvent():
    global EnablePrintMGC3130RawFirmwareInfo
    global _Counter
    global _Data
    global _FirstTelegram
    global _GestInfo
    global _TouchInfo
    global _xyz

    _Counter = 0

    if (_FirstTelegram == True):
        _FirstTelegram = False
        _Data = I2Cbus.read_i2c_block_data(_i2caddr, 0x00, 0x20)
    else:
        _Data = I2Cbus.read_i2c_block_data(_i2caddr, 0x00, 0x1A)

    print _Data

    if (_Data[3] == ID_FW_VERSION):
        if (_Data[4] == 0xAA):
            if (EnablePrintMGC3130RawFirmwareInfo == True):
                MGC3130_PrintMGC3130RawFirmwareInfo()

            print("###################################################")
            print("Valid Library detected")
            print 'Hardware Rev: {}.{}'.format(_Data[5], _Data[6])
            print 'Library Loader Version: {}.{}'.format(_Data[9], _Data[8])
            # ===========================================
            sys.stdout.write("Gestic Library Version: ")
            TempStr = ""
            for i in range(12, 17):
                TempStr = TempStr + chr(_Data[i])
            sys.stdout.write(TempStr + "\n")
            # ===========================================

            # ===========================================
            sys.stdout.write("Platform: ")
            TempStr = ""
            for i in range(20, 31):
                TempStr = TempStr + chr(_Data[i])
            sys.stdout.write(TempStr + "\n")
            # ===========================================
            print("###################################################")
            print("\n")

        else:
            print("Invalid Library detected")

    elif (_Data[3] == ID_DATA_OUTPUT):
        # ===========================================
        # Save Data into internal array
        for i in range(4):
            _GestInfo.GestInfoArray[i] = _Data[i + 10]
            _TouchInfo.TouchInfoArray[i] = _Data[i + 14]

        _GestInfo.GestureInfoLong &= MASK_GESTURE_RAW
        _TouchInfo.TouchInfoLong &= MASK_TOUCH_RAW
        AirWheelInfo = _Data[18]
        for i in range(6):
            _xyz.xyzArray[i] = _Data[i + 20]
            # ===========================================
    else:
        print("Telegram not managed")


# ===================================================================

# ===================================================================
def MGC3130_DecodeGesture():
    global EnablePrintMGC3130RawData
    global EnablePrintMGC3130Gesture
    global EnablePrintMGC3130xyz
    global _GestInfo
    global _TouchInfo
    global _GestOutput
    global LastTouch
    global LastGesture

    Mask = 0x00000001
    if (((_TouchInfo.TouchInfoLong ^ LastTouch) > 0) or ((_GestInfo.GestureInfoLong ^ LastGesture) > 0)):
        if (EnablePrintMGC3130RawData == True):
            MGC3130_PrintMGC3130RawData()
        _GestOutput.GestureLong = 0x00000000
        if ((_TouchInfo.TouchInfoLong ^ LastTouch) > 0):
            LastTouch = _TouchInfo.TouchInfoLong
            for i in range(15):
                if ((_TouchInfo.TouchInfoLong & Mask) > 0):
                    _GestOutput.GestureLong |= Mask
                Mask = Mask << 1
        elif ((_GestInfo.GestureInfoLong ^ LastGesture) > 0):
            LastGesture = _GestInfo.GestureInfoLong
            if (_GestInfo.GestureInfo32Bit.GestureCode == NO_GESTURE):
                print("No Gesture")
            elif (_GestInfo.GestureInfo32Bit.GestureCode == GESTURE_GARBAGE):
                print("Garbage Gesture")
            elif (_GestInfo.GestureInfo32Bit.GestureCode == GESTURE_WEST_EAST):
                if (_GestInfo.GestureInfo32Bit.Edgeflick == 0):
                    _GestOutput.GestureLong |= GESTURE_MASK_WEST_EAST
                else:
                    _GestOutput.GestureLong |= GESTURE_MASK_EDGE_WEST_EAST
            elif (_GestInfo.GestureInfo32Bit.GestureCode == GESTURE_EAST_WEST):
                if (_GestInfo.GestureInfo32Bit.Edgeflick == 0):
                    _GestOutput.GestureLong |= GESTURE_MASK_EAST_WEST
                else:
                    _GestOutput.GestureLong |= GESTURE_MASK_EDGE_EAST_WEST
            elif (_GestInfo.GestureInfo32Bit.GestureCode == GESTURE_SOUTH_NORTH):
                if (_GestInfo.GestureInfo32Bit.Edgeflick == 0):
                    _GestOutput.GestureLong |= GESTURE_MASK_SOUTH_NORTH
                else:
                    _GestOutput.GestureLong |= GESTURE_MASK_EDGE_SOUTH_NORTH
            elif (_GestInfo.GestureInfo32Bit.GestureCode == GESTURE_NORTH_SOUTH):
                if (_GestInfo.GestureInfo32Bit.Edgeflick == 0):
                    _GestOutput.GestureLong |= GESTURE_MASK_NORTH_SOUTH
                else:
                    _GestOutput.GestureLong |= GESTURE_MASK_EDGE_NORTH_SOUTH
            elif (_GestInfo.GestureInfo32Bit.GestureCode == GESTURE_CLOCK_WISE):
                _GestOutput.GestureLong |= GESTURE_MASK_CLOCK_WISE
            elif (_GestInfo.GestureInfo32Bit.GestureCode == GESTURE_COUNTER_CLOCK_WISE):
                _GestOutput.GestureLong |= GESTURE_MASK_COUNTER_CLOCK_WISE

        _GestOutput.GestureLong &= ~(MASK_FILTER_GESTURE)

        if _GestOutput.Gesture32Bit.TapNorth:
            orange_pi_control.turn_led_on(2)
            time.sleep(0.25)
            orange_pi_control.turn_led_on(24)
            time.sleep(0.25)
            orange_pi_control.turn_led_on(25)
            time.sleep(0.25)
            orange_pi_control.turn_led_on(16)
        elif _GestOutput.Gesture32Bit.TapSouth:
            orange_pi_control.turn_led_off(2)
            time.sleep(0.25)
            orange_pi_control.turn_led_off(24)
            time.sleep(0.25)
            orange_pi_control.turn_led_off(25)
            time.sleep(0.25)
            orange_pi_control.turn_led_off(16)

        if (EnablePrintMGC3130Gesture == True):
            MGC3130_PrintMGC3130Gesture()
        if (EnablePrintMGC3130xyz == True):
            MGC3130_PrintMGC3130xyz()


# ===================================================================

# ===================================================================
def MGC3130_PrintMGC3130RawFirmwareInfo():
    global _Data

    RawInfoIndent = "#####################################################################################\nRow Firmware Info from MGC3130 \n"
    HeaderInfo = "Header: "
    PayloadInfo = "Payload: "
    RawInfoCloseIndent = "\n#####################################################################################\n\n";

    if (_Data[3] == ID_FW_VERSION):
        sys.stdout.write(RawInfoIndent)
        # ===========================================
        #	Header
        sys.stdout.write(HeaderInfo)
        for i in range(4):
            MGC3130_SetHexPrintOutput(_Data[i])
        print("\n")
        # ===========================================

        # ===========================================
        #	Payload
        sys.stdout.write(PayloadInfo)
        MGC3130_SetHexPrintOutput(_Data[4])
        sys.stdout.write(" | ")
        # ===========================================

        # ===========================================
        #	HwRev
        for i in range(5, 7):
            MGC3130_SetHexPrintOutput(_Data[i])
        sys.stdout.write(" | ")
        # ===========================================

        # ===========================================
        #	ParameterStartAddr
        MGC3130_SetHexPrintOutput(_Data[7])
        sys.stdout.write(" | ")
        # ===========================================

        # ===========================================
        #	LibraryLoaderVersion
        for i in range(8, 11):
            MGC3130_SetHexPrintOutput(_Data[i])
        sys.stdout.write(" | ")
        # ===========================================

        # ===========================================
        #	FwStartAddr
        MGC3130_SetHexPrintOutput(_Data[11])
        sys.stdout.write(" | ")
        # ===========================================

        # ===========================================
        #	LibraryLoaderVersion
        for i in range(12, len(_Data)):
            MGC3130_SetHexPrintOutput(_Data[i])
        sys.stdout.write(RawInfoCloseIndent)
        # ===========================================


def MGC3130_SetHexPrintOutput(Data):
    if (Data < 0x10):
        sys.stdout.write("0x" + format(Data, "02x") + " ")
    else:
        sys.stdout.write(hex(Data) + " ")


# ===================================================================

# ===================================================================
def MGC3130_PrintMGC3130RawData():
    global _Data

    RawDataIndent = "#####################################################################################\nRow data from MGC3130 \n"
    HeaderRawData = "Header: "
    PayloadRawData = "Payload: "
    RawDataCloseIndent = "\n#####################################################################################\n\n";

    if (_Data[3] == ID_DATA_OUTPUT):
        sys.stdout.write(RawDataIndent)
        # ===========================================
        #	Header
        sys.stdout.write(HeaderRawData)
        for i in range(4):
            MGC3130_SetHexPrintOutput(_Data[i])
        print("\n")
        # ===========================================

        # ===========================================
        #	Payload
        #	DataOutputConfigMask
        sys.stdout.write(PayloadRawData)
        for i in range(4, 6):
            MGC3130_SetHexPrintOutput(_Data[i])
        sys.stdout.write(" | ")
        # ===========================================

        # ===========================================
        #	TimeStamp
        MGC3130_SetHexPrintOutput(_Data[6])
        sys.stdout.write(" | ")
        # ===========================================

        # ===========================================
        #	SystemInfo
        MGC3130_SetHexPrintOutput(_Data[7])
        sys.stdout.write(" | ")
        # ===========================================

        # ===========================================
        #	DSPStatus
        for i in range(8, 10):
            MGC3130_SetHexPrintOutput(_Data[i])
        sys.stdout.write(" | ")
        # ===========================================

        # ===========================================
        #	GestureInfo
        for i in range(10, 14):
            MGC3130_SetHexPrintOutput(_Data[i])
        sys.stdout.write(" | ")
        # ===========================================

        # ===========================================
        #	TouchInfo
        for i in range(14, 18):
            MGC3130_SetHexPrintOutput(_Data[i])
        sys.stdout.write(" | ")
        # ===========================================

        # ===========================================
        #	AirWheelInfo
        for i in range(18, 20):
            MGC3130_SetHexPrintOutput(_Data[i])
        sys.stdout.write(" | ")
        # ===========================================

        # ===========================================
        #	xyzPosition
        for i in range(20, 26):
            MGC3130_SetHexPrintOutput(_Data[i])
        sys.stdout.write(" | ")
        # ===========================================

        sys.stdout.write(RawDataCloseIndent)


# ===================================================================

# ===================================================================
def MGC3130_PrintMGC3130Gesture():
    global _GestOutput

    if (_GestOutput.Gesture32Bit.TouchSouth):
        print ("Touch South")
        print("\n")
    if (_GestOutput.Gesture32Bit.TouchWest):
        print ("Touch West")
        print("\n")
    if (_GestOutput.Gesture32Bit.TouchNorth):
        print ("Touch North")
        print("\n")
    if (_GestOutput.Gesture32Bit.TouchEast):
        print ("Touch East")
        print("\n")
    if (_GestOutput.Gesture32Bit.TouchCentre):
        print ("Touch Centre")
        print("\n")

    if (_GestOutput.Gesture32Bit.TapSouth):
        print ("Tap South")
        print("\n")
    if (_GestOutput.Gesture32Bit.TapWest):
        print ("Tap West")
        print("\n")
    if (_GestOutput.Gesture32Bit.TapNorth):
        print ("Tap North")
        print("\n")
    if (_GestOutput.Gesture32Bit.TapEast):
        print ("Tap East")
        print("\n")
    if (_GestOutput.Gesture32Bit.TapCentre):
        print ("Tap Centre")
        print("\n")

    if (_GestOutput.Gesture32Bit.DoubleTapSouth):
        print ("Double Tap South")
        print("\n")
    if (_GestOutput.Gesture32Bit.DoubleTapWest):
        print ("Double Tap West")
        print("\n")
    if (_GestOutput.Gesture32Bit.DoubleTapNorth):
        print ("Double Tap North")
        print("\n")
    if (_GestOutput.Gesture32Bit.DoubleTapEast):
        print ("Double Tap East")
        print("\n")
    if (_GestOutput.Gesture32Bit.DoubleTapCentre):
        print ("Double Tap Centre")
        print("\n")

    if (_GestOutput.Gesture32Bit.GestWestEast):
        print ("Gesture Flick West to East")
        print("\n")
    if (_GestOutput.Gesture32Bit.GestEastWest):
        print ("Gesture Flick East to West")
        print("\n")
    if (_GestOutput.Gesture32Bit.GestSouthNorth):
        print ("Gesture Flick North to South")
        print("\n")
    if (_GestOutput.Gesture32Bit.GestNorthSouth):
        print ("Gesture Flick South to North")
        print("\n")

    if (_GestOutput.Gesture32Bit.EdgeGestWestEast):
        print ("Gesture Flick Edge West to East")
        print("\n")
    if (_GestOutput.Gesture32Bit.EdgeGestEastWest):
        print ("Gesture Flick Edge East to West")
        print("\n")
    if (_GestOutput.Gesture32Bit.EdgeGestSouthNorth):
        print ("Gesture Flick Edge North to South")
        print("\n")
    if (_GestOutput.Gesture32Bit.EdgeGestNorthSouth):
        print ("Gesture Flick Edge South to North")
        print("\n")

    if (_GestOutput.Gesture32Bit.GestClockWise):
        print ("Gesture Clock Wise")
        print("\n")
    if (_GestOutput.Gesture32Bit.GestCounterClockWise):
        print ("Gesture Counter Clock Wise")
        print("\n")


# ===================================================================

# ===================================================================
def MGC3130_PrintMGC3130xyz():
    global _xyz
    global Last_X
    global Last_Y
    global Last_Z

    if (Last_X != _xyz.xyz.x):
        Last_X = _xyz.xyz.x
        sys.stdout.write("The X coordinate is: ")
        print(_xyz.xInt)
    if (Last_Y != _xyz.xyz.y):
        Last_Y = _xyz.xyz.y
        sys.stdout.write("The Y coordinate is: ")
        print(_xyz.yInt)
    if (Last_Z != _xyz.xyz.z):
        Last_Z = _xyz.xyz.z
        sys.stdout.write("The Z coordinate is: ")
        print(_xyz.zInt)
    print("\n")

# ===================================================================

if __name__  == '__main__':
    MGC3130_SetAdd(Addr_MGC3130)
    MGC3130_Begin(gpio, MGC3130_TS_LINE, MGC3130_RESET_LINE)

    while True:
        # ====================================
        if (MGC3130_GetTsLineStatus(gpio, MGC3130_TS_LINE) == True):
            MGC3130_GetEvent()
            MGC3130_DecodeGesture()
            MGC3130_ReleaseTsLine(gpio, MGC3130_TS_LINE)
