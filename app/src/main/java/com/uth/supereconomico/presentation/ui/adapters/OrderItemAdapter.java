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
import com.uth.supereconomico.domain.entities.Pedido;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private final List<Pedido.Item> items = new ArrayList<>();
    private final OnOrderItemClickListener listener;

    public interface OnOrderItemClickListener {
        void onUpdateQuantity(Pedido.Item item, int newQuantity);
        void onDeleteItem(Pedido.Item item);
    }

    public OrderItemAdapter(OnOrderItemClickListener listener) {
        this.listener = listener;
    }

    private boolean isEditable = true;

    public void setEditable(boolean editable) {
        this.isEditable = editable;
    }

    public void setItems(List<Pedido.Item> newItems) {
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
        Pedido.Item item = items.get(position);
        
        holder.tvName.setText(item.getNombre() != null ? item.getNombre() : "Producto #" + item.getProductoId());
        
        double precio = item.getPrecioUnitario() != null ? item.getPrecioUnitario() : 0.0;
        int cantidad = item.getCantidad() != null ? item.getCantidad() : 0;
        
        holder.tvPrice.setText(String.format(Locale.US, "L. %,.2f", precio));
        holder.tvQuantityLabel.setText("Cant: " + cantidad);

        if (item.getImagenUrl() != null && !item.getImagenUrl().isEmpty()) {
            String url = item.getImagenUrl();
            if (url.length() > 200) {
                try {
                    byte[] decodedString = Base64.decode(url, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.ivProduct.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    com.bumptech.glide.Glide.with(holder.itemView.getContext()).load(url).into(holder.ivProduct);
                }
            } else {
                com.bumptech.glide.Glide.with(holder.itemView.getContext())
                        .load(url)
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
