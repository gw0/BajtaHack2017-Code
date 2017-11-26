"""Base web app for BajtaHack."""

from flask import Flask

from web import views


def create_app():
    """Create b"""
    app_ = Flask(__name__)
    views.init_views(app_)
    return app_


app = create_app()

__all__ = [
    'app',
]
