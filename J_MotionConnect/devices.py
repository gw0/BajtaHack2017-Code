import pymongo

import config

def get_possible_gestures():
    client = pymongo.MongoClient(config.mongo_url)
    mongo_db = client[config.mongo_database]
    gestures = mongo_db[config.mongo_collection_gestures]

    saved_gestures = gestures.find({})
    all_gestures = []
    for gesture in saved_gestures:
        all_gestures.append(gesture)

    return all_gestures


def get_all_devices_for_user(user_id, test=False):
    if test:
        client = pymongo.MongoClient(config.mongo_url)
        mongo_db = client[config.mongo_database]
        devices = mongo_db[config.mongo_collection_devices]

        results_devices = devices.find({'user_id'})


    device_json = {
        'device_list': [
            {
                'device_id': '1234',
                'device_name': 'music',
                'available_gestures':
                [
                    {
                        'gesture_id':'1',
                        'gesture_name': 'swipe up'
                    }
                ],
                'available_actions':[{
                    'action_id': '1',
                    'action_name':'volume_up'
                    }
                ],
                'used_gestures': [
                    {
                        'gesture_id': '1',
                        'gesture_name': 'swipe up',
                        'gesture_action_id': '2',
                        'gesture_action_name': 'louder'
                    }
                ]
            }
        ]
    }

    return device_json


if __name__ == '__main__':
    get_all_devices_for_user()