#ifndef ATUADOR_H
#define ATUADOR_H

#include <Arduino.h>
#include "Estado.h"

class Atuador {
public:
        Atuador(uint8_t pino);
        void atualizar(Estado estado);
private:
        uint8_t pino;
        Estado estado;
};

#endif
