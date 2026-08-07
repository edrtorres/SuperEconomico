package com.uth.supereconomico.presentation.viewmodel.repartidor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uth.supereconomico.data.remote.SesionSupabase
import com.uth.supereconomico.domain.entities.Pedido
import com.uth.supereconomico.domain.repositories.RepartidorRepository
import com.uth.supereconomico.utils.Resource

class HomeRepartidorViewModel(private val repository: RepartidorRepository) : ViewModel() {

    private val _pedidos = MutableLiveData<Resource<List<Pedido>>>()
    val pedidos: LiveData<Resource<List<Pedido>>> = _pedidos

    fun cargarPedidos() {
        val repartidorId = SesionSupabase.obtenerIdUsuario() ?: return
        _pedidos.value = Resource.Loading
        
        repository.getPedidosActivos(repartidorId, object : RepartidorRepository.Callback<List<Pedido>> {
            override fun onSuccess(result: List<Pedido>) {
                _pedidos.postValue(Resource.Success(result))
            }

            override fun onError(message: String) {
                _pedidos.postValue(Resource.Error(message))
            }
        })
    }
}
