#include <IRremote.h>

// Command GPIO pins
#define CMD_PIN_1   7
#define CMD_PIN_2   8
#define CMD_PIN_3   9

// Command codes
#define COMMAND_NONE        0
#define COMMAND_LED_OFF     1
#define COMMAND_LED_ON      10
#define COMMAND_LED_WHITE   11
#define COMMAND_LED_B_UP    100
#define COMMAND_LED_B_DOWN  101
#define COMMAND_LED_SMOOTH  110

#define COMMAND_LED_B_DELAY 3000

// IR Codes
#define LED_IR_CODE_ON      0xF7C03F  // On
#define LED_IR_CODE_OFF     0xF740BF  // Off
#define LED_IR_CODE_WHITE   0xF7E01F  // White color
#define LED_IR_CODE_B_UP    0xF700FF  // Brightness up
#define LED_IR_CODE_B_DOWN  0xF7807F  // Brightness down
#define LED_IR_CODE_SMOOTH  0xF7E817  // Smooth mode

#define LED_IR_CODE_SIZE 32

IRsend irsend;

void sendIRCommand(unsigned long command, int repetitions) {
  for (int i = 0; i < repetitions; i++) {
    irsend.sendNEC(command, LED_IR_CODE_SIZE);
    delay(40);
  }
}

void setup() {
  pinMode(CMD_PIN_1, INPUT);
  pinMode(CMD_PIN_2, INPUT);
  pinMode(CMD_PIN_3, INPUT);
}

void loop() {
  // Reads in a loop (with a 250ms delay between each iteration) the status
  // of the GPIO pins. When the appropriate command GPIO sequence is detected
  // we will send the appropriate IR code


  // Read the command GPIO pins
  int cmdPin1 = digitalRead(CMD_PIN_1);
  int cmdPin2 = digitalRead(CMD_PIN_2);
  int cmdPin3 = digitalRead(CMD_PIN_3);

  int cmd = cmdPin1 * 100 + cmdPin2 * 10 + cmdPin3;

  switch(cmd) {
    case COMMAND_NONE:
      // Do nothing
      break;

    case COMMAND_LED_OFF:
      sendIRCommand(LED_IR_CODE_OFF, 3);
    break;

    case COMMAND_LED_ON:
      sendIRCommand(LED_IR_CODE_ON, 3);
    break;

    case COMMAND_LED_WHITE:
      sendIRCommand(LED_IR_CODE_WHITE, 3);
    break;

    case COMMAND_LED_B_UP:
      sendIRCommand(LED_IR_CODE_B_UP, 1);
      delay(COMMAND_LED_B_DELAY);
      break;

    case COMMAND_LED_B_DOWN:
      sendIRCommand(LED_IR_CODE_B_DOWN, 1);
      delay(COMMAND_LED_B_DELAY);
      break;

    case COMMAND_LED_SMOOTH:
      sendIRCommand(LED_IR_CODE_SMOOTH, 3);
      break;

    default:
      // Unknown command
      break;
  }

  delay(250);
}

