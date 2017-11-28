# FRIĐ

## Installing FriĐ app

Installing friđ app localy

### Install python3

You probably know how to install python and add it as system enviroment variable, so we are gonna skip this part.
[How to install python](https://www.python.org/downloads/)

### Fork this repo

Create a fork of this repository and clone your version of this repo anywhere on your pc

### Create virtual enviroment

Cd into your Friđ directory and create virtual python enviroment called anything you want, but for this quide we gonna call it flask.
You can do so by calling

```$ python -m venv flask```

Note that in some operating systems you may need to use `python3` instead of `python`.

Now install libraries used by flask

```
$ flask/bin/pip install flask
$ flask/bin/pip install flask-login
$ flask/bin/pip install flask-openid
$ flask/bin/pip install flask-mail
$ flask/bin/pip install flask-sqlalchemy
$ flask/bin/pip install sqlalchemy-migrate
$ flask/bin/pip install flask-whooshalchemy
$ flask/bin/pip install flask-wtf
$ flask/bin/pip install flask-babel
$ flask/bin/pip install guess_language
$ flask/bin/pip install flipflop
$ flask/bin/pip install coverage
```


### Initialising example database

We added 2 sample database builds.
* `initdb.py`
* `subelbase.py`

Note that only `subelbase.py` creates fully suportet database that supports pictures of products

The script will create local database file in your tmp folder

Welll done you are almost there

### Runing app localy

You are all set and only need to run your aplication.

You can do that by running `run.py` file. You can do so by cd int folder that has `run.py` file in it and calling

```python ./run.py```

or

```python3 ./run.py```

If your default python is python2.

Note that tis installation is for non windows users. You may need to take a few different steps if you are using windows. And if you do you should know how to do so.

### Opening your app in your prefered browser (tested on chrome)

You just open 

```http://localhost:5000```

or some other port. You can read the port number in terminal just after you run your app.

## Preparing Hardware

To Do

## Final thoughts

Enjoy using our app and help reduce food waste. 

Feedback is welcome.


