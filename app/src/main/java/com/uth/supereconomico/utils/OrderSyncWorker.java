package com.uth.supereconomico.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.uth.supereconomico.data.remote.RetrofitClient;
import com.uth.supereconomico.data.remote.SesionSupabase;
import com.uth.supereconomico.data.remote.SupabaseApi;
import com.uth.supereconomico.data.remote.models.OrderDTO;
import java.util.List;
import retrofit2.Response;

public class OrderSyncWorker extends Worker {
    private static final String PREF_ORDERS = "orders_status_prefs";

    public OrderSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SesionSupabase.inicializar(getApplicationContext());
        if (!SesionSupabase.haySesionActiva()) return Result.success();

        String userId = SesionSupabase.obtenerIdUsuario();
        if (userId == null) return Result.success();

        SupabaseApi api = RetrofitClient.getClient().create(SupabaseApi.class);
        try {
            // Buscamos los últimos pedidos del usuario
            Response<List<OrderDTO>> response = api.getPedidos("eq." + userId, "*", "id.desc").execute();
            
            if (response.isSuccessful() && response.body() != null) {
                checkChanges(response.body());
            }
        } catch (Exception e) {
            return Result.retry();
        }

        return Result.success();
    }

    private void checkChanges(List<OrderDTO> orders) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREF_ORDERS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        for (OrderDTO order : orders) {
            String key = "order_" + order.id;
            String lastStatus = prefs.getString(key, null);
            String currentStatus = order.estado;

            if (lastStatus != null && !lastStatus.equals(currentStatus)) {
                // El estado cambió, notificar al usuario
                notifyUser(order.id, currentStatus);
            }
            
            // Guardar el estado actual para la próxima revisión
            editor.putString(key, currentStatus);
        }
        editor.apply();
    }

    private void notifyUser(Long id, String status) {
        String title = "Actualización de Pedido #" + id;
        String body = "Tu pedido ahora está: " + status.toUpperCase();
        
        if ("preparando".equals(status)) body = "¡Estamos preparando tus productos! 📦";
        if ("en_camino".equals(status)) body = "¡El repartidor va hacia tu casa! 🛵";
        if ("entregado".equals(status)) body = "¡Pedido entregado! Gracias por tu compra. 😊";

        NotificationHelper.showNotification(getApplicationContext(), title, body, id);
    }
}
