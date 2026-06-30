package com.uth.supereconomico.presentation.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.uth.supereconomico.R;
import com.uth.supereconomico.domain.entities.Producto;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final List<Producto> productos;

    public ProductAdapter(List<Producto> productos) {
        this.productos = productos;
    }

    public void updateProducts(List<Producto> newProducts) {
        this.productos.clear();
        this.productos.addAll(newProducts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto = productos.get(position);
        holder.tvName.setText(producto.getNombre());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "L. %.2f", producto.getPrecio()));

        if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(producto.getImagenUrl())
                    .placeholder(android.R.drawable.ic_menu_report_image)
                    .into(holder.ivImage);
        }
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName;
        TextView tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_product_image);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
        }
    }
}
