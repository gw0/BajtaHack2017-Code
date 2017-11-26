#include <stdio.h>
#include <wiringPi.h>
#include <wiringPiI2C.h>

int main(int argc, char *argv[]){
    
    if(argc > 1){
        wiringPiSetup();
        if(strcmp("gpio", argv[1]) == 0){
            int pin = atoi(argv[2]);
            if(argc > 3){
                if(strcmp("out", argv[3]) == 0) pinMode(pin, OUTPUT);
                else if(strcmp("in", argv[3]) == 0) {
                    pinMode(pin, INPUT);
                    if(argc > 4){
                        if(strcmp("up", argv[4]) == 0) pullUpDnControl(pin, PUD_UP);
                        else if(strcmp("down", argv[4]) == 0) pullUpDnControl(pin, PUD_DOWN);
                        else if(strcmp("off", argv[4]) == 0) pullUpDnControl(pin, PUD_OFF);
                    }
                }
                else{
                    int value = atoi(argv[3]);
                    digitalWrite(pin, value);
                }
            }
            printf("%d", digitalRead(pin));
        }
        else if(strcmp("i2c", argv[1]) == 0){
            int device = atoi(argv[2]);
            wiringPiI2CSetup(device);
            int reg = 0;
            if(argc > 3) reg = atoi(argv[3]);
            int value = wiringPiI2CReadReg8(device, reg);
            printf("%d", value);
        }
    }
    else {
        printf("GPIO and I2C interfacing utility\n");
        printf("Usage: \n");
        printf("\t io gpio <pin> [out|int [up|down|off]]\n");
        printf("\t io i2c <device> [<register>]\n");
    }
}

