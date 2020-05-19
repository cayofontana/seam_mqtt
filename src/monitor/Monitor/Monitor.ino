// MONITOR - SEAM: Programa Principal
// Descrição: este programa possui uma lista de sensores onde a cada laço (loop)
//            do programa (função loop()), cada elemento (objeto sensor) da lista 
//            verifica se algum objeto foi sensoriado (capturado). Caso todos os 
//            Sensores acusem a presença de um objeto, simultanemanete em um 
//            determinado período, um LED é ligado para informar a detecção conjunta.

#include <vector>
#include <memory>
#include "Sensor.h"
#include "Atuador.h"
#include "Ultrassom.h"
#include "InfraVermelho.h"
#include "Wifi.h"
#include "Mqtt.h"

std::vector<std::shared_ptr<Sensor>> sensores;
std::vector<Atuador> atuadores;
Wifi wifi("CAYO", "cayo220383");
Mqtt mqtt(*wifi.obterClienteWifi(), "tailor.cloudmqtt.com", 10011, "ajfyskve", "S3SkYh43226v", "SEAM", "SEAM_01", "SEAM_SIRENE_01");
bool dadosEnviados;

void setup() {
        Serial.begin(115200);

        sensores.push_back(std::make_shared<Ultrassom>(14, 100, 15000, 12, 50, 20));
        sensores.push_back(std::make_shared<InfraVermelho>(10, 10, 15000));

        atuadores.push_back(Atuador(15));

        dadosEnviados = false;
}

void loop() {
        bool objetoDetectado = false;
        
        for (std::vector<std::shared_ptr<Sensor>>::iterator sensor = sensores.begin(); sensor != sensores.end(); ++sensor)
                (*sensor)->detectar();

        for (std::vector<std::shared_ptr<Sensor>>::iterator sensor = sensores.begin(); sensor != sensores.end(); ++sensor) {
                if (!(*sensor)->existeObjeto()) {
                        objetoDetectado = false;
                        dadosEnviados = false;
                        break;
                }
                objetoDetectado = true;
        }

        if (objetoDetectado) {
                if (!dadosEnviados && wifi.conectar() && mqtt.conectar()) {
                        for (std::vector<Atuador>::iterator atuador = atuadores.begin(); atuador != atuadores.end(); ++atuador)
                                atuador->atualizar(Estado::LIGADO);
                        mqtt.publicar("1");
                        dadosEnviados = !dadosEnviados;
                }
        }
        else
                for (std::vector<Atuador>::iterator atuador = atuadores.begin(); atuador != atuadores.end(); ++atuador)
                        atuador->atualizar(Estado::DESLIGADO);

        mqtt.permanecerConectado();
}
