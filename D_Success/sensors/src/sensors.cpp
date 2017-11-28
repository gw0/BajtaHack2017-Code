#include "sensors.h"
#include <iostream>
#include <string>
using namespace std;

Sensors::Sensors(){
	reset();
}

Sensors::~Sensors(){
	close();
}

void Sensors::close(){
	serial.Close();
	reset();
}

void Sensors::reset(){
	head = false;
	connected = false;
	buff_counter = 0;
}

int Sensors::connectTo(const char *device,const unsigned int bauds){
	for(int q=0; q<10; q++){
		if(q==9){
			//cout << "FAILED\n";
			return -1;
		}
		if(serial.Open(device, bauds) != 1){
			//cout << "  - FAIL\n";
			#if defined (_WIN32) || defined( _WIN64)
			Sleep(1000);
			#else
			sleep(1);
			#endif
		}
		else break;
	}
	//cout << "  - SUCCESS\n";
	connected = true;
	serial.FlushReceiver();
	return 1;
}

int Sensors::getNext(Packet *new_packet){
	if(!connected) return -1;
  uint8_t len = 0;

	// najprej beremo s serijskega porta toliko časa, da imamo
	// vseh 7B, ali dokler na portu ni novih podatkov
	// še prej pa moramo detektirat začetek paketa
	if(!head){
		switch(serial.ReadChar(buff_ch, 1)){
		case 0: // na vhodu ni nobenega znaka, prekinemo
			return 0;
			break;
		case 1: // uspešno smo prebrali znak
			if(buff_ch[0] == (char)PACKET_SIG){ // znak se ujema s podpisom začetka paketa
				head = true;
				buff_counter == 0;
			}
			else return 0; // vèasih je na začetku že nekaj starih podatkov na portu
			break;
		default: // sicer napaka med branjem
			return -2;
		}
	}
	// sedaj pa lahko preberemo paket ali vsaj dele paketa
	if(head){
		while(buff_counter < 18){
			switch(serial.ReadChar(buff_ch, 1)){
			case 0:
				return 0;
				break;
			case 1:
				buff[buff_counter++] = buff_ch[0];
				break;
			default:
				return -2;
			}
		}
		// v buff imamo vseh sedem bytov paketa
		if(new_packet != NULL){
			//*new_packet = (Packet&)*buff;
			/*
			new_packet->ID = (uint8_t)buff[0];
			new_packet->value = (uint16_t&)buff[1];
			new_packet->time = (uint32_t&)buff[3];
			*/
      int i = 0;
			new_packet->PIR_counter =       ((uint16_t&)buff[i++]) << 8 | buff[i++];
			new_packet->PIR_change =         buff[i++];
			new_packet->vibration_counter = ((uint16_t&)buff[i++]) << 8 | buff[i++];
      new_packet->vibration_change =   buff[i++];
      new_packet->DHT_huminity =      ((uint16_t&)buff[i++]) << 8 | buff[i++];
      new_packet->DHT_temperature =   ((uint16_t&)buff[i++]) << 8 | buff[i++];
      new_packet->co =                ((uint16_t&)buff[i++]) << 8 | buff[i++];
      new_packet->illumination =      ((uint16_t&)buff[i++]) << 8 | buff[i++];
      new_packet->sound =             ((uint16_t&)buff[i++]) << 8 | buff[i++];
      new_packet->flame =             ((uint16_t&)buff[i++]) << 8 | buff[i++];
      cout << "value of i: " << i << endl;
			head = false;
			buff_counter = 0;
			return 1;
		}
		//usleep(20000);
	}

	return 0;
}

int Sensors::start(){
	//return serial.WriteChar('S');
	return serial.WriteChar(83);
}

int Sensors::stop(){
	//return serial.WriteChar('s');
	return serial.WriteChar(115);
}
