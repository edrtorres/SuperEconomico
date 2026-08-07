package com.uth.supereconomico.data.remote

import com.uth.supereconomico.data.remote.models.OrderDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RepartidorApi {
    @POST("rest/v1/rpc/pedidos_activos_por_repartidor")
    fun getPedidosActivos(@Body request: PedidosActivosRequest): Call<List<OrderDTO>>

    data class PedidosActivosRequest(
        val p_repartidor_id: String
    )
}
