#include <iostream>
#include <iomanip>
#include <time.h>
#include <fstream>
#include <string>
#include <algorithm>
//#include <curl/curl.h>

#include "sensors.h"

using namespace std;

int main(int argc, char** argv){
	
	if(argc != 2){
		#if defined(_WIN32) || defined(_WIN64) 
		cout << "EXAMPLE: " << argv[0] << " COM1\n"; 
		#endif
		#ifdef __linux__
		cout << "EXAMPLE: " << argv[0] << " /dev/ttyACM0\n";
		#endif
		
		return 0;
	}
	
	Sensors sensors;
	if(sensors.connectTo(argv[1], 9600) !=1){
		cout << "** Neuspesno vzpostavljanje povezave z napravo.\n";
		return -1;
	}
	Packet *pack = new Packet;
  
  /*
  CURL *curl;
  CURLcode res;
	curl = curl_easy_init();
  string URL = "https://193.2.177.156:16200";
  string path = "/log/string/";
  //curl --insecure -X PUT "${URL}/log/string/${VARIABLE_NAME}/value" -d "${VALUE}"
  */
  
  sleep(1);
  while(true){
    if(sensors.getNext(pack) == 1){
      cout << "PIR_counter " << pack->PIR_counter << endl;
      cout << "PIR_change " << pack->PIR_change << endl;
      cout << "vibration_counter " << pack->vibration_counter << endl;
      cout << "vibration_change " << pack->vibration_change << endl;
      cout << "DHT_huminity " << pack->DHT_huminity << endl;
      cout << "DHT_temperature " << pack->DHT_temperature << endl;
      cout << "co " << pack->co << endl;
      cout << "illumination " << pack->illumination << endl;
      cout << "sound " << pack->sound << endl;
      cout << "flame " << pack->flame << endl;
      
      /*
      if(curl){
        string url = URL + path + "flame/value";
        // enable uploading 
        curl_easy_setopt(curl, CURLOPT_UPLOAD, 1L);
     
        // HTTP PUT please 
        curl_easy_setopt(curl, CURLOPT_PUT, 1L);
     
        // specify target URL, and note that this URL should include a file
           name, not only a directory 
        curl_easy_setopt(curl, CURLOPT_URL, url);
      }
      */
      
      cout << "----------------\n";
      char buffer[255];
      const char *upload_script = "./srm_set_logical_value.sh";
      sprintf(buffer, "%s %s %d", upload_script, "flame", pack->flame);
      int status = system(buffer);
      printf("command: '%s', status=%d\n", buffer, status);
      sprintf(buffer, "%s %s %d", upload_script, "dht_humidity", pack->DHT_huminity);
      status = system(buffer);
      printf("command: '%s', status=%d\n", buffer, status);
      sprintf(buffer, "%s %s %d", upload_script, "dht_temperature", pack->DHT_temperature);
      status = system(buffer);
      printf("command: '%s', status=%d\n", buffer, status);
      sprintf(buffer, "%s %s %d", upload_script, "pir_counter", pack->PIR_counter);
      status = system(buffer);
      printf("command: '%s', status=%d\n", buffer, status);
      sprintf(buffer, "%s %s %d", upload_script, "pir_change", pack->PIR_change);
      status = system(buffer);
      printf("command: '%s', status=%d\n", buffer, status);
      sprintf(buffer, "%s %s %d", upload_script, "vibration_counter", pack->vibration_counter);
      status = system(buffer);
      printf("command: '%s', status=%d\n", buffer, status);
      sprintf(buffer, "%s %s %d", upload_script, "vibration_change", pack->vibration_change);
      status = system(buffer);
      printf("command: '%s', status=%d\n", buffer, status);
      sprintf(buffer, "%s %s %d", upload_script, "co", pack->co);
      status = system(buffer);
      printf("command: '%s', status=%d\n", buffer, status);
      sprintf(buffer, "%s %s %d", upload_script, "illumination", pack->illumination);
      status = system(buffer);
      printf("command: '%s', status=%d\n", buffer, status);
      sprintf(buffer, "%s %s %d", upload_script, "sound", pack->sound);
      status = system(buffer);
      printf("command: '%s', status=%d\n", buffer, status);
    }
    usleep(5);
  }
	
	return 0;
}
