package com.uth.supereconomico.data.remote;

import com.uth.supereconomico.data.remote.models.CategoriaDTO;
import com.uth.supereconomico.data.remote.models.DireccionRequest;
import com.uth.supereconomico.data.remote.models.ErrorLogRequest;
import com.uth.supereconomico.data.remote.models.OrderRequest;
import com.uth.supereconomico.data.remote.models.ProductDTO;
import com.uth.supereconomico.data.remote.models.UserDTO;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseApi {
    
    @GET("rest/v1/categorias")
    Call<List<CategoriaDTO>> getCategorias(@Query("select") String select);

    @GET("rest/v1/productos")
    Call<List<ProductDTO>> getProductos(
        @Query("esta_activo") String estaActivo,
        @Query("select") String select
    );

    @GET("rest/v1/productos")
    Call<List<ProductDTO>> getProductosPorCategoria(
        @Query("categoria_id") String categoriaId,
        @Query("esta_activo") String estaActivo,
        @Query("select") String select
    );

    @POST("rest/v1/pedidos")
    Call<List<OrderRequest>> crearPedido(
        @Header("Prefer") String prefer,
        @Body OrderRequest pedido
    );

    @POST("rest/v1/pedido_items")
    Call<Void> crearItemsPedido(@Body List<OrderRequest.Item> items);

    @GET("rest/v1/pedidos")
    Call<List<OrderRequest>> getPedidos(
            @Query("perfil_id") String perfilId,
            @Query("select") String select,
            @Query("order") String order
    );

    @DELETE("rest/v1/pedidos")
    Call<Void> deletePedido(@Query("id") String filtroId);

    @GET("rest/v1/pedido_items")
    Call<List<OrderRequest.Item>> getPedidoItems(
            @Query("pedido_id") String pedidoId,
            @Query("select") String select
    );

    @PATCH("rest/v1/pedido_items")
    Call<Void> updateItemPedido(
            @Query("id") String filtroId,
            @Body OrderRequest.Item item
    );

    @DELETE("rest/v1/pedido_items")
    Call<Void> deleteItemPedido(@Query("id") String filtroId);

    @GET("rest/v1/perfiles")
    Call<List<UserDTO>> getPerfil(
        @Query("id") String id,
        @Query("select") String select
    );

    @GET("rest/v1/perfiles")
    Call<List<UserDTO>> getPerfilPorTelefono(
            @Query("telefono") String filtroTelefono,
            @Query("select") String columnas
    );

    @PATCH("rest/v1/perfiles")
    Call<Void> updatePerfil(
            @Query("id") String id,
            @Body UserDTO perfil
    );

    @GET("rest/v1/direcciones")
    Call<List<DireccionRequest>> getDirecciones(
            @Query("perfil_id") String perfilId,
            @Query("select") String select
    );

    @POST("rest/v1/direcciones")
    Call<Void> insertDireccion(@Body DireccionRequest direccion);

    @DELETE("rest/v1/direcciones")
    Call<Void> deleteDireccion(@Query("id") String filtroId);

    @GET("rest/v1/metodos_pago")
    Call<List<com.uth.supereconomico.data.remote.models.MetodoPagoDTO>> getMetodosPago(
            @Query("perfil_id") String perfilId,
            @Query("select") String select
    );

    @POST("rest/v1/metodos_pago")
    Call<Void> insertMetodoPago(@Body com.uth.supereconomico.data.remote.models.MetodoPagoDTO metodo);

    @DELETE("rest/v1/metodos_pago")
    Call<Void> deleteMetodoPago(@Query("id") String filtroId);

    @POST("rest/v1/logs_errores")
    Call<Void> logError(@Body ErrorLogRequest request);
}
