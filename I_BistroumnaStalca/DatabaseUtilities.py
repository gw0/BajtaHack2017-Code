import sqlite3
from flask import Flask, request, session, g, redirect, url_for, abort, \
    render_template, flash

def connect_db(app):
    """Connects to the specific database."""
    rv = sqlite3.connect(app.config['DATABASE'])
    rv.row_factory = sqlite3.Row
    return rv

def init_db(app):
    with app.app_context():
        db = get_db(app)
        with app.open_resource('schema.sql', mode='r') as f:
            db.cursor().executescript(f.read())
        with app.open_resource('schema_measurements.sql', mode='r') as f:
            db.cursor().executescript(f.read())
        with app.open_resource('schema_motion.sql', mode='r') as f:
            db.cursor().executescript(f.read())
        db.commit()
        print "database created"

def get_db(app):
    """Opens a new database connection if there is none yet for the
    current application context.
    """
    if not hasattr(g, 'sqlite_db'):
        g.sqlite_db = connect_db(app)
    return g.sqlite_db