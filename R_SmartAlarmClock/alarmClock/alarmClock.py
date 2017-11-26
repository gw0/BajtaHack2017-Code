#!/usr/bin/python3

import json
from datetime import datetime
from time import sleep
import requests

DATA_FILE = "data.json"
ALARM_URL_BASE_START = "https://r2.srm.bajtahack.si:12200/sys/interpreter/"
ALARM_URL_BASE_END = "/value"
ALARM_URL_CMD_OFF = "1"
ALARM_URL_CMD_ON = "2"
ALARM_URL_CMD_ALRM = "6"
ALARM_URL_CMD_RST = "7"

# ALARM_LEN = 2*60
ALARM_LEN = 15

def readData(simple=False):
    with open(DATA_FILE) as f:
        read_data = f.read()
        data = json.loads(read_data)
        today = datetime.now()
        return data

def writeData(data):
    print("Writing to file", data)
    with open(DATA_FILE, 'w') as f:
        f.write(json.dumps(data))

def addAlarm(newAlarm):
    oldD = readData()
    try:
        oldD['alarms'].append(newAlarm)
    except KeyError:
        oldD = {}
        oldD['alarms'] = [ newAlarm ]
    writeData(oldD)

def deleteAlarm(aid):
    oldD = readData()
    try:
        for alarm in oldD['alarms']:
            if alarm['alarm'] == aid:
                oldD['alarms'].remove(alarm)

        writeData(oldD)
    except KeyError as F:
        print(F)
        pass

def triggerAlarm():
    print("Alarm trigger!!!")
    requests.put(ALARM_URL_BASE_START + ALARM_URL_CMD_ON + ALARM_URL_BASE_END,verify=False)
    sleep(1);
    requests.put(ALARM_URL_BASE_START + ALARM_URL_CMD_ALRM + ALARM_URL_BASE_END,verify=False)
    sleep(1);
    requests.put(ALARM_URL_BASE_START + ALARM_URL_CMD_RST + ALARM_URL_BASE_END,verify=False)
    sleep(ALARM_LEN)
    requests.put(ALARM_URL_BASE_START + ALARM_URL_CMD_OFF + ALARM_URL_BASE_END,verify=False)


def main():
    data = readData()
    print(data)
    currTime = datetime.now()

    try:
        for alarm in data['alarms']:
            alrm = datetime.strptime(alarm['alarm'], "%H:%M:%S")
            alrm = alrm.replace(day=currTime.day, month=currTime.month, year=currTime.year)

            if alarm['lastTrigger'] is None:
                diff = (currTime - alrm).total_seconds()
                if diff <= 65 and diff >= 0:
                    triggerAlarm()
                    alarm['lastTrigger'] = currTime.strftime("%Y-%m-%d %H:%M:%S")
                    writeData(data)

            else:
                lstTrig = datetime.strptime(alarm['lastTrigger'], "%Y-%m-%d %H:%M:%S")
                diff = (currTime - alrm).total_seconds()
                if diff <= 65 and diff >= 0 and (currTime - lstTrig).total_seconds() > 60*60*24-1:
                    triggerAlarm()
                    alarm['lastTrigger'] = currTime
                    writeData(data)

    except KeyError:
        print("No alarms at this time")

if __name__ == '__main__':
    main()
