#include "Atuador.h"

Atuador::Atuador(uint8_t pino) {
        this->pino = pino;
        pinMode(this->pino, OUTPUT);
        digitalWrite(this->pino, LOW);
        estado = Estado::DESLIGADO;
}

void
Atuador::atualizar(Estado estado) {
        this->estado = estado;
        digitalWrite(this->pino, this->estado == Estado::LIGADO ? HIGH : LOW);
}
