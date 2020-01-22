#ifndef INFRAVERMELHO_H
#define INFRAVERMELHO_H

#include "Sensor.h"

class InfraVermelho: public Sensor {
public:
        InfraVermelho(uint8_t pinoEntrada, uint16_t frequencia, uint16_t intervalo);
        
        void detectar(void);

private:
        int valorEntrada;
        void executar(void);
};

#endif
