#include "Wifi.h"

Wifi::Wifi(const char *nome, const char *senha)
{
        this->nome = nome;
        this->senha = senha;
}

bool
Wifi::conectar(void)
{
        if (estahConectado())
                return (true);

        criarConexao();
                
        while (!estahConectado())
                delay(500);
        
        return (true);
}

bool
Wifi::enviarDados(const char *endereco, uint16_t porta, const char *arquivo, uint16_t valor)
{
        HTTPClient clienteHTTP;
        
        clienteHTTP.begin((String)"http://" + endereco + (String)":" + porta + (String)"/" + arquivo + (String)"?distancia_media=" + (String)valor);
        int codigoRetornoHTTP = clienteHTTP.GET();
        
        if (codigoRetornoHTTP > 0)
                Serial.println("Erro:" + (String)clienteHTTP.getString());
        
        clienteHTTP.end();
}

WiFiClient *
Wifi::obterClienteWifi(void) {
        WiFiClient clienteWifi;
        return (&clienteWifi);
}

void
Wifi::desconectar(void)
{
        WiFi.disconnect();
}

void
Wifi::criarConexao(void)
{
        WiFi.mode(WIFI_STA);
        WiFi.begin(nome, senha);
}

bool
Wifi::estahConectado(void)
{
        return (WiFi.status() == WL_CONNECTED);
}
