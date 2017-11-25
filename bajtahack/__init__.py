from flask import Flask

from bajtahack import views


def create_app():
    app = Flask(__name__)
    views.init_views(app)
    return app


if __name__ == '__main__':
    app = create_app()
    app.run(port=5000)
