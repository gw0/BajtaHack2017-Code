from http import cookies
import json
from http.server import HTTPServer, BaseHTTPRequestHandler
import sqlite3
import datetime
import arpreq


"""
fping -g -a 193.2.179.0/22 2>/dev/null

c1.srm.bajtahack.si/?users=1 | /?users=0
"""

conn = sqlite3.connect('BajtaHack.db')
c = conn.cursor()

#c.execute("DROP TABLE IF EXISTS users")
c.execute("CREATE TABLE IF NOT EXISTS passwords ( password PRIMARY KEY)")
c.execute("CREATE TABLE IF NOT EXISTS users (name TEXT, mac TEXT PRIMARY KEY)")
c.execute("DROP TABLE IF EXISTS active")
c.execute("CREATE TABLE IF NOT EXISTS active (mac TEXT, FOREIGN KEY (mac) REFERENCES users(mac))")
c.execute("INSERT OR IGNORE INTO passwords (password) VALUES ('izviren')")
conn.commit()

static_cooke_lol = set()

expiration = datetime.datetime.now() + datetime.timedelta(days=30)
cookie = cookies.BaseCookie()
cookie["session"] = static_cooke_lol
cookie["session"]["domain"] = "never.sleep.com"
cookie["session"]["path"] = "/main.html"
cookie["session"]["expires"] = expiration.strftime("%a, %d-%b-%Y %H:%M:%S PST")
cookie.update()
print(cookie.output())


def get_mac_from_ip(ip):
    return arpreq.arpreq(ip)


def get_file(filename):
    with open("web/" + filename, "rb") as f:
        return f.read()

def get_content_length(headers_list):
    for i in headers_list:
        if i[0] == 'Content-Length':
            return int(i[1])

def get_header(headers_list, key):
    for i in headers_list:
        if i[0] == key:
            return i[1]

def get_users(self):
    davidm = [[a, b] for a, b in c.execute("SELECT * FROM users JOIN active ON (mac)")]
    #sezam = []
    #for mac in c.execute("SELECT * FROM active"):
    #    macek = mac[0]
    #    for user, mac in c.execute("SELECT * FROM users WHERE mac = (?)", (macek,)):
    #        sezam.append([user, mac])

    users = {"users" : davidm}
    self.send_response(200)
    self.send_header("Content-Type", "application/json")
    self.end_headers()
    dumped = json.dumps(users).encode()
    self.wfile.write(json.dumps(users).encode())

class S(BaseHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

    def do_GET(self):

        #a = get_header(self.headers._headers, "Cookie")
        #if a not in static_cooke_lol and not (self.path in ["/login.html", "/login.js", "/favicon.ico"]):
        #    return login(self)

        if self.path.endswith(".js") or self.path.endswith(".html"):
            self._set_headers()
            return self.wfile.write(get_file(self.path[1:]))
        elif self.path == "/users/":
            return get_users(self)

        else:
            return home(self)


    def do_HEAD(self):
        self._set_headers()


    def do_POST(self):
        '''
        if self.path == "/login/":
            return do_login(self)

        a = get_header(self.headers._headers, "Cookie")
        if a not in static_cooke_lol:
            return login(self)
        ''' 
        if self.path == "/adduser/":
            return add_user(self)

        return home



def run(server_class=HTTPServer, handler_class=S, port=1234):
    server_address = ('c1.srm.bajtahack.si', port)
    httpd = server_class(server_address, handler_class)
    print('Starting httpd...')
    httpd.serve_forever()


def do_login(self):
    data = json.loads(self.rfile.read(get_content_length(self.headers._headers)).decode())
    pasw = data.get("password", None)

    if pasw:
        for a in c.execute("SELECT password FROM passwords WHERE password = (?)", (pasw,)):
            h = get_header(self.headers._headers, "Cookie")
            if h is not None:
                static_cooke_lol.add(h)
            else:
                print("ERROR")


def login(self):
    self._set_headers()
    return self.wfile.write(get_file("login.html"))


def home(self):
    self._set_headers()
    return self.wfile.write(get_file("main.html"))


def add_user(self):
    d = self.rfile.read(get_content_length(self.headers._headers))
    data = json.loads(d.decode())
    user = data.get("name", None)
    ip = data.get("ip", None)

    mac = get_mac_from_ip(ip)

    c.execute("INSERT OR REPLACE INTO users (name, mac) VALUES (?, ?)", (user, mac))
    conn.commit()

    print(user, ip, mac)
    self.send_response(200)
    return home(self)

if __name__ == "__main__":
    from sys import argv

    if len(argv) == 2:
        run(port=int(argv[1]))
    else:
        run('0.0.0.0')



