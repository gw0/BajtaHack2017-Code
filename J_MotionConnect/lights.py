import orange_pi_control
import time


def lights_on():
    orange_pi_control.turn_led_on(2)
    time.sleep(0.25)
    orange_pi_control.turn_led_on(24)
    time.sleep(0.25)
    orange_pi_control.turn_led_on(25)
    time.sleep(0.25)
    orange_pi_control.turn_led_on(16)


def lights_off():
    orange_pi_control.turn_led_off(2)
    time.sleep(0.25)
    orange_pi_control.turn_led_off(24)
    time.sleep(0.25)
    orange_pi_control.turn_led_off(25)
    time.sleep(0.25)
    orange_pi_control.turn_led_off(16)