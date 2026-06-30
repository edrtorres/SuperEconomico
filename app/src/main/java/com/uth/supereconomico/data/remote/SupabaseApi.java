package com.uth.supereconomico.data.remote;

import com.uth.supereconomico.data.remote.models.ErrorLogRequest;
import com.uth.supereconomico.data.remote.models.ProductDTO;
import com.uth.supereconomico.data.remote.models.UserDTO;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseApi {
    
    @GET("rest/v1/productos")
    Call<List<ProductDTO>> getProductos(
        @Query("esta_activo") String estaActivo,
        @Query("select") String select
    );

    @GET("rest/v1/perfiles")
    Call<List<UserDTO>> getPerfil(
        @Query("id") String id,
        @Query("select") String select
    );

    @GET("rest/v1/perfiles")
    Call<List<UserDTO>> getPerfilPorTelefono(
        @Query("telefono") String telefono,
        @Query("select") String select
    );

    @POST("rest/v1/logs_errores")
    Call<Void> logError(@Body ErrorLogRequest request);
}
