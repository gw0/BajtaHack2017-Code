import pymongo


# todo: proper authentication, but do later
def authenticate_user(token):
    if token == 'Bearer masterToken':
        return 0
    else:
        return None