#include <dht.h>

//#define test

#define PACKET_SIG 0b11001010 // 0xCA

#define PIN_PIR_INT        2
#define PIN_VIBRATION_INT  3
#define PIN_BUZZER         4
#define PIN_LED_R          5
#define PIN_LED_G          6
#define PIN_LED_B          7
#define DHT_APIN          A0 // Analog Pin sensor is connected to
#define PIN_ILLUMINATION  A1
#define PIN_SOUND         A2
#define PIN_FLAME         A3
#define PIN_CO            A4

 
dht DHT;
byte buff[24];

struct senzorji{
  uint16_t PIR_counter;
  uint8_t  PIR_change;
  uint16_t vibration_counter;
  uint8_t  vibration_change;
  uint16_t DHT_huminity;
  uint16_t DHT_temperature;
  uint16_t co;
  uint16_t illumination;
  uint16_t sound;
  uint16_t flame;
}sen1;

// threshold is ignored if value is set to 0
struct threshold_values{
  uint16_t PIR_counter;
  uint16_t vibration_counter;
  uint16_t DHT_huminity;
  uint16_t DHT_temperature;
  uint16_t co;
  uint16_t illumination;
  uint16_t sound;
  uint16_t flame;
}th_val1;

struct events_types{
  uint8_t led_r;
  uint8_t led_g;
  uint8_t led_b;
  uint8_t buzzer;
}event_t;

 
void setup(){
 
  Serial.begin(9600);
  delay(500);

  // PIR interrupt
  pinMode(PIN_PIR_INT, INPUT);
  attachInterrupt(digitalPinToInterrupt(PIN_PIR_INT), pir_sig, RISING);
  pinMode(13, OUTPUT);
  digitalWrite(13, LOW);

  // vibration interrupt
  pinMode(PIN_VIBRATION_INT, INPUT);
  attachInterrupt(digitalPinToInterrupt(PIN_VIBRATION_INT), potres_sig, RISING);

  // buzzer
  pinMode(PIN_BUZZER, OUTPUT);
  //LEDS
  pinMode(PIN_LED_R, OUTPUT);
  pinMode(PIN_LED_G, OUTPUT);
  pinMode(PIN_LED_B, OUTPUT);

  // set def. values
  def_threshold_values();
 
}

void def_threshold_values(){
  th_val1.flame = 1000;
  th_val1.co = 500;
  th_val1.DHT_huminity = 60;
  th_val1.DHT_temperature = 40;
}

void pir_sig(){
  digitalWrite(13, HIGH);
  //pir_status != pir_status;
  sen1.PIR_counter++;
  sen1.PIR_change = 1;
}

void potres_sig(){
  sen1.vibration_counter++;
  sen1.vibration_change = 1;
}

void read_sensors(){
  
  //**** DHT
  DHT.read11(DHT_APIN);
  sen1.DHT_huminity = DHT.humidity;
  sen1.DHT_temperature = DHT.temperature;

  //**** lumination
  sen1.illumination = analogRead(PIN_ILLUMINATION);
  
  //**** sound level
  sen1.sound = analogRead(PIN_SOUND);

  //**** flame level
  sen1.flame = analogRead(PIN_FLAME);

  //**** CO level
  sen1.co = analogRead(PIN_CO);
  /*
  sen1.DHT_huminity = 500;
  sen1.DHT_temperature = 600;

  //**** lumination
  sen1.illumination = 100;
  
  //**** sound level
  sen1.sound = 200;

  //**** flame level
  sen1.flame = 300;

  //**** CO level
  sen1.co = 400;
  */
}

