"""Plint fixtures for BajtaHack tests."""
import pytest

from web import create_app


@pytest.fixture
def app():
    """Base app fixture for flask app testing.

    Returns:
        flask test app.
    """
    app_ = create_app()
    app_.debug = True
    app_.testing = True
    app_.config["SERVER_NAME"] = "localhost"
    return app_
