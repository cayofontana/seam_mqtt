package br.com.seam_mqtt.src.cliente;

import android.app.Application;
import android.content.Intent;

import br.com.seam_mqtt.src.cliente.servico.MqttServico;

public class Principal extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Intent intencao = new Intent(this, MqttServico.class);
        startService(intencao);

//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
    }
}
