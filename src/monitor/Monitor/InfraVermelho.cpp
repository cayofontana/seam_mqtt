#include "InfraVermelho.h"

InfraVermelho::InfraVermelho(uint8_t pinoEntrada, uint16_t frequencia, uint16_t intervalo) : Sensor(pinoEntrada, frequencia, intervalo) {
        valorEntrada = 0;
}

void
InfraVermelho::detectar(void) {
        if (deveExecutar()) {
                setIntervalo(getFrequencia());
                executar();

                if (valorEntrada == HIGH) {
                        setIntervalo(getIntervalo());
                        objetoDetectado = true;
                        Serial.println("Infravermelho Detectou!");
                }
                else
                        objetoDetectado = false;
        }
}

void
InfraVermelho::executar(void) {
        valorEntrada = digitalRead(getPinoEntrada());
        
        executado();
}
