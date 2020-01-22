#ifndef SENSOR_H
#define SENSOR_H

#include "Thread.h"

class Sensor: public Thread {
public:
        Sensor(uint8_t pinoEntrada, uint16_t frequencia, uint16_t intervalo);

        bool existeObjeto(void);
        virtual void detectar(void) = 0;

protected:
        bool objetoDetectado;

        uint8_t getPinoEntrada(void);
        uint16_t getFrequencia(void);
        uint16_t getIntervalo(void);

private:
        uint8_t pinoEntrada;
        uint16_t frequencia;
        uint16_t intervalo;
};

#endif
