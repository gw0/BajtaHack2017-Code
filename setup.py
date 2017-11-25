#!/usr/bin/env python3

"""BajtaHack sample project

This is a basic server for handling a SRM module via a web interface.
"""

from setuptools import setup


if __name__ == '__main__':
    setup(
        name='BajtaHack',
        version='0.0.1',
        licence='AGPL',
        packages=['bajtahack'],
        include_package_data=True,
        install_requires=[
            'flask',
        ],
        extras_require={
            'dev': [
                'pylint',
            ],
            'test': [
                'pytest-runner',
                'pytest-flask',
                'pytest',
            ],
        },
    )
