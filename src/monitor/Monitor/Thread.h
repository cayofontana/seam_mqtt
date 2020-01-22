#ifndef THREAD_H
#define THREAD_H

#include <Arduino.h>

class Thread {
protected:
        Thread();
        
        virtual void setIntervalo(unsigned long intervalo);
        virtual void executar();
        bool deveExecutar() {
                return deveExecutar(millis());
        }
        void executado() {
                executado(millis());
        }
        
private:
        bool habilitado;
        unsigned long intervalo;
        unsigned long ultimaExecucao;
        unsigned long proximaExecucao;

        void executado(unsigned long horario);
        virtual bool deveExecutar(unsigned long horario);
};

#endif
