# conftest.py
import pytest

from web import create_app


@pytest.fixture
def app():
    app = create_app()
    app.debug = True
    app.testing = True
    app.config["SERVER_NAME"] = "localhost"
    return app
