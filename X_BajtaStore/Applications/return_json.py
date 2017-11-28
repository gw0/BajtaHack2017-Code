import json
from datetime import datetime


def dataToJSON(temperature_value, humidity_value, motion_value):
    time_stamp = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    dataDictionary = {'time': time_stamp ,'temperature': temperature_value , 'humidity':humidity_value, 'motion': motion_value}
    dataJson = json.dumps(dataDictionary)

    return dataJson
