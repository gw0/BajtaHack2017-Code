import os, sys
class Config(object):
    DATABASE = os.path.abspath(os.path.dirname(sys.argv[0])) + '\\flaskr.db'
    SECRET_KEY = 'development key'
    USERNAME = 'admin'
    PASSWORD = 'admin'

class DevelopmentConfig(Config):
    DEBUG = True
