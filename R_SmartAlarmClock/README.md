# Team R - Smart Alarm Clock
Our teams hack for the 2017 IOT hackaton BajtaHack.

Our hack is a LED alarm clock, that when the alarm is triggered it wakes you up with a soothing display of LED colors.

## Technologies used
* Arduino Nano
* Orange PI Zero
* Bed lamp
* LED with IR remote control
* IR LED diode

## Technical overview
We took a smart LED which came along with a remote to control it. For the needs of our project we interfaced with it by simulating the remote control via the IR codes it was sending for certain functions. This enabled us to control the LED via its original remote or using our SRM enabled IR remote.
A technical challenge that we had to overcome was using PWM send the necessary IR signals for the LED bulb to receive. This was solved by using an Arduino Nano board to send the appropriate signals (since it has an onboard PWM pin) and then interfacing our SRM module with the Arduino Nano. The communication was possible by creating a 3-bit GPIO-to-GPIO data bus interface.

## Short Setup Instructions
### Arduino
The Arduino controls the LED diode with which we can communicate with the smart LED. Install the IRremote library into your Arduino library folder (usually: C:\\Users\&lt;your_username&gt;\My documents\Arduino\libraries\)
then install the ledCtrl sketch onto a Arduino nano compatible board.

Connect to PIN 3 the LED diode signal line (as well as connecting 5+v and GND the led diode), then pins 7-9 are used to communicate IR LED codes to change the settings of the LED light.

|Pins/CMD_NO | 7 8 9 | Function          | IR_Code |
|------------|-------|-------------------|---------|
| 1          | 0 0 1 | LED OFF           | F740BF  |
| 2          | 0 1 0 | LED ON            | F7C03F  |
| 3          | 0 1 1 | LED WHITE         | F7E01F  |
| 4          | 1 0 0 | LED BRIGHTNESS +  | F700FF  |
| 5          | 1 0 1 | LED BRIGHTNESS -  | F7807F  |
| 6          | 1 1 0 | LED SMOOTH MODE   | F7E817  |
| 7          | 0 0 0 | CLEAR - No signal | F7E817  |
| -          | 1 1 1 | UNDEFINED         |    -    |

Note when we send a signal such as LED WHITE, we need to (after a very short delay eg. 500ms) send the CLEAR signal so we will not constantly blast the LED light with an IR command.

### Orange PI Zero
The Orange PI Zero (OPZ from now on) is used as a gateway to the Arduino commands. Using the SRM module we map certain REST PUT endpoints (scripts in SRM) to certain Arduino commands. This is achieved though the connectivity between the OPZ and Arduino via the GPIO pins - in our case the mapping goes like this:

| OPZ GPIO PIN | Arduino GPIO PIN |
|--------------|------------------|
| 17           | 7                |
| 27           | 8                |
| 26           | 9                |

The scripts are available in the SRMscript folder in the root of the project.

The scripts are mapped to the CMD_NO as follows:

| Script NO | CMD_NO |
|-----------|--------|
| 1         | 1      |
| 2         | 2      |
| 3         | 3      |
| 4         | 4      |
| 5         | 5      |
| 6         | 6      |
| 7         | 7      |

Once we set it up we should be able to turn on the LED light using the following endpoint: `PUT https://<YOUR_DOMAIN>/sys/interpreter/2/value`. As you can see the two before interpreter is the Script NO - which maps one to one with the CMD_NO (as visible in the table above).

### Alarm Clock Program
The Orange PI Zero besides serving as a API gateway for the LED commands serves as the database for the alarm settings. Copy the contents of the folder alarmClock. Here is a brief description of the files:
* `alarmClock.py` - the actual alarm clock implementation - creating, reading, updating, deleting alarm settings is implemented here
* `data.json` - the data storage file where the alarm settings are stored
* `minirest.py` - a simple flask app that contains the Alarm Clock REST API

Install the necessary packages via pip: `pip3 install -r requirements.txt`.
Run the `minirest.py` file: `python3 ./minirest.py` - By default it will accessible to all IP's on `port: 5000`.
Set up a cronjob for the `python3 alarmClock.py` script to launch every minute.

REST API Endpoints:
* [GET] / - all alarms
* [PUT] / - all alarms (form data, required fields: time, eg. time="07:30:00")
* [DELETE] /<TIME> - deletes the alarm (eg. /07-30-00, note: the dashes are converted into colons by the REST API)  

### Web Interface
The web interface is a simple HTML single page app that calls the REST API of the SRM enabled Orange PI Zero. Just be sure to change the domains for the API calls.
