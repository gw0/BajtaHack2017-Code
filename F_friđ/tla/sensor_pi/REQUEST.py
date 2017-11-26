import requests

def PUT(url, dt):
    print(requests.put(url, data=str(dt), verify=False).status_code)


def GET(url, dt):
    return str(requests.get(url, verify=False).status_code)


def POST(url, dt):
    print(requests.post(url, data=str(dt), verify=False).status_code)

