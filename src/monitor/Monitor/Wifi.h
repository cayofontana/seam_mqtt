#ifndef WIFI_H
#define WIFI_H

#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>

class Wifi
{
public:
        Wifi(const char *nome, const char *senha);
        
        bool conectar(void);
        bool enviarDados(const char *endereco, uint16_t porta, const char *arquivo, uint16_t valor);
        void desconectar(void);
        WiFiClient *obterClienteWifi(void);

private:
        const char *nome;
        const char *senha;
        
        void criarConexao(void);
        bool estahConectado(void);
};

#endif
