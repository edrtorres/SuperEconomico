package com.uth.supereconomico.domain.repositories;

import com.uth.supereconomico.data.remote.models.OrderRequest;
import java.util.List;

public interface OrderRepository {
    interface Callback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    void createOrder(String perfilId, Long direccionId, String metodoPago, double total, List<OrderRequest.Item> items, Callback<Void> callback);
    void getOrders(String perfilId, Callback<List<OrderRequest>> callback);
    void deleteOrder(Long orderId, Callback<Void> callback);
    void getOrderItems(Long orderId, Callback<List<OrderRequest.Item>> callback);
    void updateItemQuantity(Long itemId, Integer newQuantity, Callback<Void> callback);
    void deleteOrderItem(Long itemId, Callback<Void> callback);
    void addItemToOrder(OrderRequest.Item item, Callback<Void> callback);
}
