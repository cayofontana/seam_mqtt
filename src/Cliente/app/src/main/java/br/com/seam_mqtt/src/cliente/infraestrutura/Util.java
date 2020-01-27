package br.com.seam_mqtt.src.cliente.infraestrutura;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.seam_mqtt.src.cliente.MainActivity;
import br.com.seam_mqtt.src.cliente.R;

public class Util {
    private Util() {}

    public static String alterarFormatoData(String strData, String formato) {
        String novaStrData = null;
        SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date data = null;
        try {
            data = formatoData.parse(strData);
            formatoData.applyPattern("dd/MM/yyyy hh:mm:ss");
            novaStrData = formatoData.format(data);
        }
        catch (ParseException excecao) {
            excecao.printStackTrace();
        }
        return (novaStrData);
    }

    public static void notificar(Context contexto, int id, String titulo, String mensagem) {
        NotificationManager gerenteNotificacao = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNEL_ID = "seam_01";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence nome = "seam";
            String descricao = "Canal App SEAM";
            int importancia = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel canalNotificacao = new NotificationChannel(CHANNEL_ID, nome, importancia);
            canalNotificacao.setDescription(descricao);
            canalNotificacao.enableLights(true);
            canalNotificacao.setLightColor(Color.RED);
            canalNotificacao.enableVibration(true);
            canalNotificacao.setVibrationPattern(new long[] { 100, 200, 300, 400, 500, 400, 300, 200, 400 });
            canalNotificacao.setShowBadge(false);
            gerenteNotificacao.createNotificationChannel(canalNotificacao);
        }

        NotificationCompat.Builder construtor = new NotificationCompat.Builder(contexto, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(titulo)
                .setContentText(mensagem)
                .setAutoCancel(true);

        Intent intent = new Intent(contexto, MainActivity.class);
        TaskStackBuilder pilhaTarefasConstrutor = TaskStackBuilder.create(contexto);
        pilhaTarefasConstrutor.addParentStack(MainActivity.class);
        pilhaTarefasConstrutor.addNextIntent(intent);
        PendingIntent resultPendingIntent = pilhaTarefasConstrutor.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        construtor.setContentIntent(resultPendingIntent);

        gerenteNotificacao.notify(id, construtor.build());
    }

    public static void cancelarNotificacao(Context contexto, int id) {
        String servicoNotificacao = Context.NOTIFICATION_SERVICE;
        NotificationManager gerenteNotificacao = (NotificationManager) contexto.getSystemService(servicoNotificacao);
        gerenteNotificacao.cancel(id);
    }
}
