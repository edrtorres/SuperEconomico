package com.uth.supereconomico.presentation.ui.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.uth.supereconomico.R;
import com.uth.supereconomico.data.remote.models.OrderRequest;
import com.uth.supereconomico.domain.entities.Producto;
import com.uth.supereconomico.di.Injection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final List<OrderRequest.Item> items = new ArrayList<>();
    private final OnCartItemClickListener listener;

    public interface OnCartItemClickListener {
        void onUpdateQuantity(OrderRequest.Item item, int newQuantity);
        void onDeleteItem(OrderRequest.Item item);
    }

    public CartAdapter(OnCartItemClickListener listener) {
        this.listener = listener;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderRequest.Item item = items.get(position);
        
        holder.tvName.setText(item.nombre != null ? item.nombre : "Producto #" + item.productoId);
        holder.tvDescription.setText(""); // Opcional: podrías guardar también la descripción
        
        double currentPrice = item.precioUnitario != null ? item.precioUnitario : 0.0;
        holder.tvPrice.setText(String.format(Locale.US, "L. %,.2f", currentPrice));
        holder.tvQuantity.setText(String.valueOf(item.cantidad));

        // Cargar imagen (Soporta URL y Base64)
        if (item.imagenUrl != null && !item.imagenUrl.isEmpty()) {
            if (item.imagenUrl.length() > 200) {
                try {
                    byte[] decodedString = Base64.decode(item.imagenUrl, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.ivProduct.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    Glide.with(holder.itemView.getContext()).load(item.imagenUrl).into(holder.ivProduct);
                }
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(item.imagenUrl)
                        .placeholder(android.R.drawable.ic_menu_report_image)
                        .into(holder.ivProduct);
            }
        } else {
            holder.ivProduct.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        holder.btnIncrease.setOnClickListener(v -> listener.onUpdateQuantity(item, item.cantidad + 1));
        holder.btnDecrease.setOnClickListener(v -> {
            if (item.cantidad > 1) {
                listener.onUpdateQuantity(item, item.cantidad - 1);
            } else {
                listener.onDeleteItem(item);
            }
        });
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteItem(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvPrice, tvOriginalPrice, tvQuantity;
        ImageView ivProduct;
        View btnIncrease, btnDecrease;
        ImageButton btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvDescription = itemView.findViewById(R.id.tvProductDescription);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            ivProduct = itemView.findViewById(R.id.ivProductImage);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
