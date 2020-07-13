package br.com.seam_mqtt.src.cliente.servico;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Calendar;
import java.util.Random;

import br.com.seam_mqtt.src.cliente.infraestrutura.Util;

//public class MqttServico extends Service implements MqttCallback, IMqttActionListener {
public class MqttServico extends Service {
    private final IBinder binder = new MyBinder();

    private final String ENDERECO_MQTT = "tcp://tailor.cloudmqtt.com:10011";
    private final String USUARIO = "ajfyskve";
    private final String SENHA = "S3SkYh43226v";
    private final String topico = "SEAM_SIRENE_01";
    private MqttAndroidClient clienteMqtt;

    private String broker_url = "tcp://tailor.cloudmqtt.com:10011";

    public MqttServico() {}

    public class MyBinder extends Binder {
        public MqttServico getService() {
            return (MqttServico.this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return (binder);
    }

    @Override
    public void onCreate() {
        super.onCreate();

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
                    definirInscricao(topico);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
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

    @Override
    public void onDestroy() {
        //if (isConnectInvoked && clienteMqtt != null && clienteMqtt.isConnected()) {
        if (clienteMqtt != null && clienteMqtt.isConnected()) {
            try {
                clienteMqtt.disconnect();
                clienteMqtt.unregisterResources();
                clienteMqtt.close();
            }
            catch (MqttException e) {
                Log.e("TAG", e.toString());
            }
        }
        super.onDestroy();
    }

    private void definirInscricao(String topico){
        try {
            clienteMqtt.subscribe(topico,0);
        }
        catch (MqttException excecao) {
            excecao.printStackTrace();
        }
    }

//    public Runnable connect = new Runnable() {
//        public void run() {
//            connectClient();
//            handler.postDelayed(connect, RECONNECT_INTERVAL);
//        }
//    };
//
//    public Runnable disconnect = new Runnable() {
//        public void run() {
//            disconnectClients();
//            handler.postDelayed(disconnect, DISCONNECT_INTERVAL);
//        }
//    };


    private void registrarCallBack(String mensagem, Context contexto) {//(Notificacao notificacao, Context contexto) {
        Intent intent = new Intent();
        int id = new Random().nextInt(1000);
        intent.putExtra("seam", id);
        intent.setAction(".MainActivity");
        sendBroadcast(intent);

        Util.notificar(contexto, id, "ALARME DISPARADO! ", mensagem + "Possível invasão ocorrida em " + Calendar.getInstance().getTime().toString());
    }
}
