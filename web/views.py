"""Bajta hack views module."""


def init_views(app):
  @app.route('/')
  def index():
      return 'Hello World!'
