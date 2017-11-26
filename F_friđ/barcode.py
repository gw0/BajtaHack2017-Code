import requests
from lxml import html

koda = open('kodar.png', 'rb')

r = requests.post('http://zxing.org/w/decode', files = { 'file': ('koda.png', koda, 'image/png')})

if (r.text.split("<title>")[1].split("</title>")[0] == "Decode Succeeded"):
	print(r.text.split("Raw text</td><td><pre>")[1].split("</pre>")[0])
else:
	print("slaba slika")








