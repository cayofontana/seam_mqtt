package br.com.seam_mqtt.src.cliente;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    private final String ENDERECO_MQTT = "tcp://tailor.cloudmqtt.com:10011";
    private final String USUARIO = "ajfyskve";
    private final String SENHA = "S3SkYh43226v";
    private final String topico = "SEAM_SIRENE_01";
    private MqttAndroidClient clienteMqtt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String idClienteMqtt = MqttClient.generateClientId();
        clienteMqtt = new MqttAndroidClient(getApplicationContext(), ENDERECO_MQTT, idClienteMqtt);
        MqttConnectOptions opcoesConexao = new MqttConnectOptions();
        opcoesConexao.setUserName(USUARIO);
        opcoesConexao.setPassword(SENHA.toCharArray());

        try {
            IMqttToken tokenMqtt = clienteMqtt.connect(opcoesConexao);
            tokenMqtt.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "conectado", Toast.LENGTH_LONG).show();
                    definirInscricao(topico);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "não conectado", Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (MqttException excecao) {
            excecao.printStackTrace();
        }

        clienteMqtt.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Toast.makeText(MainActivity.this, new String(message.getPayload()), Toast.LENGTH_LONG).show();
                final Vibrator vibrador = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    vibrador.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                else
                    vibrador.vibrate(500);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    private void definirInscricao(String topico){
        try {
            clienteMqtt.subscribe(topico,0);
        }
        catch (MqttException excecao) {
            excecao.printStackTrace();
        }
    }
}