package com.uth.supereconomico.data.repositories;

import com.uth.supereconomico.data.remote.SupabaseApi;
import com.uth.supereconomico.data.remote.models.OrderRequest;
import com.uth.supereconomico.domain.repositories.OrderRepository;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

public class OrderRepositoryImpl implements OrderRepository {
    private final SupabaseApi supabaseApi;

    public OrderRepositoryImpl(SupabaseApi supabaseApi) {
        this.supabaseApi = supabaseApi;
    }

    @Override
    public void createOrder(String perfilId, Long direccionId, String metodoPago, double total, List<OrderRequest.Item> items, Callback<Void> callback) {
        OrderRequest request = new OrderRequest();
        request.perfilId = perfilId;
        request.direccionId = direccionId;
        request.metodoPago = metodoPago;
        request.total = total;
        request.estado = "pendiente";

        supabaseApi.crearPedido("return=representation", request).enqueue(new retrofit2.Callback<List<OrderRequest>>() {
            @Override
            public void onResponse(Call<List<OrderRequest>> call, Response<List<OrderRequest>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    long pedidoId = response.body().get(0).id;
                    for (OrderRequest.Item item : items) {
                        item.pedidoId = pedidoId;
                    }
                    insertarItems(items, callback);
                } else {
                    callback.onError("Error al crear el encabezado: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<OrderRequest>> call, Throwable t) {
                callback.onError("Fallo de red: " + t.getMessage());
            }
        });
    }

    @Override
    public void getOrders(String perfilId, Callback<List<OrderRequest>> callback) {
        supabaseApi.getPedidos("eq." + perfilId, "*", "creado_at.desc").enqueue(new retrofit2.Callback<List<OrderRequest>>() {
            @Override
            public void onResponse(Call<List<OrderRequest>> call, Response<List<OrderRequest>> response) {
                if (response.isSuccessful()) callback.onSuccess(response.body());
                else callback.onError("Error al obtener pedidos: " + response.code());
            }
            @Override
            public void onFailure(Call<List<OrderRequest>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    @Override
    public void deleteOrder(Long orderId, Callback<Void> callback) {
        if (orderId == null) return;
        supabaseApi.deletePedido("eq." + orderId).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onSuccess(null);
                else callback.onError("Error al eliminar pedido: " + response.code());
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    @Override
    public void getOrderItems(Long orderId, Callback<List<OrderRequest.Item>> callback) {
        if (orderId == null) return;
        // Realizamos un join con la tabla productos para obtener nombre e imagen_url
        supabaseApi.getPedidoItems("eq." + orderId, "*,productos(nombre,imagen_url)").enqueue(new retrofit2.Callback<List<OrderRequest.Item>>() {
            @Override
            public void onResponse(Call<List<OrderRequest.Item>> call, Response<List<OrderRequest.Item>> response) {
                if (response.isSuccessful()) callback.onSuccess(response.body());
                else callback.onError("Error al obtener detalles: " + response.code());
            }
            @Override
            public void onFailure(Call<List<OrderRequest.Item>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    @Override
    public void updateItemQuantity(Long itemId, Integer newQuantity, Callback<Void> callback) {
        if (itemId == null) return;
        OrderRequest.Item update = new OrderRequest.Item();
        update.cantidad = newQuantity;
        
        supabaseApi.updateItemPedido("eq." + itemId, update).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onSuccess(null);
                else callback.onError("Error al actualizar cantidad: " + response.code());
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    @Override
    public void deleteOrderItem(Long itemId, Callback<Void> callback) {
        if (itemId == null) return;
        supabaseApi.deleteItemPedido("eq." + itemId).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onSuccess(null);
                else callback.onError("Error al eliminar item: " + response.code());
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    @Override
    public void addItemToOrder(OrderRequest.Item item, Callback<Void> callback) {
        if (item == null) return;
        java.util.List<OrderRequest.Item> list = new java.util.ArrayList<>();
        list.add(item);
        supabaseApi.crearItemsPedido(list).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onSuccess(null);
                else callback.onError("Error al agregar item: " + response.code());
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    private void insertarItems(List<OrderRequest.Item> items, Callback<Void> callback) {
        supabaseApi.crearItemsPedido(items).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error al insertar productos del pedido: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Fallo de red al insertar productos: " + t.getMessage());
            }
        });
    }
}
