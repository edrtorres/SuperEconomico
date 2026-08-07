package com.uth.supereconomico.presentation.ui.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.uth.supereconomico.R;
import com.uth.supereconomico.domain.entities.Pedido;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final List<Pedido.Item> items = new ArrayList<>();
    private final OnCartClickListener listener;

    public interface OnCartClickListener {
        void onUpdateQuantity(Pedido.Item item, int newQuantity);
        void onDeleteItem(Pedido.Item item);
    }

    public CartAdapter(OnCartClickListener listener) {
        this.listener = listener;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pedido.Item item = items.get(position);
        
        holder.tvName.setText(item.getNombre());
        holder.tvPrice.setText(String.format(Locale.US, "L. %,.2f", item.getPrecioUnitario()));
        holder.tvQuantity.setText(String.valueOf(item.getCantidad()));

        if (item.getImagenUrl() != null && !item.getImagenUrl().isEmpty()) {
            String url = item.getImagenUrl();
            if (url.length() > 200) {
                try {
                    byte[] decodedString = Base64.decode(url, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.ivProduct.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    Glide.with(holder.itemView.getContext()).load(url).into(holder.ivProduct);
                }
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(url)
                        .placeholder(android.R.drawable.ic_menu_report_image)
                        .into(holder.ivProduct);
            }
        } else {
            holder.ivProduct.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        holder.btnPlus.setOnClickListener(v -> listener.onUpdateQuantity(item, item.getCantidad() + 1));
        holder.btnMinus.setOnClickListener(v -> {
            if (item.getCantidad() > 1) {
                listener.onUpdateQuantity(item, item.getCantidad() - 1);
            }
        });
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteItem(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice, tvQuantity;
        View btnPlus, btnMinus, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnIncrease);
            btnMinus = itemView.findViewById(R.id.btnDecrease);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
