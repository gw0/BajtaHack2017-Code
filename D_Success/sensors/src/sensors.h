//#include <cstdint>
#include <stdint.h>

#include "libserial.h"

#define PACKET_SIG 0xCA

struct Packet{
	uint16_t PIR_counter;
  uint16_t  PIR_change;
  uint16_t vibration_counter;
  uint16_t  vibration_change;
  uint16_t DHT_huminity;
  uint16_t DHT_temperature;
  uint16_t co;
  uint16_t illumination;
  uint16_t sound;
  uint16_t flame;
};

class Sensors{

public:
	Sensors();
	~Sensors();
	int connectTo(const char *device,const unsigned int bauds);
	void close();
	int getNext(Packet *new_packet);
	int start();
	int stop();

private:
	void reset();
	libserial serial;
	bool connected;
	bool head;
	unsigned char buff[20];
	char buff_ch[1];
	int buff_counter;


};
