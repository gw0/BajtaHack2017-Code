#!/usr/bin/python
from __future__ import print_function
from time import sleep
import sqlite3
import processIp
import subprocess 
import requests

conn = sqlite3.connect("BajtaHack.db")
c = conn.cursor()

def check_forMembers(mac):
    for addr in c.execute("SELECT mac FROM users WHERE mac = ?", (mac,)):
        return True
    return False 

memberCount = 0
connectedMembers = set()
oldConnected = set()
newMembers = set()
network = "193.2.178.0/22"

while(True):
    proc = subprocess.Popen(["fping", "-a", "-g", network], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True)
    for ip in iter(proc.stdout.readline, ""):
        if "ICMP" not in ip and ip != "":
            ip = ip.strip()
            print(ip)
            if ip not in oldConnected:
                mac = processIp.get_MAC(ip)
                if check_forMembers(mac):
                    if memberCount == 0:
                        # UGASAN TAJ LARAM
                        requests.get("http://c1.srm.bajtahack.si:2017/?|users=1|")
                    memberCount += 1
                    c.execute("INSERT OR IGNORE INTO active (mac) VALUES (?)", (mac,))
                    conn.commit()
                connectedMembers.add(ip)
    newMembers = connectedMembers - oldConnected
    missingMembers = oldConnected - connectedMembers
    for ip in missingMembers:
        mac = processIp.get_MAC(ip)
        if check_forMembers(mac):
            memberCount -= 1
            if memberCount == 0:
                # PRŽGIJ TAJ ALARAM hvala dobartek
                requests.get("http://c1.srm.bajtahack.si:2017/?|users=0|")
            c.execute("DELETE FROM active WHERE mac = ?", (mac,))
            conn.commit()
    oldConnected = connectedMembers.copy()
    connectedMembers = set()
    if memberCount == 0:
        requests.get("http://c1.srm.bajtahack.si:2017/?|users=1|")
    else:
        requests.get("http://c1.srm.bajtahack.si:2017/?|users=0|")
    print("okol")	