void events(){
  //
  if(th_val1.DHT_huminity && sen1.DHT_huminity > th_val1.DHT_huminity){
    //event_t.led_r;
    //event_t.led_g;
    event_t.led_b = 1;
    Serial.println("Previsoka vlaznost.");
    //event_t.buzzer;
  }
  if(th_val1.DHT_temperature && sen1.DHT_temperature > th_val1.DHT_temperature){
    event_t.led_r = 1;
  }
  if(th_val1.illumination && sen1.illumination > th_val1.illumination){
    
  }
  if(th_val1.sound && sen1.sound > th_val1.sound){
    
  }
  if(th_val1.flame && sen1.flame > th_val1.flame){
    event_t.buzzer = 1;
    event_t.led_r = 1;
  }
  if(th_val1.co && sen1.co > th_val1.co){
    event_t.buzzer = 1;
  }
}

void events_signal(){
  static uint8_t state = 0;
  static uint8_t state_led_r = 0;
  static uint8_t state_led_g = 0;
  static uint8_t state_led_b = 0;
  static uint8_t state_buzzer = 0;

  if(!state_buzzer && event_t.buzzer){
    digitalWrite(PIN_BUZZER, HIGH);
    state_buzzer = 1;
    event_t.buzzer = 0;
  }
  else if(state_buzzer){
    digitalWrite(PIN_BUZZER, LOW);
    state_buzzer = 0;
  }

  if(!state_led_r && event_t.led_r){
    digitalWrite(PIN_LED_R, HIGH);
    state_led_r = 1;
    event_t.led_r = 0;
  }
  else if(state_led_r){
    digitalWrite(PIN_LED_R, LOW);
    state_led_r = 0;
  }

  if(!state_led_g && event_t.led_g){
    digitalWrite(PIN_LED_G, HIGH);
    state_led_g = 1;
    event_t.led_g = 0;
  }
  else if(state_led_g){
    digitalWrite(PIN_LED_G, LOW);
    state_led_g = 0;
  }

  if(!state_led_b && event_t.led_b){
    digitalWrite(PIN_LED_B, HIGH);
    state_led_b = 1;
    event_t.led_b = 0;
  }
  else if(state_led_b){
    digitalWrite(PIN_LED_B, LOW);
    state_led_b = 0;
  }
  
}

void text_output(){
  Serial.print("Current humidity = ");
  Serial.print(sen1.DHT_huminity);
  Serial.print("%  ");
  Serial.print("temperature = ");
  Serial.print(sen1.DHT_temperature); 
  Serial.println("C  ");

  Serial.print("Svetloba: ");
  Serial.println(sen1.illumination);

  Serial.print("Zvok: ");
  Serial.println(sen1.sound);

  Serial.print("Ogenj: ");
  Serial.println(sen1.flame);

  Serial.print("CO: ");
  Serial.println(sen1.co);

  Serial.print("PIR: ");
  Serial.println(sen1.PIR_counter);
  digitalWrite(13, LOW);

  Serial.print("Potres: ");
  Serial.println(sen1.vibration_counter);
}

void output_raw_data(){
  uint8_t i = 0;
  buff[i++] = PACKET_SIG;
  //buff[i++] = 0;
  buff[i++] = sen1.PIR_counter >> 8;
  buff[i++] = sen1.PIR_counter;
  buff[i++] = sen1.PIR_change;
  buff[i++] = sen1.vibration_counter >> 8;
  buff[i++] = sen1.vibration_counter;
  buff[i++] = sen1.vibration_change;
  buff[i++] = sen1.DHT_huminity >> 8;
  buff[i++] = sen1.DHT_huminity;
  buff[i++] = sen1.DHT_temperature >> 8;
  buff[i++] = sen1.DHT_temperature;
  buff[i++] = sen1.co >> 8;
  buff[i++] = sen1.co;
  buff[i++] = sen1.illumination >> 8;
  buff[i++] = sen1.illumination;
  buff[i++] = sen1.sound >> 8;
  buff[i++] = sen1.sound;
  buff[i++] = sen1.flame >> 8;
  buff[i++] = sen1.flame;
  //buff[1] = i;
  Serial.write(buff, i);
}
 
void loop(){
  read_sensors();
  events();
  events_signal();
  #ifdef test
  text_output();
  #else
  output_raw_data();
  #endif
  delay(5000);
 
} 
