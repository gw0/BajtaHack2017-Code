# FRIĐ

Installing friđ app localy

### Install python3

You probably know how to install python and add it as system enviroment variable, so we are gonna skip this part.

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





