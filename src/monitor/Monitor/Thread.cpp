#include "Thread.h"

Thread::Thread() {
        habilitado = true;
        proximaExecucao = 0;
        ultimaExecucao = millis();
}

void
Thread::setIntervalo(unsigned long intervalo) {
        this->intervalo = intervalo;
        proximaExecucao = ultimaExecucao + intervalo;
}

void
Thread::executar() {
        executado();
}

bool
Thread::deveExecutar(unsigned long horario) {
        bool restaAlgumTempo = (horario - proximaExecucao) & 0x80000000;
        return (!restaAlgumTempo && habilitado);
}

void
Thread::executado(unsigned long horario) {
        ultimaExecucao = horario;
        proximaExecucao = ultimaExecucao + intervalo;
}
