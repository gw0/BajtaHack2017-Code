"""Tests for base web init."""
from flask import url_for


def test_ping(app):
    with app.app_context():
        response = app.test_client().get(url_for('index'))
        assert response.status_code == 200
        assert response.data == b'Hello World!'
