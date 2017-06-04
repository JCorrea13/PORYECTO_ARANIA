#define TIEMPO90 1000
#define TIEMPO45 500
#define TIEMPOAVANCE 1000


// --------------------------------------------------------------------------- Motors
int motor_left[] = {9, 10};
int motor_right[] = {7, 8};

//-----------------------------------------Variables Ultrasonico
const int pinecho = 11;
const int pintrigger = 12;


void setup() {
  Serial.begin(9600);
 
  pinMode(motor_left[0], OUTPUT);
  pinMode(motor_left[1], OUTPUT);
  pinMode(motor_right[0], OUTPUT);
  pinMode(motor_right[1], OUTPUT);

  //Configuracion pines de ultrasonico
  pinMode(pinecho, INPUT);
  pinMode(pintrigger, OUTPUT);
}


char dato;
void loop() { 

    
  if(Serial.available()){
     dato = Serial.read();

    switch(dato){
      case 'B':
              turn_left();
              delay(TIEMPO90);
              motor_stop();
              Serial.write((byte)1);
              break;
      case 'C':
              turn_left();
              delay(TIEMPO45);
              motor_stop();
              Serial.write((byte)1);
              break;
      case 'D':
              drive_forward();
              delay(TIEMPOAVANCE);
              motor_stop();
              Serial.write((byte)1);
              break;
      case 'E':
              turn_right();
              delay(TIEMPO45);
              motor_stop();
              Serial.write((byte)1);
              break;
      case 'F':
              turn_right();
              delay(TIEMPO90);
              motor_stop();
              Serial.write((byte)1);
              break;
      case 'G':
              drive_backward();
              delay(TIEMPOAVANCE);
              motor_stop();
              Serial.write((byte)1);
              break;
      case 'H':
              Serial.write((byte)getDistancia());
              break;
    }

  }

  delay(200);

}

// --------------------------------------------------------------------------- Drive

void motor_stop(){
digitalWrite(motor_left[0], LOW); 
digitalWrite(motor_left[1], LOW); 

digitalWrite(motor_right[0], LOW); 
digitalWrite(motor_right[1], LOW);
delay(25);
}

void drive_forward(){
digitalWrite(motor_left[0], HIGH); 
digitalWrite(motor_left[1], LOW); 

digitalWrite(motor_right[0], HIGH); 
digitalWrite(motor_right[1], LOW); 
}

void drive_backward(){
digitalWrite(motor_left[0], LOW); 
digitalWrite(motor_left[1], HIGH); 

digitalWrite(motor_right[0], LOW); 
digitalWrite(motor_right[1], HIGH); 
}

void turn_left(){
digitalWrite(motor_left[0], LOW); 
digitalWrite(motor_left[1], HIGH); 

digitalWrite(motor_right[0], HIGH); 
digitalWrite(motor_right[1], LOW);
}

void turn_right(){
digitalWrite(motor_left[0], HIGH); 
digitalWrite(motor_left[1], LOW); 

digitalWrite(motor_right[0], LOW); 
digitalWrite(motor_right[1], HIGH); 
}

unsigned int tiempo;
int getDistancia(){
  
  digitalWrite(pintrigger, LOW);
  delayMicroseconds(2);
  digitalWrite(pintrigger, HIGH);
  // EL PULSO DURA AL MENOS 10 uS EN ESTADO ALTO
  delayMicroseconds(10);
  digitalWrite(pintrigger, LOW);
 
  // MEDIR EL TIEMPO EN ESTADO ALTO DEL PIN "ECHO" EL PULSO ES PROPORCIONAL A LA DISTANCIA MEDIDA
  tiempo = pulseIn(pinecho, HIGH);
 
  // LA VELOCIDAD DEL SONIDO ES DE 340 M/S O 29 MICROSEGUNDOS POR CENTIMETRO
  // DIVIDIMOS EL TIEMPO DEL PULSO ENTRE 58, TIEMPO QUE TARDA RECORRER IDA Y VUELTA UN CENTIMETRO LA ONDA SONORA
  return tiempo / 58;  
}
