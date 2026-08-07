package com.uth.supereconomico.domain.repositories;

import com.uth.supereconomico.domain.entities.Pedido;
import java.util.List;

public interface OrderRepository {
    interface Callback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    void createOrder(String perfilId, Long direccionId, String metodoPago, double total, List<Pedido.Item> items, Callback<Void> callback);
    void getOrders(String perfilId, Callback<List<Pedido>> callback);
    void deleteOrder(Long orderId, Callback<Void> callback);
    void getOrderItems(Long orderId, Callback<List<Pedido.Item>> callback);
    void updateItemQuantity(Long itemId, Integer newQuantity, Callback<Void> callback);
    void deleteOrderItem(Long itemId, Callback<Void> callback);
}
