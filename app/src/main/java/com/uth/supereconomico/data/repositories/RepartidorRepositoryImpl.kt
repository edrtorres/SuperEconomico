package com.uth.supereconomico.data.repositories

import com.uth.supereconomico.data.remote.RepartidorApi
import com.uth.supereconomico.data.remote.models.OrderDTO
import com.uth.supereconomico.domain.entities.Pedido
import com.uth.supereconomico.domain.repositories.RepartidorRepository
import com.uth.supereconomico.utils.UserFriendlyError
import retrofit2.Call
import retrofit2.Response

class RepartidorRepositoryImpl(private val api: RepartidorApi) : RepartidorRepository {
    override fun getPedidosActivos(repartidorId: String, callback: RepartidorRepository.Callback<List<Pedido>>) {
        api.getPedidosActivos(RepartidorApi.PedidosActivosRequest(repartidorId)).enqueue(object : retrofit2.Callback<List<OrderDTO>> {
            override fun onResponse(call: Call<List<OrderDTO>>, response: Response<List<OrderDTO>>) {
                if (response.isSuccessful && response.body() != null) {
                    val domainList = response.body()!!.map { it.toDomain() }
                    callback.onSuccess(domainList)
                } else {
                    callback.onError(UserFriendlyError.fromResponse(response, "No se pudieron cargar los pedidos"))
                }
            }

            override fun onFailure(call: Call<List<OrderDTO>>, t: Throwable) {
                callback.onError(UserFriendlyError.fromThrowable(t))
            }
        })
    }
}
