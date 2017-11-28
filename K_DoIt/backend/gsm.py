import subprocess
import threading
from time import sleep


class Gsm:

    def __init__(self, read_func=print):
        self.read_func = read_func
        while self.check_for_new_msg():
            self.del_msg(self.check_for_new_msg())
        self.init_msg_check_thread()

    def check_for_new_msg(self, created=False):
        p = subprocess.Popen(["mmcli", "-m", "0", "--messaging-list-sms"], stdout=subprocess.PIPE)
        p.wait()
        output = str(p.stdout.read(), "utf-8")
        if output.find('No SMS messages were found') != -1:
            return False
        elif not created and output.find('(unknown)') != -1:
            return False
        else:
            return int(output.split("/")[-1].split(" ")[0])

    def del_msg(self, msg_id):
        p = subprocess.Popen(["mmcli", "-m", "0", "--messaging-delete-sms="+str(msg_id)], stdout=subprocess.PIPE)
        p.wait()
        output = str(p.stdout.read(), "utf-8")
        if output.find("successfully deleted SMS from modem") != -1:
            return True
        else:
            return False

    def read_msg(self, msg_id):
        p = subprocess.Popen(["mmcli", "-s", str(msg_id)], stdout=subprocess.PIPE)
        p.wait()
        output = str(p.stdout.read(), "utf-8")
        number = output.split("number: ")[-1].split("\n")[0].strip("'")
        content = output.split("text: ")[-1].split("\n")[0].strip("'")
        self.del_msg(msg_id)
        return tuple([number, content])

    def send_msg(self, number, content):
        msg = "--messaging-create-sms=number=\"" + number + "\",text=\"" + content + "\",smsc=\"+38641001333\",validity=100,class=1,delivery-report-request=no"
        p = subprocess.Popen(["mmcli", "-m", "0", msg], stdout=subprocess.PIPE)
        p.wait()
        msg_id = self.check_for_new_msg(True)
        p = subprocess.Popen(["mmcli", "-m", "0", "-s", str(msg_id), "--send"], stdout=subprocess.PIPE)
        p.wait()
        self.del_msg(msg_id)
        return None

    def msg_check_thread(self):
        while 1:
            if self.check_for_new_msg():
                self.read_func(self.read_msg(self.check_for_new_msg()))
            sleep(1)

    def init_msg_check_thread(self):
        rec_t = threading.Thread(target=self.msg_check_thread)
        rec_t.start()
