BajtaStore
=====================

Setup instructions (Server):
1. Server (BajtaStore platform):
- install Ubuntu 16.04 platform, Flask, Sqlite3
- copy Server folder to Ubuntu
- move to Server folder
- run "export FLASK_APP=app.py"
- run "flask run --host=0.0.0.0" 
- server should be available on port 5000, exposed to public

2. Applications
- run the mian.py script
- the sendSMS.py script should be uploaded on the Orange Pi (in the /usr/pi/ folder)
- we need to install paramiko library

3. Client:
- direktorij Client
- bajtastore_pi.py
    klient za vzpostavitev TCP povezave s streznikom - posreduje REST api na SRM
- bajtastore_server.py
    manjka opis
- startup.sh
    skripta, ki z malo zamude starta klienta (potrebno je spremeniti pot)
- rc.local
    primer vnosa za avtostart (/etc/rc.local) (potrebno je spremeniti pot)
