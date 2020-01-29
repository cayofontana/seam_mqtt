package br.com.seam_mqtt.src.cliente.servico;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import br.com.seam_mqtt.src.cliente.infraestrutura.Util;

public class MqttServico extends Service implements MqttCallback, IMqttActionListener {

    private final IBinder binder = new MyBinder();

    private MqttAndroidClient mqttClient;
    private MqttConnectOptions mqttConnectOptions;
    private static final MemoryPersistence persistence = new MemoryPersistence();
    private ArrayList<MqttAndroidClient> lostConnectionClients;

    private String clientId = MqttClient.generateClientId();
    private boolean isReady = false;
    private boolean doConnectTask = true;
    private boolean isConnectInvoked = false;
    private final String USUARIO = "ajfyskve";
    private final String SENHA = "S3SkYh43226v";

    private Handler handler = new Handler();
    private final int RECONNECT_INTERVAL = 10000; // 10 seconds
    private final int DISCONNECT_INTERVAL = 20000; // 20 seconds
    private final int CONNECTION_TIMEOUT = 60;
    private final int KEEP_ALIVE_INTERVAL = 200;

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
        Log.i("MqttServico", "Serviço iniciado!");
        initMqttClient();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        disconnectClients();
        if (isConnectInvoked && mqttClient != null && mqttClient.isConnected()) {
            try {
                // unsubscribe here
                unsubscribe("¯SEAM_SIRENE_01");
                mqttClient.disconnect();
            }
            catch (MqttException e) {
                Log.e("TAG", e.toString());
            }
        }

        handler.removeCallbacks(connect);
        handler.removeCallbacks(disconnect);
    }

    private void initMqttClient() {
        if (mqttClient != null) {
            mqttClient = null;
        }

        lostConnectionClients = new ArrayList<>();

        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(USUARIO);
        mqttConnectOptions.setPassword(SENHA.toCharArray());
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setConnectionTimeout(CONNECTION_TIMEOUT);
        mqttConnectOptions.setKeepAliveInterval(KEEP_ALIVE_INTERVAL);

        setNewMqttClient();

        handler.post(connect);
        handler.postDelayed(disconnect, DISCONNECT_INTERVAL);
    }

    private void setNewMqttClient() {
        mqttClient = new MqttAndroidClient(MqttServico.this, broker_url, clientId, persistence);
        mqttClient.setCallback(this);
    }

    public Runnable connect = new Runnable() {
        public void run() {
            connectClient();
            handler.postDelayed(connect, RECONNECT_INTERVAL);
        }
    };

    public Runnable disconnect = new Runnable() {
        public void run() {
            disconnectClients();
            handler.postDelayed(disconnect, DISCONNECT_INTERVAL);
        }
    };

    private void connectClient() {
        if (doConnectTask) {
            doConnectTask = false;

            try {
                isConnectInvoked = true;
                mqttClient.connect(mqttConnectOptions, null, this);
            }
            catch (MqttException ex) {
                doConnectTask = true;
                Log.e("TAG", ex.toString());
            }
        }
    }

    private void disconnectClients() {
        if (lostConnectionClients.size() > 0) {
            // Disconnect lost connection clients
            for (MqttAndroidClient client : lostConnectionClients) {
                if (client.isConnected()) {
                    try {
                        client.disconnect();
                    }
                    catch (MqttException e) {
                        Log.e("TAG", e.toString());
                    }
                }
            }

            // Close already disconnected clients
            for (int i = lostConnectionClients.size() - 1; i >= 0; i--) {
                try {
                    if (!lostConnectionClients.get(i).isConnected()) {
                        MqttAndroidClient client = lostConnectionClients.get(i);
                        client.unregisterResources();
                        client.close();
                        lostConnectionClients.remove(i);
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    Log.e("TAG", e.toString());
                }
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.e("TAG", "deliveryComplete()");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        Toast.makeText(this, payload, Toast.LENGTH_LONG).show();
        registrarCallBack(new String(message.getPayload()), this);
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.e("TAG", cause.getMessage());
    }

    @Override
    public void onSuccess(IMqttToken iMqttToken) {
        isReady = true;

        Toast.makeText(this, "conectado", Toast.LENGTH_LONG).show();
        subscribe("SEAM_SIRENE_01");
    }

    @Override
    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
        Toast.makeText(this, "Não conectado", Toast.LENGTH_LONG).show();
        setNewMqttClient();
        isReady = false;
        doConnectTask = true;
        isConnectInvoked = false;
    }

    private void subscribe(String topic) {
        try {
            mqttClient.subscribe(topic, 0);
            isReady = true;
        }
        catch (MqttSecurityException mqttSexEx) {
            isReady = false;
        }
        catch (MqttException mqttEx) {
            isReady = false;
        }
    }

    private void unsubscribe(String topic) {
        try {
            mqttClient.unsubscribe(topic);
        }
        catch (MqttSecurityException mqttSecEx) {
            Log.e("TAG", mqttSecEx.getMessage());
        }
        catch (MqttException mqttEx) {
            Log.e("TAG", mqttEx.getMessage());
        }
    }

    private void publish(String topic, String jsonPayload) {
        if (isReady) {
            try {
                MqttMessage msg = new MqttMessage();
                msg.setQos(0);
                msg.setPayload(jsonPayload.getBytes("UTF-8"));
                mqttClient.publish(topic, msg);
            }
            catch (Exception ex) {
                Log.e("TAG", ex.toString());
            }
        }
    }

    private void registrarCallBack(String mensagem, Context contexto) {//(Notificacao notificacao, Context contexto) {
        Intent intent = new Intent();
        int id = new Random().nextInt(1000);
        intent.putExtra("seam", id);
        intent.setAction(".MainActivity");
        sendBroadcast(intent);

        Util.notificar(contexto, id, "ALARME DISPARADO! ", mensagem + "Possível invasão ocorrida em " + Calendar.getInstance().getTime().toString());
    }
}