int array[] = {12, 14};
int low[] = {5, 18, 19, 15};

void setup() {
  // Configure pins in the `array` array as outputs
  for (int i = 0; i < sizeof(array) / sizeof(array[0]); i++) {
    pinMode(array[i], OUTPUT);
  }

  // Configure pins in the `low` array as outputs
  for (int i = 0; i < sizeof(low) / sizeof(low[0]); i++) {
    pinMode(low[i], OUTPUT);
  }
}

void loop() {
  // Turn pins in the `array` array on high
  for (int i = 0; i < sizeof(array) / sizeof(array[0]); i++) {
    digitalWrite(array[i], HIGH);
  }

  // Turn pins in the `low` array on low
  for (int i = 0; i < sizeof(low) / sizeof(low[0]); i++) {
    digitalWrite(low[i], LOW);
  }

  // Delay for a certain period of time
  delay(1000);

  // Turn off all pins
  for (int i = 0; i < sizeof(array) / sizeof(array[0]); i++) {
    digitalWrite(array[i], LOW);
  }
  for (int i = 0; i < sizeof(low) / sizeof(low[0]); i++) {
    digitalWrite(low[i], LOW);
  }

  // Delay for a certain period of time
  delay(1000);
}
