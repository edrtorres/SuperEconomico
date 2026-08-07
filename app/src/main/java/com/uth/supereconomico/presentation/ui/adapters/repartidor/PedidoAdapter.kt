package com.uth.supereconomico.presentation.ui.adapters.repartidor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.uth.supereconomico.R
import com.uth.supereconomico.domain.entities.Pedido
import com.uth.supereconomico.utils.CurrencyUtils
import com.uth.supereconomico.utils.DateUtils

class PedidoAdapter(
    private var pedidos: List<Pedido>,
    private val onPedidoClick: (Pedido) -> Unit
) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    fun updatePedidos(newPedidos: List<Pedido>) {
        pedidos = newPedidos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido_repartidor, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        holder.bind(pedidos[position], onPedidoClick)
    }

    override fun getItemCount(): Int = pedidos.size

    class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        private val chipEstado: Chip = itemView.findViewById(R.id.chipEstado)
        private val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        private val tvDireccion: TextView = itemView.findViewById(R.id.tvDireccion)
        private val tvMetodoPago: TextView = itemView.findViewById(R.id.tvMetodoPago)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)

        fun bind(pedido: Pedido, onClick: (Pedido) -> Unit) {
            tvOrderId.text = "Pedido #${pedido.id}"
            tvTotal.text = CurrencyUtils.formatGTQ(pedido.total)
            tvDireccion.text = pedido.direccionId?.toString() ?: "N/A"
            tvMetodoPago.text = pedido.metodoPago ?: "N/A"
            tvFecha.text = DateUtils.formatSupabaseDate(pedido.creadoAt)

            chipEstado.text = pedido.estado?.uppercase() ?: "PENDIENTE"
            val colorRes = when (pedido.estado?.lowercase()) {
                "preparando" -> R.color.status_preparing
                "en_camino" -> R.color.status_on_way
                "entregado" -> R.color.status_delivered
                else -> R.color.status_pending
            }
            chipEstado.setChipBackgroundColorResource(colorRes)
            
            itemView.setOnClickListener { onClick(pedido) }
        }
    }
}
