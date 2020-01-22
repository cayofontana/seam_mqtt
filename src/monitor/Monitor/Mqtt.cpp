#include "Mqtt.h"

Mqtt::Mqtt(WiFiClient wifi, const char *servidorMqtt, int porta, const char *usuario, const char *senha, const char *projeto, const char *idMqtt, const char *topicoMqtt) {
        mqtt = new PubSubClient(wifi);
        this->usuario = usuario;
        this->senha = senha;
        this->projeto = projeto;
        this->idMqtt = idMqtt;
        this->topicoMqtt = topicoMqtt;
        
        mqtt->setServer(servidorMqtt, porta);
        mqtt->setCallback([this] (char* topico, byte* pacote, unsigned int tamanho)
        {
                this->receberAssinatura(topico, pacote, tamanho);
        });
}

bool
Mqtt::conectar(void) {
        while (!mqtt->connected()) {
                if (mqtt->connect(projeto, usuario, senha))
                        return (true);
                else
                        delay(5000);
        }
}

void
Mqtt::permanecerConectado(void) {
        if (!mqtt->connected())
                conectar();
}

void
Mqtt::publicar(const char *mensagem) {
        mqtt->publish(topicoMqtt, mensagem);
}

void
Mqtt::receberAssinatura(char *topico, byte *pacote, unsigned int tamanho) {
        Serial.print("Message arrived [");
        Serial.print(topico);
        Serial.print("] ");
        for (int i = 0; i < tamanho; i++)
                Serial.print((char)pacote[i]);
        Serial.println();
        
        // Switch on the LED if an 1 was received as first character
        if ((char)pacote[0] == '1')
                digitalWrite(BUILTIN_LED, LOW);   // Turn the LED on (Note that LOW is the voltage level but actually the LED is on; this is because it is active low on the ESP-01)
        else
                digitalWrite(BUILTIN_LED, HIGH);  // Turn the LED off by making the voltage HIGH
}
