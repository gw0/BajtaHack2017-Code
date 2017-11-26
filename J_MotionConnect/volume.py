import applescript
import config
import pymongo


def increase_volume_by_one(device_id):
    client = pymongo.MongoClient(config.mongo_url)
    mongo_db = client[config.mongo_database]
    volume_col = mongo_db[config.mongo_collection_volume]
    current_volume = volume_col.find_one_and_update(
        {'device_id': device_id},
        {'$inc': {'current_volume': 10}},
    return_document=pymongo.ReturnDocument.AFTER)
    vol = current_volume['current_volume']
    if vol > 100:
        current_volume = volume_col.find_one_and_update(
            {'device_id': device_id},
            {'$set': {'current_volume': 100}})
        return 100
    else:
        applescript.AppleScript("set volume output volume {}".format(str(vol))).run()
        return vol


def decrease_volume_by_one(device_id):
    client = pymongo.MongoClient(config.mongo_url)
    mongo_db = client[config.mongo_database]
    volume_col = mongo_db[config.mongo_collection_volume]
    current_volume = volume_col.find_one_and_update(
        {'device_id': device_id},
        {'$inc': {'current_volume': -10}},
    return_document=pymongo.ReturnDocument.AFTER)
    vol = current_volume['current_volume']
    if vol < 0:
        current_volume = volume_col.find_one_and_update(
            {'device_id': device_id},
            {'$set': {'current_volume': 0}})
        return 0
    else:
        applescript.AppleScript("set volume output volume {}".format(str(vol))).run()
        return vol
