
  int elevation[] = {
      13,  //BIN1
      12,  //BIN2
      14,  //AIN2
      27,  //AIN1
  };


int azimuth[] = {
    19,  // AIN1
    21,  // AIN2
     1,  // BIN1
    22,  // BIN2
     3   // STNDBY
};

int itteration_elevation = 0;
    //int RX0 = 3;  int TX0 = 1;  int RX2 = 16;  int TX2 = 17;


void turn(int array[]){



  for (int steps = 20; steps > 0; steps--){


    if (itteration_elevation == 4){itteration_elevation = 0;};
    digitalWrite(array[itteration_elevation], HIGH);
    Serial.print(String(array[itteration_elevation]) + ":");
    delay(5000);
    digitalWrite(array[itteration_elevation], LOW);
    //Serial.println(array[itteration_elevation]);
    delay(500);
    itteration_elevation++;

  }


}



void setup() {
  Serial.begin(115200);

  for (int pin = 0;pin>6;pin++){
    pinMode(elevation[pin] , OUTPUT);
    pinMode(azimuth[pin] , OUTPUT);
    }


}




void loop() {

Serial.println("something");
turn(elevation);


}




