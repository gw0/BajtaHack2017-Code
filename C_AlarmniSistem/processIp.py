#!/usr/bin/python
import subprocess
import re
import arpreq

def get_IpMac():
    addresses = subprocess.check_output(["arp", "-a"])
    dict = {}
    for line in addresses.split("\n"):
        mac = re.search(r"([0-9A-F]{2}[:-]){5}([0-9A-F]{2})", line, re.I)    
        ip = re.search(r"((2[0-5]|1[0-9]|[0-9])?[0-9]\.){3}((2[0-5]|1[0-9]|[0-9])?[0-9])", line, re.I)
        if mac is not None and ip is not None:
            mac = mac.group()
            ip = ip.group()
            dict[mac] = ip
    return dict

def get_MAC(ipAddr):
    return arpreq.arpreq(ipAddr)

def show_IpMac():
    dict = get_IpMac()
    for mac, ip in dict.items():
        print("MAC naslov: " + mac + "   IP naslov: " + ip)





