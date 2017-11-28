#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
SRM module library in Python.
"""
__author__ = "Amela, Gregor <gw.2017@ena.one>"
__version__ = '0.1.0+ublox'

import json
import re
import requests
import time
from os.path import dirname

from requests.packages.urllib3.exceptions import InsecureRequestWarning
from requests.adapters import HTTPAdapter
from requests.exceptions import HTTPError
from requests.packages.urllib3.poolmanager import PoolManager

# Disable warnings for HTTPS_BASIC
requests.packages.urllib3.disable_warnings(InsecureRequestWarning)


### Constants

# Level of HTTPS security
HTTPS_PUBLIC_CA = 1
HTTPS_PRIVATE_CA = 2
HTTPS_DEVELOPMENT = 3
HTTPS_BASIC = 0

# SRM service names
SERVICE_NAMES = set(['value', 'alloc', 'auth', 'observe', 'save', 'ui'])

# SRM access rights
AUTH_ALL = 0xFFFFFFF
AUTH_NONE = 0x0000000

LISTING_GET = 0x8000000
LISTING_PUT = 0x4000000
LISTING_POST = 0x2000000
LISTING_DELETE = 0x1000000
LISTING_ALL = 0xF000000

ACCESS_GET = 0x0800000
ACCESS_PUT = 0x0400000
ACCESS_POST = 0x0200000
ACCESS_DELETE = 0x0100000
ACCESS_ALL = 0x0F00000

PERSISTENCY_GET = 0x0080000
PERSISTENCY_PUT = 0x0040000
PERSISTENCY_POST = 0x0020000
PERSISTENCY_DELETE = 0x0010000
PERSISTENCY_ALL = 0x00F0000

ALLOCATION_GET = 0x0008000
ALLOCATION_PUT = 0x0004000
ALLOCATION_POST = 0x0002000
ALLOCATION_DELETE = 0x0001000
ALLOCATION_ALL = 0x000F000

AUTHORIZATION_GET = 0x0000800
AUTHORIZATION_PUT = 0x0000400
AUTHORIZATION_POST = 0x0000200
AUTHORIZATION_DELETE = 0x0000100
AUTHORIZATION_ALL = 0x0000F00

OBSERVE_GET = 0x0000080
OBSERVE_PUT = 0x0000040
OBSERVE_POST = 0x0000020
OBSERVE_DELETE = 0x0000010
OBSERVE_ALL = 0x00000F0

UI_GET = 0x0000008
UI_PUT = 0x0000004
UI_POST = 0x0000002
UI_DELETE = 0x0000001
UI_ALL = 0x000000F


### Helpers

class HostNameIgnoringAdapter(HTTPAdapter):
    """Ignore hostname in certificate check."""
    def init_poolmanager(self, connections, maxsize, block=False):
        self.poolmanager = PoolManager(num_pools=connections, maxsize=maxsize, block=block, assert_hostname=False)


def url_builder(url=None, schema=None, user=None, password=None, hostname=None, port=None, path=None, query=None, fragment=None):
    """Helper function for building URLs.

    `scheme://user:password@hostname:port/path?query#fragment`
    """

    if url is not None:
        # parse given URL
        m = re.match('^([hH][tT][tT][pP][sS]?)://(?:(\w+):(\w+)@)?((?:\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})|(?:[\w-]+(?:\.[\w-]+)+))(?::(\d+))?(/?.*)(?:\?(.*))?(?:#(.*))?', url)
        _schema, _user, _password, _hostname, _port, _path, _query, _fragment = m.groups()

        # update given componenets
        if schema is None:
            schema = _schema
        if user is None:
            user = _user
        if password is None:
            password = _password
        if hostname is None:
            hostname = _hostname
        if port is None:
            port = _port
        if path is None:
            path = _path
        if query is None:
            query = _query
        if fragment is None:
            fragment = _fragment

    # rebuild URL
    url = schema + '://'
    if user and password:
        url = url + user + ":" + password + "@"
    url = url + hostname
    if port:
        url = url + ':' + str(port)
    if path:
        url = url + path
    if query:
        url = url + '?' + query
    if fragment:
        url = url + '#' + fragment
    return url


### Classes

class SRMClient(object):
    """SRMClient REST interface for interacting with SRM modules."""

    # defaults
    url = 'https://192.168.10.1/'
    https_check = HTTPS_PUBLIC_CA
    ca_bundle = '{}/ca.cert.pem'.format(dirname(__file__))
    connect_timeout = 15000
    read_timeout = 5000
    verbose = False
    shared_session = None

    def __init__(self, url=None, https_check=None, ca_bundle=None, connect_timeout=None, read_timeout=None, verbose=None):
        if url is None:
            url = SRMClient.url
        if https_check is None:
            https_check = SRMClient.https_check
        if ca_bundle is None:
            ca_bundle = SRMClient.ca_bundle
        if connect_timeout is None:
            connect_timeout = SRMClient.connect_timeout
        if read_timeout is None:
            read_timeout = SRMClient.read_timeout
        if verbose is None:
            verbose = SRMClient.verbose

        self.url = url
        self.https_check = https_check
        self.ca_bundle = ca_bundle
        self.timeout = (connect_timeout / 1000., read_timeout / 1000.)
        self.verbose = verbose

        if SRMClient.shared_session is None:
            SRMClient.shared_session = requests.Session()

        self.session = SRMClient.shared_session  # reuse session
        if self.https_check in (HTTPS_DEVELOPMENT, HTTPS_BASIC):
            # turn off hostname verification
            self.session.mount('https://', HostNameIgnoringAdapter())

    def get(self, path=''):
        """
        REST GET wrapper.

        :param path: Path where REST GET request is sent
        """
        if self.verbose:
            print("  GET('{}')".format(self.url + path))

        if self.https_check == HTTPS_PUBLIC_CA:
            # regular request
            r = self.session.get(self.url + path, timeout=self.timeout)

        elif self.https_check in (HTTPS_PRIVATE_CA, HTTPS_DEVELOPMENT):
            # with private CA bundle verification
            r = self.session.get(self.url + path, verify=self.ca_bundle, timeout=self.timeout)

        elif self.https_check == HTTPS_BASIC:
            # without any verification
            r = self.session.get(self.url + path, verify=False, timeout=self.timeout)

        if self.verbose:
            print("  = {}: {}".format(r.status_code, r.content))
        if r.status_code < 200 or r.status_code >= 300:
            raise HTTPError("Unsuccessful HTTP status code: {}. Request: GET {}.".format(r.status_code, path), response=r)
        return r

    def put(self, path='', data=''):
        """
        REST PUT wrapper.

        :param path: Path where REST PUT request is sent
        :param data: Data to be sent
        """
        if isinstance(data, (dict, list, set)):
            data = json.dumps(data)
        elif data is not None:
            data = str(data)
        if self.verbose:
            print("  PUT('{}', '{}')".format(self.url + path, data))

        if self.https_check == HTTPS_PUBLIC_CA:
            # regular request
            r = self.session.put(self.url + path, data=data, timeout=self.timeout)

        elif self.https_check in (HTTPS_PRIVATE_CA, HTTPS_DEVELOPMENT):
            # with private CA bundle verification
            r = self.session.put(self.url + path, data=data, verify=self.ca_bundle, timeout=self.timeout)

        elif self.https_check == HTTPS_BASIC:
            # without any verification
            r = self.session.put(self.url + path, data=data, verify=False, timeout=self.timeout)

        if self.verbose:
            print("  = {}: {}".format(r.status_code, r.content))
        if r.status_code < 200 or r.status_code >= 300:
            raise HTTPError("Unsuccessful HTTP status code: {}. Request: PUT {}.".format(r.status_code, path), response=r)
        return r

    def post(self, path='', data=''):
        """
        REST POST wrapper.

        :param path: Path where REST POST request is sent
        :param data: Data to be sent
        """
        if isinstance(data, (dict, list, set)):
            data = json.dumps(data)
        else:
            data = str(data)
        if self.verbose:
            print("  POST('{}', '{}')".format(self.url + path, data))

        if self.https_check == HTTPS_PUBLIC_CA:
            # regular request
            r = self.session.post(self.url + path, data=data, timeout=self.timeout)

        elif self.https_check in (HTTPS_PRIVATE_CA, HTTPS_DEVELOPMENT):
            # with private CA bundle verification
            r = self.session.post(self.url + path, data=data, verify=self.ca_bundle, timeout=self.timeout)

        elif self.https_check == HTTPS_BASIC:
            # without any verification
            r = self.session.post(self.url + path, data=data, verify=False, timeout=self.timeout)

        if self.verbose:
            print("  = {}: {}".format(r.status_code, r.content))
        if r.status_code < 200 or r.status_code >= 300:
            raise HTTPError("Unsuccessful HTTP status code: {}. Request: POST {}.".format(r.status_code, path), response=r)
        return r

    def delete(self, path='', data=None):
        """
        REST DELETE wrapper.

        :param path: Path where REST DELETE request is sent
        :param data: Data to be sent
        """
        if self.verbose:
            print("  DELETE('{}')".format(self.url + path))

        if self.https_check == HTTPS_PUBLIC_CA:
            # regular request
            r = self.session.delete(self.url + path, data=data, timeout=self.timeout)

        elif self.https_check in (HTTPS_PRIVATE_CA, HTTPS_DEVELOPMENT):
            # with private CA bundle verification
            r = self.session.delete(self.url + path, data=data, verify=self.ca_bundle, timeout=self.timeout)

        elif self.https_check == HTTPS_BASIC:
            # without any verification
            r = self.session.delete(self.url + path, data=data, verify=False, timeout=self.timeout)

        if self.verbose:
            print("  = {}: {}".format(r.status_code, r.content))
        if r.status_code < 200 or r.status_code >= 300:
            raise HTTPError("Unsuccessful HTTP status code: {}. Request: DELETE {}.".format(r.status_code, path), response=r)
        return r

    def deallocate_all(self, path, exceptions=()):
        """Function for deallocation of all subresources under a path."""
        all = self.get(path).json()
        for sub in all:
            if sub not in SERVICE_NAMES and sub not in exceptions:
                self.delete('{}/{}/alloc'.format(path, sub))

    def reboot(self, wait=False):
        """Reboot."""
        self.put('/sys/reboot/value', data='reboot')

        # wait until ready
        if wait:
            time.sleep(10)
        while wait:
            try:
                self.get("/")
                break
            except KeyboardInterrupt:
                break
            except:
                pass
