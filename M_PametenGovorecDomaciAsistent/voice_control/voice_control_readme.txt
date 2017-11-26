##########################################################################
#
#  READ ME to setup Alexa voice control on Pi 3.
#
##########################################################################

--------------------
1. Get Voice Input
--------------------

Either get an Amazon Echo device or equip the pi with a microphone. In this project a USB headset was used. 

To enable the microphone the default sound device of the Pi has to be disabeld. This can be done by creating the file
/etc/modprobe.d/alsa-blacklist.conf and inserting the line: blacklist snd_bcm2835 and saving the file. The Pi should 
be rebooted after this step. The sound device should best be in slot zero. Therefore the file 
/lib/modprobe.d/aliases.conf has to be edited. The line snd-usb-audio index=-2 should be changed to snd-usb-audio index=0. To check if the change was sucessful, run cat /proc/asound/cards to see if your device is at position 0.

To test the volume and the recording quality follow the instructions in step 1 in:
https://diyhacking.com/best-voice-recognition-software-for-raspberry-pi/

If the choice was to go without Amazon Echo, Alexa has to be installed on the Pi, follow the instruction in:
https://medium.com/iotforall/how-to-add-alexa-to-a-raspberry-pi-6cedfe15662e


--------------------
2. Use Voice Input
--------------------

To use the Alexa API and recieve custom commands, follow the instruction in:
http://www.instructables.com/id/Control-Raspberry-Pi-GPIO-With-Amazon-Echo-and-Pyt/

The ngrok, the python service and Alexa voice recognition have to be active at the same time. Our implementation 
and configuration can be found in the files:

  * voice_control_center.py
  * IntentSchema.txt
  * CustomSlotTypes.txt
  * SampleUtterances.txt
  
The LightControlIntent is used to switch lights on and of. The RewardIntent intent is intended for later user feedback
on the performance of the home assistent.


Info: in case of a headless Pi installation, it might be usefull to have a cli webbrowser which supports copy/paste 
(e.g w3m).
Info: the Device ID can be found in: https://alexa.amazon.com/spa/index.html#settings
