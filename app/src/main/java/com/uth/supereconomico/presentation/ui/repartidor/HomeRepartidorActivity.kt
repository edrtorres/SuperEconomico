package com.uth.supereconomico.presentation.ui.repartidor

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.uth.supereconomico.R
import com.uth.supereconomico.databinding.ActivityHomeRepartidorBinding
import com.uth.supereconomico.presentation.ui.LoginActivity
import com.uth.supereconomico.presentation.ui.adapters.repartidor.PedidoAdapter
import com.uth.supereconomico.presentation.ui.repartidor.detalle.DetallePedidoRepartidorActivity
import com.uth.supereconomico.presentation.viewmodel.ViewModelFactory
import com.uth.supereconomico.presentation.viewmodel.repartidor.HomeRepartidorViewModel
import com.uth.supereconomico.utils.Resource

class HomeRepartidorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeRepartidorBinding
    private lateinit var viewModel: HomeRepartidorViewModel
    private lateinit var adapter: PedidoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeRepartidorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        
        setupViewModel()
        setupRecyclerView()
        setupListeners()
        
        viewModel.cargarPedidos()
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[HomeRepartidorViewModel::class.java]

        viewModel.pedidos.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.swipeRefresh.isRefreshing = true
                    binding.layoutEmpty.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    val pedidos = resource.data
                    adapter.updatePedidos(pedidos)
                    binding.layoutEmpty.visibility = if (pedidos.isEmpty()) View.VISIBLE else View.GONE
                }
                is Resource.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = PedidoAdapter(emptyList()) { pedido ->
            val intent = Intent(this, DetallePedidoRepartidorActivity::class.java)
            intent.putExtra("pedido_id", pedido.id)
            startActivity(intent)
        }
        binding.rvPedidos.layoutManager = LinearLayoutManager(this)
        binding.rvPedidos.adapter = adapter
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.cargarPedidos()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_repartidor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            com.uth.supereconomico.di.Injection.provideAuthRepository().logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

