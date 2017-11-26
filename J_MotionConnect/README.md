# MotionConnect
Where motion meets magic

## Details
All details are written in BajtaHack.pdf

## Setup
### Needed:
 - MongoDB database for data storage
 - Python 2.7
 - Gesture sensor PCB with MGC3030

### Steps:
 - run prepare_database.py to populate default values to database
 - run api.py to run API specific to this project
 - run orange_pi_controls to setup the J2 orange pi
 - load & run 3d_gestures.py on J1 to communicate with gesture sensor (time sensitive I2C communication, can not afford delays from internet, but said script can control other orange pis)
 - watch magic happen

Disclaimer: for testing/demonstration purposes a webpage with controls similar to the gesture sensor was made and is fully functional.
