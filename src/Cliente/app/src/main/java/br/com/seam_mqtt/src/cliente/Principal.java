package br.com.seam_mqtt.src.cliente;

import android.app.Application;
import android.app.Service;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class Principal extends Application {
    private Handler gerenciador;
    private Runnable executavel;

    public Principal() {
        gerenciador = new Handler();
        executavel = new Runnable() {
            @Override
            public void run() {
                final Vibrator vibrador = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    vibrador.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                else
                    vibrador.vibrate(500);
                gerenciador.postDelayed(this, 5000);
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        gerenciador.postDelayed(executavel, 5000);
    }
}
