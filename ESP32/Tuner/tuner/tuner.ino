  int pins1[] = {25,32,26,33};
  int pins2[] = {27,13,14,12};

  int step1 = 0;
  int step2 = 0;


void turn(int steps){
  if (steps > 0){ 
      Serial.println("MOTOR1:clockwise");
      for (int i = 0; i <= steps;i++){     
          step1++;
          if (step1 == 4){
            step1 = 0;
          }

          Serial.println(step1);
          digitalWrite(pins2[step1], HIGH);
          delay(1);
          digitalWrite(pins2[step1], LOW);
          delay(2);
        }
    }
}



void setup() {
  Serial.begin(115200);


}

void loop() {
  

}
