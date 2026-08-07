package com.uth.supereconomico.domain.repositories

import com.uth.supereconomico.domain.entities.Pedido

interface RepartidorRepository {
    interface Callback<T> {
        fun onSuccess(result: T)
        fun onError(message: String)
    }

    fun getPedidosActivos(repartidorId: String, callback: Callback<List<Pedido>>)
}
