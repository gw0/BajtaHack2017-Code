
import DatabaseUtilities
from flask import Flask, request, session, g, redirect, url_for, abort, \
    render_template, flash
from bokeh.embed import components
from bokeh.plotting import figure
from bokeh.resources import INLINE
import pandas as pd
import numpy as np

app = Flask(__name__)
app.config.from_object(__name__)

# Load default config from an object
app.config.from_object('ConfigModule.DevelopmentConfig')

@app.route('/')
def show_entries():
    db = DatabaseUtilities.get_db(app)
    cur = db.execute('select cowID from entries order by id desc')
    entries = cur.fetchall()
    return render_template('show_entries.html', entries=entries)

@app.route('/add', methods=['POST'])
def add_entry():
    if not session.get('logged_in'):
        abort(401)
    db = DatabaseUtilities.get_db(app)
    cowID = request.form['cowID']
    cowID = cowID.strip()
    db.execute('insert into entries (cowID) values (?)',
                 [cowID])
    db.commit()
    return redirect(url_for('show_entries'))

@app.route('/remove', methods=['POST'])
def remove_entry():
    if not session.get('logged_in'):
        abort(401)
    db = DatabaseUtilities.get_db(app)
    cowID = request.form['cowID_delete']
    cowID = cowID.strip()
    db.execute('delete from entries where cowID=''?''',
                  [cowID])
    db.commit()
    return redirect(url_for('show_entries'))

@app.route('/login', methods=['GET', 'POST'])
def login():
    error = None
    if request.method == 'POST':
        if request.form['username'] != app.config['USERNAME']:
            error = 'Napacno uporabnisko ime'
        elif request.form['password'] != app.config['PASSWORD']:
            error = 'Napacno geslo'
        else:
            session['logged_in'] = True
            return redirect(url_for('show_entries'))
    return render_template('login.html', error=error)

@app.route('/measurements', methods=['POST'])
def measurements():
    cowID = request.form['cowID']
    db = DatabaseUtilities.get_db(app)
    if cowID == '9999':
        temperature = request.form['temperature']
        humidity = request.form['humidity']
        luminosity = request.form['luminosity']
        db.execute('insert into measurements (temperature, humidity, luminosity) values (' +
                      str(temperature) + ', ' + str(humidity) + ', ' + str(luminosity) + ')')
    else:
        milk = request.form['milk']
        food = request.form['food']
        db.execute('insert into motion (cowID, milk, food) values (' +
                   str(cowID) + ', ' + str(milk) + ', ' + str(food) + ')')

    db.commit()
    return 'ok'

@app.route('/show_measurements', methods=['GET'])
def show_measurements():
    cowID_form = request.args.get('cowID')

    db = DatabaseUtilities.get_db(app)

    #polling
    df = pd.read_sql('select * from measurements', db)
    temperature = df['temperature']
    humidity = df['humidity']
    brightness = df['luminosity']
    date = pd.to_datetime(df['Timestamp'])

    #motion
    df_motion = pd.read_sql('select * from motion where cowID=' + cowID_form, db)
    milk = df_motion['milk']
    food = df_motion['food']
    dates = pd.to_datetime(df_motion['Timestamp'])

    pixel_num = 400

    # First Plot
    p = figure(plot_width=pixel_num, plot_height=pixel_num, x_axis_type='datetime')
    p.line(date, temperature)
    p.title.text = 'Temperatura'
    p.xaxis.axis_label = 'Cas'
    p.yaxis.axis_label = 'Temperatura [C]'

    # Second Plot
    p2 = figure(plot_width=pixel_num, plot_height=pixel_num, x_axis_type='datetime')
    p2.line(date, humidity)
    p2.title.text = 'Vlaznost'
    p2.xaxis.axis_label = 'Cas'
    p2.yaxis.axis_label = 'Vlaznost'

    # Second Plot
    p3 = figure(plot_width=pixel_num, plot_height=pixel_num, x_axis_type='datetime')
    p3.line(date, brightness)
    p3.title.text = 'Svetilnost'
    p3.xaxis.axis_label = 'Cas'
    p3.yaxis.axis_label = 'Svetilnost'

    # Second Plot
    p4 = figure(plot_width=1230, plot_height=pixel_num, x_axis_type='datetime')
    p4.vbar(x=dates, top=food, width=1000)
    p4.title.text = 'Poraba hrane'
    p4.xaxis.axis_label = 'Cas'
    p4.yaxis.axis_label = 'Poraba hrane [kg]'

    p5 = figure(plot_width=1230, plot_height=pixel_num, x_axis_type='datetime')
    p5.vbar(x=dates, top=milk, width=1000)
    p5.title.text = 'Produkcija mleka'
    p5.xaxis.axis_label = 'Cas'
    p5.yaxis.axis_label = 'Produkcija mleka [l]'

    js_resources = INLINE.render_js()
    css_resources = INLINE.render_css()

    script, div = components(p)
    script2, div2 = components(p2)
    script3, div3 = components(p3)
    script4, div4 = components(p4)
    script5, div5 = components(p5)

    return render_template('show_measurements.html',
                           plot_script=script, plot_div=div,
                           plot_script2=script2, plot_div2=div2,
                           plot_script3=script3, plot_div3=div3,
                           plot_script4=script4, plot_div4=div4,
                           plot_script5=script5, plot_div5=div5,
                           js_resources=js_resources, css_resources=css_resources)

@app.route('/logout')
def logout():
    session.pop('logged_in', None)
    return redirect(url_for('show_entries'))

@app.teardown_appcontext
def close_db(error):
    """Closes the database again at the end of the request."""
    if hasattr(g, 'sqlite_db'):
        g.sqlite_db.close()

if __name__ == '__main__':
    DatabaseUtilities.init_db(app)
    app.run()