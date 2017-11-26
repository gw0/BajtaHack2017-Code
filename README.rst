**BajtaHack hackathon demo project**

.. image:: https://travis-ci.org/Psywerx/BajtaHack.svg?branch=master
  :target: https://travis-ci.org/Psywerx/BajtaHack
  :alt: Travis CI

.. image:: https://codecov.io/gh/Psywerx/BajtaHack/branch/master/graph/badge.svg
  :target: https://codecov.io/gh/Psywerx/BajtaHack
  :alt: Codecov


This is an example project for Bajtahack heckaton. This app contains a simple
web server for controlling a SRM module.


Installation
------------

For basic usage:

.. code:: sh

    pip instal git+git://github.com/psywerx/BatjaHack.git


Usage
-----

.. code:: python

    export FLASK_APP=web
    flask run

For development
---------------

Install

.. code:: sh

    git clone git://github.com/psywerx/BajtaHack.git
    cd BajtaHack
    virtualenv --python=python3 bajtahack_env
    . bajtahack_env/bin/activate
    pip install -e .[dev,test]

Run tests

.. code:: sh

    python setup.py test
    pylint web test butler

