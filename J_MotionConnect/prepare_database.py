# for adding extra indexes if we will need it

import pymongo

import config

client = pymongo.MongoClient(config.mongo_url)
mongo_db = client[config.mongo_database]
devices = mongo_db[config.mongo_collection_devices]
gestures = mongo_db[config.mongo_collection_gestures]
volume = mongo_db[config.mongo_collection_volume]

devices.create_index([('user_id', pymongo.ASCENDING)])
volume.create_index([('device_id', pymongo.ASCENDING)])

hard_coded_stuff = True

if hard_coded_stuff:
    client.drop_database(config.mongo_database)

    gestures.insert({'_id': '1', 'name': 'approach'})
    gestures.insert({'_id': '2', 'name': 'air_wheel'})
    gestures.insert({'_id': '3', 'name': 'swipe_left'})
    gestures.insert({'_id': '4', 'name': 'swipe_right'})
    gestures.insert({'_id': '5', 'name': 'swipe_up'})
    gestures.insert({'_id': '6', 'name': 'swipe_down'})
    gestures.insert({'_id': '7', 'name': 'hold'})
    gestures.insert({'_id': '8', 'name': 'wave'})

    volume.insert({'_id': '1', 'device_id': '1', 'current_volume': 100})

    devices.insert({
        '_id': '1',
        'user_id': '1',
        'name': 'Living Room Light',
        'used_gestures': [
        ]
    })

    devices.insert({
        '_id': '2',
        'user_id': '1',
        'name': 'Musicbox',
        'used_gestures': [
        ]
    })

