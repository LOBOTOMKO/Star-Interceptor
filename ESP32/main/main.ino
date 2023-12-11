#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>

#include <string>



int pins1[] = {25,32,26,33};
int pins2[] = {27,13,14,12};

int step1 = 0;
int step2 = 0;

// = 0,7488
float angle = 0.7488;
int   speed = 5;
int   hold  = 2;

float latitude     = 0.000;
float longtitude   = 0.000;




#define SERVICE_UUID        "6e400001-b5a3-f393-e0a9-e50e24dcca9e"
#define CHARACTERISTIC_UUID "6e400003-b5a3-f393-e0a9-e50e24dcca9e"

void turn(int steps, bool motor){
  if (steps != 0){
  Serial.print(steps);
  if (motor){

    Serial.print(steps + "_MOTOR1");
    if (steps > 0){ 

      Serial.println("_MOTOR1:clockwise");
      for (int i = 0; i <= steps;i++){      
          step1++;
          if (step1 == 4){
            step1 = 0;
          }

          digitalWrite(pins1[step1], HIGH);
          delay(hold);
          digitalWrite(pins1[step1], LOW);
          delay(speed);
        }
    }


    else{
      
      Serial.println("MOTOR1:counterclockwise");
      for (int i = steps; i <= 0;i++){     
          step1--;
          if (step1 == -1){
            step1 = 3;
          }

          digitalWrite(pins1[step1], HIGH);
          delay(hold);
          digitalWrite(pins1[step1], LOW);
          delay(speed);
        }
    }
  }
  else{
      Serial.println(steps+ "_MOTOR2:STEPS");
        if (steps > 0){
      Serial.println("MOTOR2:clockwise");
      for (int i = 0; i <= steps;i++){     
          step1++;
          if (step1 == 4){
            step1 = 0;
          }

          digitalWrite(pins2[step1], HIGH);

          delay(hold);
          digitalWrite(pins2[step1], LOW);

          delay(speed);
        }
    }


    else{
      Serial.println("MOTOR2:counterclockwise");
      for (int i = steps; i <= 0;i++){     
          step1--;
          if (step1 == -1){
            step1 = 3;
          }

          digitalWrite(pins2[step1], HIGH);
          delay(hold);
          digitalWrite(pins2[step1], LOW);
          delay(speed);
        }
    }
  }
  }
}

BLECharacteristic *pCharacteristic;

class MyCallbacks : public BLECharacteristicCallbacks {
void onWrite(BLECharacteristic* characteristic) {
  std::__cxx11::basic_string<char> value = characteristic->getValue();

  int   index       = value.find(" ");
        latitude    += std::stof(value.substr(0,index)) ;
        longtitude  += std::stof(value.substr(index)  ) ;

  Serial.println(value.c_str());
  Serial.println(latitude,   3);
  Serial.println(longtitude, 3);

  turn(round(longtitude/angle),1);
  longtitude = longtitude/angle - round(longtitude/angle);
  Serial.print(longtitude, 3);
  Serial.println(":reaminer, MOTOR1");


  turn(round(latitude/angle),0);
  latitude = latitude/angle - round(latitude/angle);
  Serial.print  (latitude,3   );
  Serial.println(":reaminer, MOTOR2"   );


  digitalWrite(2, HIGH);
  delay(500);
  digitalWrite(2, LOW);

}

};

class MyServerCallbacks : public BLEServerCallbacks {
  void onConnect(BLEServer* pServer) {
    Serial.println("Device connected");
  }

  void onDisconnect(BLEServer* pServer) {
    Serial.println("Device disconnected");
    pServer->startAdvertising();
  }
};

void setup() {
  Serial.begin(115200);
  while (!Serial) {}

  BLEDevice::init("noscope");
  BLEServer *pServer = BLEDevice::createServer();
  pServer->setCallbacks(new MyServerCallbacks()); // Assign the server callbacks

  BLEService *pService = pServer->createService(SERVICE_UUID);
  pCharacteristic = pService->createCharacteristic(
    CHARACTERISTIC_UUID,
    BLECharacteristic::PROPERTY_WRITE
  );

  pCharacteristic->setCallbacks(new MyCallbacks()); // Assign the characteristic callbacks

  pService->start();

  BLEAdvertising *pAdvertising = pServer->getAdvertising();
  pAdvertising->addServiceUUID(SERVICE_UUID);
  pAdvertising->start();

  Serial.println("Characteristic defined! Now you can write data to it.");


  pinMode(2, OUTPUT);
  for (int i = 0; i <5 ; i++){
    pinMode(pins1[i],OUTPUT);
    pinMode(pins2[i],OUTPUT);

  }
}

void loop() {

}