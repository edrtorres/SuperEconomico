package com.uth.supereconomico.presentation.ui.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
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
import com.uth.supereconomico.domain.entities.Producto;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final List<Producto> productos;
    private final OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Producto producto);
    }

    public ProductAdapter(List<Producto> productos, OnProductClickListener listener) {
        this.productos = productos;
        this.listener = listener;
    }

    public void updateProducts(List<Producto> newProducts) {
        this.productos.clear();
        if (newProducts != null) {
            this.productos.addAll(newProducts);
        }
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
        
        if (producto.isEsOferta()) {
            holder.tvBadgeOferta.setVisibility(View.VISIBLE);
            holder.tvOriginalPrice.setVisibility(View.VISIBLE);
            holder.tvOriginalPrice.setText(String.format(Locale.US, "L. %,.2f", producto.getPrecio()));
            holder.tvOriginalPrice.setPaintFlags(holder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvPrice.setText(String.format(Locale.US, "L. %,.2f", producto.getPrecioOferta()));
            holder.tvPrice.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.amarillo_oferta));
        } else {
            holder.tvBadgeOferta.setVisibility(View.GONE);
            holder.tvOriginalPrice.setVisibility(View.GONE);
            holder.tvPrice.setText(String.format(Locale.US, "L. %,.2f", producto.getPrecio()));
            holder.tvPrice.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.primary));
        }

        if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
            String iconSource = producto.getImagenUrl();
            if (iconSource.length() > 200) { // Probablemente Base64
                try {
                    byte[] decodedString = Base64.decode(iconSource, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.ivImage.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    Glide.with(holder.itemView.getContext()).load(iconSource).into(holder.ivImage);
                }
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(iconSource)
                        .placeholder(android.R.drawable.ic_menu_report_image)
                        .into(holder.ivImage);
            }
        } else {
            holder.ivImage.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        // El usuario solicitó que el click sea en el signo más y no en la imagen/item completo
        holder.btnAddToCart.setOnClickListener(v -> listener.onProductClick(producto));
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName;
        TextView tvPrice;
        TextView tvOriginalPrice;
        TextView tvBadgeOferta;
        View btnAddToCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_product_image);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvOriginalPrice = itemView.findViewById(R.id.tv_original_price);
            tvBadgeOferta = itemView.findViewById(R.id.tv_badge_oferta);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
        }
    }
}
