package com.uth.supereconomico.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.uth.supereconomico.MainActivity;
import com.uth.supereconomico.R;

public class NotificationHelper {
    public static final String CHANNEL_ID = "pedidos_canal";

    public static void showNotification(Context context, String title, String body, Long orderId) {
        android.content.SharedPreferences prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        
        // Si las notificaciones generales están apagadas, no hacer nada
        if (!prefs.getBoolean("notif_enabled", true)) return;

        boolean useSound = prefs.getBoolean("notif_sound", true);
        boolean useVibration = prefs.getBoolean("notif_vibration", true);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "pedidos_urgentes_v3"; 

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Alertas de Pedidos", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Canal para cambios de estado de pedidos");
            
            if (useVibration) {
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{0, 800, 200, 800});
            } else {
                channel.enableVibration(false);
            }
            
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            nm.createNotificationChannel(channel);
        }

        // Crear Intent para abrir la App en el pedido
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("order_id", orderId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                (int) System.currentTimeMillis(), 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.carrito)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (useSound) {
            builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        } else {
            builder.setDefaults(useVibration ? NotificationCompat.DEFAULT_VIBRATE : 0);
        }

        nm.notify((int) System.currentTimeMillis(), builder.build());
        
        // Vibración manual si está activa
        if (useVibration) {
            android.os.Vibrator v = (android.os.Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null && v.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(android.os.VibrationEffect.createOneShot(500, android.os.VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(500);
                }
            }
        }
    }
}
