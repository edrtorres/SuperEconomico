package com.uth.supereconomico.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.uth.supereconomico.R;
import com.uth.supereconomico.data.remote.RetrofitClient;
import com.uth.supereconomico.data.remote.SesionSupabase;
import com.uth.supereconomico.data.remote.SupabaseApi;
import com.uth.supereconomico.data.remote.models.OrderRequest;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import retrofit2.Response;

public class OrderRealtimeService extends Service {
    private static final String PREF_ORDERS = "orders_realtime_prefs";
    private ScheduledExecutorService scheduler;
    private static final String CHANNEL_ID = "service_monitor";

    @Override
    public void onCreate() {
        super.onCreate();
        crearCanalMonitoreo();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SuperEconomico Activo")
                .setContentText("Vigilando tus pedidos en tiempo real...")
                .setSmallIcon(R.drawable.carrito)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        
        startForeground(100, notification);
        iniciarRastreo();
    }

    private void iniciarRastreo() {
        SharedPreferences settings = getSharedPreferences("app_settings", MODE_PRIVATE);
        int interval = settings.getInt("sync_interval_seconds", 30);
        
        if (scheduler != null) scheduler.shutdown();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(this::revisarPedidos, 0, interval, TimeUnit.SECONDS);
    }

    private void revisarPedidos() {
        SharedPreferences settings = getSharedPreferences("app_settings", MODE_PRIVATE);
        if (!settings.getBoolean("notif_enabled", true)) {
            // Si el usuario apagó las notificaciones, detenemos el rastreo por ahora
            if (scheduler != null) scheduler.shutdown();
            stopSelf();
            return;
        }

        SesionSupabase.inicializar(this);
        if (!SesionSupabase.haySesionActiva()) return;

        String userId = SesionSupabase.obtenerIdUsuario();
        
        // Si no tenemos el ID, intentamos recuperarlo por seguridad
        if (userId == null || userId.isEmpty()) {
            android.util.Log.e("RealtimeService", "ID de usuario nulo, reintentando carga...");
            // Si el ID es nulo, no podemos filtrar, abortamos este ciclo
            return;
        }

        android.util.Log.d("RealtimeService", "Revisando pedidos para usuario: " + userId);
        SupabaseApi api = RetrofitClient.getClient().create(SupabaseApi.class);
        try {
            // Buscamos cambios en los pedidos
            Response<List<OrderRequest>> response = api.getPedidos("eq." + userId, "*", "id.desc").execute();
            if (response.isSuccessful() && response.body() != null) {
                android.util.Log.d("RealtimeService", "Pedidos encontrados: " + response.body().size());
                compararEstados(response.body());
            } else {
                android.util.Log.e("RealtimeService", "Error en respuesta: " + response.code());
            }
        } catch (Exception e) {
            android.util.Log.e("RealtimeService", "Error de red: " + e.getMessage());
        }
    }

    private void compararEstados(List<OrderRequest> orders) {
        SharedPreferences prefs = getSharedPreferences(PREF_ORDERS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        for (OrderRequest order : orders) {
            String key = "status_" + order.id;
            String lastStatus = prefs.getString(key, null);
            String currentStatus = order.estado;

            // Log de depuración
            android.util.Log.d("RealtimeService", "Orden #" + order.id + " -> BD: " + currentStatus + " | Local: " + lastStatus);

            if (lastStatus != null && !lastStatus.equalsIgnoreCase(currentStatus)) {
                android.util.Log.i("RealtimeService", "¡CAMBIO DETECTADO! Notificando...");
                NotificationHelper.showNotification(this, 
                    "El Economico: Orden #" + order.id, 
                    obtenerMensajeEstado(currentStatus),
                    order.id);
            }
            
            // Si es la primera vez que vemos el pedido y ya no es 'pendiente', avisamos también
            if (lastStatus == null && !"pendiente".equalsIgnoreCase(currentStatus)) {
                 NotificationHelper.showNotification(this, 
                    "Actualización de Pedido #" + order.id, 
                    obtenerMensajeEstado(currentStatus),
                    order.id);
            }

            editor.putString(key, currentStatus);
        }
        editor.apply();
    }

    private String obtenerMensajeEstado(String s) {
        if ("preparando".equals(s)) return "¡Estamos preparando tus productos! 📦";
        if ("en_camino".equals(s)) return "¡El repartidor ya va hacia ti! 🛵";
        if ("entregado".equals(s)) return "¡Pedido entregado con éxito! 😊";
        return "Tu pedido cambió a: " + s.toUpperCase();
    }

    private void crearCanalMonitoreo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Servicio de Monitoreo", NotificationManager.IMPORTANCE_LOW);
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }

    @Override
    public void onDestroy() {
        if (scheduler != null) scheduler.shutdown();
        super.onDestroy();
    }

    @Nullable @Override
    public IBinder onBind(Intent intent) { return null; }
}
