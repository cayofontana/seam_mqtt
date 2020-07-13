package br.com.seam_mqtt.src.cliente;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.Calendar;
import java.util.Random;

import br.com.seam_mqtt.src.cliente.infraestrutura.Util;
import br.com.seam_mqtt.src.cliente.servico.MqttServico;

public class MainActivity extends AppCompatActivity {
    private final String ENDERECO_MQTT = "tcp://tailor.cloudmqtt.com:10011";
    private final String USUARIO = "ajfyskve";
    private final String SENHA = "S3SkYh43226v";
    private final String topico = "SEAM_SIRENE_01";
    private final int INTERVALO_RECONEXAO = 10000; // 10 segundos
    private final int INTERVALO_DESCONEXAO = 20000; // 20 seconds
    private MqttAndroidClient clienteMqtt;
    private MqttConnectOptions opcoesConexao;

    private Handler manipulador;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manipulador = new Handler();
        manipulador.post(monitorConexao);
        manipulador.postDelayed(monitorConexao, INTERVALO_DESCONEXAO);

        inicializarClienteMqtt();




    }

        public Runnable monitorConexao = new Runnable() {
        public void run() {
            conectarClienteMqtt();
            manipulador.postDelayed(monitorConexao, INTERVALO_RECONEXAO);
        }
    };

    private void definirInscricao(String topico){
        try {
            clienteMqtt.subscribe(topico,0);
        }
        catch (MqttException excecao) {
            excecao.printStackTrace();
        }
    }

    private void inicializarClienteMqtt() {
        if (clienteMqtt != null)
            clienteMqtt = null;

        String idClienteMqtt = MqttClient.generateClientId();
        clienteMqtt = new MqttAndroidClient(getApplicationContext(), ENDERECO_MQTT, idClienteMqtt);
        opcoesConexao = new MqttConnectOptions();
        MqttConnectOptions opcoesConexao = new MqttConnectOptions();
        opcoesConexao.setUserName(USUARIO);
        opcoesConexao.setPassword(SENHA.toCharArray());
        opcoesConexao.setCleanSession(false);
        opcoesConexao.setConnectionTimeout(60);
        opcoesConexao.setKeepAliveInterval(200);

        //setNewMqttClient();
    }

    private void conectarClienteMqtt() {
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
                registrarCallBack(new String(message.getPayload()), getApplicationContext());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    private void registrarCallBack(String mensagem, Context contexto) {//(Notificacao notificacao, Context contexto) {
        Intent intent = new Intent();
        int id = new Random().nextInt(1000);
        intent.putExtra("seam", id);
        intent.setAction(".MainActivity");
        sendBroadcast(intent);

        Util.notificar(contexto, id, "SEAM ", mensagem + "Alerta em " + Calendar.getInstance().getTime().toString());
    }
}

//public class MainActivity extends AppCompatActivity {
//    MainActivityReceiver receiver = new MainActivityReceiver();
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        Intent intent = new Intent(this, MqttServico.class);
//        startService(intent);
//    }
//
//    @Override
//    protected void onResume() {
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(".MainActivity");
//        registerReceiver(receiver, intentFilter);
//
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        unregisterReceiver(receiver);
//        super.onPause();
//    }
//
//    private class MainActivityReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Bundle extras = intent.getExtras();
//            Long idNotificacao = extras.getLong("seam");
//            //exibirNotificacoes();
//            intent.removeExtra("seam");
//        }
//    }
//}