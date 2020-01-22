#ifndef MQTT_H
#define MQTT_H

#include <PubSubClient.h>
#include <ESP8266WiFi.h>

class Mqtt {
public:
      Mqtt(WiFiClient wifi, const char *servidorMqtt, int porta, const char *usuario, const char *senha, const char *projeto, const char *idMqtt, const char *topicoMqtt);

      void publicar(const char *mensagem);
      void receberAssinatura(char *topico, byte *pacote, unsigned int tamanho);
      bool conectar(void);
      void permanecerConectado(void);

private:
      PubSubClient *mqtt;
      const char *usuario;
      const char *senha;
      const char *projeto;
      const char *idMqtt;
      const char *topicoMqtt;
};

#endif
