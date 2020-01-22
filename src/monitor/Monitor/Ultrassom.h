#ifndef ULTRASSOM_H
#define ULTRASSOM_H

#include "Sensor.h"

class Ultrassom: public Sensor
{
public:
        Ultrassom(uint8_t pinoEntrada, uint16_t frequencia, uint16_t intervalo, uint8_t pinoSaida, uint16_t distancia, uint16_t limiteDeteccoes);
        
        void detectar(void);

private:
        const float velocidadeSom = 0.034;
        uint8_t pinoSaida;
        uint16_t distancia;
        uint16_t limiteDeteccoes;
        unsigned long distanciaEcoada;
        uint8_t deteccoes;
        
        void executar(void);
};

#endif
