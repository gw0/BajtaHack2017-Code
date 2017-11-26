# Bistroumna štal'ca

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

Python 2.7.12 |Anaconda 4.2.0 
google drive slike
Raspberry Pi Orange
u-blox SRM modfule

### Installing
* Connect Rasberry Pi with u-blox SRM module. 
* Remove firewall over SSH:

```
sudo rm /etc/iptables/*
sudo reboot
```
* Copy images from Google drive to project path
* Run server.py
* Run post_measurements.py
* Run post_motion.py
* Navigate to http://127.0.0.1:5000/ and enjoy!

## Authors

* **Krivec Tadej** - [PurpleBooth](https://github.com/tadejkrivec)
* **Miha Gazvoda** - [PurpleBooth](https://github.com/mihagazvoda)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the AGPLv3 license - see the https://opensource.org/licenses/AGPL-3.0 file for details

