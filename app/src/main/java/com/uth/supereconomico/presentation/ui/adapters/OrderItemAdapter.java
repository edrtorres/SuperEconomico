package com.uth.supereconomico.presentation.ui.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.uth.supereconomico.R;
import com.uth.supereconomico.data.remote.models.OrderRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private final List<OrderRequest.Item> items = new ArrayList<>();
    private final OnOrderItemClickListener listener;

    public interface OnOrderItemClickListener {
        void onUpdateQuantity(OrderRequest.Item item, int newQuantity);
        void onDeleteItem(OrderRequest.Item item);
    }

    public OrderItemAdapter(OnOrderItemClickListener listener) {
        this.listener = listener;
    }

    private boolean isEditable = true;

    public void setEditable(boolean editable) {
        this.isEditable = editable;
    }

    public void setItems(List<OrderRequest.Item> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderRequest.Item item = items.get(position);
        
        // Priorizar datos del join 'productos' si los campos locales son nulos
        String nombre = item.nombre;
        String imagenUrl = item.imagenUrl;
        
        if (item.producto != null) {
            if (nombre == null || nombre.isEmpty()) nombre = item.producto.nombre;
            if (imagenUrl == null || imagenUrl.isEmpty()) imagenUrl = item.producto.imagenUrl;
        }

        holder.tvName.setText(nombre != null ? nombre : "Producto #" + item.productoId);
        
        double precio = item.precioUnitario != null ? item.precioUnitario : 0.0;
        int cantidad = item.cantidad != null ? item.cantidad : 0;
        
        holder.tvPrice.setText(String.format(Locale.US, "L. %,.2f", precio));
        holder.tvQuantityLabel.setText("Cant: " + cantidad);

        // Cargar imagen (Soporta URL y Base64)
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            if (imagenUrl.length() > 200) {
                try {
                    byte[] decodedString = Base64.decode(imagenUrl, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.ivProduct.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    com.bumptech.glide.Glide.with(holder.itemView.getContext()).load(imagenUrl).into(holder.ivProduct);
                }
            } else {
                com.bumptech.glide.Glide.with(holder.itemView.getContext())
                        .load(imagenUrl)
                        .placeholder(android.R.drawable.ic_menu_report_image)
                        .into(holder.ivProduct);
            }
        } else {
            holder.ivProduct.setImageResource(android.R.drawable.ic_menu_report_image);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantityLabel;
        android.widget.ImageView ivProduct;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvItemPrice);
            tvQuantityLabel = itemView.findViewById(R.id.tvQuantityLabel);
            ivProduct = itemView.findViewById(R.id.ivItemImage);
        }
    }
}
