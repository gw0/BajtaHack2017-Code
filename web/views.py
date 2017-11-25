"""Bajta hack views module."""


def init_views(app):
    """Initialize all views.

    Views should be initialized in a function to prevent dependencies between
    module imports.

    Args:
        app: Flask App.
    """
    # pylint: disable=unused-variable
    @app.route('/')
    def index():
        return 'Hello World!'
