package com.uth.supereconomico.data.repositories;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.uth.supereconomico.data.remote.SupabaseApi;
import com.uth.supereconomico.data.remote.models.OrderDTO;
import com.uth.supereconomico.domain.entities.Pedido;
import com.uth.supereconomico.domain.repositories.OrderRepository;
import com.uth.supereconomico.utils.UserFriendlyError;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Response;

public class OrderRepositoryImpl implements OrderRepository {
    private final SupabaseApi supabaseApi;

    public OrderRepositoryImpl(SupabaseApi supabaseApi) {
        this.supabaseApi = supabaseApi;
    }

    @Override
    public void createOrder(String perfilId, Long direccionId, String metodoPago, double total, List<Pedido.Item> items, Callback<Void> callback) {
        JsonArray requestItems = new JsonArray();
        for (Pedido.Item item : items) {
            JsonObject requestItem = new JsonObject();
            requestItem.addProperty("producto_id", item.getProductoId());
            requestItem.addProperty("cantidad", item.getCantidad());
            requestItems.add(requestItem);
        }

        JsonObject request = new JsonObject();
        if (direccionId == null) {
            request.add("p_direccion_id", JsonNull.INSTANCE);
        } else {
            request.addProperty("p_direccion_id", direccionId);
        }
        request.addProperty("p_metodo_pago", metodoPago);
        request.add("p_items", requestItems);

        supabaseApi.crearPedidoSeguro(request).enqueue(new retrofit2.Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(detalleError(response));
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                callback.onError(UserFriendlyError.fromThrowable(t));
            }
        });
    }

    private String detalleError(Response<?> response) {
        String detalle = null;
        try {
            if (response.errorBody() != null) {
                detalle = response.errorBody().string();
            }
        } catch (IOException ignored) {
            detalle = null;
        }

        if (detalle == null || detalle.trim().isEmpty()) {
            return UserFriendlyError.fromResponse(response, "No se pudo crear el pedido");
        }
        String friendly = UserFriendlyError.fromMessage(detalle);
        return friendly.equals(detalle) ? UserFriendlyError.fromResponse(response, "No se pudo crear el pedido") : friendly;
    }

    @Override
    public void getOrders(String perfilId, Callback<List<Pedido>> callback) {
        supabaseApi.getPedidos("eq." + perfilId, "*", "creado_at.desc").enqueue(new retrofit2.Callback<List<OrderDTO>>() {
            @Override
            public void onResponse(Call<List<OrderDTO>> call, Response<List<OrderDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pedido> domainOrders = new ArrayList<>();
                    for (OrderDTO dto : response.body()) {
                        domainOrders.add(dto.toDomain());
                    }
                    callback.onSuccess(domainOrders);
                } else callback.onError(UserFriendlyError.fromResponse(response, "No se pudieron cargar tus pedidos"));
            }
            @Override
            public void onFailure(Call<List<OrderDTO>> call, Throwable t) {
                callback.onError(UserFriendlyError.fromThrowable(t));
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
                else callback.onError(UserFriendlyError.fromResponse(response, "No se pudo eliminar el pedido"));
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(UserFriendlyError.fromThrowable(t));
            }
        });
    }

    @Override
    public void getOrderItems(Long orderId, Callback<List<Pedido.Item>> callback) {
        if (orderId == null) return;
        supabaseApi.getPedidoItems("eq." + orderId, "*,productos(nombre,imagen_url)").enqueue(new retrofit2.Callback<List<OrderDTO.Item>>() {
            @Override
            public void onResponse(Call<List<OrderDTO.Item>> call, Response<List<OrderDTO.Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pedido.Item> domainItems = new ArrayList<>();
                    for (OrderDTO.Item dto : response.body()) {
                        domainItems.add(dto.toDomain());
                    }
                    callback.onSuccess(domainItems);
                } else callback.onError(UserFriendlyError.fromResponse(response, "No se pudo cargar el detalle del pedido"));
            }
            @Override
            public void onFailure(Call<List<OrderDTO.Item>> call, Throwable t) {
                callback.onError(UserFriendlyError.fromThrowable(t));
            }
        });
    }

    @Override
    public void updateItemQuantity(Long itemId, Integer newQuantity, Callback<Void> callback) {
        if (itemId == null) return;
        OrderDTO.Item update = new OrderDTO.Item();
        update.cantidad = newQuantity;
        
        supabaseApi.updateItemPedido("eq." + itemId, update).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onSuccess(null);
                else callback.onError(UserFriendlyError.fromResponse(response, "No se pudo actualizar la cantidad"));
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(UserFriendlyError.fromThrowable(t));
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
                else callback.onError(UserFriendlyError.fromResponse(response, "No se pudo quitar el producto del pedido"));
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(UserFriendlyError.fromThrowable(t));
            }
        });
    }

}
