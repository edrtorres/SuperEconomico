package com.uth.supereconomico.domain.repositories;

import com.uth.supereconomico.domain.entities.Pedido;
import java.util.List;

public interface OrderRepository {
    interface Callback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    void placeOrder(Pedido pedido, Callback<Pedido> callback);
    void getMyOrders(String usuarioId, Callback<List<Pedido>> callback);
    void getAllActiveOrders(Callback<List<Pedido>> callback);
    void updateOrderStatus(String pedidoId, Pedido.Estado estado, Callback<Void> callback);
    void rateOrder(String pedidoId, int calificacion, Callback<Void> callback);
}
