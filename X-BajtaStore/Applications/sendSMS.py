import os
import subprocess
import sys

#mmcli -m 0 --messaging-list-sms
#mmcli -m 0 --messaging-create-sms='number="+38641596509", text="OLLLA!", smsc="+38641001333", validity=100, class=1, delivery-report-request=no '
#mmcli --modem=/org/freedesktop/ModemManager1/Modem/0 -s 2 --send

#phone numbers to which we want to send the data
phoneNumber1 = "+38641596509"
phonenumber2 = "+38631578083"
phonenumber3 = "+38631457897"
phoneNubers = [phoneNumber1, phonenumber2, phonenumber3]

smscNumber = "+38641001333"

def sendSMS(temperature_value):

    pipe = subprocess.Popen("mmcli -m 0 --messaging-list-sms", shell=True, stdout=subprocess.PIPE).stdout
    output = pipe.read()
    print(output)
    smsID = int(output[6:9])

    for number in phoneNubers:
        smsText = 'POZOR! Temperatura dosegla vrednost: {} stopinj C.'.format(temperature_value)
        createMessage_Text = 'mmcli -m 0 --messaging-create-sms=' + '\'number=\"{}\", text=\"{}\", smsc=\"{}\", validity=100, class=1, delivery-report-request=no\''.format(number, smsText, smscNumber)
        sendMessage_Text = 'mmcli --modem=/org/freedesktop/ModemManager1/Modem/0 -s {} --send'.format(smsID)
        os.system(createMessage_Text)
        os.system(sendMessage_Text)
        smsID = smsID + 1

if __name__ == "__main__":
    sendSMS(sys.argv[1])
