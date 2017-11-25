from flask import Flask

from web import views


def create_app():
    app = Flask(__name__)
    views.init_views(app)
    return app


app = create_app()
